package tpl.nutz.web;

import org.nutz.mvc.annotation.IocBy;
import org.nutz.mvc.annotation.Modules;
import org.nutz.mvc.annotation.Views;

@Modules(packages={"tpl.nutz.web.modules"})
@IocBy(type=TplJsonIocProvider.class, args={
	"NUTZ-MVC"
})
@Views({ClasspathJspViewMaker.class, VelocityViewMaker.class})
public class MainModule {

}
