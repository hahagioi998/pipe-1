package org.nutz.pipe;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import javax.cache.Cache;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.ehcache.CacheManager;
import net.sf.ehcache.jcache.JCache;

import org.nutz.dao.Dao;
import org.nutz.dao.impl.NutDao;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.mapl.Mapl;
import org.nutz.pipe.annotation.Mapping;
import org.nutz.pipe.impl.ActionInfo;
import org.nutz.resource.Scans;

import com.alibaba.druid.pool.DruidDataSource;

public class PipeFilter implements Filter {
	
	public FilterConfig filterConfig;
	
	public List<ActionInfo> actionList = new ArrayList<>();
	
	public Cache<String, Object> cache;
	
	public Dao dao;

	public void init(FilterConfig conf) {
		this.filterConfig = conf;
		pipeInit();
	}
	
	protected void scanActions() {
		String pkg = getClass().getPackage().getName();
		for(Class<?> klass : Scans.me().scanPackage(pkg + ".action")) {
			Object obj = null;
			Mapping klassMapping = klass.getAnnotation(Mapping.class);
			String[] klassMs = klassMapping == null ? new String[]{""} : klassMapping.value();
			for (Method method : klass.getMethods()) {
				Mapping mapping = method.getAnnotation(Mapping.class);
				if (mapping == null)
					continue;
				if (obj == null)
					try {
						obj = klass.newInstance();
					} catch (InstantiationException | IllegalAccessException e) {
						throw new RuntimeException(e);
					}
				for (String klassM : klassMs) {
					if (mapping.value().length == 0) {
						String sourceMapping = klassM + "/" + method.getName();
						actionList.add(new ActionInfo(obj, method, sourceMapping, Pattern.compile(sourceMapping)));
						continue;
					}
					for (String methodMapping : mapping.value()) {
						String sourceMapping = klassM + methodMapping;
						actionList.add(new ActionInfo(obj, method, sourceMapping, Pattern.compile(sourceMapping)));
					}
				}
			}
		};
	}
	
	protected void addDao() {
		InputStream in = getClass().getResourceAsStream("dao.properties");
		if (in == null)
			return;
		PropertiesProxy pp = new PropertiesProxy(in);
		HashMap<String, String> daoCnf = new HashMap<>();
		for (Entry<Object, Object> en : pp.toProperties().entrySet()) {
			daoCnf.put(String.valueOf(en.getKey().toString()).trim(), String.valueOf(en.getValue()).trim());
		}
		DruidDataSource dataSource = (DruidDataSource) Mapl.maplistToObj(daoCnf, DruidDataSource.class);
		dao = new NutDao(dataSource);
	}
	
	protected void addCache() {
		InputStream in = getClass().getResourceAsStream("cache.properties");
		if (in == null)
			return;
		CacheManager cacheManager = CacheManager.create(in);
		cache = new JCache<String, Object>(cacheManager.getCache(filterConfig.getFilterName()), null, getClass().getClassLoader());
	}
	
	public void pipeInit() {
		scanActions();
		addDao();
		addCache();
	}

	public void destroy() {
		pipeDepose();
	}
	
	public void pipeDepose() {
	}

	public void doFilter(ServletRequest _req, ServletResponse _resp, FilterChain _chain) throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest)_req;
		HttpServletResponse resp = (HttpServletResponse)_resp;
		for (ActionInfo actionInfo : actionList) {
			if (actionInfo.match(req) && doAction(actionInfo, req, resp)) {
				return;
			}
		}
		_chain.doFilter(_req, _resp);
	}
	
	protected boolean doAction(ActionInfo actionInfo, HttpServletRequest req,HttpServletResponse resp) {
		return new ActionExec(actionInfo, req, resp, cache, dao).doAction();
	}
}
