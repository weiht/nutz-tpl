package tpl.shiro.velocity;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.util.Collection;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.Permission;
import org.apache.shiro.subject.Subject;
import org.apache.velocity.tools.Scope;
import org.apache.velocity.tools.config.DefaultKey;
import org.apache.velocity.tools.config.ValidScope;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@DefaultKey("shiro")
@ValidScope(Scope.APPLICATION)
public class ShiroTool {

	private static final String ROLE_NAMES_DELIMETER = ",";
	private static final String PERMISSION_NAMES_DELIMETER = ",";

	private static final Logger logger = LoggerFactory.getLogger(Permission.class);

	public boolean isAuthenticated() {
		Subject subject = SecurityUtils.getSubject();
		return subject != null && subject.isAuthenticated() == true;
	}

	public boolean isNotAuthenticated() {
		Subject subject = SecurityUtils.getSubject();
		return subject == null || subject.isAuthenticated() == false;
	}

	public boolean isGuest() {
		Subject subject = SecurityUtils.getSubject();
		return subject == null || subject.getPrincipal() == null;
	}

	public boolean isUser() {
		Subject subject = SecurityUtils.getSubject();
		return subject != null && subject.getPrincipal() != null;
	}

	public Object getPrincipal() {
		Subject subject = SecurityUtils.getSubject();
		return subject != null ? subject.getPrincipal() : null;
	}

	public Object getPrincipalProperty(String property) {
		Subject subject = SecurityUtils.getSubject();

		if (subject != null) {
			Object principal = subject.getPrincipal();

			try {
				BeanInfo bi = Introspector.getBeanInfo(principal.getClass());

				for (PropertyDescriptor pd : bi.getPropertyDescriptors()) {
					if (pd.getName().equals(property) == true) {
						return pd.getReadMethod().invoke(principal, (Object[]) null);
					}
				}

				logger.trace("Property [{}] not found in principal of type [{}]", property,
						principal.getClass().getName());
			} catch (Exception e) {
				logger.trace("Error reading property [{}] from principal of type [{}]", property,
						principal.getClass().getName());
			}
		}

		return null;
	}

	public boolean hasRole(String role) {
		Subject subject = SecurityUtils.getSubject();
		return subject != null && subject.hasRole(role) == true;
	}

	public boolean lacksRole(String role) {
		return hasRole(role) != true;
	}

	public boolean hasAnyRoles(String roleNames, String delimeter) {
		Subject subject = SecurityUtils.getSubject();
		if (subject != null) {
			if (delimeter == null || delimeter.length() == 0) {
				delimeter = ROLE_NAMES_DELIMETER;
			}

			for (String role : roleNames.split(delimeter)) {
				if (subject.hasRole(role.trim()) == true) {
					return true;
				}
			}
		}

		return false;
	}

	public boolean hasAnyRoles(String roleNames) {
		return hasAnyRoles(roleNames, ROLE_NAMES_DELIMETER);
	}

	public boolean hasAnyRoles(Collection<String> roleNames) {
		Subject subject = SecurityUtils.getSubject();

		if (subject != null && roleNames != null) {
			for (String role : roleNames) {
				if (role != null && subject.hasRole(role.trim()) == true) {
					return true;
				}
			}
		}

		return false;
	}

	public boolean hasAnyRoles(String[] roleNames) {
		Subject subject = SecurityUtils.getSubject();

		if (subject != null && roleNames != null) {
			for (int i = 0; i < roleNames.length; i++) {
				String role = roleNames[i];
				if (role != null && subject.hasRole(role.trim()) == true) {
					return true;
				}
			}
		}

		return false;
	}

	public boolean hasPermission(String permission) {
		Subject subject = SecurityUtils.getSubject();
		return subject != null && subject.isPermitted(permission);
	}

	public boolean lacksPermission(String permission) {
		return hasPermission(permission) != true;
	}

	public boolean hasAnyPermissions(String permissions, String delimeter) {
		Subject subject = SecurityUtils.getSubject();

		if (subject != null) {
			if (delimeter == null || delimeter.length() == 0) {
				delimeter = PERMISSION_NAMES_DELIMETER;
			}

			for (String permission : permissions.split(delimeter)) {
				if (permission != null && subject.isPermitted(permission.trim()) == true) {
					return true;
				}
			}
		}

		return false;
	}

	public boolean hasAnyPermissions(String permissions) {
		return hasAnyPermissions(permissions, PERMISSION_NAMES_DELIMETER);
	}

	public boolean hasAnyPermissions(Collection<String> permissions) {
		Subject subject = SecurityUtils.getSubject();

		if (subject != null && permissions != null) {
			for (String permission : permissions) {
				if (permission != null && subject.isPermitted(permission.trim()) == true) {
					return true;
				}
			}
		}

		return false;
	}

	public boolean hasAnyPermissions(String[] permissions) {
		Subject subject = SecurityUtils.getSubject();

		if (subject != null && permissions != null) {
			for (int i = 0; i < permissions.length; i++) {
				String permission = permissions[i];
				if (permission != null && subject.isPermitted(permission.trim()) == true) {
					return true;
				}
			}
		}

		return false;
	}
}
