package tpl.ds;

import org.nutz.dao.Dao;
import org.nutz.ioc.Ioc2;

public class NutzDataSourceManager
implements DataSourceManager {
	private Ioc2 ioc;
	private Dao dao;
	
	@Override
	public DataSourceDef get(String name) {
		return dao.fetch(DataSourceDef.class, name);
	}

	@Override
	public DataSourceDef add(DataSourceDef def) {
		if (def == null)
			throw new RuntimeException("No data source to save.");
		String name = def.getName();
		if (name == null || (name = name.trim()).isEmpty())
			throw new RuntimeException("Data source definition name is required.");
		DataSourceDef origin = get(name);
		if (origin != null)
			throw new RuntimeException("Data source definition already exists.");
		def.setName(name);
		dao.insert(def);
		// TODO Add data source to IoC context according to this definition and it's status.
		return def;
	}

	@Override
	public DataSourceDef update(DataSourceDef def) {
		if (def == null)
			throw new RuntimeException("No data source to save.");
		String name = def.getName();
		if (name == null || (name = name.trim()).isEmpty())
			throw new RuntimeException("Data source definition name is required.");
		DataSourceDef origin = get(name);
		if (origin == null)
			throw new RuntimeException("Data source definition is not found.");
		origin.setUrl(def.getUrl());
		origin.setUser(def.getUser());
		origin.setPassword(def.getPassword());
		origin.setDescription(def.getDescription());
		dao.update(origin);
		// TODO Add or update data source to IoC context according to this definition and it's status.
		return origin;
	}

	@Override
	public DataSourceDef remove(String name) {
		if (name == null || (name = name.trim()).isEmpty())
			return null;
		DataSourceDef origin = get(name);
		if (origin == null)
			return null;
		dao.delete(origin);
		return origin;
	}

	@Override
	public DataSourceDef activate(String name) {
		if (name == null || (name = name.trim()).isEmpty())
			return null;
		DataSourceDef origin = get(name);
		if (origin == null)
			return null;
		origin.setStatus(origin.getStatus() | DataSourceDef.STATUS_ACTIVATED);
		dao.update(origin);
		return origin;
	}

	@Override
	public DataSourceDef deactivate(String name) {
		if (name == null || (name = name.trim()).isEmpty())
			return null;
		DataSourceDef origin = get(name);
		if (origin == null)
			return null;
		origin.setStatus(origin.getStatus() ^ DataSourceDef.STATUS_ACTIVATED);
		dao.update(origin);
		return origin;
	}

	public Ioc2 getIoc() {
		return ioc;
	}

	public void setIoc(Ioc2 ioc) {
		this.ioc = ioc;
	}

	public Dao getDao() {
		return dao;
	}

	public void setDao(Dao dao) {
		this.dao = dao;
	}
}
