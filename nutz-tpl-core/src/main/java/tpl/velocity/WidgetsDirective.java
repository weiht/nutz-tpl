package tpl.velocity;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.velocity.context.InternalContextAdapter;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.runtime.directive.Directive;
import org.apache.velocity.runtime.parser.node.Node;
import org.apache.velocity.runtime.parser.node.SimpleNode;
import org.nutz.resource.NutResource;
import org.nutz.resource.Scans;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WidgetsDirective
extends Directive {
	private static final Logger logger = LoggerFactory.getLogger(WidgetsDirective.class);
	
	public static final String KEY_WIDGETS = "widgets";
	private static final String PATH_PREFIX = "widgets";
	
	private HashMap<String, List<String>> widgetLists = new HashMap<String, List<String>>();
	private VelocityConfig viewConfig;
	private static final String[] widgetExtensions = {
		".html", ".vm", ".htm", ".widget"
	};
	
	@Override
	public String getName() {
		return "widgets";
	}

	@Override
	public int getType() {
		return BLOCK;
	}

	@Override
	public boolean render(InternalContextAdapter context, Writer writer,
			Node node) throws IOException, ResourceNotFoundException,
			ParseErrorException, MethodInvocationException {
		viewConfig = (VelocityConfig) context.get(VelocityConfig.KEY_VIEW_CONFIG);
		String key = getWidgetListKey(context, node);
		logger.trace("Loading widgets for list [{}]...", key);
		try {
			if (key == null || key.isEmpty()) {
				return doRender(context, writer, node, widgetList(context, ""));
			} else {
				return doRender(context, writer, node, widgetList(context, key));
			}
		} catch (Exception e) {
			logger.warn("", e);
			return false;
		}
	}

	private boolean doRender(InternalContextAdapter context, Writer writer,
			Node node, List<String> widgets)
					throws MethodInvocationException, ParseErrorException,
					ResourceNotFoundException, IOException {
		logger.trace("Widgets: {}", widgets);
		Object oldWidgets = context.get(KEY_WIDGETS);
		context.put(KEY_WIDGETS, widgets);
		try {
			SimpleNode block = (SimpleNode) node.jjtGetChild(node.jjtGetNumChildren() - 1);
			return block.render(context, writer);
		} finally {
			if (oldWidgets == null) {
				context.remove(KEY_WIDGETS);
			} else {
				context.put(KEY_WIDGETS, oldWidgets);
			}
		}
	}

	private List<String> widgetList(InternalContextAdapter context, String key) {
		File[] repos = (File[]) context.get(VelocityConfig.KEY_REPO_DIRS);
		logger.trace("Repos for resources: {}", (Object)repos);
		if (repos == null || repos.length < 1) {
			return classpathWidgetList(context, key);
		} else {
			return mixedWidgetList(context, key, repos);
		}
	}

	private List<String> classpathWidgetList(InternalContextAdapter context,
			String key) {
		boolean devMode = viewConfig.isDevMode();
		List<String> loaded = null;
		if (!devMode) {
			loaded = widgetLists.get(key); 
		}
		if (loaded == null) {
			Set<String> dedup = new HashSet<String>();
			loadClasspathWidgets(dedup, key);
			loaded = new ArrayList<String>(dedup);
			Collections.sort(loaded);
		}
		if (!devMode) {
			widgetLists.put(key, loaded);
		}
		return loaded;
	}
	
	private String combinePath(String... parts) {
		StringBuilder buff = new StringBuilder();
		for (String p: parts) {
			if (!p.startsWith("/")) {
				buff.append("/");
			}
			buff.append(p);
			int last = buff.length() - 1;
			if (buff.charAt(last) == '/') {
				buff.deleteCharAt(last);
			}
		}
		return buff.toString();
	}

	private void loadClasspathWidgets(Collection<String> loaded, String key) {
		logger.trace("Loading classpath widgets...");
		String prefix = PATH_PREFIX + (key == null || key.isEmpty() ? "" : ("/" + key));
		for (NutResource res: Scans.me().scan(viewConfig.getResourceLocation() + "/" + prefix)) {
			String n = res.getName();
			if (isWidget(n)) {
				logger.trace("Classpath widget: /{}/{}/{}", PATH_PREFIX, key, n);
				loaded.add(combinePath(prefix, n));
			}
		}
	}

	private List<String> mixedWidgetList(InternalContextAdapter context,
			String key, File[] repos) {
		boolean devMode = viewConfig.isDevMode();
		List<String> loaded = null;
		if (!devMode) {
			loaded = widgetLists.get(key); 
		}
		if (loaded == null) {
			Set<String> dedup = new HashSet<String>();
			loadRepoWidgets(dedup, key, repos);
			loadClasspathWidgets(dedup, key);
			loaded = new ArrayList<String>(dedup);
			Collections.sort(loaded);
		}
		if (!devMode) {
			widgetLists.put(key, loaded);
		}
		return loaded;
	}

	private void loadRepoWidgets(Collection<String> loaded, String key, File[] repos) {
		logger.trace("Loading repository widgets...");
		for (File f: repos) {
			File d = new File(f, PATH_PREFIX);
			if (key != null && !key.isEmpty()) {
				d = new File(d, key);
			}
			if (d.exists()) {
				for (File w: d.listFiles()) {
					if (w.isFile()) {
						String n = w.getName();
						if (isWidget(n)) {
							logger.trace("Repo widget: /{}/{}/{}", PATH_PREFIX, key, n);
							loaded.add("/" + PATH_PREFIX + "/" + key + "/" + ((d == f) ? (d.getName() + "/") : "") + n);
						}
					}
				}
			}
		}
	}

	private boolean isWidget(String n) {
		for (String ext: widgetExtensions) {
			if (n.endsWith(ext)) return true;
		}
		return false;
	}

	private String getWidgetListKey(InternalContextAdapter context, Node node) {
		if (node.jjtGetNumChildren() < 2) return null;
		SimpleNode sn = (SimpleNode) node.jjtGetChild(0);
		return (String) sn.value(context);
	}
}
