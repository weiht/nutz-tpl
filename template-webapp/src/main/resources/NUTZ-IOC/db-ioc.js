var ioc = {
	"coreDataSource": {
		"type": "com.alibaba.druid.pool.DruidDataSource",
		"fields": {
			"url": {
				"java": "$coreConfig.get('jdbc.url')"
			},
			"username": {
				"java": "$coreConfig.get('jdbc.user')"
			},
			"password": {
				"java": "$coreConfig.get('jdbc.password')"
			}
		}
	},
	"coreDao": {
		"type": "org.nutz.dao.impl.NutDao",
		"args" : [{
			"refer":"coreDataSource"
		}]
	}
};