package tpl.admin.api.res;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.nutz.ioc.annotation.InjectName;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.GET;

@InjectName("api.res.pageModule")
@At("/pages")
public class PageModule {
	private ResourceUtil resourceUtil;
	
	@At("/all")
	@GET
	public Map<String, List<String>> all() {
		Map<String, List<String>> retval = new HashMap<String, List<String>>();
		retval.put("pages", resourceUtil.findAllPages("^.+[.]html$"));
		retval.put("scripts", resourceUtil.findAllScripts("^.+[.]groovy"));
		return retval;
	}

	public void setResourceUtil(ResourceUtil resourceUtil) {
		this.resourceUtil = resourceUtil;
	}
}
