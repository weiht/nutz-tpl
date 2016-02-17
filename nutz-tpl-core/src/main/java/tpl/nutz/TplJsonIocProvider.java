package tpl.nutz;

import java.util.List;

import org.nutz.ioc.Ioc;
import org.nutz.ioc.Ioc2;
import org.nutz.ioc.impl.NutIoc;
import org.nutz.ioc.impl.ScopeContext;
import org.nutz.ioc.loader.json.JsonLoader;
import org.nutz.lang.Lang;
import org.nutz.mvc.IocProvider;
import org.nutz.mvc.NutConfig;

public class TplJsonIocProvider
implements IocProvider {
	private static final Ioc2 rootIoc;
	
	static {
		CompositeIocContext appContext = new CompositeIocContext();
		appContext.addContext(new ScopeContext("app"));
		rootIoc = new NutIoc(
			new JsonLoader("NUTZ-IOC/"),
			appContext,
			"app"
		);
	}

	public Ioc create(NutConfig config, String[] args) {
		List<String> loc = Lang.array2list(args);
		loc.add("NUTZ-IOC/");
		Ioc2 ioc = new NutIoc(new JsonLoader(loc.toArray(args)), rootIoc.getIocContext(), "app");
		return ioc;
	}
	
	public static Ioc2 nutzIoc() {
		return rootIoc;
	}
}
