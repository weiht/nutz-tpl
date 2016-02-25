package tpl.admin.api.res;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.nutz.ioc.annotation.InjectName;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.GET;

@InjectName("api.resourceModule")
@At("/res")
public class ResourceModule {
	@At("/templates/?")
	@GET
	public Map<String, Object> templates(String type) {
		List<?> lst = ResourceConstants.TEMPLATES.get(type);
		if (lst == null) throw new RuntimeException(type);
		Map<String, Object> retval = new HashMap<String, Object>();
		retval.put("result", lst);
		return retval;
	}
}
