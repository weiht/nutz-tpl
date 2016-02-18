package tpl.ds;

import java.util.List;

import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.dao.util.cri.SimpleCriteria;
import org.nutz.ioc.Ioc2;
import org.nutz.ioc.impl.NutIoc;

import tpl.nutz.CompositeIocContext;

public class ExtraDaoInitializer
implements Runnable {
	private Ioc2 ioc, loaderIoc;
	private Dao dao;
	private DaoLoader loader;
	
	@Override
	public void run() {
		List<DataSourceDef> dsDefs = loadActivatedDataSources();
		for (DataSourceDef def: dsDefs) {
			addToContext(def);
		}
	}

	private List<DataSourceDef> loadActivatedDataSources() {
		SimpleCriteria cri = Cnd.cri();
		cri.where().and("status", "=", DataSourceDef.STATUS_ACTIVATED);
		List<DataSourceDef> dsDefs = dao.query(DataSourceDef.class, cri);
		return dsDefs;
	}

	private void addToContext(DataSourceDef dsdef) {
		if (loader == null) {
			loader = new DaoLoader();
			loaderIoc = new NutIoc(loader);
			((CompositeIocContext)ioc.getIocContext()).addContext(loaderIoc.getIocContext());
		}
		loader.add(dsdef, ioc);
	}

	public void setIoc(Ioc2 ioc) {
		this.ioc = ioc;
	}

	public void setDao(Dao dao) {
		this.dao = dao;
	}
}
