cj('#userDashboard').ready(function(){
	var openState = false;
	
	cj("#dashboardSummaryToggler").click(function(){
		if (!openState) {
			cj("#dashboardSummary").show("fold", {mode:"show"}, 1500);
			openState = true;
		} else {
			cj("#dashboardSummary").hide("fold", {mode:"hide"}, 1500);
			openState = false;
		}
	});
	
	cj( "#accordion" )
	.accordion({
		header: "> div > h3",
		autoHeight: false,
		navigation: true,
		collapsible: true
	})
	.sortable({
		axis: "y",
		handle: "h3",
		stop: function( event, ui ) {
			// IE doesn't register the blur when sorting
			// so trigger focusout handlers to remove .ui-state-focus
			ui.item.children( "h3" ).triggerHandler( "focusout" );
		}
	});
	
});

