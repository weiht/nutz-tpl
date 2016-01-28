package tpl.nutz.web;

import org.nutz.ioc.Ioc;
import org.nutz.mvc.View;
import org.nutz.mvc.ViewMaker;

public class ClasspathJspViewMaker
implements ViewMaker {

	public View make(Ioc ioc, String type, String value) {
		if ("nsp".equalsIgnoreCase(type)) {
			return new ClasspathJspView(value);
		}
		return null;
	}
}
