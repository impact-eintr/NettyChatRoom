package com.eintr.NettyChatRoom.client;

import com.eintr.NettyChatRoom.message.*;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SimpleChatClientHandler extends ChannelInboundHandlerAdapter {
	private static final Logger logger = Logger.getLogger(
					com.eintr.NettyChatRoom.client.SimpleChatClientHandler.class.getName());
	private final LoginRequestMessage message;

	public SimpleChatClientHandler(int messageSize) {
		if (messageSize <= 0) {
			throw new IllegalArgumentException(
							"firstMessageSize: " + messageSize);
		}
		Scanner scanner = new Scanner(System.in);
		System.out.println("请输入用户名:");
		String username = scanner.nextLine();
		System.out.println("请输入密码:");
		String password = scanner.nextLine();

		message = new LoginRequestMessage(username, password);
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		// Send the message to Server
		ctx.writeAndFlush(message);
	}
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		// you can use the Object from Server here
		if (msg instanceof String) {
			System.out.println(msg);
		} else if(((AbstractResponseMessage)msg).getMessageType() == Message.LoginResponseMessage) {
			if (((LoginResponseMessage) msg).isSuccess()) {
				System.out.println("成功登陆");
			}else {
				System.out.println("用户名或者密码错误, 即将退出");
				ctx.close();
			}
		} else if(((AbstractResponseMessage) msg).getMessageType() == Message.ChatResponseMessage) {
			if (((ChatResponseMessage) msg).getFrom().
							equals(ctx.channel().localAddress().toString())) {
				System.out.printf ("[我]:%s\n========================\n",
								((ChatResponseMessage) msg).getContent());
			} else {
				System.out.printf ("[%s]:%s\n========================\n",
								((ChatResponseMessage) msg).getFrom(),
								((ChatResponseMessage) msg).getContent());
			}
		}
	}

	@Override
	public void exceptionCaught(
					ChannelHandlerContext ctx, Throwable cause) throws Exception {
		logger.log(
						Level.WARNING,
						"Unexpected exception from downstream.", cause);
		ctx.close();
	}
}