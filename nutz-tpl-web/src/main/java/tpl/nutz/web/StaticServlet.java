package tpl.nutz.web;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.activation.MimetypesFileTypeMap;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.lang.Streams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StaticServlet
extends HttpServlet {
	private static final long serialVersionUID = -2975418745666594014L;
	private static final Logger logger = LoggerFactory.getLogger(StaticServlet.class);
	
	public static final String KEY_RESOURCE_LOCATION = "resourceLocation";
	public static final String DEFAULT_RESOURCE_LOCATION = "STATIC-RES";
	public static final String SYS_PROP_STATIC_RESOURCE_PATHS = "tpl.staticresource.path";
	
	private String resourceLocation = DEFAULT_RESOURCE_LOCATION;
	private List<File> locations;
	private String staticResourceRepositories;
	
	@Override
	public void init() throws ServletException {
		super.init();
		if (locations == null) {
			doInit();
		}
	}
	
	public void doInit() throws ServletException {
		prepareResourceLocation();
		initPaths();
	}

	private void prepareResourceLocation() {
		String loc = getServletConfig().getInitParameter(KEY_RESOURCE_LOCATION);
		if (loc != null && !(loc = loc.trim()).isEmpty()) {
			resourceLocation = loc;
		}
	}

	private void initPaths() {
		String paths = staticResourceRepositories != null ? staticResourceRepositories : System.getProperty(SYS_PROP_STATIC_RESOURCE_PATHS);
		logger.info("System property {}: {}", SYS_PROP_STATIC_RESOURCE_PATHS, paths);
		if (paths == null || (paths = paths.trim()).isEmpty()) return;
		ArrayList<File> locs = new ArrayList<File>();
		for (String p: paths.split(File.pathSeparator)) {
			p = p.trim();
			if (!p.isEmpty()) {
				File f = new File(p, resourceLocation);
				if (f.exists() && !f.isFile())
					locs.add(f);
			}
		}
		if (!locs.isEmpty()) locations = locs;
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String path = req.getPathInfo();
		logger.debug("Finding static resource: {}", path);
		InputStream ins = null;
		if (locations != null) {
			logger.debug("Finding resource in locations: {}, {}", locations, path);
			ins = fileResourceAsStream(path);
		}
		if (ins == null) {
			logger.debug("Finding resource in classpath. {}", path);
			ins = classpathResourceAsStream(path);
		}
		if (ins == null) {
			//TODO 404
			logger.debug("Resource {} not found.", path);
			super.doGet(req, resp);
		} else {
			resp.setContentType(MimetypesFileTypeMap.getDefaultFileTypeMap().getContentType(path));
			writeToResp(ins, resp);
		}
	}

	private InputStream fileResourceAsStream(String path) {
		for (File p: locations) {
			File f = new File(p, path);
			logger.trace("Testing resource file: {}", f);
			if (f.exists() && f.isFile())
				try {
					return new FileInputStream(f);
				} catch (FileNotFoundException e) {
					//This won't happen.
				}
		}
		return null;
	}

	private InputStream classpathResourceAsStream(String path) {
		return getClass().getClassLoader().getResourceAsStream(resourceLocation + path);
	}

	private void writeToResp(InputStream ins, HttpServletResponse resp) throws IOException {
		Streams.writeAndClose(resp.getOutputStream(), ins);
	}
	
	public void reload() {
		locations = null;
		try {
			doInit();
		} catch (ServletException e) {
			logger.error("");
		}
	}

	public void setStaticResourceRepositories(String resourceRepositories) {
		String repos = resourceRepositories;
		if (repos == null || (repos = repos.trim()).isEmpty()) {
			staticResourceRepositories = null;
		} else {
			staticResourceRepositories = repos;
		}
		reload();
	}
}
