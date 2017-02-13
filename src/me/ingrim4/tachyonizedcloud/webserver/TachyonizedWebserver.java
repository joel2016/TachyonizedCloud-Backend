package me.ingrim4.tachyonizedcloud.webserver;

import java.net.InetSocketAddress;
import java.util.Objects;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.HttpContentCompressor;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.ssl.SslContext;
import me.ingrim4.tachyonizedcloud.TachyonizedServer;
import me.ingrim4.tachyonizedcloud.webserver.handler.http.TachyonizedHttpDefaultHandler;
import me.ingrim4.tachyonizedcloud.webserver.handler.http.TachyonizedHttpHandler;
import me.ingrim4.tachyonizedcloud.webserver.handler.websocket.TachyonizedWebSocketSwitcher;

public class TachyonizedWebserver extends TachyonizedServer {

	/* TODO Add SSL
	 * impl ssl + forcessl (letencrypt like)
	 * */
	private SslContext sslContext = null;
	private int sslPort = -1;
	private boolean forceSsl = false;

	public TachyonizedWebserver ssl(SslContext sslContext, int sslPort) {
		this.sslContext = sslContext;
		this.sslPort = sslPort;
		return this;
	}

	public TachyonizedWebserver forceSsl(boolean forceSsl) {
		Objects.requireNonNull(sslContext, "To enable forceSsl you need to define a SslContext");
		this.forceSsl = forceSsl;
		return this;
	}

	public TachyonizedWebserver start(InetSocketAddress address) {
		this.start(address, 12, new ChannelInitializer<Channel>() {

			@Override
			protected void initChannel(Channel channel) throws Exception {
				channel.config().setOption(ChannelOption.TCP_NODELAY, true);
				channel.pipeline()
				.addLast("httpCodec", new HttpServerCodec())
				.addLast("webSocketSwitcher", new TachyonizedWebSocketSwitcher())
				.addLast("compressor", new HttpContentCompressor())
				.addLast("tachyonizedDecoder", new TachyonizedHttpDefaultHandler())
				.addLast("httpHandler", new TachyonizedHttpHandler());
			}
		});
		return this;
	}
}
