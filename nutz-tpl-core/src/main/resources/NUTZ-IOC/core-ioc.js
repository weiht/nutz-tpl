var ioc = {
	"velocityConfig": {
		"type": "tpl.velocity.VelocityConfig",
		"fields": {
			"ioc": {"refer": "$ioc"}
		}
	},
	"groovyConfig": {
		"type": "tpl.groovy.GroovyConfig",
		"fields": {
			"ioc": {"refer": "$ioc"}
		}
	}
};