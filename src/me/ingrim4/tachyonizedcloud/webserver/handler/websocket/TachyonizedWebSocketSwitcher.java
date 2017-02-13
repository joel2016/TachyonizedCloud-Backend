package me.ingrim4.tachyonizedcloud.webserver.handler.websocket;

import java.util.List;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.codec.http.websocketx.extensions.compression.WebSocketServerCompressionHandler;

public class TachyonizedWebSocketSwitcher extends MessageToMessageDecoder<HttpRequest> {

	/* TODO Auth
	 * 1st need to add authsys
	 * */
	@Override
	protected void decode(ChannelHandlerContext ctx, HttpRequest request, List<Object> list) throws Exception {
		list.add(request);

		if(new QueryStringDecoder(request.uri()).path().equals("/ws") && request.headers().contains(HttpHeaderNames.UPGRADE)) {
			ChannelPipeline pipeline = ctx.channel().pipeline();
			pipeline.replace("webSocketSwitcher", "aggregator", new HttpObjectAggregator(65536));
			pipeline.replace("compressor", "compressor", new WebSocketServerCompressionHandler());
			pipeline.replace("tachyonizedDecoder", "websocketCodec", new WebSocketServerProtocolHandler("/ws", null, true));
			pipeline.replace("httpHandler", "websocketHandler", new TachyonizedWebSocketHandler());
		}
	}

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		ctx.flush();
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		cause.printStackTrace();
	}
}
