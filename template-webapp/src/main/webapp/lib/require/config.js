// require.baseUrl is required before using this configuration.
window.requireConfig = {
    paths: {
        'domReady': 'lib/require/domReady'
    },
    
    waitSeconds: 0,
    
    packages: [{
    	name: 'jquery',
    	location: 'lib/jquery',
    	main: 'jquery-2.2.0.min'
    }, {
    	name: 'bootstrap',
    	location: 'lib/bootstrap',
    	main: 'js/bootstrap.min'
    }, {
    	name: 'codemirror',
    	location: 'lib/codemirror',
    	main: 'lib/codemirror'
    }, {
    	name: 'd3',
    	location: 'lib/d3',
    	main: 'd3.min'
    }, {
    	name: 'ztree',
    	location: 'lib/ztree',
    	main: 'jquery.ztree.all-3.5.min'
    }],
    
    map: {
    	'*': {
    		css: 'lib/js/require/css'
    	}
    },
    
    /**
	 * for libs that either do not support AMD out of the box, or require some
	 * fine tuning to dependency mgt'
	 */
    shim: {
        'jquery': {
            exports: 'jQuery'
        },
        'bootstrap': {
            deps: ['jquery']
        },
        'angular': {
            exports: 'angular',
            deps: ['jquery']
        },
        'angular-route': {
            deps: ['angular']
        },
        'date-utils': {
            deps: ['angular']
        },
        'bs-date-picker': {
            deps: ['bootstrap']
        },
        'bs-date-picker-cn': {
            deps: ['bs-date-picker']
        },
        'date-picker': {
            deps: ['angular', 'bs-date-picker', 'bs-date-picker-cn']
        },
        'angular-resource': {
            deps: ['angular']
        },
        'ui-route': {
            deps: ['angular']
        },
        'jq-ui': {
        	deps: ['jquery']
        },
        'jq-ui-layout': {
        	deps: ['jquery', 'jq-ui']
        },
        'ui-layout': {
        	deps: ['angular', 'jq-ui-layout']
        },
        'tinymce': {
        	deps: ['jquery']
        },
        'gritter': {
        	deps: ['jquery']
        },
        'ztree': {
        	deps: ['jquery']
        },
        'autocomplete': {
        	deps: ['jquery']
        },
        'ui-tinymce': {
        	deps: ['tinymce']
        },
        'hats-jq-plugins': {
        	deps: ['jquery', 'bootstrap', 'angular', 'gritter', 'jq-ui-layout', 'bootstrap-context-menu']
        },
        'bootstrap-context-menu': {
        	deps: ['jquery', 'bootstrap']
        },
        'context-menu': {
        	deps: ['angular', 'bootstrap-context-menu']
        },
        'file-upload': {
        	deps: ['lib/js/jquery.iframe-transport.min', 'lib/js/upload/jquery.fileupload']
        },
        'fullscreen': {
        	deps: ['jquery']
        }
    },
    deps: []
};