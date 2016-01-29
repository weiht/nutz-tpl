package tpl.shiro.velocity;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;

public class SignedInDirective
extends AuthDirective {
	@Override
	public String getName() {
		return "signedin";
	}
	
	@Override
	protected boolean authState() {
		Subject sub = SecurityUtils.getSubject();
		return sub.isAuthenticated() || sub.isRemembered();
	}
}
