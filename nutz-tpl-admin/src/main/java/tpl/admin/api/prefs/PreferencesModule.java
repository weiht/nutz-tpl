package tpl.admin.api.prefs;

import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.dao.pager.Pager;
import org.nutz.dao.util.cri.SimpleCriteria;
import org.nutz.ioc.annotation.InjectName;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Param;

import tpl.nutz.Pagination;
import tpl.prefs.Preference;

@InjectName("api.preferencesModule")
@At("/prefs")
public class PreferencesModule {
	private Dao dao;
	
	private SimpleCriteria createCriteria() {
		SimpleCriteria criteria = Cnd.cri();
		criteria.asc("key");
		return criteria;
	}
	
	@At("/page")
	public Pagination<Preference> page(@Param("cri") String cri, @Param("..") Pagination<Preference> page) {
		String partialKey = cri == null ? "" : cri;
		SimpleCriteria criteria = createCriteria();
		criteria.where().andLike("key", partialKey.trim());
		Pager pager = new Pager();
		pager.setPageNumber(page.getPageNum());
		pager.setPageSize(page.getPageSize());
		page.setResult(dao.query(Preference.class, criteria, pager));
		page.setRecordCount(dao.count(Preference.class, criteria));
		return page;
	}

	public void setDao(Dao dao) {
		this.dao = dao;
	}
}
