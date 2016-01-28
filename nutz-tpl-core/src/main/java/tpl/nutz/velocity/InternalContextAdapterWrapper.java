package tpl.nutz.velocity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.velocity.app.event.EventCartridge;
import org.apache.velocity.context.Context;
import org.apache.velocity.context.InternalContextAdapter;
import org.apache.velocity.runtime.resource.Resource;
import org.apache.velocity.util.introspection.IntrospectionCacheData;

public class InternalContextAdapterWrapper implements InternalContextAdapter {
	private InternalContextAdapter internalCotnextAdapter;
	private Map<String, Object> localContext;
	
	public InternalContextAdapterWrapper(InternalContextAdapter wrapped) {
		if (wrapped == null) throw new NullPointerException();
		this.internalCotnextAdapter = wrapped;
		localContext = new HashMap<String, Object>();
	}

	public Context getInternalUserContext() {
		return internalCotnextAdapter.getInternalUserContext();
	}

	public InternalContextAdapter getBaseContext() {
		return internalCotnextAdapter;
	}

	public EventCartridge attachEventCartridge(EventCartridge ec) {
		return internalCotnextAdapter.attachEventCartridge(ec);
	}

	public Object localPut(String key, Object value) {
		return localContext.put(key, value);
	}

	public EventCartridge getEventCartridge() {
		return internalCotnextAdapter.getEventCartridge();
	}

	public Object put(String key, Object value) {
		return internalCotnextAdapter.put(key, value);
	}

	public void pushCurrentTemplateName(String s) {
		internalCotnextAdapter.pushCurrentTemplateName(s);
	}

	public Object get(String key) {
		Object v = localContext.get(key);
		if (v == null)
			v = internalCotnextAdapter.get(key);
		return v;
	}

	public void popCurrentTemplateName() {
		internalCotnextAdapter.popCurrentTemplateName();
	}

	public String getCurrentTemplateName() {
		return internalCotnextAdapter.getCurrentTemplateName();
	}

	public boolean containsKey(Object key) {
		return internalCotnextAdapter.containsKey(key);
	}

	public Object[] getTemplateNameStack() {
		return internalCotnextAdapter.getTemplateNameStack();
	}

	public Object[] getKeys() {
		return internalCotnextAdapter.getKeys();
	}

	public void pushCurrentMacroName(String s) {
		internalCotnextAdapter.pushCurrentMacroName(s);
	}

	public Object remove(Object key) {
		return internalCotnextAdapter.remove(key);
	}

	public void popCurrentMacroName() {
		internalCotnextAdapter.popCurrentMacroName();
	}

	public String getCurrentMacroName() {
		return internalCotnextAdapter.getCurrentMacroName();
	}

	public int getCurrentMacroCallDepth() {
		return internalCotnextAdapter.getCurrentMacroCallDepth();
	}

	public Object[] getMacroNameStack() {
		return internalCotnextAdapter.getMacroNameStack();
	}

	public IntrospectionCacheData icacheGet(Object key) {
		return internalCotnextAdapter.icacheGet(key);
	}

	public void icachePut(Object key, IntrospectionCacheData o) {
		internalCotnextAdapter.icachePut(key, o);
	}

	public Resource getCurrentResource() {
		return internalCotnextAdapter.getCurrentResource();
	}

	public void setCurrentResource(Resource r) {
		internalCotnextAdapter.setCurrentResource(r);
	}

	@SuppressWarnings("rawtypes")
	public void setMacroLibraries(List macroLibraries) {
		internalCotnextAdapter.setMacroLibraries(macroLibraries);
	}

	@SuppressWarnings("rawtypes")
	public List getMacroLibraries() {
		return internalCotnextAdapter.getMacroLibraries();
	}
}
