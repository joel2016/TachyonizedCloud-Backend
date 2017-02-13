package me.ingrim4.tachyonizedcloud.webserver.handler.http;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import me.ingrim4.tachyonizedcloud.webserver.handler.http.container.TachyonizedHttpRequest;

public class TachyonizedHttpHandler extends SimpleChannelInboundHandler<TachyonizedHttpRequest> {

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, TachyonizedHttpRequest request) throws Exception {
		
	}
}
