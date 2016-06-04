package tpl.nutz.web;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import tpl.nutz.TplJsonIocProvider;

@SuppressWarnings("serial")
public class NutzDelegatingServlet
extends HttpServlet {
	public static final String KEY_SERVLET_NAME = "servletName";

	private HttpServlet internalServlet;
	
	@Override
	public void init(ServletConfig config) throws ServletException {
		String n = config.getInitParameter(KEY_SERVLET_NAME);
		if (n == null || (n = n.trim()).isEmpty()) {
			n = config.getServletName();
		}
		internalServlet = TplJsonIocProvider.nutzIoc().get(HttpServlet.class, n);
		if (internalServlet != null) {
			internalServlet.init(config);
		}
	}
	
	@Override
	protected void service(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		if (internalServlet != null) {
			internalServlet.service(req, resp);
		} else {
			super.service(req, resp);
		}
	}
	
	@Override
	public void destroy() {
		super.destroy();
	}
}
