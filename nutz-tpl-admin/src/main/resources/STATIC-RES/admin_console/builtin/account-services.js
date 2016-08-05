define(['angular', 'app', 'angular/angular-resource.min'],
		function(ng, app) {

app.register.factory('admin_console.entities.builtin.account.dsservice',
		['$resource', '$window',
		 function($resource, $window) {
	var basePath = ($window.contextPath || '') + '/api/admin_console/builtin/account';
	return $resource(
		basePath + '/ds/da',
		{},
		{
			all: {
				url: basePath + '/ds/'
			},
			apply: {
				url: basePath + '/ds/',
				method: 'POST'
			},
			add: {
				method: 'POST'
			},
			remove: {
				url: basePath + '/ds/da/:name',
				method: 'DELETE'
			},
			get: {
				url: basePath + '/ds/da/:name',
				method: 'GET'
			}
		}
	);
}]);

});