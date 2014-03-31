package org.nutz.pipe.testapp;

import javax.servlet.annotation.WebFilter;
import javax.servlet.annotation.WebInitParam;

import org.nutz.pipe.PipeFilter;

@WebFilter(filterName="blog",urlPatterns={"/*"}, 
	initParams={@WebInitParam(name="dev", value="true")})
public class BlogFilter extends PipeFilter {
}
