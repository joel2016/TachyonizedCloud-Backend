package me.ingrim4.tachyonizedcloud.webserver.handler.websocket;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler.ServerHandshakeStateEvent;

/* TODO SessionSupport
 * autoclose + getter
 * */
public class TachyonizedWebSocketHandler extends SimpleChannelInboundHandler<WebSocketFrame> {

	public static final List<TachyonizedWebSocketHandler> HANDLER = new ArrayList<TachyonizedWebSocketHandler>();
	private static final JsonParser JSON_PARSER = new JsonParser();

	private Channel channel;

	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
		if(evt instanceof ServerHandshakeStateEvent) {
			this.channel = ctx.channel();
			TachyonizedWebSocketHandler.HANDLER.add(this);
		}
	}

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		if(this.channel != null)
			this.channel.flush();
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, WebSocketFrame frame) throws Exception {
		if(frame instanceof TextWebSocketFrame) {
			try {
				JsonElement jsonElement = JSON_PARSER.parse(((TextWebSocketFrame) frame).text());

				JsonArray root;
				if(!jsonElement.isJsonArray() || (root = jsonElement.getAsJsonArray()).size() != 2) {
					this.channel.write(new TextWebSocketFrame(WebSocketErrors.INVALID_REQUEST.getError()), this.channel.voidPromise());
					return;
				}

				JsonObject payload = root.get(1).isJsonObject() ? root.get(1).getAsJsonObject() : new JsonObject();
				switch (root.get(0).getAsString()) {
					default:
						this.channel.write(new TextWebSocketFrame(WebSocketErrors.UNKNOWN_ACTION.getError()), this.channel.voidPromise());
						break;
				}
			} catch (UnsupportedOperationException | JsonParseException e) {
				this.channel.write(new TextWebSocketFrame(WebSocketErrors.INVALID_REQUEST.getError()), this.channel.voidPromise());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		TachyonizedWebSocketHandler.HANDLER.remove(this);
	}
}
