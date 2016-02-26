package tpl.admin.api.res;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.nutz.ioc.annotation.InjectName;
import org.nutz.lang.Strings;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.GET;
import org.nutz.mvc.annotation.POST;
import org.nutz.mvc.annotation.Param;

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

	private static String toPackage(String url) {
		int ix = url.lastIndexOf('/');
		if (ix <= 0) return "";
		String p = url.substring(0, ix);
		if (p.charAt(0) == '/') p = p.substring(1);
		return p.replaceAll("/", ".");
	}
	
	@At("/da")
	@POST
	public Object add(@Param("url") String url,
			@Param("templateName") String templateName) {
		if (url == null || url.isEmpty()) {
			throw new RuntimeException("No page is specified.");
		}
		boolean isScript = url.endsWith(".groovy");
		Map<String, Object> result = new HashMap<String, Object>();
		if (isScript) {
			String pkg = toPackage(url);
			Map<String, Object> section = new HashMap<String, Object>();
			section.put("package", pkg);
			section.put("url", url);
			String retval = resourceUtil.ensureScriptResource(url, ResourceConstants.GROOVY_TEMPLATE,
					null, section);
			result.put("result", retval);
		} else {
			if (Strings.isBlank(templateName)) {
				throw new RuntimeException("No page template is specified.");
			}
			String retval = resourceUtil.ensurePageResource(url, templateName, null);
			result.put("result", retval);
		}
		return result;
	}
	
	@At("/da")
	@GET
	public Object get(@Param("url") String url) {
		boolean isScript = url.endsWith(".groovy");
		Map<String, Object> result = new HashMap<String, Object>();
		if (isScript) {
			result.put("result", resourceUtil.getScriptResource(url));
		} else {
			result.put("result", resourceUtil.getPageResource(url));
		}
		return result;
	}

	public void setResourceUtil(ResourceUtil resourceUtil) {
		this.resourceUtil = resourceUtil;
	}
}
