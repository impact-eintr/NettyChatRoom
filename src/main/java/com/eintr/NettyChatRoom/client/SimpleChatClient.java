package com.eintr.NettyChatRoom.client;

import com.eintr.NettyChatRoom.message.ChatRequestMessage;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.io.BufferedReader;
import java.io.InputStreamReader;


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