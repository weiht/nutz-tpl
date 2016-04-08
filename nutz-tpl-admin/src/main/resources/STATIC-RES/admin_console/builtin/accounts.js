define(['angular', 'app', 'angular/angular-resource.min',
        './account-services', 's/core/ds'],
		function(ng, app) {

app.register.controller('admin_console.builtin.accounts',
		['$scope', 'admin_console.entities.builtin.account.dsservice',
		 'core.ds.manager',
		 function($scope, AccountDsService, DsManager) {
	$scope.sections = ['admin_console.builtin.accounts.page'];
	$scope.intersects = [];
	
	function calcIntersect() {
		var edses = $scope.edses;
		if (!edses || !edses.result || (typeof edses.result.length == 'undefined')) return;
		var dslst = $scope.dslst;
		if (!dslst || !dslst.length) return;
		intersects = $scope.intersects;
		if (intersects.length) intersects.splice(0, intersects.length);
		for (var i = 0; i < dslst.length; i ++) {
			var found = false;
			var ds = dslst[i];
			for (var j = 0; j < edses.result.length; j ++) {
				if (edses.result[j].dataSourceName == ds.name) {
					found = true;
					break;
				}
			}
			if (!found) intersects.push(ds);
		}
	}
	
	$scope.edses = AccountDsService.all(calcIntersect);
	$scope.dslst = DsManager.all(calcIntersect);
	
	$scope.addDs = function(ds) {
		AccountDsService.add({}, {
			'dataSourceName': ds.name
		}, function(r) {
			if (typeof r.stackTrace != 'undefined') {
				alert(r.detailMessage || 'Unknown error.');
				return;
			}
			$scope.edses.result.push(r);
			calcIntersect();
		});
	};
	
	$scope.remove = function(eds) {
		if (confirm('Are you sure want to delete account info in data source ['
				+ eds.dataSourceName + ']?')) {
			AccountDsService.remove({name: eds.dataSourceName}, function(r) {
				if (!r) return;
				if (typeof r.stackTrace != 'undefined') {
					alert(r.detailMessage || 'Unknown error.');
					return;
				}
				for (var i = 0; i < $scope.edses.result.length; i ++) {
					var eds = $scope.edses.result[i];
					if (r.dataSourceName == eds.dataSourceName) {
						$scope.edses.result.splice(i, 1);
						calcIntersect();
						break;
					}
				}
			});
		}
	};
	
	$scope.viewAccounts = function(eds) {
		$scope.viewingEDS = eds;
		$scope.visibleSection = 'admin_console.builtin.accounts.page';
	};
	
	$scope.goback = function() {
		$scope.visibleSection = null;
	};
}]);

});
