package tpl.nutz.web;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tpl.javasrc.RuntimeClassFinder;
import tpl.nutz.TplJsonIocProvider;

public class RuntimeJavaServlet
extends HttpServlet {
	private static final long serialVersionUID = -5820311121440447893L;

	private static final Logger logger = LoggerFactory.getLogger(RuntimeJavaServlet.class);
	
	public static final String KEY_RUNTIME_FINDER_BEAN = "runtimeLoaderBean";
	public static final String DEF_RUNTIME_FINDER_KEY = "runtimeLoader";
	
	private RuntimeClassFinder finder;

	@Override
	protected void service(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		super.service(req, resp);
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
	}
}
