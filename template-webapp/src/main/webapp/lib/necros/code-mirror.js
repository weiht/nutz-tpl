(function(factory) {
	"use strict";
	if (typeof define === "function" && define.amd) {
		define(['angular', 'require', 'jquery', 'necros',
		        'codemirror',
		        'css!codemirror/lib/codemirror.css'], factory)
	} else {
		factory(angular)
	}
})(function(angular, require, $, mod) {

var nCodemirrorConfig = {};

var codeMirrorDef = ['$timeout', function($timeout) {
	return {
		restrict : 'A',
		require : '?ngModel',
		link : function (scope, iElement, iAttrs, ngModel) {
			var codemirrorOptions = angular.extend({
				value : iElement.text()
			}, nCodemirrorConfig.codemirror || {},
				scope.$eval(iAttrs.codeMirror),
				scope.$eval(iAttrs.codeMirrorOpts)
			);
			require(['codemirror', 'css!codemirror/lib/codemirror.css'], function(CodeMirror) {
				if (!CodeMirror.commands.save) {
					CodeMirror.commands.save = function(cm) {
						var save = cm.getOption('save');
						if (typeof save == 'function') {
							$timeout(function(cm) {
								return function() {
									save(cm);
								};
							}(cm));
						}
					};
				}
				$timeout(function() {
					var codemirror = newCodemirrorEditor(iElement,
							codemirrorOptions, ngModel.$viewValue, CodeMirror);
					configOptionsWatcher(codemirror, iAttrs.codeMirror
							|| iAttrs.codeMirrorOpts, scope, CodeMirror);
					configNgModelLink(codemirror, ngModel, scope);
					configUiRefreshAttribute(codemirror, iAttrs.uiRefresh,
							scope);
			
					scope.$on('CodeMirror', function(event, callback) {
						if (angular.isFunction(callback)) {
							callback(codemirror);
						} else {
							throw new Error(
									'the CodeMirror event requires a callback function');
						}
					});

					// onLoad callback
					if (angular.isFunction(codemirrorOptions.onLoad)) {
						codemirrorOptions.onLoad(codemirror);
					}
				});
			});
		}
	};

	function newCodemirrorEditor(iElement, codemirrorOptions, val, CodeMirror) {
		var codemirrot;
		var opts = angular.extend({
			lineWrapping: true,
			lineNumbers: true
		}, codemirrorOptions, {value: val || ''});

		if (iElement[0].tagName === 'TEXTAREA') {
			// Might bug but still ...
			codemirrot = CodeMirror.fromTextArea(iElement[0], opts);
		} else {
			iElement.html('');
			codemirrot = new CodeMirror(function(cm_el) {
				iElement.append(cm_el);
			}, opts);
		}

		return codemirrot;
	}

	function configOptionsWatcher(codemirrot, codeMirrorAttr,
			scope, CodeMirror) {
		if (!codeMirrorAttr) {
			return;
		}

		var codemirrorDefaultsKeys = Object.keys(CodeMirror.defaults);
		scope.$watch(codeMirrorAttr, updateOptions, true);

		function updateOptions(newValues, oldValue) {
			if (!angular.isObject(newValues)) {
				return;
			}
			codemirrorDefaultsKeys.forEach(function(key) {
				if (newValues.hasOwnProperty(key)) {

					if (oldValue
							&& newValues[key] === oldValue[key]) {
						return;
					}

					codemirrot.setOption(key, newValues[key]);
				}
			});
		}
	}

	function configNgModelLink(codemirror, ngModel, scope) {
		if (!ngModel) {
			return;
		}
		// CodeMirror expects a string, so make sure it gets one.
		// This does not change the model.
		ngModel.$formatters
				.push(function(value) {
					if (angular.isUndefined(value)
							|| value === null) {
						return '';
					} else if (angular.isObject(value)
							|| angular.isArray(value)) {
						throw new Error(
								'codemirror cannot use an object or an array as a model');
					}
					return value;
				});

		ngModel.$render = function() {
			var safeViewValue = ngModel.$viewValue || '';
			codemirror.setValue(safeViewValue);
		};

		codemirror.on('change', function(instance) {
			var newValue = instance.getValue();
			if (newValue !== ngModel.$viewValue) {
				scope.$evalAsync(function() {
					ngModel.$setViewValue(newValue);
				});
			}
		});
	}

	function configUiRefreshAttribute(codeMirror, uiRefreshAttr,
			scope) {
		if (!uiRefreshAttr) {
			return;
		}

		scope.$watch(uiRefreshAttr, function(newVal, oldVal) {
			// Skip the initial watch firing
			if (newVal !== oldVal) {
				$timeout(function() {
					codeMirror.refresh();
				});
			}
		});
	}
}];

mod.register.directive('codeMirror', codeMirrorDef);

return mod;


});
