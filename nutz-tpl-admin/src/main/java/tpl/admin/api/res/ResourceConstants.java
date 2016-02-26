package tpl.admin.api.res;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ResourceConstants {
	public static final String GROOVY_TEMPLATE = "/templates/scripts/groovy.script";
	
	public static final String TYPE_PAGE = "page";
	public static final String TYPE_SECTION = "section";
	public static final String TYPE_COMPONENT = "component";
	public static final String[] TYPES = {
		TYPE_PAGE, TYPE_SECTION, TYPE_COMPONENT
	};
	
	public static final List<Map<String, String>> PAGE_TEMPLATES;
	public static final List<Map<String, String>> SECTION_TEMPLATES;
	public static final List<Map<String, String>> COMPONENT_TEMPLATES;
	public static final Map<String, List<Map<String, String>>> TEMPLATES;

	static {
		Map<String, List<Map<String, String>>> tpls = new HashMap<String, List<Map<String, String>>>();
		
		List<Map<String, String>> lst = new ArrayList<Map<String, String>>();
		addTemplate(
				lst, "res.page.tempalate.blank",
				"/templates/pages/blank.html",
				"/s/templates/images/blank.png"
			);
		addTemplate(
				lst, "res.page.tempalate.accordionsidebar",
				"/templates/pages/sidebar-accordion.html",
				"/s/templates/images/sidebar-accordion.png"
			);
		addTemplate(
				lst, "res.page.tempalate.nosidebar",
				"/templates/pages/without-sidebar.html",
				"/s/templates/images/without-sidebar.png"
			);
		PAGE_TEMPLATES = Collections.unmodifiableList(lst);
		tpls.put(TYPE_PAGE, PAGE_TEMPLATES);

		lst = new ArrayList<Map<String, String>>();
		addTemplate(
				lst, "res.section.tempalate.blank",
				"/templates/sections/blank.html",
				"/s/templates/images/blank.png"
			);
		addTemplate(
				lst, "res.section.tempalate.blank",
				"/templates/sections/border-layout.html",
				"/s/templates/images/border-layout.png"
			);
		addTemplate(
				lst, "res.section.tempalate.blank",
				"/templates/sections/form-layout.html",
				"/s/templates/images/form-layout.png"
			);
		SECTION_TEMPLATES = Collections.unmodifiableList(lst);
		tpls.put(TYPE_SECTION, SECTION_TEMPLATES);

		lst = new ArrayList<Map<String, String>>();
		addTemplate(
				lst, "res.component.tempalate.dropdownbutton",
				"/templates/components/dropdown-button.html",
				"/s/templates/images/dropdown-button.png"
			);
		COMPONENT_TEMPLATES = Collections.unmodifiableList(lst);
		tpls.put(TYPE_COMPONENT, COMPONENT_TEMPLATES);
		
		TEMPLATES = Collections.unmodifiableMap(tpls);
	}
	
	private static final void addTemplate(
			List<Map<String, String>> lst,
			String name, String path, String image) {
		Map<String, String> tpl = new HashMap<String, String>();
		tpl.put("name", name);
		tpl.put("path", path);
		tpl.put("image", image);
		lst.add(Collections.unmodifiableMap(tpl));
	}
}
