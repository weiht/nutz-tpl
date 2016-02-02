package tpl.admin.api.prefs;

import org.nutz.dao.Dao;
import org.nutz.ioc.annotation.InjectName;
import org.nutz.mvc.adaptor.JsonAdaptor;
import org.nutz.mvc.annotation.AdaptBy;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.DELETE;
import org.nutz.mvc.annotation.Fail;
import org.nutz.mvc.annotation.GET;
import org.nutz.mvc.annotation.Ok;
import org.nutz.mvc.annotation.POST;
import org.nutz.mvc.annotation.PUT;

import tpl.prefs.Preference;

@InjectName("api.preferenceModule")
public class PreferenceModule {
	private Dao dao;
	
	@At("/pref/?")
	@Ok("json") @Fail("json")
	@GET
	public Preference get(String key) {
		return dao.fetch(Preference.class, key);
	}
	
	@At("/pref")
	@Ok("json") @Fail("json")
	@POST
	@AdaptBy(type=JsonAdaptor.class)
	public Preference add(Preference pref) {
		if (pref == null || pref.getKey() == null)
			throw new RuntimeException("No preference to save.");
		Preference origin = dao.fetch(Preference.class, pref.getKey());
		if (origin != null)
			throw new RuntimeException("Preference already exists.");
		return dao.insert(pref);
	}
	
	@At("/pref")
	@Ok("json") @Fail("json")
	@PUT
	@AdaptBy(type=JsonAdaptor.class)
	public Preference update(Preference pref) {
		if (pref == null || pref.getKey() == null)
			throw new RuntimeException("No preference to save.");
		Preference origin = dao.fetch(Preference.class, pref.getKey());
		if (origin == null)
			throw new RuntimeException("Preference is not found.");
		origin.setValue(pref.getValue());
		origin.setDescription(pref.getDescription());
		dao.update(origin);
		return pref;
	}
	
	@At("/pref/?")
	@Ok("json") @Fail("json")
	@DELETE
	public Preference delete(String key) {
		Preference origin = dao.fetch(Preference.class, key);
		if (origin != null) {
			dao.delete(origin);
		}
		return origin;
	}

	public void setDao(Dao dao) {
		this.dao = dao;
	}
}
