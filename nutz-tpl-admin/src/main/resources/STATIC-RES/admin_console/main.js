requireConfig.paths.app = 's/admin_console/app';
requireConfig.packages.push({
	name: 'matrix',
	location: 'matrix',
	main: 'js/matrix'
});

require.config(requireConfig);

define([
    'require',
	'css!matrix/css/styles.css',
    'angular',
    'app'
], function (require, __, ng) {
    'use strict';

    require(['domReady!'], function (document) {
        ng.bootstrap(document, ['app']);
    });
});
