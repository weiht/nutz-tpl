package tpl.ds;

import java.util.HashMap;
import java.util.Map;

import org.nutz.ioc.Ioc2;
import org.nutz.ioc.loader.map.MapLoader;

public class DaoLoader
extends MapLoader {
	public void add(DataSourceDef dsdef, Ioc2 ioc) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("type", "com.alibaba.druid.pool.DruidDataSource");
		Map<String, Object> fields = new HashMap<String, Object>();
		map.put("fields", fields);
		fields.put("url", dsdef.getUrl());
		fields.put("username", dsdef.getUser());
		fields.put("password", dsdef.getPassword());
		getMap().put(dsdef.getName(), map);
		//
		map = new HashMap<String, Object>();
		map.put("type", "org.nutz.dao.impl.NutDao");
		fields = new HashMap<String, Object>();
		map.put("fields", fields);
		Map<String, Object> refer = new HashMap<String, Object>();
		fields.put("dataSource", refer);
		refer.put("refer", dsdef.getName());
		getMap().put(dsdef.getName() + "Dao", map);
		System.out.println(getMap());
	}
}
