package tpl.velocity;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.context.Context;
import org.apache.velocity.tools.ToolManager;
import org.apache.velocity.tools.config.FactoryConfiguration;
import org.apache.velocity.tools.config.XmlFactoryConfiguration;
import org.nutz.ioc.Ioc2;
import org.nutz.lang.Streams;
import org.nutz.resource.NutResource;
import org.nutz.resource.Scans;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VelocityConfig {
	private static final Logger logger = LoggerFactory.getLogger(VelocityConfig.class);
	
	public static final String DEFAULT_CONFIG_LOCATION = "NUTZ-MVC";
	public static final String DEFAULT_RESOURCE_LOCATION = "NUTZ-RES";
	public static final String DEFAULT_CONFIG_FILE = ".*velocity.properties";
	public static final String DEFAULT_TOOLBOX_FILE = "velocity-toolbox.xml";
	public static final String CONFIG_VALUE_SEPARATOR = ",";
	public static final String RES_LOADER_KEY = "resource.loader";
	public static final String ENCODING_KEY = "input.encoding";
	public static final String DEFAULT_ENCODING = "UTF-8";
	public static final String INCLUDE_HANDLER_KEY = "eventhandler.include.class";
	public static final String INCLUDE_HANDLER_VALUE = "org.apache.velocity.app.event.implement.IncludeRelativePath";
	public static final String FILE_LOADER = "file";
	public static final String LOADER_CLASS_KEY = ".resource.loader.class";
	public static final String FILE_LOADER_PATH_KEY = ".resource.loader.path";
	public static final String FILE_LOADER_NAME = "org.apache.velocity.runtime.resource.loader.FileResourceLoader";
	public static final String SYS_PROP_VELOCITY_TEMPLATE_PATHS = "tpl.velocity.tplpath";
	public static final String SYS_PROP_DEV_MODE = "devMode";
	public static final String KEY_IOC = "ioc";
	public static final String KEY_PATH = "path";
	public static final String KEY_CONTEXT_PATH = "contextPath";
	public static final String KEY_REQUEST_URI = "requestUri";
	public static final String KEY_REQUEST = "request";
	public static final String KEY_RESPONSE = "response";
	public static final String KEY_RESULT = "obj";
	public static final String KEY_REPO_DIRS = "repoDirs";
	public static final String KEY_DEV_MODE = "__dev_mode__";
	public static final String KEY_VIEW_CONFIG = "viewConfig";

	private static final String[] MERGIBLE_CONFIG_KEYS = {
		"userdirective", RES_LOADER_KEY
	};

	private String configLocation = DEFAULT_CONFIG_LOCATION,
			configFile = DEFAULT_CONFIG_FILE,
			toolboxConfigFile = DEFAULT_TOOLBOX_FILE;
	private String resourceLocation = DEFAULT_RESOURCE_LOCATION;
	private Properties config;
	private String encoding = DEFAULT_ENCODING;
	private Context rootContext;
	private String templateRepositories;
	private File[] repoDirs;
	private Ioc2 ioc;
	private boolean devMode = false;
	private VelocityEngine engine;

	private void ensureEngine() {
		if (engine == null) {
			loadConfig();
			logger.trace("Velocity config initialized: {}", config);
			engine = new VelocityEngine(config);
			initContext();
		}
	}
	
	private void initContext() {
		ToolManager manager = new ToolManager(true);
		FactoryConfiguration conf = new FactoryConfiguration();
		for (NutResource xmlres: Scans.me().scan(configLocation, toolboxConfigFile)) {
			try {
				XmlFactoryConfiguration c = readToolConfig(xmlres);
				conf.addConfiguration(c);
			} catch (IOException e) {
				logger.warn("Error loading configuration resource: {}", xmlres.getName(), e);
			}
		}
		manager.configure(conf);
		rootContext = manager.createContext();
		rootContext.put(KEY_IOC, ioc);
		if (System.getProperty(SYS_PROP_DEV_MODE) != null) {
			rootContext.put(KEY_DEV_MODE, true);
			devMode = true;
		}
		rootContext.put(KEY_VIEW_CONFIG, this);
		rootContext.put(KEY_REPO_DIRS, repoDirs);
	}

	private XmlFactoryConfiguration readToolConfig(NutResource xmlres)
			throws IOException {
		XmlFactoryConfiguration c = new XmlFactoryConfiguration();
		InputStream ins = xmlres.getInputStream();
		try {
			c.read(ins);
		} finally {
			Streams.safeClose(ins);
		};
		return c;
	}

	private void loadConfig() {
		config = new Properties();
		initDefaultConfig();
		for (NutResource res: Scans.me().scan(configLocation, configFile)) {
			try {
				doLoadConfig(res);
			} catch (IOException e) {
				logger.warn("Error loading velocity config file.", e);
			}
		}
		encoding = config.getProperty(ENCODING_KEY, DEFAULT_ENCODING);
	}

	private void initDefaultConfig() {
		config.put(INCLUDE_HANDLER_KEY, INCLUDE_HANDLER_VALUE);
		initFileResourceLoader();
	}

	private void initFileResourceLoader() {
		String paths = templateRepositories;
		if (paths == null || paths.isEmpty())
			paths = System.getProperty(SYS_PROP_VELOCITY_TEMPLATE_PATHS);
		if (paths == null || paths.isEmpty()) return;
		List<File> repos = new ArrayList<File>();
		StringBuilder buff = new StringBuilder();
		for (String p: paths.split(File.pathSeparator)) {
			p = p.trim();
			if (!p.isEmpty()) {
				File f = new File(p, resourceLocation);
				if (f.exists() && f.isDirectory()) {
					if (buff.length() > 0) {
						buff.append(CONFIG_VALUE_SEPARATOR);
					}
					buff.append(f.getAbsolutePath());
					repos.add(f);
				}
			}
		}
		config.put(FILE_LOADER + FILE_LOADER_PATH_KEY, buff.toString());
		config.put(FILE_LOADER + LOADER_CLASS_KEY, FILE_LOADER_NAME);
		config.put(RES_LOADER_KEY, FILE_LOADER);
		repoDirs = repos.toArray(new File[0]);
	}

	private void doLoadConfig(NutResource res) throws IOException {
		Reader r = res.getReader();
		if (r == null) return;
		Properties props = new Properties();
		try {
			props.load(r);
			mergeToConfig(props);
		} finally {
			Streams.safeClose(r);
		}
	}

	private void mergeToConfig(Properties props) {
		for (Object ok: props.keySet()) {
			String k = (String)ok;
			String v = props.getProperty(k);
			if (v == null || (v = v.trim()).isEmpty()) continue;

			if (isMergibleConfig(k)) {
				String ov = config.getProperty(k);
				if (ov == null || (ov = ov.trim()).isEmpty()) {
					config.setProperty(k, v);
				} else {
					config.setProperty(k, ov + CONFIG_VALUE_SEPARATOR + v);
				}
			} else {
				config.setProperty(k, v);
			}
		}
	}

	private boolean isMergibleConfig(String k) {
		for (String sk: MERGIBLE_CONFIG_KEYS) {
			if (sk.equals(k)) return true;
		}
		return false;
	}
	
	public Context newContext() {
		ensureEngine();
		return new VelocityContext(rootContext);
	}

	public Context newContext(Map<String, Object> result) {
		ensureEngine();
		return new VelocityContext(result, rootContext);
	}
	
	public VelocityEngine getEngine() {
		ensureEngine();
		return engine;
	}
	
	public String getEncoding() {
		ensureEngine();
		logger.trace("Encoding: {}", encoding);
		return encoding;
	}
	
	public void setConfigFile(String configFile) {
		this.configFile = configFile;
	}

	public void setResourceLocation(String resourceLocation) {
		this.resourceLocation = resourceLocation;
	}

	public boolean isDevMode() {
		return devMode;
	}

	public String getResourceLocation() {
		return resourceLocation;
	}
	
	public void setTemplateRepositories(String repos) {
		this.templateRepositories = repos;
	}
}
