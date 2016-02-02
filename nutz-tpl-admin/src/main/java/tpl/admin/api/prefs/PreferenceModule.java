package tpl.admin.api.prefs;

import org.nutz.dao.Dao;
import org.nutz.ioc.annotation.InjectName;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Fail;
import org.nutz.mvc.annotation.GET;
import org.nutz.mvc.annotation.Ok;

import tpl.prefs.Preference;

@InjectName("api.preferenceModule")
public class PreferenceModule {
	private Dao dao;
	
	@At("/pref")
	@Ok("json") @Fail("json")
	@GET
	public Preference get() {
		System.out.println(dao);
		throw new RuntimeException("Not implemented yet.");
	}

	public void setDao(Dao dao) {
		this.dao = dao;
	}
}
