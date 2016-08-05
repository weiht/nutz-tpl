package tpl.admin.api.entities.builtin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.nutz.NutRuntimeException;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.dao.util.Daos;
import org.nutz.dao.util.cri.SimpleCriteria;
import org.nutz.ioc.annotation.InjectName;
import org.nutz.mvc.adaptor.JsonAdaptor;
import org.nutz.mvc.annotation.AdaptBy;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.DELETE;
import org.nutz.mvc.annotation.GET;
import org.nutz.mvc.annotation.POST;
import org.nutz.mvc.annotation.PUT;

import tpl.authc.DbAuthenticationInfo;
import tpl.entities.EntityDataSource;
import tpl.nutz.TplJsonIocProvider;

@InjectName("api.entities.builtinAccountDsModule")
@At("/builtin/account")
public class AccountDsModule {
	private static final String ENTITY_NAME = DbAuthenticationInfo.class.getName();
	
	private Dao dao;

	private SimpleCriteria createCriteria() {
		SimpleCriteria cri = Cnd.cri();
		cri.asc("dataSourceName");
		return cri;
	}

	@At("/ds/da/?")
	@GET
	public EntityDataSource get(String name) {
		return dao.fetchx(EntityDataSource.class, name, ENTITY_NAME);
	}
	
	@At("/ds/da")
	@POST
	@AdaptBy(type=JsonAdaptor.class)
	public EntityDataSource add(EntityDataSource def) {
		if (get(def.getDataSourceName()) != null) throw new NutRuntimeException("Duplicated entry.");
		def.setEntityName(ENTITY_NAME);
		return dao.insert(def);
	}
	
	@At("/ds/da")
	@PUT
	@AdaptBy(type=JsonAdaptor.class)
	public EntityDataSource update(EntityDataSource def) {
		throw new NutRuntimeException("Not allowed for updating.");
	}
	
	@At("/ds/da/?")
	@DELETE
	public EntityDataSource delete(String name) {
		EntityDataSource def = get(name);
		if (def != null)
			dao.deletex(EntityDataSource.class, name, ENTITY_NAME);
		return def;
	}
	
	@At("/ds/")
	@GET
	public Map<String, Object> all() {
		List<EntityDataSource> lst = dao.query(EntityDataSource.class, createCriteria());
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("result", lst);
		return result;
	}

	@At("/ds/")
	@POST
	public Map<String, Object> apply() {
		List<EntityDataSource> lst = dao.query(EntityDataSource.class, createCriteria());
		for (EntityDataSource eds: lst) {
			Dao edao = TplJsonIocProvider.nutzIoc().get(null, eds.getDataSourceName() + "Dao");
			String tbl = DbAuthenticationInfo.TABLE_NAME;
			if (edao.exists(tbl)) {
				Daos.migration(edao, DbAuthenticationInfo.class, true, true);
			} else {
				edao.create(DbAuthenticationInfo.class, false);
			}
		}
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("result", lst);
		return result;
	}
	
	public void setDao(Dao dao) {
		this.dao = dao;
	}
}
