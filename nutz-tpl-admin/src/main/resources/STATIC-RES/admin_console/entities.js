define(['angular', 'app', './entities/services'], function(ng, app) {

app.register.controller('admin_console.entities',
		['$scope', 'admin_console.entities.service', function($scope, EntityService) {
	$scope.sections = ['admin_console.entities.edit'];
	$scope.page = {
		pageNum: 1,
		pageSize: 20
	};
	$scope.goPage = function(pn, ps, cri) {
		var page = {};
		page.pageNum = pn || $scope.page.pageNum;
		page.pageSize = ps || $scope.page.pageSize;
		page.cri = typeof cri == 'undefined' ? $scope.page.cri : cri;
		EntityService.page(page, function(r) {
			if (r.stackTrace) {
				alert(r.detailMessage || 'Unknown error.')
			} else {
				$scope.page = r;
			}
		});
	};
	$scope.goPage();
	$scope.criKeyPressed = function(e, cri) {
		if (e.key == 'Enter') {
			$scope.goPage(1, $scope.page.pageSize, cri);
		}
	};
	$scope.goback = function() {
		$scope.visibleSection = null;
	};
	$scope.add = function() {
		$scope.editingItem = {
			_is_new: true
		};
		$scope.visibleSection = 'admin_console.entities.edit';
	};
	$scope.save = function() {
		if (!$scope.editingItem) return;
		var isNew = $scope.editingItem._is_new;
		(isNew ? EntityService.add : EntityService.update)(
				{}, $scope.editingItem,
				function(r) {
			if (r.stackTrace) {
				alert(r.detailMessage);
			} else {
				$scope.goPage();
				$scope.goback();
			}
		}, function() {
			alert('Failure saving item.');
		});
	};
	$scope.edit = function(entity) {
		$scope.editingItem = ng.extend({}, entity);
		$scope.visibleSection = 'admin_console.entities.edit';
	};
	$scope.remove = function(entity) {
		if (confirm('Remove entity ' + entity.name + '?')) {
			EntityService.remove(entity, {}, function(r) {
				if (r.stackTrace) {
					alert(r.detailMessage);
				} else {
					$scope.goPage();
				}
			});
		}
	};
}]);

});