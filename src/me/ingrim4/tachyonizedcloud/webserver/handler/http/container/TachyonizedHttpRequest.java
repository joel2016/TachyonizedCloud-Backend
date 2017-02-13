package me.ingrim4.tachyonizedcloud.webserver.handler.http.container;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.cookie.Cookie;

public class TachyonizedHttpRequest {

	private final HttpRequest request;
	private final String path;
	private final List<Cookie> cookies = new ArrayList<Cookie>();
	private final Map<String, List<String>> get = new HashMap<String, List<String>>();
	private final Map<String, Object> post = new HashMap<String, Object>();

	public TachyonizedHttpRequest(HttpRequest request, String path) {
		this.request = request;
		this.path = path.endsWith("/") ? path + "index.html" : path;
	}

	public HttpRequest getRequest() {
		return request;
	}

	public String getPath() {
		return path;
	}

	public List<Cookie> getCookies() {
		return cookies;
	}

	public Map<String, List<String>> getGet() {
		return get;
	}

	public Map<String, Object> getPost() {
		return post;
	}
}
