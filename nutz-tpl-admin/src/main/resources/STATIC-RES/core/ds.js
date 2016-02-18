define(['angular', 'app', 'angular/angular-resource.min'], function(ng, app) {

app.register.service('core.ds.manager',
		['$resource', '$window', function($resource, $window) {
	var basePath = ($window.contextPath || '') + '/api/admin_console/ds';
	return $resource(
		basePath,
		{},
		{
			all: {
				isArray: true
			},
			active: {
				isArray: true,
				url: basePath + '/active'
			},
			inactive: {
				isArray: true,
				url: basePath + '/inactive'
			},
			writeConfig: {
				method: 'POST'
			},
			add: {
				url: basePath + '/da',
				method: 'POST'
			},
			update: {
				url: basePath + '/da',
				method: 'PUT'
			},
			remove: {
				url: basePath + '/da/:name',
				method: 'DELETE'
			},
			get: {
				url: basePath + '/da/:name',
				method: 'GET'
			},
			activate: {
				url: basePath + '/act/:name',
				method: 'POST'
			},
			deactivate: {
				url: basePath + '/act/:name',
				method: 'DELETE'
			}
		}
	);
}]);

app.register.service('core.ds.status', [function() {
	return {
		isActive: function(itm) {
			return !!itm.status;
		}
	};
}]);

app.register.filter('dataSourceStatusText', [function() {
	return function(st) {
		//TODO Acquire status texts
		return st == 0 ? 'inactive' : 'active'
	};
}]);

return app;

});
