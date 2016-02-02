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
	}
};