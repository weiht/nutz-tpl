define(['angular', 'app', 'angular/angular-resource.min'],
		function(ng, app) {

app.register.factory('admin_console.entities.service',
		['$resource', '$window',
		 function($resource, $window) {
	var basePath = ($window.contextPath || '') + '/api/admin_console';
	return $resource(
		basePath + '/entity',
		{},
		{
			page: {
				url: basePath + '/entities/page'
			},
			add: {
				method: 'POST'
			},
			update: {
				method: 'PUT'
			},
			remove: {
				url: basePath + '/entity/:name',
				method: 'DELETE'
			},
			get: {
				url: basePath + '/entity/:name',
				method: 'GET'
			}
		}
	);
}]);

});