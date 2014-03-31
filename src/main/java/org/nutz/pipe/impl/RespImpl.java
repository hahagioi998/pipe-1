package org.nutz.pipe.impl;

import javax.servlet.http.HttpServletResponse;

import org.nutz.json.Json;
import org.nutz.mvc.view.RawView;
import org.nutz.pipe.spi.Resp;

public class RespImpl implements Resp {

	public HttpServletResponse _resp;

	public RespImpl(HttpServletResponse _resp) {
		super();
		this._resp = _resp;
	}

	public Resp http(int code) {
		_resp.setStatus(code);
		return this;
	}

	public Resp render(String body, String type) {
		try {
			switch (type) {
			case "json":
				Json.toJson(_resp.getWriter(), body);
				return this;
			case "raw" :
				new RawView(null).render(null, _resp, body);
				return this;
			default:
				_resp.getWriter().write(String.valueOf(body));
				return this;
			}
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}
	
	public Resp header(String key, Object val) {
		_resp.setHeader(key, String.valueOf(val));
		return this;
	}
	
}
