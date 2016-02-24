define(['angular', 'app', 'necros/gritters',
        './resource-services'], function(ng, app) {

app.register.filter('pagedefListFilter', function() {
	return function(pages, f) {
		if (!f) return pages;
		var r = [];
		for (var i = 0; i < pages.length; i ++) {
			var p = pages[i];
			if (p.requestUri.indexOf(f) >= 0) {
				r.push(p);
			}
		}
		return r;
	}
});

function pagesSorter(me, you) {
	return me.requestUri > you.requestUri ? 1 : -1;
}

function isNothing(obj) {
	var t = typeof obj;
	return t == 'undefined' || obj == null;
}

app.register.controller('admin_console.res.pages',
		['$scope', 'admin_console.res.ressvc', 'gritterService',
		 function($scope, ResourceService, gritterService) {
	var gritters = gritterService.Light(gritterService.Gritters);
	$scope.sections =
		[
			'admin_console.res.pages.addpage',
			'admin_console.res.pages.editpage',
			'admin_console.res.pages.addscript',
			'admin_console.res.pages.editscript'
		];
	$scope.isNothing = isNothing;
	
	$scope.pages = {result: []};
	
	$scope.addPage = function(page) {
		$scope.editingItem = {
			path: (page ? page.requestUri : null)
		};
		$scope.visibleSection = 'admin_console.res.pages.addpage';
	};
	
	function exists(url, prop) {
		var lst = $scope.pages.result;
		if (!lst || !lst.length) return;
		for (var i = 0; i < lst.length; i ++) {
			var f = lst[i];
			if (f[prop] == url) return f;
		}
	}
	
	function saveNewPage() {
		var itm = $scope.editingItem;
		var url = $scope.editingItem.path;
		var found = exists(url, 'pageUrl');
		if (found) {
			gritters.error({text: 'This page already exists.'});
			return;
		}
		var tpl = $scope.editingItem.template;
		if (!tpl) {
			gritters.error({text: 'Please choose a template.'});
			return;
		}
		ResourceService.add({url: itm.path, templateName: tpl.path}, {}, function(p) {
			return function(r) {
				if (typeof r.exception !== 'undefined') {
					gritters.error({text: 'Failure when saving: ' + (r.exception || 'Unknown error.')});
				} else {
					var page = exists(p, 'requestUri');
					if (page) {
						page.pageUrl = p;
					} else {
						page = {
							requestUri: p,
							pageUrl: p
						};
						$scope.pages.result.push(page);
						$scope.pages.result.sort(pagesSorter);
					}
					addPageDialog.visible = false;
					editPage(page);
				}
			};
		}(itm.path));
	}
	$scope.saveNewPage = saveNewPage;
}]);

});
