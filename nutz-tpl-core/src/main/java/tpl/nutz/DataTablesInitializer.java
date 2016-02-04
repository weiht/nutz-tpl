package tpl.nutz;

import org.nutz.dao.Dao;

import tpl.prefs.Preference;

public class DataTablesInitializer
implements Runnable {
	private Dao dao;
	
	@Override
	public void run() {
		if (!dao.exists(Preference.class))
			dao.create(Preference.class, false);
		//else Daos.migration(dao, Preference.class, true, false);
	}

	public void setDao(Dao dao) {
		this.dao = dao;
	}
}
