package tpl.ds;

import org.nutz.dao.Dao;
import org.nutz.ioc.Ioc2;

public class NutzDataSourceManager
implements DataSourceManager {
	private Ioc2 ioc;
	private Dao dao;

	@Override
	public DataSourceDef add(DataSourceDef def) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DataSourceDef update(DataSourceDef def) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DataSourceDef remove(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DataSourceDef activate(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DataSourceDef deactivate(String name) {
		// TODO Auto-generated method stub
		return null;
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
