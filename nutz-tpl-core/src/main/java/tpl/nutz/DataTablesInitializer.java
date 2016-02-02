package tpl.nutz;

import org.nutz.dao.Dao;

import tpl.prefs.Preference;

public class DataTablesInitializer
implements Runnable {
	private Dao dao;
	
	@Override
	public void run() {
		dao.create(Preference.class, false);
		//Daos.migration(dao, Preference.class, true, false);
	}

	public void setDao(Dao dao) {
		this.dao = dao;
	}
}
