define(['angular', 'app', '../core/prefs'], function(ng, app) {

app.register.controller('admin_console.prefs',
		['$scope', 'core.prefs.service', function($scope, PreferenceService) {
	$scope.sections = ['admin_console.prefs.edit'];
	$scope.page = {
		pageNum: 1,
		pageSize: 20
	};
	$scope.goPage = function(pn, ps, cri) {
		var page = {};
		page.pageNum = pn || $scope.page.pageNum;
		page.pageSize = ps || $scope.page.pageSize;
		page.cri = typeof cri == 'undefined' ? $scope.page.cri : cri;
		PreferenceService.page(page, function(r) {
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
		$scope.visibleSection = 'admin_console.prefs.edit';
	};
	$scope.save = function() {
		if (!$scope.editingItem) return;
		var isNew = $scope.editingItem._is_new;
		(isNew ? PreferenceService.add : PreferenceService.update)(
				{}, $scope.editingItem,
				function(r) {
			if (r.stackTrace) {
				alert(r.detailMessage);
			} else {
				$scope.goPage();
				$scope.goback();
			}
		}, function() {
			alert('保存失败。');
		});
	};
	$scope.edit = function(pref) {
		$scope.editingItem = ng.extend({}, pref);
		$scope.visibleSection = 'admin_console.prefs.edit';
	};
	$scope.remove = function(pref) {
		if (confirm('Remove preference ' + pref.key + '?')) {
			PreferenceService.remove(pref, {}, function(r) {
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