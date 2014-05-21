var cj = jQuery.noConflict();

cj(".dialog-btn").mousedown(function() {
	cj(this).addClass("dialog-btn-active");
}).mouseup(function() {
	cj(this).removeClass("dialog-btn-active");
});

var uiLoadingMask = new Object();
(
	function(lm, cj){
		uiLoadingMask.start = function(){
			cj.blockUI({ css: { 
	            border: 'none', 
	            padding: '15px', 
	            backgroundColor: '#000', 
	            '-webkit-border-radius': '10px', 
	            '-moz-border-radius': '10px', 
	            opacity: .5, 
	            color: '#fff' 
	        } }); 
		};
		uiLoadingMask.stop = function(){
			cj.unblockUI();
		};
	}
)(uiLoadingMask,cj);