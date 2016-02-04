(function(factory) {
	"use strict";
	if (typeof define === "function" && define.amd) {
		define(['angular', 'require', 'jquery', 'bootstrap',
		        'css!./necros.css'], factory)
	} else {
		factory(angular)
	}
})(function(angular, require, $) {

var mod = angular.module('necros', []);

var layoutDefaults = {
	'defaults': {
		'resizable': false,
		'closable': false,
		'slidable': false,
		'enableCursorHotkey': false,
		'spacing_open': 0,
		'spacing_closed': 0,
		'togglerLength_open': 0,
		'togglerLength_closed': 0
	}
};

mod.directive('nLayout', ['$timeout', '$rootScope', function($timeout, $root) {
	return {
		'restrict': 'A',
		'scope': {
			config: '=nLayout'
		},
		'link': function(scope, element, attrs) {
			var conf = scope.config;
			element.toggleClass('fill', true);

			function doDirective() {
				$timeout(function() {
					var lc = angular.extend({}, conf || {});
					lc.defaults = angular.extend({}, layoutDefaults.defaults, lc.defaults);
					element.layout(lc);
				}, 50);
			}
			
			if (typeof require == 'function') {
				require(['jquery/etc/jquery.layout-1.4.0'], doDirective);
			} else {
				doDirective();
			}
		}
	};
}]);

function isInLayout(el) {
	return el.parents('.ui-layout-container').size();
}

//Section (can be used as part of a page, or a modal dialog body)

var sections = [];
var sectionMap = {};
var sectionDef = [function() {
	return sectionMap;
}];

function addSection(sec) {
	//TODO prevent duplications
	sections.push(sec);
	sectionMap[sec.name] = sec;
}

var nSectionsDef = [function() {
	this.add = addSection;
	this.$get = sectionDef;
}];

mod.provider('nSections', nSectionsDef);
mod.provider('sections', nSectionsDef);
mod.service('sectionConfigService', function() {
	return {
		addSection: addSection
	}
});

var nSectionDef = ['$injector', '$interpolate', '$http', '$compile', '$controller', '$templateCache', '$timeout',
                 function($injector, $interpolate, $http, $compile, $controller, $templateCache, $timeout) {
	return {
		restrict: 'A',
	    terminal: true,
	    priority: -400,
	    scope: true,
		compile: function(cElement, cAttrs) {
			function showTemplate(template, scope, element, attrs, sec) {
				element.html(template);
				if (sec.controller) {
					var $scope = scope.$new();
					$controller(sec.controller, {$scope: $scope});
					$compile(element.contents())($scope);
					//TODO Find a chance to destroy this $scope.
				} else {
					$compile(element.contents())(scope);
				}
			}
			
			function render(scope, element, attrs, sec) {
				if (sec.template) {
					showTemplate(sec.template, scope, element, attrs, sec);
				} else if (sec.templateUrl) {
					$http.get(sec.templateUrl, {cache: $templateCache})
						.success(function(template) {
							sec.template = template;
							showTemplate(template, scope, element, attrs, sec);
						});
				}
			}
			
			return function(scope, element, attrs) {
				attrs.$observe('nSection', function() {
					var sec = sectionMap[attrs.nSection];
					if (!sec) {
						try {
							sec = scope.$eval(sec);
						} catch (e) {}
					}
					if (!sec) return;
					if (!sec.__rendered && sec.requireModule && typeof window.require == 'function') {
						require(sec.requireModule.split(/[\,,\;,\s]/), function($scope, $element, $attrs, $sec) {
							return function() {
								$timeout(function() {
									render($scope, $element, $attrs, $sec);
									$sec.__rendered = true;
								});
							};
						}(scope, element, attrs, sec));
					} else {
						render(scope, element, attrs, sec);
						sec.__rendered = true;
					}
				});
			}
		}
	};
}];

mod.directive('nSection', nSectionDef);
mod.directive('section', nSectionDef);

// End of section

//Bootstrap modal (dialog)

mod.service('modalService', ['$sce', function($sce) {
	var dialogs = [];
	function isVisible() {
		for (var i = 0; i < dialogs.length; i ++) {
			if (!!dialogs[i].visible) return true;
		}
		return false;
	}
	function addDialog(modalDialog, scope) {
		var dlg = modalDialog, found = null;
		if (scope) dlg.__scope = scope;
		var dlgs = dialogs;
		for (var i = 0; i < dlgs.length; i ++) {
			if (dlg == dlgs[i]) {
				found = dlg;
				break;
			}
		}
		if (!found) dialogs.push(dlg);
	}
	return {
		'modalDialogs': dialogs,
		'addDialog': addDialog,
		'visible': isVisible
	};
}]);

mod.controller('bs.modal.ctrl', ['$scope', '$rootScope', function($scope, $root) {
	$scope.$watch('modal.visible', function(nv, ov) {
		if (!$scope.modal) return;
		if (!$scope.modal.__element) return;
		if (nv === true) {
			$root.$broadcast('shown.bs.modal', $scope.modal);
		} else if (nv === false) {
			$root.$broadcast('hidden.bs.modal', $scope.modal);
		}
	});
	
	$scope.watchModal = function(modal) {
		$scope.modal = modal;
	};
}]);

mod.service('dialogService', ['$sce', 'modalService', function($sce, modalService) {
	var addDialog = modalService.addDialog;
	var alertTemplate = '<table><tr><td align="center" class="icon-big-edit">&nbsp;</td><td valign="middle" id="alertMessage">{{msg}}</td></tr></table>';
	var alertDialog = {
		'dialogClass': 'framework-dialog alert-dialog',
		'iconClass': 'fa fa-exclamation-circle',
		'title': '警告',
		'viewHtml': alertTemplate,
		'buttons': [{
			'buttonClass': 'btn-default',
			'iconClass': 'fa fa-close',
			'text': '关闭',
			'onclick': function() {
				alertDialog.visible = false;
				if (typeof alertDialog.callback == 'function') {
					alertDialog.callback();
				}
			}
		}]
	};
	var confirmTemplate = '<table><tr><td align="center" class="icon-big-edit">&nbsp;</td><td valign="middle" id="confirmMessage">{{msg}}</td></tr></table>';
	var confirmDialog = {
		'dialogClass': 'framework-dialog confirm-dialog',
		'iconClass': 'fa fa-question-circle',
		'title': '确认',
		'viewHtml': confirmTemplate,
		'buttons': [{
			'buttonClass': 'btn-primary',
			'iconClass': 'fa fa-check',
			'text': '确定',
			'onclick': function() {
				confirmDialog.visible = false;
				if (typeof confirmDialog.okCallback == 'function') {
					confirmDialog.okCallback(true);
				}
			}
		}, {
			'buttonClass': 'btn-default',
			'text': '取消',
			'onclick': function() {
				confirmDialog.visible = false;
				if (typeof confirmDialog.cancelCallback == 'function') {
					confirmDialog.cancelCallback(false);
				} else if (typeof confirmDialog.okCallback == 'function') {
					confirmDialog.okCallback(false);
				}
			}
		}]
	};
	function alert(title, msg, callback) {
		addDialog(alertDialog);
		angular.extend(alertDialog, {
			'title': title,
			'viewHtml': $sce.trustAsHtml(alertTemplate.replace(/\{\{\msg}\}/, msg)),
			'callback': callback,
			'visible': true
		});
	}
	function confirm(title, msg, okCallback, cancelCallback) {
		addDialog(confirmDialog);
		angular.extend(confirmDialog, {
			'title': title,
			'viewHtml': $sce.trustAsHtml(confirmTemplate.replace(/\{\{msg\}\}/, msg)),
			'okCallback': okCallback,
			'cancelCallback': cancelCallback,
			'visible': true
		});
	}
	return {
		'alert': alert,
		'confirm': confirm
	};
}]);

mod.directive('nModalPlaceholder', ['modalService', function(svc, section) {
	return {
		'restrict': 'A',
		'template': '<div ng-repeat="dlg in modals" bs-modal="dlg"></div><div class="modal-backdrop in" ng-if="modalService.visible()"></div>',
		'link': function(scope, element, attr) {
			scope.modals = svc.modalDialogs;
			scope.modalService = svc;
		}
	};
}]);

var modalTemplate = {
	showTemplate: function(template, scope, element, $compile) {
		element.html(template);
		$compile(element.contents())(scope);
	}
};

var modalDef = ['modalService', '$window', '$compile', '$http', '$templateCache', function(svc, $window, $compile, $http, $templateCache) {
	return {
		'restrict': 'A',
		'link': function(scope, element, attr) {
			var modal = scope.$eval(attr.bsModal);
			modal.__element = element;
			var $scope = (modal.__scope || scope).$new();
			$scope.modal = modal;
			var template = modal.overlay ? 'overlay' : 'modal';
			if (modalTemplate[template]) {
				modalTemplate.showTemplate(modalTemplate[template], $scope, element, $compile);
			} else {
				$http.get(($window.contextPath  || '') + '/lib/necros/n-' + template + '.htm', {cache: $templateCache})
					.success(function(tpl) {
						modalTemplate[template] = tpl;
						modalTemplate.showTemplate(tpl, $scope, element, $compile);
					});
			}
			$compile(element.contents())($scope);
		}
	};
}];

mod.directive('bsModal', modalDef);
mod.directive('nModal', ['modalService', function(svc) {
	return {
		'restrict': 'A',
		'link': function(scope, element, attr) {
			var modal = scope.$eval(attr.nModal);
			if (!modal) return;
			if (attr.title) modal.title = attr.title;
			if (attr.buttons) modal.buttons = scope.$eval(attr.buttons);
			if (!modal) return;
			modal.__scope = scope;
			if (modal) {
				svc.addDialog(modal);
			}
		}
	}
}]);

function isInModal(el) {
	function im(e) {
		if (e.length == 0 || e[0] == document) return null;
		if (e.hasClass('modal')) {
			return e;
		}
		return im(e.parent());
	}
	var m = el.data('__modal');
	if (typeof m == 'undefined') {
		m = im(el);
		el.data('__modal', m);
		return m;
	} else {
		return m;
	}
}

function modalIsDirective(modal) {
	var pm = modal.parent();
	return pm.attr('bs-modal') || pm.attr('bsModal');
}

// End of bootstrap modal

//Bootstrap pagination

mod.filter('pageList', function() {
	return function(pagination) {
		if (!pagination) return;
		if (!pagination.listFirst || !pagination.listLast) return;
		var r = [];
		for (var i = pagination.listFirst; i <= pagination.listLast; i ++) {
			r.push(i);
		}
		return r;
	};
});

mod.directive('nPagination', ['$window', function($window) {
	return {
		'restrict': 'A',
		'templateUrl': ($window.contextPath || '') + '/lib/necros/n-pagination.htm',
		'scope': {
			'config': '=nPagination',
			'goPage': '=goPage',
			'pagination': '=paginationData'
		},
		'link': function(scope, element, attr) {
			scope.$watch('config.pageSize', function(nv, ov) {
				if (nv == ov) return;
				if (!nv) return;
				if (typeof scope.goPage == 'function') {
					scope.goPage(1, nv);
				}
			});
		}
	};
}]);

// End of bootstrap pagination

//Form validation
mod.directive('validIf', [function() {
	return {
		restrict: 'A',
		scope: {
			expr: '=validIf'
		},
		require: 'ngModel',
		link: function(scope, element, attrs, ngModel) {
			console.log(ngModel)
			scope.$watch('expr', function(nv, ov) {
				ngModel.$setValidity(attrs.validIf, !!nv);
			});
		}
	};
}]);
//End of form validation

return mod;
});
