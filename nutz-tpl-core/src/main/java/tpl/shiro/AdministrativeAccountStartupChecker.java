package tpl.shiro;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.shiro.crypto.hash.Sha512Hash;
import org.nutz.dao.Dao;

import tpl.authc.AuthcConstants;
import tpl.authc.DbAuthenticationInfo;

public class AdministrativeAccountStartupChecker
implements Runnable {
	private Dao dao;
	private int saltLength = 10;
	private int hashIterations = 2000;
	
	@Override
	public void run() {
		if (createTable()) {
			createAccount();
		}
	}
	
	private boolean createTable() {
		if (!dao.exists(DbAuthenticationInfo.class)) {
			dao.create(DbAuthenticationInfo.class, false);
			return true;
		}
		return dao.fetch(DbAuthenticationInfo.class, AuthcConstants.ROOT_ACCOUNT_NAME) == null;
	}
	
	private void createAccount() {
		DbAuthenticationInfo root = new DbAuthenticationInfo();
		root.setLoginName(AuthcConstants.ROOT_ACCOUNT_NAME);
		root.setPasswordSalt(randomSalt());
		root.setDisplayName(root.getLoginName());
		root.setLoginPassword(hashPassword(root));
		dao.insert(root);
	}
	
	private String hashPassword(DbAuthenticationInfo root) {
		//TODO Configurable hash algorithm.
		return new Sha512Hash(
				AuthcConstants.DEFAULT_ACCOUNT_PASSWORD,
				root.getPasswordSalt(),
				hashIterations
			).toString();
	}

	private String randomSalt() {
		return RandomStringUtils.random(saltLength);
	}

	public void setDao(Dao dao) {
		this.dao = dao;
	}
}
