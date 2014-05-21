/**
 * dashboard.js Author: Nick Hou (depends on Jquery and Jquery UI) let the user
 * can trag and drop the dashboard grids
 */
cj(".dashboard-content").ready(function() {
	
	var displayTextInWidgetLib = function() {
		var widget_lib = cj("#widgetLib");
		if(widget_lib.find('div')==null || widget_lib.find('div').length==0){
			widget_lib.find('#widgetLibTitle').text('widget lib is empty!');
		} else {
			widget_lib.find('#widgetLibTitle').text('choose your panels to display');
		}
	};
	
	var disableDropWhenOnlyOneWidget = function() {
		if(sortableDashboard.children('.dashboard-panel-flag').length == 1) {
			recycleEntry.droppable("option", "disabled", true);
			recycleEntry.hide();
			recycleEntryDisabled.show();
		}else{
			recycleEntry.droppable("option", "disabled", false);
			recycleEntry.show();
			recycleEntryDisabled.hide();
		}
	};
	
	var recycleEntryDisabled = cj("#recyclebinDisabled");
	
	var sortableDashboard = cj(".dashboard-container").sortable({
		placeholder : 'sortable-placeholder-hightlight large-shortcut',
		containment : 'body',
		opacity : '0.25',
		revert : 500,
		distance : 50,
		delay : 300,
		handle : '.widget-toolbar',
		start : function(event, ui) {			
			recycleEntry.find("div").addClass("recyclebin-close-icon");
			recycleEntryDisabled.find('.recycle-disabled').show('shake', {}, 300);
			closeRecycleBin();

		},
		stop : function(event, ui) {
			recycleEntry.find("div").removeClass("recyclebin-close-icon");
			recycleEntry.find("div").removeClass("recyclebin-open-icon");
			recycleEntryDisabled.find('.recycle-disabled').hide();
		}

	});

	var recycleEntry = cj("#recyclebin");

	recycleEntry.droppable({
		// hoverClass : "recyclebin-open-icon",
		accept : ".dashboard-container .dashboard-panel-flag",
		over : function(event, ui) {
			cj(this).find('div').removeClass('recyclebin-close-icon');
			cj(this).find('div').addClass('recyclebin-open-icon');
		},

		out : function(event, ui) {
			cj(this).find('div').removeClass('recyclebin-open-icon');
			cj(this).find('div').addClass('recyclebin-close-icon');
		},

		drop : function(event, ui) {
			cj(this).addClass('recyclebin-close-icon');
			sortableDashboard.sortable("option", "revert", 0);
			ui.helper.hide("explode", "slow", function() {
				cj(this).find('.widget-plus-press').hide();
				cj(this).appendTo(cj("#widgetLib")).show('slow');
				sortableDashboard.sortable("option", "revert", 500);
				displayTextInWidgetLib();
				disableDropWhenOnlyOneWidget();
			});
			

		},

		activate : function(event, ui) {
		}

	});

	var widgetLib = cj("#widgetLib").sortable({
		placeholder : 'sortable-placeholder-hightlight large-shortcut',
		containment : 'body',
		items : '>div',
		connectWith : '.dashboard-container',
		opacity : '0.25',
		revert : 500,
		stop : function() {
			displayTextInWidgetLib();
			disableDropWhenOnlyOneWidget();
		},
		receive : function() {
			
		}

	});

	var recycleBinOpenStatus = false;
	cj("#dashboardRecycleBinToggler").click(function() {
		if(!closeRecycleBin()){
			openRecycleBin();
		}
		
	});
	
	var closeRecycleBin = function() {
		if(recycleBinOpenStatus){
		    widgetLib.hide("blind", {}, 300);
		    recycleBinOpenStatus = false;
		    return true;
		}
		return false;
	};
	
	var openRecycleBin = function() {
		if(recycleBinOpenStatus == false) {
			widgetLib.show("bounce", {}, 300);
			recycleBinOpenStatus = true;
			return true;
		}
		return false;
		
	};

	cj(".dashboard-container, #widgetLib").disableSelection();
	
	cj("#dashboard-skillmatrix").hover(function(){
		if(cj(this).parent('#widgetLib').length != 0) {
		    cj(this).find('a').show();
		}
	},function(){
		cj(this).find('a').hide();
	});
	
	//when hover on the widget in widget bin, display the widget-plus sign
	cj(".dashboard-panel-flag").hover(function(){
		cj(this).find('.widget-plus-press').show();
	}, function(){
		cj(this).find('.widget-plus-press').hide();
	});
	
	//press the widget-plus sign, the widget will be appended to the dashboard-container
	cj('.widget-plus-press').click(function(){
		var appendElement = cj(this).parent().parent();
		appendElement.hide();
		appendElement.find('.widget-toolbar').hide();
		appendElement.prependTo(cj(".dashboard-container"));
		appendElement.show('slow');
		displayTextInWidgetLib();
		disableDropWhenOnlyOneWidget();
	});
	
	//when hover on the dashboard panel, the tool bar display
	cj('.dashboard-panel-flag').hover(function(){
		if(sortableDashboard.children('.dashboard-panel-flag').length > 1){
			cj(this).find('.widget-toolbar').show();
		}
		
	}, function(){
		cj(this).find('.widget-toolbar').hide();
	});
	
	//when hover on the dashboard panel remove button, the button background color is change to red.
	cj('.widget-remove-button').hover(function(){
		cj(this).css('background-color', 'red');
	}, function(){
		cj(this).css('background-color', 'gray');
	});
	
	//when click the dashboard panel remove button, the panel will be moved to widgetbin
	cj('.widget-remove-button').click(function(){
		var removeElement = cj(this).parent().parent().parent();
		if(sortableDashboard.children('.dashboard-panel-flag').length != 1){
			removeElement.hide('slow', function(){
				removeElement.find('.widget-plus-press').hide();
				removeElement.appendTo(cj("#widgetLib"));
				removeElement.show('slow');
				displayTextInWidgetLib();
				disableDropWhenOnlyOneWidget();
		
		    });
		}
	
	});
	
	cj("#logoutGo").click(function(){
		cj.ajax({
			type : "POST",
			url : "/asyncUser/logout",
			success : function(response) {
				if (response.status == "SUCCESS") {
					window.location = "/";
				}
			}
		});
	});
	
	// initiate dashboard page
	displayTextInWidgetLib();
	disableDropWhenOnlyOneWidget();
	
	

});