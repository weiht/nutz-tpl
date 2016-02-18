package tpl.ds;

import org.nutz.ioc.loader.json.JsonLoader;

public class DaoLoader
extends JsonLoader {
	public static final String DAO_CONFIG_LOCATION = "DAO-IOC/";
	public static final String DAO_CONFIG_DEFAULT_FILE = "dao-ioc.js";
	
	public DaoLoader() {
		super(DAO_CONFIG_LOCATION);
	}
}
