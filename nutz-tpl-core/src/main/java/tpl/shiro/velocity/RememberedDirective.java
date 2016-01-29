package tpl.shiro.velocity;

import org.apache.shiro.SecurityUtils;

public class RememberedDirective extends AuthDirective {
	@Override
	public String getName() {
		return "remember";
	}
	
	@Override
	protected boolean authState() {
		return SecurityUtils.getSubject().isRemembered();
	}
}
