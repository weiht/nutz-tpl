define(['angular', 'app', '../core/ds'], function(ng, app) {

app.register.controller('admin_console.ds',
		['$scope', 'core.ds.manager', 'core.ds.status',
		 function($scope, DataSourceManager, DataSourceStatus) {
	$scope.sections = ['admin_console.ds.edit'];
	$scope.statuses = DataSourceStatus;
	$scope.ds = {};
	$scope.loader = DataSourceManager.all;
	$scope.load = function() {
		$scope.loader(function(r) {
			if (r.stackTrace) {
				alert(r.detailMessage || 'Unknown error.')
			} else {
				$scope.ds.result = r;
			}
		});
	};
	$scope.load();
	
	$scope.add = function() {
		$scope.editingItem = {
			status: 0,
			_is_new: true
		};
		$scope.visibleSection = 'admin_console.ds.edit';
	};
	$scope.edit = function(itm) {
		$scope.editingItem = ng.extend(itm);
		$scope.visibleSection = 'admin_console.ds.edit';
	};
	$scope.goback = function() {
		$scope.visibleSection = null;
	};
	$scope.save = function() {
		if (!$scope.editingItem) return;
		var isNew = $scope.editingItem._is_new;
		(isNew ? DataSourceManager.add : DataSourceManager.update)(
				{}, $scope.editingItem,
				function(r) {
			if (r.stackTrace) {
				alert(r.detailMessage || 'Unknown error.');
			} else {
				$scope.load();
				$scope.goback();
			}
		}, function() {
			alert('Failure saving item.');
		});
	};

	$scope.remove = function(itm) {
		if (confirm('Remove data source ' + itm.name + '?')) {
			DataSourceManager.remove(itm, {}, function(r) {
				if (r.stackTrace) {
					alert(r.detailMessage);
				} else {
					$scope.load();
				}
			});
		}
	};

	$scope.activate = function(itm) {
		DataSourceManager.activate(itm, {}, function(r) {
			if (r.stackTrace) {
				alert(r.detailMessage);
			} else {
				$scope.load();
			}
		});
	};

	$scope.deactivate = function(itm) {
		DataSourceManager.deactivate(itm, {}, function(r) {
			if (r.stackTrace) {
				alert(r.detailMessage);
			} else {
				$scope.load();
			}
		});
	};
	
	$scope.write = function() {
		DataSourceManager.writeConfig(function(r) {
			if (r && r.stackTrace) {
				alert(r.detailMessage);
			} else {
				alert('Configuration is written.')
			}
		});
	};
}]);

});
