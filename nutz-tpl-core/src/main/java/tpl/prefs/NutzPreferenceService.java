package tpl.prefs;

import org.nutz.dao.Dao;
import org.nutz.lang.Strings;

public class NutzPreferenceService
implements PreferenceService {
	private Dao dao;

	public Integer intValue(String key, Integer defVal) {
		String val = stringValue(key, null);
		try {
			return Strings.isBlank(val) ? defVal : Integer.valueOf(val);
		} catch (NumberFormatException e) {
			return defVal;
		}
	}

	public Long longValue(String key, Long defVal) {
		String val = stringValue(key, null);
		try {
			return Strings.isBlank(val) ? defVal : Long.valueOf(val);
		} catch (NumberFormatException e) {
			return defVal;
		}
	}

	public Float floatValue(String key, Float defVal) {
		String val = stringValue(key, null);
		try {
			return Strings.isBlank(val) ? defVal : Float.valueOf(val);
		} catch (NumberFormatException e) {
			return defVal;
		}
	}

	public Double doubleValue(String key, Double defVal) {
		String val = stringValue(key, null);
		try {
			return Strings.isBlank(val) ? defVal : Double.valueOf(val);
		} catch (NumberFormatException e) {
			return defVal;
		}
	}

	public String stringValue(String key, String defVal) {
		Preference pref = dao.fetch(Preference.class, key);
		String val = pref.getValue();
		return Strings.isEmpty(val) ? defVal : val;
	}

	public void setDao(Dao dao) {
		this.dao = dao;
	}

}
