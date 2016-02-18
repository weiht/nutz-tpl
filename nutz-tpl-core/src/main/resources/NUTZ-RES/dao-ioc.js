var ioc = {
#foreach ($ds in $dataSources)
	"${ds.name}": {
		"type": "com.alibaba.druid.pool.DruidDataSource",
		"fields": {
			"url": "${ds.url}",
			"username": "${ds.user}",
			"password": "${ds.password}"
		}
	},
	"${ds.name}Dao": {
		"type": "org.nutz.dao.impl.NutDao",
		"args" : [{
			"refer":"${ds.name}"
		}]
	},
#end
};