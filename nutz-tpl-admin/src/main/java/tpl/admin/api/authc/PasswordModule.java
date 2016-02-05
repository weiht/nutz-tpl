package tpl.admin.api.authc;

import java.util.Map;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.crypto.hash.Sha512Hash;
import org.nutz.dao.Dao;
import org.nutz.ioc.annotation.InjectName;
import org.nutz.mvc.adaptor.JsonAdaptor;
import org.nutz.mvc.annotation.AdaptBy;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.POST;

import tpl.authc.DbAuthenticationInfo;

@InjectName("api.passwordModule")
public class PasswordModule {
	private Dao dao;
	
	@At("/authc/chpwd")
	@POST
	@AdaptBy(type=JsonAdaptor.class)
	public String changePassword(Map<String, String> passwords) {
		String
			oldPassword = passwords.get("oldPassword"),
			newPassword = passwords.get("newPassword");
		String uid = SecurityUtils.getSubject().getPrincipal().toString();
		DbAuthenticationInfo ai = dao.fetch(DbAuthenticationInfo.class, uid);
		//TODO Inject a hash algorithm
		Sha512Hash hash = new Sha512Hash(oldPassword, ai.getPasswordSalt(), 2000);
		if (!hash.toString().equals(ai.getLoginPassword()))
			throw new IncorrectCredentialsException("Invalid password for: " + uid);
		//TODO Inject salt length
		String salt = RandomStringUtils.random(10);
		ai.setPasswordSalt(salt);
		hash = new Sha512Hash(newPassword, ai.getPasswordSalt(), 2000);
		ai.setLoginPassword(hash.toString());
		dao.update(ai);
		return ai.getLoginName();
	}

	public void setDao(Dao dao) {
		this.dao = dao;
	}
}
