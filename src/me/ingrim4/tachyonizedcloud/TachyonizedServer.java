package me.ingrim4.tachyonizedcloud;

import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.List;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import me.ingrim4.tachyonizedcloud.util.ThreadUtil;

public class TachyonizedServer {

	public static final boolean EPOLL = Epoll.isAvailable();
	private final List<ChannelFuture> serverChannel = new ArrayList<ChannelFuture>();

	protected void start(SocketAddress address, int threads, ChannelInitializer<Channel> channelInitializer) {
		synchronized (this.serverChannel) {
			this.serverChannel.add(
				new ServerBootstrap()
				.group(EPOLL ? new EpollEventLoopGroup(threads, ThreadUtil.getThreadFactory("Netty Epoll Server IO #%d", true)) : new NioEventLoopGroup(threads, ThreadUtil.getThreadFactory("Netty Server IO #%d", true)))
				.channel(EPOLL ? EpollServerSocketChannel.class : NioServerSocketChannel.class)
				.childHandler(channelInitializer).localAddress(address).bind().syncUninterruptibly()
			);
		}
	}

	public void close() {
		for(ChannelFuture channelFuture : this.serverChannel)
			try {
				channelFuture.channel().close().sync();
			} catch (InterruptedException e) {
				throw new Error("Unable to stop webserver", e);
			}
	}
}
