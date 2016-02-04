var ioc = {
	"shiroAdminFilter": {
		"type": "tpl.shiro.ShiroFilterImpl",
		"fields": {
			"securityManager": {
				"refer": "shiroAdminSecurityManager"
			},
			"loginUrl": {
				"java": "$coreConfig.get('admin_console.shiro.login_url')"
			},
			"successUrl": {
				"java": "$coreConfig.get('admin_console.shiro.success_url')"
			},
			"filters": {
				"authc": {
					"refer": "shiroAdminFormAuthenticationFilter"
				},
				"logout": {
					"refer": "shiroAdminLogoutFilter"
				}
			},
			"chainDefinitions": {
				"java": "$coreConfig.get('admin_console.shiro.filter_chain').split('\\n')"
			}
		}
	},
	"shiroAdminFormAuthenticationFilter": {
		"type": "org.apache.shiro.web.filter.authc.FormAuthenticationFilter"
	},
	"shiroAdminLogoutFilter": {
		"type": "org.apache.shiro.web.filter.authc.LogoutFilter",
		"fields": {
			"redirectUrl": {
				"java": "$coreConfig.get('admin_console.shiro.logout_url')"
			},
		}
	}
	"shiroAdminAuthzRealm": {
		"type": "tpl.shiro.NutzDaoRealm",
		"fields": {
			"dao": {
				"refer": "coreDao"
			},
			"credentialsMatcher": {
				"type": "org.apache.shiro.authc.credential.HashedCredentialsMatcher",
				"fields": {
					"hashAlgorithm": "SHA-512",
					"hashIterations": 2000
				}
			}
		}
	},
	"shiroAdminSecurityManager": {
		"type": "org.apache.shiro.web.mgt.DefaultWebSecurityManager",
		"fields": {
			"realm": {
				"refer": "shiroAdminAuthzRealm"
			}
		}
	}
};