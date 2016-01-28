package tpl.nutz.velocity;

import groovy.lang.Binding;
import groovy.util.ResourceException;
import groovy.util.ScriptException;

import java.io.IOException;
import java.io.Writer;

import org.apache.velocity.app.event.EventHandlerUtil;
import org.apache.velocity.context.InternalContextAdapter;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.runtime.directive.Directive;
import org.apache.velocity.runtime.parser.node.Node;
import org.apache.velocity.runtime.parser.node.SimpleNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tpl.nutz.groovy.GroovyBindingsHelper;
import tpl.nutz.groovy.GroovyConfig;

public class GroovyRunDirective
extends Directive {
	private static final Logger logger = LoggerFactory.getLogger(GroovyRunDirective.class);

	@Override
	public String getName() {
		return "groovyrun";
	}

	@Override
	public int getType() {
		return LINE;
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
		String groovyFile = parseScriptFile(context, node);
		Binding binding = config.getBinding();
		GroovyBindingsHelper.velocityInternalContextToBindings(context, binding);
		try {
			logger.trace("Running groovy script: {}", groovyFile);
			config.runScript(config.getResourceLocation() + groovyFile, binding);
			return true;
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

	private String parseScriptFile(InternalContextAdapter context, Node node) {
		String groovyFile = (String) ((SimpleNode)node.jjtGetChild(0)).value(context);
		String fn = EventHandlerUtil.includeEvent(rsvc, context, groovyFile, context.getCurrentTemplateName(), getName());
		if (fn.indexOf('.') < 0) fn += ".groovy";
		return fn;
	}
}
