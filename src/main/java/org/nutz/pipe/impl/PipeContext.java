package org.nutz.pipe.impl;

import javax.cache.Cache;

import org.nutz.dao.Dao;
import org.nutz.pipe.spi.Req;
import org.nutz.pipe.spi.Resp;
import org.nutz.pipe.spi.Session;

public class PipeContext {

	public Dao dao;
	public Cache<String, Object> cache;
	public Req req;
	public Resp resp;
	public Session session;
	
	public static ThreadLocal<PipeContext> me = new ThreadLocal<>();
	
}
