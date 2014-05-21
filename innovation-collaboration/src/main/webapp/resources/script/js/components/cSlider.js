(function(cj){  

    cj.fn.cSlider = function(vars) {       
        
        var element     = this;
        var timeOut     = (vars.timeOut != undefined) ? vars.timeOut : 4000;
        var current     = null;
        var timeOutFn   = null;
        var faderStat   = true;
        var mOver       = false;
        var items       = cj("#" + element[0].id + "Content ." + element[0].id + "Image");
        var itemsSpan   = cj("#" + element[0].id + "Content ." + element[0].id + "Image span");
            
        items.each(function(i) {
    
            cj(items[i]).mouseover(function() {
               mOver = true;
            });
            
            cj(items[i]).mouseout(function() {
                mOver   = false;
                fadeElement(true);
            });
            
        });
        
        var fadeElement = function(isMouseOut) {
            var thisTimeOut = (isMouseOut) ? (9*timeOut) : timeOut;
            thisTimeOut = (faderStat) ? 10 : thisTimeOut;
            if(items.length > 0) {
                timeOutFn = setTimeout(makeSlider, thisTimeOut);
            } else {
                console.log("Poof..");
            }
        };
        
        var makeSlider = function() {
            current = (current != null) ? current : items[(items.length-1)];
            var currNo      = jQuery.inArray(current, items) + 1;
            currNo = (currNo == items.length) ? 0 : (currNo - 1);
            var newMargin   = cj(element).width() * currNo;
            if(faderStat == true) {
                if(!mOver) {
                    cj(items[currNo]).fadeIn((timeOut/6), function() {
                        if(cj(itemsSpan[currNo]).css('bottom') == 0) {
                            cj(itemsSpan[currNo]).slideUp((timeOut/6), function() {
                                faderStat = false;
                                current = items[currNo];
                                if(!mOver) {
                                    fadeElement(false);
                                }
                            });
                        } else {
                            cj(itemsSpan[currNo]).slideDown((timeOut/6), function() {
                            	cj(".home-optbar").slideDown(timeOut/6);
                                faderStat = false;
                                current = items[currNo];
                                if(!mOver) {
                                    fadeElement(false);
                                }
                            });
                        }
                    });
                }
            } else {
                if(!mOver) {
                    if(cj(itemsSpan[currNo]).css('bottom') == 0) {
                        cj(itemsSpan[currNo]).slideDown((timeOut/6), function() {
                        	alert("2");
                            cj(items[currNo]).fadeOut((timeOut/6), function() {
                                faderStat = true;
                                current = items[(currNo+1)];
                                if(!mOver) {
                                    fadeElement(false);
                                }
                            });
                        });
                    } else {
                        cj(itemsSpan[currNo]).slideUp((timeOut/6), function() {
                        	cj(".home-optbar").slideUp(timeOut/6);
	                        cj(items[currNo]).fadeOut((timeOut/6), function() {
	                                faderStat = true;
	                                current = items[(currNo+1)];
	                                if(!mOver) {
	                                    fadeElement(false);
	                                }
	                            });
                        });
                    }
                }
            }
        };
        
        makeSlider();

    };

})(jQuery);  