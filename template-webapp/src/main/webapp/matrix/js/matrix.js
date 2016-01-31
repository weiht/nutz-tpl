define(['require', 'jquery'], function(require, $) {
	function allActiveOff() {
		$('#sidebar-accordion>.panel').toggleClass('active', false);
		$('#sidebar-accordion>.panel .panel-body>.list-group>.list-group-item').toggleClass('active', false);
	}

	function toggleActiveMenu() {
		allActiveOff();
		$(this).parents('.panel').toggleClass('active', true);
	}

	function toggleActiveSubMenu() {
		allActiveOff();
		$(this).parents('.list-group-item').toggleClass('active', true)
			.parents('.panel').toggleClass('active', true);
	}

    require(['domReady!'], function (document) {
    	$('#sidebar-accordion .panel-title a[ng-click]').click(toggleActiveMenu);
    	$('#sidebar-accordion .panel .list-group .list-group-item a[ng-click]').click(toggleActiveSubMenu);
    });
});