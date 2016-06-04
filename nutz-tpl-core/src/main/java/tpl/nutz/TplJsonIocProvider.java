package tpl.nutz;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import org.nutz.ioc.Ioc;
import org.nutz.ioc.Ioc2;
import org.nutz.ioc.IocLoader;
import org.nutz.ioc.impl.NutIoc;
import org.nutz.ioc.loader.combo.ComboIocLoader;
import org.nutz.ioc.loader.json.JsonLoader;
import org.nutz.mvc.IocProvider;
import org.nutz.mvc.NutConfig;
import org.nutz.resource.Scans;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tpl.config.ConfigConstants;
import tpl.ds.DaoLoader;

public class TplJsonIocProvider
implements IocProvider {
	private static final Logger logger = LoggerFactory.getLogger(TplJsonIocProvider.class);
	
	private static final Ioc2 rootIoc;
	
	static {
		String extraPaths = System.getProperty(ConfigConstants.KEY_GENERATED_RESOURCE_PATH);
		if (extraPaths != null) registerPaths(extraPaths);
		IocLoader daoLoader = null;
		try {
			daoLoader = new DaoLoader();
		} catch (Exception e) {
			logger.warn("", e);
		}
		IocLoader loader = new ComboIocLoader(
			new JsonLoader("NUTZ-IOC/", "NUTZ-MVC/"),
			daoLoader
		);
		rootIoc = new NutIoc(loader);
	}
	
	private static void registerPaths(String paths) {
		logger.trace("Registering additional paths to Nutz's Scans: {}", paths);
		for (String p: paths.split(File.pathSeparator)) {
			String fn = p.trim();
			logger.trace("Registering path: {}", fn);
			if (!fn.isEmpty()) {
				File f = new File(fn);
				if (f.exists()) {
					try {
						URL url = f.toURI().toURL();
						Scans.me().registerLocation(url);
						logger.trace("URL registered to Scnas: {}", url);
					} catch (MalformedURLException e) {
						logger.warn("", e);
					}
				}
			}
		}
	}

	public Ioc create(NutConfig config, String[] args) {
		return rootIoc;
	}

	public static Ioc2 nutzIoc() {
		return rootIoc;
	}
}
