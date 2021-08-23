package com.eintr.NettyChatRoom.server;

import com.eintr.NettyChatRoom.message.*;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.util.logging.Level;
import java.util.logging.Logger;

public class SimpleChatServerHandler extends ChannelInboundHandlerAdapter {
	public static ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
	private static final Logger logger = Logger.getLogger(
					SimpleChatServerHandler.class.getName());
	@Override
	public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
		System.out.println(ctx.channel().remoteAddress()+"上线");
		Channel incoming = ctx.channel();
		channels.writeAndFlush("[SERVER] - " + incoming.remoteAddress() + " 上线\n");
		channels.add(incoming);
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		System.out.println(ctx.channel().remoteAddress()+"在线");
		Channel incoming = ctx.channel();
		// Broadcast a message to multiple Channels
		channels.writeAndFlush("[SERVER] - " + incoming.remoteAddress() + " 在线\n");
	}
	@Override
	public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {  // (3)
		Channel incoming = ctx.channel();
		// Broadcast a message to multiple Channels
		channels.writeAndFlush("[SERVER] - " + incoming.remoteAddress() + " 离开\n");
	}

	@Override
	public void channelRead(
					ChannelHandlerContext ctx, Object msg) throws Exception {
		if(((Message)msg).getMessageType() == Message.LoginRequestMessage) {
			// 处理登陆
			//if (((LoginRequestMessage) msg).getUsername().equals("eintr")&&
			//				((LoginRequestMessage) msg).getPassword().equals("123")) {
			//	LoginResponseMessage respMsg = new LoginResponseMessage(true, null);
			//	ctx.write(respMsg);
			//	System.out.println("login OK!!!");
			//}else {
			//	LoginResponseMessage respMsg = new LoginResponseMessage(false, null);
			//	ctx.write(respMsg);
			//	System.out.println("login Not OK!!!");
			//}
			LoginResponseMessage respMsg = new LoginResponseMessage(true, null);
			ctx.write(respMsg);
		} else if(((Message) msg).getMessageType() == Message.ChatRequestMessage) {
			// 处理聊天
			ChatResponseMessage respMsg = new ChatResponseMessage(
							((ChatRequestMessage) msg).getFrom(),
							((ChatRequestMessage) msg).getContent()
			);
			channels.writeAndFlush(respMsg);
		}
	}
	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		ctx.flush();
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
