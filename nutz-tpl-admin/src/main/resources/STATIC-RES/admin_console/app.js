define([
	'angular',
	'admin_console/menu.js.html#',
	'admin_console/sections.js.html#',
    'necros',
	'matrix',
	'angular/angular-resource.min'
], function(ng, mainMenu, allSections) {

var app = ng.module('app', ['necros', 'ngResource']);

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

var homeSection;
var homeSectionName = 'admin_console.home';

function addMenuToSections(items, parent, sections) {
	for (var i = 0; i < items.length; i ++) {
		var itm = items[i];
		if (itm.name == homeSectionName) homeSection = itm;
		if (itm.children) {
			addMenuToSections(itm.children, itm, sections);
		}
		if (parent) {
			itm.parent = parent;
			itm.path = [];
			if (parent && parent.path && parent.path.length) {
				for (var j = 0; j < parent.path.length; j ++) {
					itm.path.push(parent.path[j]);
				}
			}
		}
		if (itm.templateUrl) {
			sections.add(itm);
		}
	}
}

app.sections = allSections;

app.config(['nSectionsProvider', function(sections) {
	addMenuToSections(mainMenu, null, sections);
	for (var i = 0; i < allSections.length; i ++) {
		sections.add(allSections[i]);
	}
}]);

app.run(['$rootScope', '$timeout', function($root, $timeout) {
	$root.workbenchSection = 'admin_console.home';
	$root.breadcrumb = [homeSection];
	$root.mainMenu = mainMenu;
	$root.homeSection = homeSection;
	$root.openView = function(sec, path) {
		$root.breadcrumb.splice(1, $root.breadcrumb.length - 1);
		$root.workbenchSection = sec.name;
		if (path) {
			for (var i = 0; i < path.length; i ++) {
				var p = path[i];
				if (p != homeSection && p.name != homeSection.name)
					$root.breadcrumb.push(p);
			}
		} else {
			if (sec != homeSection && sec.name != homeSection.name)
				$root.breadcrumb.push(sec);
		}
	};
	$timeout(function() {
		var mnu = mainMenu[0];
		$root.openView(mnu, mnu.path);
		$('#sidebar-accordion>.panel:first-child').toggleClass('active', true);
	}, 100);
}]);

return app;

});
