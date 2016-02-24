(function(factory) {
	"use strict";
	if (typeof define === "function" && define.amd) {
		define(['angular', 'require', 'jquery', 'necros',
		        'jquery/etc/jquery.gritter',
		        'css!jquery/etc/jquery.gritter.css'], factory)
	} else {
		factory(angular)
	}
})(function(angular, require, $, mod) {

var gritterConfig = {
	time: 3000,
	sticky: false
};

var Gritters = {
	class_name: ''
};

Gritters.combineClass = function(class_name) {
	var cn = class_name + ' ' + this.class_name;
	return angular.extend(this, {class_name: cn});
};

Gritters.add = function(conf) {
	conf = conf || {};
	conf.class_name = (conf.class_name || '') + ' ' + this.class_name;
	angular.element.gritter.add(angular.extend(gritterConfig, conf));
};

Gritters.error = function(conf) {
	Gritters.add(angular.extend(conf, {class_name: 'gritter-error'}));
};

Gritters.info = function(conf) {
	Gritters.add(angular.extend(conf, {class_name: 'gritter-info'}));
};

Gritters.warn = function(conf) {
	Gritters.add(angular.extend(conf, {class_name: 'gritter-warning'}));
};

Gritters.success = function(conf) {
	Gritters.add(angular.extend(conf, {class_name: 'gritter-success'}));
};

mod.register.service('gritterService', [function() {
	return {
		'Gritters': Gritters,
		'Light': function(gritters) {
			return (gritters || Gritters).combineClass('gritter-light');
		},
		'Centered': function(gritters) {
			return (gritters || Gritters).combineClass('gritter-center');
		}
	};
}]);

return mod;

});
