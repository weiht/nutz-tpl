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
	}
};