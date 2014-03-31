package org.nutz.pipe;

import javax.cache.Cache;

import org.nutz.dao.Dao;
import org.nutz.pipe.impl.PipeContext;
import org.nutz.pipe.spi.Req;
import org.nutz.pipe.spi.Resp;
import org.nutz.pipe.spi.Session;

public interface Pipes {

	default Req req() {
		return PipeContext.me.get().req;
	};
	default Resp resp() {
		return PipeContext.me.get().resp;
	};
	default Dao dao(){
		return PipeContext.me.get().dao;
	};
	default Cache<String, Object> cache(){
		return PipeContext.me.get().cache;
	};
	default Session session(){
		return PipeContext.me.get().session;
	};
}
