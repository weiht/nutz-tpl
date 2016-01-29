define([
	'angular',
	'matrix'
], function(ng) {

var app = ng.module('app', []);

app.config(['$controllerProvider', 
            '$compileProvider', '$filterProvider', '$provide',
            function ($controllerProvider, 
            		$compileProvider, $filterProvider, $provide) {
	app.register = {
		controller: $controllerProvider.register,
		directive: $compileProvider.directive,
		filter: $filterProvider.register,
		factory: $provide.factory,
		service: $provide.service
	};
}]);

app.run(['$rootScope', '$window', function($root, $window) {
}]);

return app;

});
