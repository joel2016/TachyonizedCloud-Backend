package me.ingrim4.tachyonizedcloud.webserver.handler.http;

import java.io.File;
import java.util.List;

import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.handler.codec.http.cookie.ServerCookieDecoder;
import io.netty.handler.codec.http.multipart.Attribute;
import io.netty.handler.codec.http.multipart.DefaultHttpDataFactory;
import io.netty.handler.codec.http.multipart.DiskFileUpload;
import io.netty.handler.codec.http.multipart.FileUpload;
import io.netty.handler.codec.http.multipart.HttpDataFactory;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder.ErrorDataDecoderException;
import io.netty.handler.codec.http.multipart.InterfaceHttpData;
import me.ingrim4.tachyonizedcloud.webserver.handler.http.container.TachyonizedHttpRequest;

public class TachyonizedHttpDefaultHandler extends MessageToMessageDecoder<Object> {

	private static final HttpDataFactory HTTP_DATA_FACTORY = new DefaultHttpDataFactory(DefaultHttpDataFactory.MINSIZE);
	private static final FullHttpResponse INVALID_METHOD = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.METHOD_NOT_ALLOWED, Unpooled.EMPTY_BUFFER);

	private TachyonizedHttpRequest tachyonizedRequest;
	private HttpPostRequestDecoder decoder;
	private Channel channel;

	static {
		File baseDir = new File("Temp/");
		if(!baseDir.exists())
			baseDir.mkdirs();
		DiskFileUpload.deleteOnExitTemporaryFile = true;
		DiskFileUpload.baseDirectory = baseDir.getAbsolutePath();
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		super.channelActive(ctx);
		this.channel = ctx.channel();
	}

	@Override
	protected void decode(ChannelHandlerContext ctx, Object obj, List<Object> list) throws Exception {
		if(obj instanceof HttpRequest) {
			HttpRequest request = (HttpRequest) obj;

			QueryStringDecoder queryDecoder = new QueryStringDecoder(request.uri());
			this.tachyonizedRequest = new TachyonizedHttpRequest(request, queryDecoder.path());

			if(request.method().equals(HttpMethod.GET)) {
				this.tachyonizedRequest.getGet().putAll(queryDecoder.parameters());
			} else if(request.method().equals(HttpMethod.POST)) {
				try {
					this.decoder = new HttpPostRequestDecoder(HTTP_DATA_FACTORY, request);
				} catch (ErrorDataDecoderException e) {
					this.channel.close();
					return;
				}
			} else {
				this.channel.writeAndFlush(INVALID_METHOD.duplicate()).addListener(listener -> this.channel.close());
				return;
			}

			if(request.headers().contains(HttpHeaderNames.COOKIE))
				this.tachyonizedRequest.getCookies().addAll(ServerCookieDecoder.STRICT.decode(request.headers().get(HttpHeaderNames.COOKIE)));
		} else if(obj instanceof HttpContent) {
			HttpContent content = (HttpContent) obj;

			if(this.decoder != null) {
				try {
					decoder.offer(content);
				} catch (ErrorDataDecoderException e) {
					this.channel.close();
					return;
				}
			}

			if (content instanceof LastHttpContent) {
				if(this.decoder != null) {
					try {
						while (decoder.hasNext()) {
							InterfaceHttpData data = decoder.next();
							if(data != null)
								switch (data.getHttpDataType()) {
									case Attribute:
										Attribute attribute = (Attribute) data;
										this.tachyonizedRequest.getPost().put(data.getName(), attribute.getValue());
										attribute.delete();
										break;

									case FileUpload:
										FileUpload fileUpload = (FileUpload) data;
										this.tachyonizedRequest.getPost().put(data.getName(), fileUpload.copy());
										break;
		
									default:
										data.release();
										break;
								}
						}
					} catch (Exception e) { }

					this.decoder.destroy();
					this.decoder = null;
				}

				if(this.tachyonizedRequest != null) {
					list.add(this.tachyonizedRequest);
					this.tachyonizedRequest = null;
				}
			}
		}
	}
}
