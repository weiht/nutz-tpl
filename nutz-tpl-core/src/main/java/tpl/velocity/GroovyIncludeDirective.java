package tpl.velocity;

import groovy.lang.Binding;
import groovy.util.ResourceException;
import groovy.util.ScriptException;
import tpl.groovy.GroovyBindingsHelper;
import tpl.groovy.GroovyConfig;

import java.io.IOException;
import java.io.Writer;

import org.apache.velocity.app.event.EventHandlerUtil;
import org.apache.velocity.context.InternalContextAdapter;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.runtime.directive.Parse;
import org.apache.velocity.runtime.parser.node.Node;
import org.apache.velocity.runtime.parser.node.SimpleNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GroovyIncludeDirective
extends Parse {
	private static final Logger logger = LoggerFactory.getLogger(GroovyIncludeDirective.class);

	@Override
	public String getName() {
		return "groovyinclude";
	}

	@Override
	public boolean render(InternalContextAdapter context, Writer writer,
			Node node) throws IOException, ResourceNotFoundException,
			ParseErrorException, MethodInvocationException {
		if (node.jjtGetNumChildren() < 1) {
			throw new ParseErrorException("No groovy script specified.");
		}
		GroovyConfig config = (GroovyConfig) context.get(GroovyConfig.KEY_GROOVY_CONFIG);
		if (config == null) throw new ParseErrorException("No groovy config found.");
		String fn = parseScriptFile(context, node);
		Binding binding = config.getBinding();
		GroovyBindingsHelper.velocityInternalContextToBindings(context, binding);
		try {
			logger.trace("Running groovy script: {}", fn);
			config.runScript(toGroovyFile(config, fn), binding);
			GroovyBindingsHelper.bindingsToVelocityInternalContext(binding, context);
			return tryParse(context, writer, node);
		} catch (ResourceException e) {
			if (config.isDevMode()) {
				throw new IOException("", e);
			} else {
				return false;
			}
		} catch (ScriptException e) {
			if (config.isDevMode()) {
				throw new IOException("", e);
			} else {
				return false;
			}
		}
	}

	private boolean tryParse(InternalContextAdapter context, Writer writer,
			Node node) throws IOException {
		try {
			return super.render(context, writer, node);
		} catch (ResourceNotFoundException e) {
			return true;
		} catch (ParseErrorException e) {
			throw e;
		} catch (MethodInvocationException e) {
			throw e;
		} catch (IOException e) {
			throw e;
		}
	}

	private String toGroovyFile(GroovyConfig config, String groovyFile) {
		String fn = config.getResourceLocation() + groovyFile;
		if (fn.indexOf('.') < 0) fn += ".groovy";
		else {
			fn = fn.substring(0, fn.lastIndexOf('.')) + ".groovy";
		}
		return fn;
	}

	private String parseScriptFile(InternalContextAdapter context, Node node) {
		String groovyFile = (String) ((SimpleNode)node.jjtGetChild(0)).value(context);
		String fn = EventHandlerUtil.includeEvent(rsvc, context, groovyFile, context.getCurrentTemplateName(), getName());
		return fn;
	}

}
