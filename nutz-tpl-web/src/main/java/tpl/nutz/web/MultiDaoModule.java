package tpl.nutz.web;

import org.nutz.dao.Dao;
import org.nutz.ioc.Ioc2;

public class MultiDaoModule {
	protected Ioc2 ioc;
	protected Dao defaultDao;
	
	protected Dao ensureDao(String dsName) {
		if (dsName == null || (dsName = dsName.trim()).isEmpty()) {
			return defaultDao;
		}
		Dao dao = ioc.get(Dao.class, dsName);
		if (dao != null) return dao;
		dao = ioc.get(Dao.class, dsName + "Dao");
		if (dao != null) return dao;
		return defaultDao;
	}

	public void setIoc(Ioc2 ioc) {
		this.ioc = ioc;
	}

	public void setDefaultDao(Dao defaultDao) {
		this.defaultDao = defaultDao;
	}
}
