requireConfig.paths.app = 's/admin_console/app';
requireConfig.packages.push({
	name: 'matrix',
	location: 'matrix',
	main: 'js/matrix'
});

require.config(requireConfig);

define([
    'require',
	'css!bootstrap/css/bootstrap.min.css',
	'css!bootstrap/css/bootstrap-theme.min.css',
	'css!matrix/css/styles.css',
    'angular',
    'matrix',
    'app',
    'css!lib/fontawesome/css/font-awesome.min.css'
], function (require, _, __, ___, ng) {
    'use strict';

    require(['domReady!'], function (document) {
        ng.bootstrap(document, ['app']);
    });
});
