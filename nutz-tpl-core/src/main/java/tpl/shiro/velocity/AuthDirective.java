package tpl.shiro.velocity;

import java.io.IOException;
import java.io.Writer;

import org.apache.shiro.SecurityUtils;
import org.apache.velocity.context.InternalContextAdapter;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.runtime.directive.Directive;
import org.apache.velocity.runtime.parser.node.Node;
import org.apache.velocity.runtime.parser.node.SimpleNode;

public class AuthDirective
extends Directive {
	@Override
	public String getName() {
		return "auth";
	}
	
	@Override
	public int getType() {
		return BLOCK;
	}
	
	@Override
	public boolean render(InternalContextAdapter context, Writer writer,
			Node node) throws IOException, ResourceNotFoundException,
			ParseErrorException, MethodInvocationException {
		boolean authRequired = authenticatedRequired(context, node);
		boolean auth = authState();
		if (authRequired == auth) {
			return doRender(context, writer, node);
		}
		return false;
	}

	protected boolean authState() {
		return SecurityUtils.getSubject().isAuthenticated();
	}

	private boolean authenticatedRequired(InternalContextAdapter context,
			Node node) {
		if (node.jjtGetNumChildren() < 2) return true;
		SimpleNode n = (SimpleNode) node.jjtGetChild(0);
		Object v = n.value(context);
		if (v == null) return true;
		if (v instanceof Boolean) {
			return (Boolean)v;
		}
		return true;
	}

	private boolean doRender(InternalContextAdapter context, Writer writer,
			Node node)
					throws MethodInvocationException, ParseErrorException,
					ResourceNotFoundException, IOException {
		SimpleNode block = (SimpleNode) node.jjtGetChild(node.jjtGetNumChildren() - 1);
		return block.render(context, writer);
	}
}
