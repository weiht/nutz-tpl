package tpl.nutz.web;

import groovy.lang.Binding;
import groovy.util.ResourceException;
import groovy.util.ScriptException;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.context.Context;
import org.nutz.lang.Streams;
import org.nutz.mvc.Mvcs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tpl.nutz.groovy.GroovyConfig;
import tpl.nutz.velocity.VelocityConfig;

public class GroovyVelocityServlet
extends HttpServlet {
	private static final long serialVersionUID = 4378889324537046202L;
	
	private static final Logger logger = LoggerFactory.getLogger(GroovyVelocityServlet.class);
	
	private VelocityConfig velocityConfig;
	private GroovyConfig groovyConfig;

	@Override
	protected void service(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String path = Mvcs.getRequestPath(req);
		if (path.endsWith("/")) {
			super.service(req, resp);
			return;
		}
		req.setCharacterEncoding(velocityConfig.getEncoding());
		resp.setCharacterEncoding(velocityConfig.getEncoding());
		render(req, resp, path);
	}

	private void render(HttpServletRequest req, HttpServletResponse resp,
			String path) throws ServletException, IOException {
		Map<String, Object> result;
		try {
			result = runScript(path + ".groovy", req, resp);
		} catch (ResourceException e) {
			logger.debug("", e);
			result = null;
		} catch (ScriptException e) {
			throw new ServletException(e);
		}
		if (!groovyConfig.preRender(result, getWriter(resp))) {
			Object forwarded;
			if (result != null && (forwarded = result.get(GroovyConfig.KEY_FORWARD_TO)) != null) {
				String fwd = forwarded.toString();
				render(req, resp, fwd);
			} else {
				renderTemplate(path + ".html", req, resp, result);
			}
		}
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

	private Map<String, Object> runScript(String path, HttpServletRequest req,
			HttpServletResponse resp) throws ResourceException, ScriptException {
		Binding binding = getBinding(path, req, resp);
		return groovyConfig.runScript(path, binding);
	}

	private Binding getBinding(String path, HttpServletRequest req,
			HttpServletResponse resp) {
		Binding binding = groovyConfig.getBinding();
		binding.setVariable(VelocityConfig.KEY_PATH, path);
		binding.setVariable(VelocityConfig.KEY_REQUEST, req);
		binding.setVariable(VelocityConfig.KEY_RESPONSE, resp);
		binding.setVariable(VelocityConfig.KEY_CONTEXT_PATH, req.getContextPath());
		binding.setVariable(VelocityConfig.KEY_REQUEST_URI, Mvcs.getRequestPath(req));
		return binding;
	}

	public void setVelocityConfig(VelocityConfig velocityConfig) {
		this.velocityConfig = velocityConfig;
	}
}
