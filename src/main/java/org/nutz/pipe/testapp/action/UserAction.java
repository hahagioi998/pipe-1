package org.nutz.pipe.testapp.action;

import java.util.Date;

import org.nutz.pipe.Pipes;
import org.nutz.pipe.annotation.Mapping;

public class UserAction implements Pipes {

	@Mapping
	public void index(String name) {
		resp().render("Hi," + name + ", now=" + new Date(), "");
	}
}
