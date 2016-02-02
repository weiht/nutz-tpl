define(['angular', 'app', 'angular/angular-resource.min'], function(ng, app) {

app.register.service('core.prefs.service',
		['$resource', '$window', function($resource, $window) {
	var basePath = ($window.contextPath || '') + '/api/admin_console';
	return $resource(
		basePath + '/pref',
		{},
		{
			page: {
				url: basePath + '/prefs/page'
			},
			add: {
				method: 'POST'
			},
			update: {
				method: 'PUT'
			},
			remove: {
				url: basePath + '/pref/:key',
				method: 'DELETE'
			},
			get: {
				url: basePath + '/pref/:key',
				method: 'GET'
			}
		}
	);
}]);

return app;

});
