package com.eintr.NettyChatRoom.server;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

public class SimpleChatServerInitializer extends
				ChannelInitializer<SocketChannel> {

	@Override
	public void initChannel(SocketChannel ch) throws Exception {
		ChannelPipeline pipeline = ch.pipeline();
		pipeline.addLast(
						new ObjectEncoder(),
						new ObjectDecoder(Integer.MAX_VALUE, ClassResolvers.cacheDisabled(null)),
						new SimpleChatServerHandler());
	}
}