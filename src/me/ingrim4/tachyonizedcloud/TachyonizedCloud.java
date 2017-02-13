package me.ingrim4.tachyonizedcloud;

import java.net.InetSocketAddress;

import me.ingrim4.tachyonizedcloud.webserver.TachyonizedWebserver;

public class TachyonizedCloud {

	public static void main(String[] args) {
		new TachyonizedWebserver().start(new InetSocketAddress("localhost", 8000));
		while(true);
	}
}
