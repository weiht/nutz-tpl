package tpl.authc;

import org.apache.shiro.authc.SaltedAuthenticationInfo;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.apache.shiro.util.ByteSource;
import org.apache.shiro.util.SimpleByteSource;
import org.nutz.dao.entity.annotation.ColDefine;
import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Name;
import org.nutz.dao.entity.annotation.Table;

@Table(DbAuthenticationInfo.TABLE_NAME)
public class DbAuthenticationInfo
implements SaltedAuthenticationInfo {
	private static final long serialVersionUID = 1330120182013820951L;
	public static final String TABLE_NAME = "core_authentication_info";
	
	@Name @Column("login_name") @ColDefine(notNull=true, update=false, width=200)
	private String loginName;
	@Column("login_password") @ColDefine(width=200)
	private String loginPassword;
	@Column("display_name") @ColDefine(width=200)
	private String displayName;
	@Column("password_salt") @ColDefine(notNull=true, width=200)
	private String passwordSalt;
	private PrincipalCollection principals;

	@Override
	public PrincipalCollection getPrincipals() {
		if (principals == null) {
			principals = new SimplePrincipalCollection(loginName,
					displayName == null || displayName.isEmpty() ? loginName : displayName);
		}
		return principals;
	}

	@Override
	public Object getCredentials() {
		return loginPassword;
	}

	@Override
	public ByteSource getCredentialsSalt() {
		return passwordSalt == null ? null : new SimpleByteSource(passwordSalt);
	}

	public String getLoginName() {
		return loginName;
	}

	public void setLoginName(String loginName) {
		this.loginName = loginName;
		this.principals = null;
	}

	public String getLoginPassword() {
		return loginPassword;
	}

	public void setLoginPassword(String loginPassword) {
		this.loginPassword = loginPassword;
	}

	public String getPasswordSalt() {
		return passwordSalt;
	}

	public void setPasswordSalt(String passwordSalt) {
		this.passwordSalt = passwordSalt;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
		this.principals = null;
	}
}
