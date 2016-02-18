package tpl.ds;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.AccessDeniedException;
import java.nio.file.NotDirectoryException;
import java.util.List;

import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.context.Context;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.dao.util.cri.SimpleCriteria;
import org.nutz.ioc.Ioc2;
import org.nutz.lang.Streams;

import tpl.config.ConfigConstants;
import tpl.config.PlaceholderConfigure;
import tpl.velocity.VelocityConfig;

public class NutzDataSourceManager
implements DataSourceManager {
	private Ioc2 ioc;
	private Dao dao;
	private PlaceholderConfigure config;
	private VelocityConfig velocityConfig;
	
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
	
	@Override
	public void writeConfig() throws IOException {
		String p = config.get(ConfigConstants.KEY_GENERATED_RESOURCE_PATH);
		File f = new File(p, DaoLoader.DAO_CONFIG_LOCATION);
		if (!f.exists() && !f.mkdirs()) throw new FileNotFoundException(f.getAbsolutePath());
		if (!f.isDirectory()) throw new NotDirectoryException(f.getAbsolutePath());
		File conf = new File(f, DaoLoader.DAO_CONFIG_DEFAULT_FILE);
		if (conf.exists() && !conf.delete()) throw new AccessDeniedException(conf.getAbsolutePath());
		generateIocConfig(conf);
	}

	private void generateIocConfig(File conf) throws IOException {
		SimpleCriteria cri = Cnd.cri();
		cri.asc("name");
		cri.where().and("status", "=", DataSourceDef.STATUS_ACTIVATED);
		List<DataSourceDef> dslst = dao.query(DataSourceDef.class, cri);
		if (dslst.isEmpty()) return;
		Context ctx = velocityConfig.newContext();
		ctx.put("dataSources", dslst);
		VelocityEngine engine = velocityConfig.getEngine();
		Writer w = new FileWriter(conf);
		try {
			engine.mergeTemplate("/dao-ioc.js", velocityConfig.getEncoding(), ctx, w);
		} finally {
			Streams.safeClose(w);
		}
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

	public void setConfig(PlaceholderConfigure config) {
		this.config = config;
	}

	public void setVelocityConfig(VelocityConfig velocityConfig) {
		this.velocityConfig = velocityConfig;
	}
}
