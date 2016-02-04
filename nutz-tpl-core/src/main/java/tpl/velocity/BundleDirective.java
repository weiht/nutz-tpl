package tpl.velocity;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Stack;

import javax.servlet.http.HttpServletRequest;

import org.apache.velocity.context.InternalContextAdapter;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.runtime.directive.Directive;
import org.apache.velocity.runtime.parser.node.Node;
import org.apache.velocity.runtime.parser.node.SimpleNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BundleDirective
extends Directive {
	private static final Logger logger = LoggerFactory.getLogger(BundleDirective.class);
	
	public static final String BUNDLE_KEY = "bundle";
	private static ClassLoader bundleClassLoader;
	private VelocityConfig viewConfig;

	@Override
	public String getName() {
		return "bundle";
	}

	@Override
	public int getType() {
		return BLOCK;
	}
	
	private boolean isEmpty(String str) {
		return str == null || (str = str.trim()).isEmpty();
	}

	@Override
	public boolean render(InternalContextAdapter ctx, Writer w, Node n)
			throws IOException, ResourceNotFoundException, ParseErrorException,
			MethodInvocationException {
		this.viewConfig = (VelocityConfig) ctx.get(VelocityConfig.KEY_VIEW_CONFIG);
		String bundleName = getBundleName(ctx, n);
		if (isEmpty(bundleName)) {
			logger.trace("No bundle name specified.");
			return false;
		}
		Locale lc = getLocaleName(ctx);
		logger.trace("Locale for resource bundle {}: {}", bundleName, lc);
		File[] repos = getRepos(ctx);
		logger.trace("Repos for resources: {}", (Object)repos);
		try {
			InternalContextAdapter newCtx;
			try {
				newCtx = loadBundle(ctx, bundleName, lc, repos);
			} catch (Exception ex) {
				newCtx = ctx;
			}
			logger.trace("Wrapping context: {}", newCtx);
			Node block = n.jjtGetChild(n.jjtGetNumChildren() - 1);
			logger.trace("Content block: {}", block);
			return block.render(newCtx, w);
		} catch (MissingResourceException e) {
			logger.warn("", e);
			return false;
		}
	}

	private InternalContextAdapter loadBundle(InternalContextAdapter ctx, String bundleName,
			Locale lc, File[] repos) {
		if (repos == null || repos.length < 1) {
			return loadClasspathBundles(ctx, bundleName, lc);
		} else {
			return loadMixedBundles(ctx, bundleName, lc, repos);
		}
	}

	private InternalContextAdapter wrapBundle(InternalContextAdapter ctx,
			ResourceBundle bundle) {
		InternalContextAdapter newCtx = new InternalContextAdapterWrapper(ctx);
		newCtx.localPut(BUNDLE_KEY, bundle);
		return newCtx;
	}

	private InternalContextAdapter loadClasspathBundles(InternalContextAdapter ctx,
			String bundleName, Locale lc) {
		ResourceBundle bundle = ResourceBundle.getBundle(viewConfig.getResourceLocation() + "/" + bundleName, lc);
		return wrapBundle(ctx, bundle);
	}

	private InternalContextAdapter loadMixedBundles(InternalContextAdapter ctx,
			String bundleName, Locale lc, File[] repos) {
		ClassLoader cloader = ensureLoader(ctx, repos);
		ResourceBundle bundle = ResourceBundle.getBundle(viewConfig.getResourceLocation() + "/" + bundleName, lc, cloader);
		return wrapBundle(ctx, bundle);
	}
	
	private ClassLoader ensureLoader(InternalContextAdapter ctx, File[] repos) {
		if (isDevMode(ctx)) return createLoader(repos);
		if (bundleClassLoader == null) {
			bundleClassLoader = createLoader(repos);
		}
		return bundleClassLoader;
	}

	private boolean isDevMode(InternalContextAdapter ctx) {
		return ctx.get(VelocityConfig.KEY_DEV_MODE) != null;
	}

	private ClassLoader createLoader(File[] repos) {
		// Only one class loader will be retained.
		URL[] urls = reposToUrls(repos);
		return new URLClassLoader(urls, getClass().getClassLoader());
	}

	private URL[] reposToUrls(File[] repos) {
		URL[] result = new URL[repos.length];
		for (int i = 0; i < result.length; i ++) {
			File r = repos[i];
			if (r.getName().equals(viewConfig.getResourceLocation())) {
				r = r.getParentFile();
			}
			try {
				result[i] = r.toURI().toURL();
			} catch (MalformedURLException e) {
				//Does nothing
			}
		}
		return result;
	}

	private Locale getLocaleName(InternalContextAdapter ctx) {
		HttpServletRequest request = (HttpServletRequest) ctx.get(VelocityConfig.KEY_REQUEST);
		if (request == null) return Locale.getDefault();
		return request.getLocale();
	}

	private File[] getRepos(InternalContextAdapter ctx) {
		return (File[]) ctx.get(VelocityConfig.KEY_REPO_DIRS);
	}

	private String getBundleName(InternalContextAdapter ctx, Node n) {
		//TODO Retrieve bundle to current template.
		String defBundle = getDefaultBundleName(ctx);
		if (n.jjtGetNumChildren() < 2) {
			// 获取默认资源
			return defBundle;
		} else {
			SimpleNode nbundle = (SimpleNode) n.jjtGetChild(0);
			String bundleName = (String) nbundle.value(ctx);
			if (bundleName == null || bundleName.isEmpty())
				return defBundle;
			return combineBundleName(bundleName, defBundle);
		}
	}

	private String combineBundleName(String bundleName, String defBundle) {
		logger.trace("Bundle name specified in directive: {}, default bundle: {}", bundleName, defBundle);
		if (bundleName.startsWith("/")) return bundleName.substring(1);
		Stack<String> bps = new Stack<String>();
		for (String p: defBundle.split("/")) {
			bps.push(p);
		}
		bps.pop();
		for (String p: bundleName.split("/")) {
			if (p.equals("..")) {
				if (!p.isEmpty())
					bps.pop();
			} else if (!p.equals(".")) {
				bps.push(p);
			}
		}
		StringBuilder buff = new StringBuilder();
		for (String p: bps) {
			if (buff.length() > 0) buff.append("/");
			buff.append(p);
		}
		return buff.toString();
	}

	private String getDefaultBundleName(InternalContextAdapter ctx) {
		String bundleName = pathToBundleName(ctx.getCurrentTemplateName());
		logger.trace("No bundle name specified in directive. Using full path: {}", bundleName);
		return bundleName;
	}

	private String pathToBundleName(String bundleName) {
		int ix = bundleName.lastIndexOf('/');
		if (ix >= 0) {
			ix = bundleName.indexOf('.', ix);
		}
		String result = ix > 0 ? bundleName.substring(0, ix) : bundleName;
		logger.trace("Translated bundle name: {}", result);
		if (result.charAt(0) == '/') return result.substring(1);
		return result;
	}
}
