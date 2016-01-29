requireConfig.paths.app = 's/admin_console/app';
requireConfig.paths.matrix = 'matrix/js/angular-framework';

require.config(requireConfig);

define([
    'require',
    'angular',
    'app'
], function (require, ng) {
    'use strict';

    /*
     * place operations that need to initialize prior to app start here
     * using the `run` function on the top-level module
     */

    require(['domReady!'], function (document) {
        ng.bootstrap(document, ['app']);
    });
});
