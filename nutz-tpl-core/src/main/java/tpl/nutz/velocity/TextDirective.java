package tpl.nutz.velocity;

import java.io.IOException;
import java.io.Writer;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.apache.velocity.context.InternalContextAdapter;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.runtime.directive.Directive;
import org.apache.velocity.runtime.parser.node.Node;
import org.apache.velocity.runtime.parser.node.SimpleNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TextDirective
extends Directive {
	private static final Logger logger = LoggerFactory.getLogger(TextDirective.class);

	@Override
	public String getName() {
		return "text";
	}

	@Override
	public int getType() {
		return LINE;
	}

	@Override
	public boolean render(InternalContextAdapter ctx, Writer w, Node n)
			throws IOException, ResourceNotFoundException, ParseErrorException,
			MethodInvocationException {
		String key = getKey(ctx, n);
		if (isEmpty(key)) {
			logger.trace("No key for resource text specified.");
			return false;
		}
		String str;
		try {
			str = getMessage(ctx, key);
		} catch (Exception e) {
			w.write(key);
			return false;
		}
		if (isEmpty(str)) {
			logger.trace("No resource with name {} found.", key);
			//TODO Format string with consequent nodes passed into this directive.
			w.write(key);
			return false;
		}
		logger.trace("Resource found: {} = {}", key, str);
		if (n.jjtGetNumChildren() > 1) {
			str = format(str, ctx, n);
		}
		w.write(str);
		return true;
	}
	
	private String getMessage(InternalContextAdapter ctx, String key) {
		ResourceBundle bundle = null;
		Object ob;
		InternalContextAdapter itctx = ctx, nctx = null;
		String str = null;
		logger.trace("Beginning bundle iteration for {}...", key);
		while (itctx != nctx) {
			ob = itctx.get(BundleDirective.BUNDLE_KEY);
			logger.trace("Bundle in\n{}:\n    {}", itctx, ob);
			if (ob != null && ResourceBundle.class.isAssignableFrom(ob.getClass())) {
				bundle = (ResourceBundle) ob;
				try {
					str = bundle.getString(key);
				} catch (MissingResourceException e) {
					str = null;
				}
				logger.trace("{} in bundle: {}", key, str);
			}
			if (isEmpty(str)) {
				logger.trace("Message {} not found in context. Searching parent...");
				nctx = itctx;
				itctx = nctx.getBaseContext();
			} else {
				return str;
			}
		}
		return str;
	}

	private boolean isEmpty(String str) {
		return str == null || (str = str.trim()).isEmpty();
	}

	private String getKey(InternalContextAdapter ctx, Node n) {
		if (n.jjtGetNumChildren() < 1) return null;
		SimpleNode nkey = (SimpleNode) n.jjtGetChild(0);
		String key = (String) nkey.value(ctx);
		return key;
	}

	private String format(String str, InternalContextAdapter ctx, Node n) {
		int num = n.jjtGetNumChildren();
		Object[] paramList = new Object[num - 1];
		for (int i = 1; i < num; i ++) {
			paramList[i - 1] = n.jjtGetChild(i).value(ctx);
		}
		try {
			return String.format(str, paramList);
		} catch (Exception e) {
			logger.warn("Error formating resource.", e);
			return str;
		}
	}
}
