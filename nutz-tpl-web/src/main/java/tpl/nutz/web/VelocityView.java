package tpl.nutz.web;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.context.Context;
import org.nutz.lang.Streams;
import org.nutz.mvc.Mvcs;
import org.nutz.mvc.View;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tpl.nutz.velocity.VelocityConfig;

public class VelocityView
implements View {
	private static final Logger logger = LoggerFactory.getLogger(VelocityView.class);
	
	
	private VelocityConfig config;
	
	private String path;
	
	public VelocityView() {
	}
	
	public VelocityView(VelocityConfig config) {
		this.config = config;
	}
	
	public void init() {
	}
	
	public void dispose() {
	}

	public void render(HttpServletRequest req, HttpServletResponse resp,
			Object obj) throws Throwable {
		VelocityEngine ve = config.getEngine();
		Context ctx = getContext(req, resp, obj);
		Writer w = getWriter(resp);
		try {
			ve.mergeTemplate(path, config.getEncoding(), ctx, w);
		} finally {
			Streams.safeClose(w);
		}
	}

	private Writer getWriter(HttpServletResponse resp) throws IOException {
		try {
			return resp.getWriter();
		} catch (IOException e) {
			logger.info("Error retrieving response's writer. Try to retrieve output stream.");
			return new OutputStreamWriter(resp.getOutputStream());
		}
	}

	private Context getContext(HttpServletRequest req,
			HttpServletResponse resp, Object obj) {
		Context ctx = config.newContext();
		ctx.put(VelocityConfig.KEY_PATH, path);
		ctx.put(VelocityConfig.KEY_REQUEST, req);
		ctx.put(VelocityConfig.KEY_RESPONSE, resp);
		ctx.put(VelocityConfig.KEY_RESULT, obj);
		ctx.put(VelocityConfig.KEY_CONTEXT_PATH, req.getContextPath());
		ctx.put(VelocityConfig.KEY_REQUEST_URI, Mvcs.getRequestPath(req));
		return ctx;
	}

	public static VelocityView serve(VelocityConfig config, String path) {
		VelocityView view = new VelocityView(config);
		view.setPath(path);
		return view;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}
}
