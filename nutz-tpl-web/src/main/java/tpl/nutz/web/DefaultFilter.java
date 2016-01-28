package tpl.nutz.web;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.mvc.Mvcs;

public class DefaultFilter
implements Filter {
	public static final String DEFAUTLT_WELCOME_PAGE = "index.html";
	public static final String KEY_WELCOME_PAGE = "welcomePage";
	private String welcomePage = DEFAUTLT_WELCOME_PAGE;

	public void init(FilterConfig filterConfig) throws ServletException {
		String welcome = filterConfig.getInitParameter(KEY_WELCOME_PAGE);
		if (welcome != null && !(welcome = welcome.trim()).isEmpty()) {
			//TODO Drop leading slashes
			if (welcome.charAt(0) == '/') welcome = welcome.substring(1);
			if (!welcome.isEmpty()) welcomePage = welcome;
		}
	}

	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest) request;
		String path = Mvcs.getRequestPathObject(req).getUrl();
		if (path.endsWith("/")) {
			((HttpServletResponse)response).sendRedirect(path + welcomePage);
		} else {
			chain.doFilter(request, response);
		}
	}

	public void destroy() {
		
	}
}
