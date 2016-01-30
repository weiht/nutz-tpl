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

});
