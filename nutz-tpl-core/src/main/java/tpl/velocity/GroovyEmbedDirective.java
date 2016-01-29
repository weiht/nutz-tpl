package tpl.velocity;

import groovy.lang.Binding;
import tpl.groovy.GroovyBindingsHelper;
import tpl.groovy.GroovyConfig;

import java.io.IOException;
import java.io.Writer;

import org.apache.commons.lang.text.StrBuilder;
import org.apache.velocity.context.InternalContextAdapter;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.runtime.directive.Directive;
import org.apache.velocity.runtime.parser.Token;
import org.apache.velocity.runtime.parser.node.Node;

public class GroovyEmbedDirective
extends Directive {

	@Override
	public String getName() {
		return "groovyembed";
	}

	@Override
	public int getType() {
		return BLOCK;
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
		Node codeBlock = node.jjtGetChild(node.jjtGetNumChildren() - 1);
		Binding binding = config.getBinding();
		GroovyBindingsHelper.velocityInternalContextToBindings(context, binding);
		try {
			config.executeSnippet(blockToString(codeBlock), binding);
			return true;
		} catch (IOException e) {
			if (config.isDevMode()) {
				throw new IOException("", e);
			} else {
				return false;
			}
		}
	}

	private String blockToString(Node block) {
        StrBuilder tokens = new StrBuilder();
        
        for (Token t = block.getFirstToken(); t != null; )
        {
            tokens.append(t.image);
            if (t.next != null)
            {
                if (t.equals(block.getLastToken()))
                {
                    break;
                }
                else
                {
                    tokens.append('\n');
                }
            }
            t = t.next;
        }
		return tokens.toString();
	}
}
