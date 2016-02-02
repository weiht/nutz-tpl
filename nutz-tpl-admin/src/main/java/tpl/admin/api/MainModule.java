package tpl.admin.api;

import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.IocBy;
import org.nutz.mvc.annotation.Modules;

import tpl.nutz.web.TplJsonIocProvider;

@Modules(packages={"tpl.admin.api"})
@IocBy(type=TplJsonIocProvider.class, args={
	"NUTZ-MVC"
})
@At("/api/admin_console")
public class MainModule {

}
