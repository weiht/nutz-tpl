package tpl.nutz;

import org.nutz.ioc.Ioc;
import org.nutz.ioc.Ioc2;
import org.nutz.ioc.impl.NutIoc;
import org.nutz.ioc.loader.json.JsonLoader;
import org.nutz.mvc.IocProvider;
import org.nutz.mvc.NutConfig;

public class TplJsonIocProvider
implements IocProvider {
	private static final Ioc2 rootIoc = new NutIoc(new JsonLoader("NUTZ-IOC/"));

	public Ioc create(NutConfig config, String[] args) {
		return rootIoc;
	}
	
	public static Ioc2 nutzIoc() {
		return rootIoc;
	}
}
