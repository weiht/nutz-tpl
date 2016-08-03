package tpl.nutz.web;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.context.Context;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.nutz.json.Json;
import org.nutz.lang.Streams;
import org.nutz.mvc.Mvcs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tpl.groovy.GroovyConfig;
import tpl.javasrc.RuntimeClassFinder;
import tpl.nutz.TplJsonIocProvider;
import tpl.velocity.VelocityConfig;

public class RuntimeJavaServlet
extends HttpServlet {
	private static final String PREFIX_FORWARD_TO = "fwd:";

	private static final long serialVersionUID = -5820311121440447893L;

	private static final Logger logger = LoggerFactory.getLogger(RuntimeJavaServlet.class);
	
	public static final String KEY_RUNTIME_FINDER_BEAN = "runtimeClassFinderBean";
	public static final String DEF_RUNTIME_FINDER_KEY = "runtimeClassFinder";
	public static final String KEY_GROOVY_CONFIG_BEAN = "groovyConfigBean";
	public static final String DEF_GROOVY_CONFIG_KEY = "groovyConfig";
	public static final String KEY_VELOCITY_CONFIG_BEAN = "velocityConfigBean";
	public static final String DEF_VELOCITY_CONFIG_KEY = "velocityConfig";
	public static final String KEY_ENCODING = "encoding";
	
	private static final Pattern REGEX_VALID_PATH = Pattern.compile("^([/][a-z,A-Z,_][\\w]*)+$");
	
	private RuntimeClassFinder finder;
	private GroovyConfig groovyConfig;
	private VelocityConfig velocityConfig;
	private String encoding = "UTF-8";

	@Override
	protected void service(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String path = Mvcs.getRequestPath(req);
		if (path.endsWith("/")) {
			super.service(req, resp);
			return;
		}
		req.setCharacterEncoding(encoding);
		resp.setCharacterEncoding(encoding);
		try {
			render(req, resp, path);
		} catch (ResourceNotFoundException e) {
			logger.info("", e);
		} catch (Exception e) {
			logger.warn("", e);
		}
	}
	
	private void render(HttpServletRequest req, HttpServletResponse resp, String path)
			throws IOException, ServletException {
		String fqcn = toClassName(path);
		logger.trace("Class name for path [{}]: {}", path, fqcn);
		if (fqcn == null) {
			renderTemplate(path + ".html", req, resp, null);
			return;
		}
		
		try {
			runClass(fqcn, path, req, resp);
		} catch (ClassNotFoundException e) {
			logger.trace("", e);
			renderTemplate(path + ".html", req, resp, null);
		} catch (InstantiationException e) {
			logger.trace("", e);
			renderTemplate(path + ".html", req, resp, null);
		} catch (IllegalAccessException e) {
			logger.trace("", e);
			renderTemplate(path + ".html", req, resp, null);
		}
	}

	private void runClass(String fqcn, String path,
			HttpServletRequest req, HttpServletResponse resp)
	throws ClassNotFoundException, IOException, InstantiationException, IllegalAccessException, ServletException {
		Class<?> clazz = finder.findClass(fqcn);
		if (HtmlRunnable.class.isAssignableFrom(clazz)) {
			runHtml((HtmlRunnable)clazz.newInstance(), path, req, resp);
		} else if (JsonRunnable.class.isAssignableFrom(clazz)) {
			runJson((JsonRunnable)clazz.newInstance(), path, req, resp);
		} else if (RawRunnable.class.isAssignableFrom(clazz)) {
			runRaw((RawRunnable)clazz.newInstance(), path, req, resp);
		} else {
			logger.debug("Class [{}] is not runnable.", clazz);
			renderTemplate(path + ".html", req, resp, null);
		}
	}

	private void runHtml(HtmlRunnable inst, String path,
			HttpServletRequest req, HttpServletResponse resp) throws IOException {
		Map<String, Object> ctx = createContext();
		try {
			String r = inst.run(path, ctx, req, resp);
			if (r == null || r.isEmpty()) {
				renderTemplate(path + ".html", req, resp, ctx);
			} else if (r.startsWith(PREFIX_FORWARD_TO)) {
				render(req, resp, r.substring(PREFIX_FORWARD_TO.length()));
			} else if (r.equals("rendered")) {
				// Does nothing.
			} else {
				renderTemplate(r + ".html", req, resp, ctx);
			}
		} catch (Exception e) {
			renderErrorPage(path, ctx, req, resp, e);
		}
	}

	private void renderErrorPage(String path, Map<String, Object> ctx,
			HttpServletRequest req, HttpServletResponse resp, Exception e) throws IOException {
		Map<String, Object> result = ctx == null ? new HashMap<String, Object>() : ctx;
		result.put("exception", e);
		renderTemplate("500.html", req, resp, result);
	}

	private void runJson(JsonRunnable inst, String path,
			HttpServletRequest req, HttpServletResponse resp) throws IOException {
		Map<String, Object> ctx = createContext();
		try {
			resp.getWriter().print(Json.toJson(inst.run(path, ctx, req, resp)));
		} catch (Exception e) {
			// TODO Output less information.
			resp.getWriter().print(Json.toJson(e));
		}
	}

	private void runRaw(RawRunnable inst, String path,
			HttpServletRequest req, HttpServletResponse resp)
					throws IOException, ServletException {
		inst.run(path, createContext(), req, resp);
	}

	private Map<String, Object> createContext() {
		// TODO Configure context in a nutz IoC container.
		return new HashMap<String, Object>();
	}

	private String toClassName(String path) {
		if (path == null) return null;
		String p = path;
		if (p.startsWith("/")) p = p.substring(1);
		if (p.endsWith("/")) p = p.substring(0, p.length() - 1);
		if (!REGEX_VALID_PATH.matcher("/" + p).matches()) return null;
		
		String cn = p.replaceAll("/", ".");
		return cn;
	}

	private void renderTemplate(String path, HttpServletRequest req,
			HttpServletResponse resp, Map<String, Object> result)
					throws IOException {
		VelocityEngine ve = velocityConfig.getEngine();
		Context ctx = getContext(path, req, resp, result);
		Writer w = getWriter(resp);
		try {
			ctx.put(GroovyConfig.KEY_GROOVY_CONFIG, groovyConfig);
			ve.mergeTemplate(path, velocityConfig.getEncoding(), ctx, w);
		} finally {
			Streams.safeClose(w);
		}
	}

	private Writer getWriter(HttpServletResponse resp) throws IOException {
		try {
			return resp.getWriter();
		} catch (IOException e) {
			return new OutputStreamWriter(resp.getOutputStream());
		}
	}

	private Context getContext(String path, HttpServletRequest req,
			HttpServletResponse resp, Map<String, Object> result) {
		Context ctx = velocityConfig.newContext(result);
		ctx.put(VelocityConfig.KEY_PATH, path);
		ctx.put(VelocityConfig.KEY_REQUEST, req);
		ctx.put(VelocityConfig.KEY_RESPONSE, resp);
		ctx.put(VelocityConfig.KEY_CONTEXT_PATH, req.getContextPath());
		ctx.put(VelocityConfig.KEY_REQUEST_URI, Mvcs.getRequestPath(req));
		return ctx;
	}

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		String n = config.getInitParameter(KEY_RUNTIME_FINDER_BEAN);
		if (n == null || n.isEmpty()) n = DEF_RUNTIME_FINDER_KEY;
		finder = TplJsonIocProvider.nutzIoc().get(null, n);
		if (finder == null) {
			finder = new RuntimeClassFinder();
			finder.init();
		}
		
		n = config.getInitParameter(KEY_GROOVY_CONFIG_BEAN);
		if (n == null || n.isEmpty()) n = DEF_GROOVY_CONFIG_KEY;
		groovyConfig = TplJsonIocProvider.nutzIoc().get(null, n);
		n = config.getInitParameter(KEY_VELOCITY_CONFIG_BEAN);
		if (n == null || n.isEmpty()) n = DEF_VELOCITY_CONFIG_KEY;
		velocityConfig = TplJsonIocProvider.nutzIoc().get(null, n);
		
		String enc = config.getInitParameter(KEY_ENCODING);
		if (enc != null && !enc.isEmpty() && Charset.isSupported(enc)) {
			encoding = enc;
		}
	}
}
