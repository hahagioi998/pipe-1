package org.nutz.pipe.impl;

import java.io.IOException;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;

import org.nutz.json.Json;
import org.nutz.mapl.Mapl;
import org.nutz.pipe.spi.Req;

public class ReqImpl implements Req {
	
	public HttpServletRequest _req;
	
	public Object jsonObject;
	
	public Collection<Part> parts;
	
	public ReqImpl(HttpServletRequest _req) {
		this._req = _req;
	}

	public Object attr(String key) {
		return _req.getAttribute(key);
	}

	public String param(String key) {
		return _req.getParameter(key);
	}
	
	public Req json() {
		if (jsonObject == null)
			try {
				jsonObject = Json.fromJson(_req.getReader());
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		return this;
	}

	@SuppressWarnings("unchecked")
	public <T> T to(Class<T> klass) {
		if (jsonObject != null) {
			//....
		} else {
			LinkedHashMap<String, String> map = new LinkedHashMap<>();
			for (Entry<String, String[]> en : _req.getParameterMap().entrySet()) {
				map.put(en.getKey(), en.getValue()[0]);
			}
			return (T) Mapl.maplistToObj(map, klass);
		}
		return null;
	}

	public String header(String key) {
		return _req.getHeader(key);
	}

	public Part file(String key) {
		if (parts == null)
			try {
				parts = _req.getParts();
			} catch (IOException | ServletException e) {
				throw new RuntimeException(e);
			}
		for (Part part : parts) {
			if (part.getName().equals(key))
				return part;
		}
		return null;
	}
}
