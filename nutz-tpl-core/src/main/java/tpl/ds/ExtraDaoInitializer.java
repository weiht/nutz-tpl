package tpl.ds;

import java.util.List;

import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.dao.util.cri.SimpleCriteria;
import org.nutz.ioc.Ioc2;

public class ExtraDaoInitializer
implements Runnable {
	private Ioc2 ioc;
	private Dao dao;
	
	@Override
	public void run() {
		List<DataSourceDef> dsDefs = loadActivatedDataSources();
		for (DataSourceDef def: dsDefs) {
			initDao(def);
		}
	}

	private List<DataSourceDef> loadActivatedDataSources() {
		SimpleCriteria cri = Cnd.cri();
		cri.where().and("status", "=", DataSourceDef.STATUS_ACTIVATED);
		List<DataSourceDef> dsDefs = dao.query(DataSourceDef.class, cri);
		return dsDefs;
	}

	private void initDao(DataSourceDef def) {
		// TODO Auto-generated method stub
		
	}

	public void setIoc(Ioc2 ioc) {
		this.ioc = ioc;
	}

	public void setDao(Dao dao) {
		this.dao = dao;
	}
}
