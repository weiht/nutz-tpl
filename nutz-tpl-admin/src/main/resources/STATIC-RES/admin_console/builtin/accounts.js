define(['angular', 'app', 'angular/angular-resource.min', './account-services'],
		function(ng, app) {

app.register.controller('admin_console.builtin.accounts',
		['$scope', 'admin_console.entities.builtin.account.dsservice',
		 function($scope, AccountDsService) {
	$scope.sections = ['admin_console.builtin.accounts.page'];
	$scope.edses = AccountDsService.all();
}]);

});
