var ioc = {
	"api.preferenceModule": {
		"type": "tpl.admin.api.prefs.PreferenceModule",
		"fields": {
			"dao": {
				"refer": "coreDao"
			}
		}
	},
	"api.preferencesModule": {
		"type": "tpl.admin.api.prefs.PreferencesModule",
		"fields": {
			"dao": {
				"refer": "coreDao"
			}
		}
	},
	"api.passwordModule": {
		"type": "tpl.admin.api.authc.PasswordModule",
		"fields": {
			"dao": {
				"refer": "coreDao"
			}
		}
	},
	"api.resourceModule": {
		"type": "tpl.admin.api.res.ResourceModule",
		"fields": {
			
		}
	},
	"api.res.pageModule": {
		"type": "tpl.admin.api.res.PageModule",
		"fields": {
			"resourceUtil": {
				"refer": "resourceUtil"
			}
		}
	},
	"api.dataSourceModule": {
		"type": "tpl.admin.api.ds.DataSourceModule",
		"fields": {
			"manager": {
				"refer": "coreDataSourceManager"
			},
			"dao": {
				"refer": "coreDao"
			}
		}
	},
	"resourceUtil": {
		"type": "tpl.admin.api.res.ResourceUtil",
		"fields": {
			"velocityConfig": {
				"refer": "velocityConfig"
			},
			"groovyConfig": {
				"refer": "groovyConfig"
			},
			"ioc": {
				"refer": "$ioc"
			}
		}
	}
};