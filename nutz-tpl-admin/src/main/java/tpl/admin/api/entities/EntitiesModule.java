package tpl.admin.api.entities;

import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.dao.pager.Pager;
import org.nutz.dao.util.cri.SimpleCriteria;
import org.nutz.ioc.annotation.InjectName;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Param;

import tpl.entities.EntityDef;
import tpl.nutz.Pagination;

@InjectName("api.entitiesModule")
@At("/entities")
public class EntitiesModule {
	private Dao dao;
	
	private SimpleCriteria createCriteria() {
		SimpleCriteria criteria = Cnd.cri();
		criteria.asc("name");
		return criteria;
	}
	
	@At("/page")
	public Pagination<EntityDef> page(@Param("cri") String cri, @Param("..") Pagination<EntityDef> page) {
		String partialName = cri == null ? "" : cri;
		SimpleCriteria criteria = createCriteria();
		criteria.where().andLike("name", partialName.trim());
		Pager pager = new Pager();
		pager.setPageNumber(page.getPageNum());
		pager.setPageSize(page.getPageSize());
		page.setResult(dao.query(EntityDef.class, criteria, pager));
		page.setRecordCount(dao.count(EntityDef.class, criteria));
		return page;
	}

	public void setDao(Dao dao) {
		this.dao = dao;
	}
}
