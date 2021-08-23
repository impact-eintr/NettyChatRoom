# NettyChatRoom 开发文档

用netty 开发为的是它的高性能和封装性(原生Socket写吐了, netty的对象序列化真香) 
## 通讯协议
- Message.java 实现了 Serializable 接口 以序列化
  - messageType 数据类型 现在实现的有登陆和聊天
  - squenceId 数据编号 保留接口后续可以用于记录消息顺序(你懂的)
  - getMessageType() 获取数据类型 子类必须要重写这个方法
  
- AbstractResponseMessage Message的抽象子类 作为所有响应消息的父类
  - success 是否成功响应
  - reason 失败原因
  ``` java
  public AbstractResponseMessage(boolean success, String reason) {
    this.success = success;
    this.reason = reason;
  }
  ```
> 请求
- LoginRequestMessage Message的子类 
  - username
  - password
- ChatRequestMessage
  - from 消息来自
  - to 消息发往
  - content 消息内容
  
> 响应
- LoginResponseMessage AbstractResponseMessage的子类 响应登陆消息
- ChatResponseMessage AbstractResponseMessage的子类 响应聊天消息
  - from 消息来自
  - content 消息内容
## 服务端
``` java
public class SimpleChatServer {

	private int port;

	public SimpleChatServer(int port) {
		this.port = port;
	}

	public void run() throws Exception {

		EventLoopGroup bossGroup = new NioEventLoopGroup(); // (1)
		EventLoopGroup workerGroup = new NioEventLoopGroup();
		try {
			ServerBootstrap b = new ServerBootstrap(); // (2)
			b.group(bossGroup, workerGroup)
							.channel(NioServerSocketChannel.class) // (3)
							.childHandler(new SimpleChatServerInitializer())  //(4)
							.option(ChannelOption.SO_BACKLOG, 128)          // (5)
							.childOption(ChannelOption.SO_KEEPALIVE, true); // (6)

			System.out.println("SimpleChatServer 启动了");

			// 绑定端口，开始接收进来的连接
			ChannelFuture f = b.bind(port).sync(); // (7)

			// 等待服务器  socket 关闭 。
			// 在这个例子中，这不会发生，但你可以优雅地关闭你的服务器。
			f.channel().closeFuture().sync();

		} finally {
			workerGroup.shutdownGracefully();
			bossGroup.shutdownGracefully();

			System.out.println("SimpleChatServer 关闭了");
		}
	}

	public static void main(String[] args) throws Exception {
		int port;
		if (args.length > 0) {
			port = Integer.parseInt(args[0]);
		} else {
			port = 6430;
		}
		new SimpleChatServer(port).run();

	}
}

```
### 服务端的总体流程:
- 建立连接
- 注册处理函数，等待客户端的访问
- 在成功建立与客户端后的链接后，会得到用户发来的登陆消息LoginRequestMessage(至于这些怎么验证 由于时间仓促没有实现 可以用数据库存储 然后保存在一个全局的map中 来保证用户登陆的唯一性)
- 构建LoginResponseMessage
- 获取ChatRequestMessage 解包消息（这里其实可以进行数据处理 比如@功能 敏感词处理等, 这里不做特殊处理）
- 构建ChatResponseMessage 发送消息时 服务端不会区分谁是谁 这个任务由客户端进行（通过解析协议 可以获取From） 可以提高一点服务端的性能


## 客户端
```java
public class SimpleChatClient {
	public static void main(String[] args) throws Exception{
		new SimpleChatClient("localhost", 6430).run();
	}

	private final String host;
	private final int port;

	public SimpleChatClient(String host, int port){
		this.host = host;
		this.port = port;
	}

	public void run() throws Exception{
		EventLoopGroup group = new NioEventLoopGroup();
		try {
			Bootstrap bootstrap  = new Bootstrap()
							.group(group)
							.channel(NioSocketChannel.class)
							.handler(new SimpleChatClientInitializer());
			Channel channel = bootstrap.connect(host, port).sync().channel();

			// 获取用户输入
			BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
			while(true){
				System.out.println("==================================");
				System.out.println("send [content]");
				System.out.println("quit");
				System.out.println("==================================");
				String command = in.readLine();
				String[] s = command.split(" ");
				switch (s[0]){
					case "send":
						if (s.length != 2) {
							continue;
						}
						ChatRequestMessage msg = new ChatRequestMessage(
										channel.localAddress().toString(),
										channel.remoteAddress().toString(),
										s[1]);
						channel.writeAndFlush(msg);
						break;
					case "quit":
						channel.close();
						return;
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			group.shutdownGracefully();
		}

	}
}
```
### 客户端的总体流程
- 建立连接 
- 注册处理函数 
- 在成功建立与服务器的链接时（处于“激活”状态，可以这个时候获取用户输入 帐号 密码 ）
- 然后解析服务端返回的信息 判断是否成功登陆
- 然后用户可以选择聊天或者退出
- 发送消息时 服务端不会区分谁是谁 这个任务由客户端进行（通过解析协议 可以获取From） 可以提高一点服务端的性能
