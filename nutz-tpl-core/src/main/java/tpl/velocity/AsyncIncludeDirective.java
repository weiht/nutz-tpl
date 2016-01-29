package tpl.velocity;

import java.io.IOException;
import java.io.Writer;
import java.util.UUID;

import org.apache.velocity.app.event.EventHandlerUtil;
import org.apache.velocity.context.Context;
import org.apache.velocity.context.InternalContextAdapter;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.runtime.directive.Directive;
import org.apache.velocity.runtime.parser.node.Node;
import org.apache.velocity.runtime.parser.node.SimpleNode;

/**
 * Assume that you have jQuery included in your page.
 * 
 * @author weiht
 *
 */
public class AsyncIncludeDirective extends Directive {
	public static final String KEY_ASYNC_INCLUDE_CONTENT = "asyncIncludeContentTemplate";
	public static final String KEY_ASYNC_INCLUDE_ID = "asyncIncludeId";
	public static final String KEY_ASYNC_INCLUDE_Path = "asyncIncludePath";
	public static final String DEFAULT_CONTENT =
			"<div id=\"${" + KEY_ASYNC_INCLUDE_ID + "}\" class=\"async-include\""
					+ " data-href=\"${contextPath}${" + KEY_ASYNC_INCLUDE_Path + "}\">"
					//TODO Add loading hints here.
					+ "<div class=\"loading\"><img src=\"${contextPath}/s/css/asyncinclude.png\"/></div></div>";

	@Override
	public String getName() {
		return "asyncinclude";
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
			throw new ParseErrorException("No templates specified.");
		}
		String path = parseTemplatePath(context, node);
		String contentToRender = getContentTemplate(context);
		return rsvc.evaluate(createContext(context, path), writer, path, contentToRender);
	}

	private Context createContext(InternalContextAdapter context, String path) {
		Context ctx = new InternalContextAdapterWrapper(context);
		ctx.put(KEY_ASYNC_INCLUDE_ID, UUID.randomUUID().toString());
		ctx.put(KEY_ASYNC_INCLUDE_Path, path);
		return ctx;
	}

	private String getContentTemplate(InternalContextAdapter context) {
		String tpl = (String) context.get(KEY_ASYNC_INCLUDE_CONTENT);
		if (tpl == null || tpl.isEmpty()) {
			tpl = DEFAULT_CONTENT;
		}
		return tpl;
	}

	private String parseTemplatePath(InternalContextAdapter context, Node node) {
		String path = (String) ((SimpleNode)node.jjtGetChild(0)).value(context);
		String template = EventHandlerUtil.includeEvent(rsvc, context, path, context.getCurrentTemplateName(), getName());
		return template;
	}
}
