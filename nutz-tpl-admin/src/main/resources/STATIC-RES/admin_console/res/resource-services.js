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
				url: basePath + '/templates/@type'
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

});
