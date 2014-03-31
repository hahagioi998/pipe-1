package org.nutz.pipe.spi;

import javax.servlet.http.Part;

public interface Req {

	Object  attr(String key);
	String  param(String key);
	Req json();
	<T> T to(Class<T> klass);
	String header(String key);
	Part file(String key);
}
