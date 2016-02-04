package tpl.admin.api;

import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Fail;
import org.nutz.mvc.annotation.IocBy;
import org.nutz.mvc.annotation.Modules;
import org.nutz.mvc.annotation.Ok;

import tpl.nutz.TplJsonIocProvider;

@Modules(packages={"tpl.admin.api"})
@IocBy(type=TplJsonIocProvider.class, args={
	"NUTZ-MVC"
})
@At("/api/admin_console")
@Ok("json")
@Fail("json")
public class MainModule {

}
