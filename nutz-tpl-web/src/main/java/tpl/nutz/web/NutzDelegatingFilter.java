package tpl.nutz.web;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import tpl.nutz.TplJsonIocProvider;

public class NutzDelegatingFilter
implements Filter {
	public static final String KEY_FILTER_NAME = "filterName";
	
	private Filter internalFilter;

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		String n = filterConfig.getInitParameter(KEY_FILTER_NAME);
		if (n == null || (n = n.trim()).isEmpty()) {
			n = filterConfig.getFilterName();
		}
		internalFilter = TplJsonIocProvider.nutzIoc().get(Filter.class, n);
		if (internalFilter != null) {
			internalFilter.init(filterConfig);
		}
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		if (internalFilter != null) {
			internalFilter.doFilter(request, response, chain);
		} else {
			chain.doFilter(request, response);
		}
	}

	@Override
	public void destroy() {
		// Does nothing.
	}
}
