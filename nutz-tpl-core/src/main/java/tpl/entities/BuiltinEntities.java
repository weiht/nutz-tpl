package tpl.entities;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import tpl.authc.DbAuthenticationInfo;

public class BuiltinEntities {
	private static final Map<String, Class<?>> predefined = new HashMap<String, Class<?>>();
	
	static {
		predefined.put(DbAuthenticationInfo.class.getName(), DbAuthenticationInfo.class);
	}
	
	public static Map<String, Class<?>> getPredefinedEntities() {
		return Collections.unmodifiableMap(predefined);
	}
	
	public static boolean isPredefined(String name) {
		return predefined.containsKey(name);
	}
}
