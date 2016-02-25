define(['angular', 'app', 'angular/angular-resource.min'],
		function(ng, app) {

app.register.factory('admin_console.res.ressvc',
		['$resource', '$window',
		 function($resource, $window) {
	var basePath = ($window.contextPath || '') + '/api/admin_console/res';
	return $resource(
		basePath + '/da',
		{},
		{
			templates: {
				url: basePath + '/templates/:type'
			},
			add: {
				method: 'POST'
			},
			update: {
				method: 'PUT'
			},
			get: {
				method: 'GET'
			}
		}
	);
}]);

app.register.factory('admin_console.res.pagesvc',
		['$resource', '$window',
		 function($resource, $window) {
	var basePath = ($window.contextPath || '') + '/api/admin_console/pages';
	return $resource(
		basePath + '/da',
		{},
		{
			all: {
				url: basePath + '/all'
			},
			add: {
				method: 'POST'
			},
			update: {
				method: 'PUT'
			},
			remove: {
				method: 'DELETE'
			},
			get: {
				method: 'GET'
			}
		}
	);
}]);

});
