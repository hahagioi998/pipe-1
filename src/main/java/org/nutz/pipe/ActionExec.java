package org.nutz.pipe;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Parameter;
import java.util.regex.Matcher;

import javax.cache.Cache;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.castor.Castors;
import org.nutz.dao.Dao;
import org.nutz.pipe.impl.ActionInfo;
import org.nutz.pipe.impl.PipeContext;
import org.nutz.pipe.impl.ReqImpl;
import org.nutz.pipe.impl.RespImpl;

public class ActionExec {

	public ActionInfo actionInfo;
	public HttpServletRequest req;
	public HttpServletResponse resp;
	public Cache<String, Object> cache;
	public Dao dao;

	public ActionExec(ActionInfo actionInfo, HttpServletRequest req, HttpServletResponse resp,
				Cache<String, Object> cache, Dao dao) {
		super();
		this.actionInfo = actionInfo;
		this.req = req;
		this.resp = resp;
		this.cache = cache;
	}

	public boolean doAction() {
		String uri = req.getRequestURI();
		PipeContext ctx = new PipeContext();
		PipeContext.me.set(ctx);
		ctx.req = new ReqImpl(req);
		ctx.resp = new RespImpl(resp);
		ctx.cache = cache;
		ctx.dao = dao;
		try {
			Object[] args = new Object[actionInfo.method.getParameterCount()];
			Matcher matcher = actionInfo.urlPattern.matcher(uri);
			Parameter[] parameters = actionInfo.method.getParameters();
			for (int i = 0; i < args.length && i < matcher.groupCount(); i++) {
				args[i] = matcher.group(i);
			}
			for (int i = 0; i < parameters.length; i++) {
				if (i < matcher.groupCount())
					args[i] = Castors.me().castTo(matcher.group(i), parameters[i].getType());
				else
					args[i] = Castors.me().castTo(req.getParameter(parameters[i].getName()), parameters[i].getType());
			}
			try {
				Object re = actionInfo.method.invoke(actionInfo.obj, args);
				if (re != null && re instanceof Boolean)
					return (Boolean) re;
			} catch (IllegalAccessException | IllegalArgumentException
					| InvocationTargetException e) {
				e.printStackTrace();
				if (!resp.isCommitted()) {
					resp.reset();
					try {
						resp.sendError(500);
					} catch (IOException e1) {
						e.printStackTrace();
					}
				}
			}
			return true;
		} finally {
			PipeContext.me.set(null);
		}
	}
}
