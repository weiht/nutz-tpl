package tpl.groovy;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import groovy.util.GroovyScriptEngine;
import groovy.util.ResourceException;
import groovy.util.ScriptException;
import tpl.velocity.VelocityConfig;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.nutz.ioc.Ioc2;
import org.nutz.json.Json;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GroovyConfig {
	private static final Logger logger = LoggerFactory.getLogger(GroovyConfig.class);
	
	public static final String SYS_PROP_GROOVY_SCRIPT_PATHS = "tpl.groovy.classpath";
	public static final String KEY_GROOVY_CONFIG = "groovyConfig";
	public static final Object KEY_FORWARD_TO = "forwardTo";
	public static final Object KEY_SKIP_VIEW = "skipView";
	public static final Object KEY_JSON_RESULT = "__json__";
	
	public static final String[] YES_VALUES = {
		"y", "yes", "true", "t"
	};
	
	public static final String[] NO_VALUES = {
		"n", "no", "false", "f"
	};
	
	private String resourceLocation = VelocityConfig.DEFAULT_RESOURCE_LOCATION;
	private String[] groovyClasspaths = new String[0];
	private Binding rootBinding = new Binding();
	private GroovyScriptEngine engine;
	private Ioc2 ioc;
	private boolean devMode;
	private Map<String, String> resMappings = new HashMap<String, String>();
	
	private void loadGroovyClasspaths() {
		if (groovyClasspaths != null && groovyClasspaths.length > 0) return;
		List<String> classpaths = new ArrayList<String>();
		String paths = System.getProperty(SYS_PROP_GROOVY_SCRIPT_PATHS);
		if (paths != null) {
			for (String p: paths.split(File.pathSeparator)) {
				p = p.trim();
				if (!p.isEmpty()) {
					File f = new File(p);
					if (f.exists() && f.isDirectory()) {
						logger.trace("Groovy classpath: {}", f);
						classpaths.add(f.toString());
					}
				}
			}
		}
		ClassLoader loader = getClass().getClassLoader();
		if (loader instanceof URLClassLoader) {
			@SuppressWarnings("resource")
			URLClassLoader uloader = (URLClassLoader)loader;
			for (URL u: uloader.getURLs()) {
				logger.trace("Groovy classpath from jvm: {}", u);
				String f = u.getPath();
				classpaths.add(f);
			}
		}
		logger.trace("GroovyRunner: classpaths: {}", classpaths);
		groovyClasspaths = classpaths.toArray(groovyClasspaths);
	}
	
	private void initRootBinding() {
		rootBinding.setVariable(VelocityConfig.KEY_IOC, ioc);
		if (System.getProperty(VelocityConfig.SYS_PROP_DEV_MODE) != null) {
			rootBinding.setVariable(VelocityConfig.KEY_DEV_MODE, true);
			devMode = true;
		}
		rootBinding.setVariable(KEY_GROOVY_CONFIG, this);
	}
	
	private void ensureEngine() throws IOException {
		if (engine == null) {
			loadGroovyClasspaths();
			initRootBinding();
			engine = new GroovyScriptEngine(groovyClasspaths, getClass().getClassLoader());
		}
	}
	
	public void executeSnippet(String snippet, Binding binding) throws IOException {
		ensureEngine();
		GroovyShell shell = new GroovyShell(engine.getGroovyClassLoader(), binding);
		shell.evaluate(snippet);
	}

	public GroovyScriptEngine getEngine() {
		try {
			ensureEngine();
			return engine;
		} catch (IOException e) {
			return null;
		}
	}
	
	@SuppressWarnings("unchecked")
	public Binding getBinding() {
		try {
			ensureEngine();
			Binding b = new Binding();
			b.getVariables().putAll(rootBinding.getVariables());
			return b;
		} catch (IOException e) {
			return null;
		}
	}
	
	public static boolean viewSkipped(Object param) {
		if (param == null) return false;
		if (param instanceof Boolean) return (Boolean)param;
		String sval = param.toString();
		return isYes(sval);
	}
	
	private static boolean inStrArr(String[] sarr, String sval) {
		String s = sval.toLowerCase();
		for (String y: sarr) {
			if (y.equals(s)) return true;
		}
		return false;
	}
	
	public static boolean isYes(String sval) {
		if (sval == null) return false;
		return inStrArr(YES_VALUES, sval);
	}
	
	public static boolean isNo(String sval) {
		if (sval == null) return false;
		return inStrArr(NO_VALUES, sval);
	}

	public boolean preRender(Map<String, Object> result, Writer w) {
		if (result == null) return false;
		Object o = result.get(KEY_SKIP_VIEW);
		if (viewSkipped(o))
			return true;
		o = result.get(KEY_JSON_RESULT);
		if (o != null) {
			logger.trace("Json result: {}", o);
			Json.toJson(w, o);
			return true;
		}
		return false;
	}

	public String calcForwardPath(String path, String fwd) {
		if (fwd.startsWith("/")) {
			return fwd;
		}
		int ix = path.lastIndexOf("/");
		if (ix < 1) {
			return fwd;
		} else {
			return path.substring(0, ix + 1) + fwd;
		}
	}

	@SuppressWarnings("unchecked")
	public Map<String, Object> runScript(String path, Binding binding)
			throws ResourceException, ScriptException {
		logger.trace("Running script {}...", path);
		GroovyScriptEngine engine = getEngine();
		String fullPath = locateScript(path);
		if (fullPath == null || fullPath.isEmpty()) {
			return null;
		}
		logger.debug("Script file: {}", fullPath);
		engine.run(fullPath, binding);
		return binding.getVariables();
	}

	private String locateScript(String path) {
		String p = getResourceLocation() + path;
		if (!devMode) {
			String fp = resMappings.get(p);
			if (fp == null) {
				fp = doLocateScript(path);
			}
			return fp;
		} else {
			return doLocateScript(path);
		}
	}

	private String doLocateScript(String p) {
		for (String cp: groovyClasspaths) {
			File f = new File(cp);
			if (f.isDirectory()) {
				File fp = new File(new File(f, getResourceLocation()), p);
				logger.trace("Examining file existance: {}", fp.toURI());
				if (fp.exists()) {
					String result = fp.toURI().toString();
					logger.trace("File located: {}", result);
					return result;
				}
			}
		}
		URL u = getClass().getClassLoader().getResource(getResourceLocation() + p);
		return u == null ? null : u.toString();
	}

	public void setResourceLocation(String resourceLocation) {
		this.resourceLocation = resourceLocation;
	}

	public void setIoc(Ioc2 ioc) {
		this.ioc = ioc;
	}

	public String getResourceLocation() {
		return resourceLocation;
	}
	
	public void setGroovyClasspath(String cp) {
		if (cp == null || cp.isEmpty()) {
			groovyClasspaths = null;
		} else {
			groovyClasspaths = cp.split(File.pathSeparator);
		}
	}

	public boolean isDevMode() {
		return devMode;
	}
}
