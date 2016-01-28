package tpl.nutz.groovy;

import java.util.Map.Entry;

import groovy.lang.Binding;

import org.apache.velocity.context.InternalContextAdapter;

public class GroovyBindingsHelper {
	private GroovyBindingsHelper() {}
	
	public static void velocityInternalContextToBindings(InternalContextAdapter context, Binding binding) {
		if (context == null || binding == null) return;
		for (Object k: context.getKeys()) {
			String key = k.toString();
			binding.setVariable(key, context.get(key));
		}
	}
	
	@SuppressWarnings("unchecked")
	public static void bindingsToVelocityInternalContext(Binding binding, InternalContextAdapter context) {
		if (context == null || binding == null) return;
		for (Object e: binding.getVariables().entrySet()) {
			Entry<String, Object> var = (Entry<String, Object>)e;
			context.put(var.getKey(), var.getValue());
		}
	}
}
