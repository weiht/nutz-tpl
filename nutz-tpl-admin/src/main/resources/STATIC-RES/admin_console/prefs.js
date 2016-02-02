define(['angular', 'app'], function(ng, app) {

app.register.controller('admin_console.prefs',
		['$scope', function($scope) {
	$scope.sections = ['admin_console.prefs.edit'];
	
}]);

});