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

});
