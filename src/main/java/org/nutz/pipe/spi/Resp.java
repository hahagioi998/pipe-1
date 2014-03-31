package org.nutz.pipe.spi;

public interface Resp {

	Resp http(int code);
	Resp render(String body, String type);
	Resp header(String key, Object val);
}
