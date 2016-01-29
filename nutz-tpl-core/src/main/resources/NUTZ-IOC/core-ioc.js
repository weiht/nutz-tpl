var ioc = {
	"velocityConfig": {
		"type": "nrt.jetty.web.VelocityConfig",
		"fields": {
			"ioc": {"refer": "$ioc"}
		}
	},
	"groovyConfig": {
		"type": "nrt.jetty.web.GroovyConfig",
		"fields": {
			"ioc": {"refer": "$ioc"}
		}
	}
};