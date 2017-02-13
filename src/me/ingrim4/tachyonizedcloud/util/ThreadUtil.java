package me.ingrim4.tachyonizedcloud.util;

import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinWorkerThread;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicLong;

public class ThreadUtil {

	public static final ThreadFactory DEFAULT_THREAD_FACTORY = Executors.defaultThreadFactory();

	public static ThreadFactory getThreadFactory(String nameFormat, final boolean daemon) {
		final AtomicLong count = (nameFormat != null) ? new AtomicLong(0) : null;
		return new ThreadFactory() {
			@Override
			public Thread newThread(Runnable runnable) {
				Thread thread = ThreadUtil.DEFAULT_THREAD_FACTORY.newThread(runnable);
				thread.setName(String.format(nameFormat, count.getAndIncrement()));
				thread.setDaemon(daemon);
				return thread;
			}
		};
	}

	public static ForkJoinPool.ForkJoinWorkerThreadFactory getForkThreadFactory(String nameFormat, final boolean daemon) {
		final AtomicLong count = (nameFormat != null) ? new AtomicLong(0) : null;
		return new ForkJoinPool.ForkJoinWorkerThreadFactory() {
			@Override
			public ForkJoinWorkerThread newThread(ForkJoinPool pool) {
				ForkJoinWorkerThread thread = ForkJoinPool.defaultForkJoinWorkerThreadFactory.newThread(pool);
				thread.setName(String.format(nameFormat, count.getAndIncrement()));
				thread.setDaemon(daemon);
				return thread;
			}
		};
	}
}
