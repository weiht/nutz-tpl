package tpl.admin.api.ds;

import java.io.IOException;
import java.util.List;

import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.dao.util.cri.SimpleCriteria;
import org.nutz.ioc.annotation.InjectName;
import org.nutz.mvc.adaptor.JsonAdaptor;
import org.nutz.mvc.annotation.AdaptBy;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.DELETE;
import org.nutz.mvc.annotation.GET;
import org.nutz.mvc.annotation.POST;
import org.nutz.mvc.annotation.PUT;

import tpl.ds.DataSourceDef;
import tpl.ds.DataSourceManager;

@InjectName("api.dataSourceModule")
public class DataSourceModule {
	private DataSourceManager manager;
	private Dao dao;

	@At("/ds/da/?")
	@GET
	public DataSourceDef get(String name) {
		return manager.get(name);
	}
	
	@At("/ds/da")
	@POST
	@AdaptBy(type=JsonAdaptor.class)
	public DataSourceDef add(DataSourceDef def) {
		return manager.add(def);
	}
	
	@At("/ds/da")
	@PUT
	@AdaptBy(type=JsonAdaptor.class)
	public DataSourceDef update(DataSourceDef def) {
		return manager.update(def);
	}
	
	@At("/ds/da/?")
	@DELETE
	public DataSourceDef delete(String name) {
		return manager.remove(name);
	}
	
	@At("/ds/")
	@GET
	public List<DataSourceDef> all() {
		return dao.query(DataSourceDef.class, createCriteria());
	}
	
	@At("/ds/")
	@POST
	public void write() throws IOException {
		manager.writeConfig();
	}
	
	@At("/ds/active")
	@GET
	public List<DataSourceDef> active() {
		SimpleCriteria cri = createCriteria();
		cri.where().and("status", "=", DataSourceDef.STATUS_ACTIVATED);
		return dao.query(DataSourceDef.class, cri);
	}
	
	@At("/ds/inactive")
	@GET
	public List<DataSourceDef> inactive() {
		SimpleCriteria cri = createCriteria();
		cri.where().and("status", "<>", DataSourceDef.STATUS_ACTIVATED);
		return dao.query(DataSourceDef.class, cri);
	}
	
	@At("/ds/act/?")
	@POST
	public DataSourceDef activate(String name) {
		return manager.activate(name);
	}
	
	@At("/ds/act/?")
	@DELETE
	public DataSourceDef deactivate(String name) {
		return manager.deactivate(name);
	}

	private SimpleCriteria createCriteria() {
		SimpleCriteria cri = Cnd.cri();
		cri.asc("name");
		return cri;
	}
	
	public void setManager(DataSourceManager manager) {
		this.manager = manager;
	}

	public void setDao(Dao dao) {
		this.dao = dao;
	}
}
