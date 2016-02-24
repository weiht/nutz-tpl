package tpl.nutz.web;

import org.nutz.mvc.annotation.IocBy;
import org.nutz.mvc.annotation.Modules;
import org.nutz.mvc.annotation.Views;

import tpl.nutz.TplJsonIocProvider;

@Modules(packages={"tpl.webmods"})
@IocBy(type=TplJsonIocProvider.class, args={
	"NUTZ-MVC"
})
@Views({ClasspathJspViewMaker.class, VelocityViewMaker.class})
public class MainModule {

}
