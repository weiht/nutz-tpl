package tpl.nutz;

import java.util.HashSet;
import java.util.Set;

import org.nutz.ioc.IocContext;
import org.nutz.ioc.ObjectProxy;
import org.nutz.ioc.impl.ComboContext;

public class CompositeIocContext
implements IocContext {
	private static final IocContext[] arr = {};
	private Set<IocContext> contexts = new HashSet<IocContext>();
	private ComboContext internalContext;
	
	public synchronized void addContext(IocContext ctx) {
		contexts.add(ctx);
		internalContext = null;
	}
	
	private synchronized IocContext ensureContext() {
		if (internalContext == null) {
			internalContext = new ComboContext(contexts.toArray(arr));
		}
		return internalContext;
	}

	@Override
	public boolean save(String scope, String name, ObjectProxy obj) {
		return ensureContext().save(scope, name, obj);
	}

	@Override
	public boolean remove(String scope, String name) {
		return ensureContext().remove(scope, name);
	}

	@Override
	public ObjectProxy fetch(String name) {
		return ensureContext().fetch(name);
	}

	@Override
	public void clear() {
		synchronized(this) {
			if (internalContext != null)
				internalContext.clear();
		}
	}

	@Override
	public void depose() {
		synchronized(this) {
			if (internalContext != null) {
				IocContext ctx = internalContext;
				internalContext = null;
				ctx.depose();
			}
		}
	}

	@Override
	public Set<String> names() {
		return ensureContext().names();
	}
}
