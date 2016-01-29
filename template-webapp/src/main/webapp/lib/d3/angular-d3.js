define([
        'angular',
    	'd3'
    ], function(angular, d3) {

var mod = angular.module('angular-d3', []);

mod.service('d3', function() {
	return d3;
});

mod.directive('d3RadialProgress', ['$timeout', function($timeout) {
	var defaults = {};
	return {
		scope: {
			config: '=d3RadialProgress'
		},
		link: function(scope, element, attrs) {
			var config = angular.extend({}, defaults, scope.config);
			if (typeof config.onclick == 'function') {
				config.onclick = function(onclick) {
					return function() {
						$timeout(function() {
							onclick();
						});
					};
				}(config.onclick);
			}
			element.toggleClass('d3RadialProgress', true);
			var progress;
			require(['d3/radial/progress', 'css!d3/radial/styles.css'], function(radialProgress) {
				progress = radialProgress(element[0], config).render();
			});
			scope.$watch('config.value', function(nv, ov) {
				if (!progress) return;
				if (typeof nv == 'undefined') return;
				progress.value(nv).render();
			});
		}
	};
}]);

mod.directive('d3BarChart', ['$timeout', function($timeout) {
	var defaults = {};
	return {
		scope: {
			config: '=d3BarChart'
		},
		link: function(scope, element, attrs) {
			var config = angular.extend({}, defaults, scope.config);
			element.toggleClass('d3BarChart', true);
			var bar;
			require(['d3/bar/barchart', 'css!d3/bar/styles.css'], function(barchart) {
				bar = barchart(element[0], config).render();
			});
			scope.$watch('config.data', function(nv, ov) {
				if (!bar) return;
				if (typeof nv == 'undefined') return;
				bar.data(nv).render();
			}, true);
		}
	};
}]);

mod.directive('d3LineChart', ['$timeout', function($timeout) {
	var defaults = {};
	return {
		scope: {
			config: '=d3LineChart'
		},
		link: function(scope, element, attrs) {
			var config = angular.extend({}, defaults, scope.config);
			element.toggleClass('d3LineChart', true);
			var line;
			require(['d3/line/linechart', 'css!d3/line/styles.css'], function(linechart) {
				line = linechart(element[0], config).render();
			});
			scope.$watch('config.data', function(nv, ov) {
				if (!line) return;
				if (typeof nv == 'undefined') return;
				line.data(nv).render();
			}, true);
		}
	};
}]);

});