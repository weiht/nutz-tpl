package tpl.nutz;

import org.nutz.dao.Dao;

import tpl.ds.DataSourceDef;
import tpl.entities.EntityDataSource;
import tpl.entities.EntityDef;
import tpl.entities.PropertyDef;
import tpl.prefs.Preference;

public class DataTablesInitializer
implements Runnable {
	private Dao dao;
	
	@Override
	public void run() {
		initTable(Preference.class);
		initTable(DataSourceDef.class);
		initTable(PropertyDef.class);
		initTable(EntityDef.class);
		initTable(PropertyDef.class);
		initTable(EntityDataSource.class);
	}
	
	private void initTable(Class<?> clazz) {
		try {
			if (!dao.exists(clazz))
				dao.create(clazz, false);
			//else Daos.migration(dao, clazz, true, false);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void setDao(Dao dao) {
		this.dao = dao;
	}
}
