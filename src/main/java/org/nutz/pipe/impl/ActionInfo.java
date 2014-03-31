package org.nutz.pipe.impl;

import java.lang.reflect.Method;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

public class ActionInfo {
	
	public Pattern urlPattern;

	public String sourceMapping;
	
	public Method method;
	
	public Object obj;
	
	public boolean match(HttpServletRequest req) {
		return urlPattern.matcher(req.getRequestURI()).find();
	}
	
	public ActionInfo() {
		// TODO Auto-generated constructor stub
	}

	public ActionInfo(Object obj, Method method, String sourceMapping,
			Pattern urlPattern) {
		super();
		this.obj = obj;
		this.method = method;
		this.sourceMapping = sourceMapping;
		this.urlPattern = urlPattern;
		System.out.println(obj + " " + method + " " + sourceMapping + " " + urlPattern);
	}
	
	
}
