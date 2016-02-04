package tpl.shiro;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.nutz.dao.Dao;

import tpl.authc.DbAuthenticationInfo;

public class NutzDaoRealm
extends AuthorizingRealm {
	private Dao dao;

	@Override
	protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
		return null;
	}

	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
		String userName = null;
		if (token instanceof UsernamePasswordToken)
			userName = ((UsernamePasswordToken) token).getUsername();
		else
			userName = token.getCredentials().toString();
		return dao.fetch(DbAuthenticationInfo.class, userName);
	}
	
	public void setDao(Dao dao) {
		this.dao = dao;
	}
}
