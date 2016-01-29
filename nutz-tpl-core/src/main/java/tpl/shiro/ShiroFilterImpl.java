package tpl.shiro;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.Filter;

import org.apache.shiro.config.Ini;
import org.apache.shiro.util.CollectionUtils;
import org.apache.shiro.util.Nameable;
import org.apache.shiro.util.StringUtils;
import org.apache.shiro.web.config.IniFilterChainResolverFactory;
import org.apache.shiro.web.filter.AccessControlFilter;
import org.apache.shiro.web.filter.authc.AuthenticationFilter;
import org.apache.shiro.web.filter.authz.AuthorizationFilter;
import org.apache.shiro.web.filter.mgt.DefaultFilterChainManager;
import org.apache.shiro.web.filter.mgt.FilterChainManager;
import org.apache.shiro.web.filter.mgt.PathMatchingFilterChainResolver;
import org.apache.shiro.web.servlet.AbstractShiroFilter;
import org.nutz.lang.Mirror;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ShiroFilterImpl extends AbstractShiroFilter {
	private static final Logger logger = LoggerFactory.getLogger(ShiroFilterImpl.class);
	
	private List<String> chainDefinitions;
	private String loginUrl, successUrl, unauthorizedUrl;
	private Map<String, Filter> filters;
	private Map<String, Map<String, Object>> defaultFilterConfigs;
	
	@Override
	public void init() throws Exception {
		FilterChainManager manager = createFilterChainManager();
		PathMatchingFilterChainResolver chainResolver = new PathMatchingFilterChainResolver();
        chainResolver.setFilterChainManager(manager);
        this.setFilterChainResolver(chainResolver);
	}

    private void applyLoginUrlIfNecessary(Filter filter) {
        logger.trace("Login filter: {}", filter);
        String loginUrl = getLoginUrl();
        logger.trace("Login URL: {}", loginUrl);
        if (StringUtils.hasText(loginUrl) && (filter instanceof AccessControlFilter)) {
            AccessControlFilter acFilter = (AccessControlFilter) filter;
            //only apply the login url if they haven't explicitly configured one already:
            String existingLoginUrl = acFilter.getLoginUrl();
            if (AccessControlFilter.DEFAULT_LOGIN_URL.equals(existingLoginUrl)) {
                acFilter.setLoginUrl(loginUrl);
            }
        }
    }

    private void applySuccessUrlIfNecessary(Filter filter) {
        logger.trace("Success filter: {}", filter);
        String successUrl = getSuccessUrl();
        logger.trace("Success URL: {}", successUrl);
        if (StringUtils.hasText(successUrl) && (filter instanceof AuthenticationFilter)) {
            AuthenticationFilter authcFilter = (AuthenticationFilter) filter;
            //only apply the successUrl if they haven't explicitly configured one already:
            String existingSuccessUrl = authcFilter.getSuccessUrl();
            if (AuthenticationFilter.DEFAULT_SUCCESS_URL.equals(existingSuccessUrl)) {
                authcFilter.setSuccessUrl(successUrl);
            }
        }
    }

    private void applyUnauthorizedUrlIfNecessary(Filter filter) {
        logger.trace("Unauthorized filter: {}", filter);
        String unauthorizedUrl = getUnauthorizedUrl();
        logger.trace("Unauthorized URL: {}", unauthorizedUrl);
        if (StringUtils.hasText(unauthorizedUrl) && (filter instanceof AuthorizationFilter)) {
            AuthorizationFilter authzFilter = (AuthorizationFilter) filter;
            //only apply the unauthorizedUrl if they haven't explicitly configured one already:
            String existingUnauthorizedUrl = authzFilter.getUnauthorizedUrl();
            if (existingUnauthorizedUrl == null) {
                authzFilter.setUnauthorizedUrl(unauthorizedUrl);
            }
        }
    }

   private void applyGlobalPropertiesIfNecessary(Filter filter) {
        applyLoginUrlIfNecessary(filter);
        applySuccessUrlIfNecessary(filter);
        applyUnauthorizedUrlIfNecessary(filter);
    }

	private FilterChainManager createFilterChainManager() {
        DefaultFilterChainManager manager = new DefaultFilterChainManager();
        Map<String, Filter> defaultFilters = manager.getFilters();
        logger.trace("Default filters: {}", defaultFilters);
        //apply global settings if necessary:
        for (Filter filter : defaultFilters.values()) {
            applyGlobalPropertiesIfNecessary(filter);
        }

        //Apply the acquired and/or configured filters:
        Map<String, Filter> filters = getFilters();
        logger.trace("User defined filters: {}", filters);
        if (!CollectionUtils.isEmpty(filters)) {
            for (Map.Entry<String, Filter> entry : filters.entrySet()) {
                String name = entry.getKey();
                Filter filter = entry.getValue();
                applyGlobalPropertiesIfNecessary(filter);
                if (filter instanceof Nameable) {
                    ((Nameable) filter).setName(name);
                }
                //'init' argument is false, since Spring-configured filters should be initialized
                //in Spring (i.e. 'init-method=blah') or implement InitializingBean:
                manager.addFilter(name, filter, false);
            }
        }
        
        reconfigDefaultFilters(manager);

        //build up the chains:
        Map<String, String> chains = toChains(getChainDefinitions());
        if (!CollectionUtils.isEmpty(chains)) {
            for (Map.Entry<String, String> entry : chains.entrySet()) {
                String url = entry.getKey();
                String chainDefinition = entry.getValue();
                manager.createChain(url, chainDefinition);
            }
        }

        return manager;
	}

	private void reconfigDefaultFilters(DefaultFilterChainManager manager) {
		if (defaultFilterConfigs == null || defaultFilterConfigs.isEmpty()) return;
		for (Entry<String, Map<String, Object>> conf: defaultFilterConfigs.entrySet()) {
			String n = conf.getKey();
			Filter f = manager.getFilter(n);
			if (f != null) {
				reconfigFilter(f, conf.getValue());
			}
		}
	}

	private void reconfigFilter(Filter f, Map<String, Object> conf) {
		Mirror<? extends Filter> m = Mirror.me(f.getClass());
		for (Entry<String, Object> c: conf.entrySet()) {
			try {
				m.setValue(f, c.getKey(), c.getValue());
			} catch (Exception e) {
				logger.warn("", e);
			}
		}
	}

	private Map<String, String> toChains(List<String> chainDefs) {
        Ini ini = new Ini();
        ini.load(join(chainDefs));
        //did they explicitly state a 'urls' section?  Not necessary, but just in case:
        Ini.Section section = ini.getSection(IniFilterChainResolverFactory.URLS);
        if (CollectionUtils.isEmpty(section)) {
            //no urls section.  Since this _is_ a urls chain definition property, just assume the
            //default section contains only the definitions:
            section = ini.getSection(Ini.DEFAULT_SECTION_NAME);
        }
        logger.trace("Chains: {}", section);
		return section;
	}

	private String join(List<String> chainDefs) {
		StringBuilder buff = new StringBuilder();
		if (chainDefs != null && !chainDefs.isEmpty()) {
			for (String d: chainDefs) {
				if (buff.length() > 0) buff.append("\n");
				buff.append(d);
			}
		}
		return buff.toString();
	}

	public void setChainDefinitions(List<String> chainDefinitions) {
		this.chainDefinitions = chainDefinitions;
	}

	public String getLoginUrl() {
		return loginUrl;
	}

	public void setLoginUrl(String loginUrl) {
		this.loginUrl = loginUrl;
	}

	public String getSuccessUrl() {
		return successUrl;
	}

	public void setSuccessUrl(String successUrl) {
		this.successUrl = successUrl;
	}

	public String getUnauthorizedUrl() {
		return unauthorizedUrl;
	}

	public void setUnauthorizedUrl(String unauthorizedUrl) {
		this.unauthorizedUrl = unauthorizedUrl;
	}

	public List<String> getChainDefinitions() {
		return chainDefinitions;
	}

	public Map<String, Filter> getFilters() {
		return filters;
	}

	public void setFilters(Map<String, Filter> filters) {
		this.filters = filters;
	}

	public void setDefaultFilterConfigs(Map<String, Map<String, Object>> defaultFilterConfigs) {
		this.defaultFilterConfigs = defaultFilterConfigs;
	}
}
