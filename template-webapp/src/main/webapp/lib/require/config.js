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
    	name: 'angular',
    	location: 'lib/angular',
    	main: 'angular.min'
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
        'bootstrap': {
            deps: ['jquery']
        },
        'angular': {
            exports: 'angular',
            deps: ['jquery']
        },
        'angular/angular-route': {
            deps: ['angular']
        },
        'angular/angular-resource': {
            deps: ['angular']
        },
        'ztree': {
        	deps: ['jquery']
        }
    },
    deps: []
};