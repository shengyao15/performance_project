var clock_wdgt = new Object();
(function(c, cj) {
	c.checktime = function(olddel) {

		var now = new Date();

		var nowdel = now.getDay() + "|" + now.getMonth() + "|" + now.getDate()
				+ "|" + now.getHours() + "|" + now.getMinutes() + "|"
				+ now.getSeconds();

		if (olddel != nowdel) {

			var oldsplit = olddel.split("|");
			var nowsplit = nowdel.split("|");

			if (oldsplit[5] != nowsplit[5]) {

				clock_slide('#sec', nowsplit[5], 11);
				if (oldsplit[4] != nowsplit[4]) {

					clock_slide('#min', nowsplit[4], 11);
					if (oldsplit[3] != nowsplit[3]) {

						clock_slide('#hour', nowsplit[3], 28);
						if (oldsplit[2] != nowsplit[2]) {

							clock_slide('#day', nowsplit[0], 100);
							clock_slide('#date', (nowsplit[2] - 1), 22);

							if (oldsplit[1] != nowsplit[1]) {
								clock_slide('#month', nowsplit[1], 57);

							};
						};
					};
				};
			};
		};

		cj.fx.speeds._default = 1000;

		function clock_slide(which, howmuch, multiple) {
			cj(which).stop().animate({
				marginLeft : ((howmuch * multiple) - 700) + 'px'
			}, 250, 'linear');
		};

		setTimeout(function() {
			c.checktime(nowdel);
		}, 250);

	};
})(clock_wdgt, cj);

var validation = new Object();
(
	function (v, cj) {
		v.checkEmail = function (obj) {
			if (cj.trim(obj.val()) != "") {
				var reg = /^([a-zA-Z0-9]+[_|\_|\.\-]?)*[a-zA-Z0-9]+@hp.com/;
				if (!reg.test(obj.val())) {
					obj.parent().find("em").html(
							"Please input your HP email");
					obj.parent().find("em").animate({
						opacity : "show",
						top : "-75"
					}, "fast");
					return;
				} else {
					obj.parent().find("em").animate({
						opacity : "hide",
						top : "-75"
					}, "fast");
				}
			}
		};
		
		v.checkUserName = function (obj) {
			var reg = /^([a-zA-Z0-9]+[_|\_|\.\-]?)*[a-zA-Z0-9]+@hp.com/;
			var userNameFlag = false;
			if (!reg.test(obj.val())) {
				obj.parent().find("em").html("Please input your HP email");
				obj.parent().find("em").animate({opacity : "show",top : "-75"}, "slow");
				userNameFlag = false;
			} else {
				obj.parent().find("em").animate({opacity : "hide",top : "-75"}, "fast");
				userNameFlag = true;
				cj.ajax({
					type : "POST",
					url : "/asyncUser/checkUserName",
					data : {
						name : obj.val()
					},
					success : function(response) {
						if (response.status == "SUCCESS") {
							userNameFlag = true;
							obj.parent().find("em").animate(
									{
										opacity : "hide",
										top : "-75"
									},
							"fast");
						} else {
							userNameFlag = false;
							obj.parent().find("em").html(response.result);
							obj.parent().find("em").animate({opacity : "show", top : "-75"}, "slow");
						}
					},
					error : function(e){
						alert(e);
					}
				});
			}
			return userNameFlag;
		};
		
		v.checkRegPassword = function (password) {
			var passwordFlag;
			if (password.val().length > 20
					|| password.val().length < 6) {
				password.parent().find("em").html("Length of password must be between 6 and 20");
				password.parent().find("em").animate({opacity : "show",top : "-75"}, "slow");
				passwordFlag = false;
			} else {
				passwordFlag = true;
				password.parent().find("em")
						.animate({
							opacity : "hide",
							top : "-75"
						}, "fast");
			}
			return passwordFlag;
		};
		
		v.checkRePassword = function (password, reppassword) {
			var reppasswordFlag;
			if (password.val() != reppassword.val()) {
				reppassword.parent().find("em").html("Password and Repeat Password must be same");
				reppassword.parent().find("em").animate({opacity : "show",top : "-75"}, "slow");
				reppasswordFlag = false;
			} else {
				reppasswordFlag = true;
				reppassword.parent().find("em").animate({opacity : "hide",top : "-75"}, "fast");
			}
			return reppasswordFlag;
		};
	}
)(validation, cj);

cj(".home-container").ready(function() {

					cj('#cslider').cSlider({
						timeOut : 9000
					});
					
					/************************************************/
					// Create two variable with the names of the months and days in an array
					var monthNames = [ "January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December" ]; 
					var dayNames= ["Sunday","Monday","Tuesday","Wednesday","Thursday","Friday","Saturday"];

					// Create a newDate() object
					var newDate = new Date();
					// Extract the current date from Date object
					newDate.setDate(newDate.getDate());
					// Output the day, date, month and year   
					cj('#Date').html(dayNames[newDate.getDay()] + " " + newDate.getDate() + ' ' + monthNames[newDate.getMonth()] + ' ' + newDate.getFullYear());

					setInterval( function() {
						// Create a newDate() object and extract the seconds of the current time on the visitor's
						var seconds = new Date().getSeconds();
						// Add a leading zero to seconds value
						cj("#sec").html(( seconds < 10 ? "0" : "" ) + seconds);
						},1000);
						
					setInterval( function() {
						// Create a newDate() object and extract the minutes of the current time on the visitor's
						var minutes = new Date().getMinutes();
						// Add a leading zero to the minutes value
						cj("#min").html(( minutes < 10 ? "0" : "" ) + minutes);
					    },1000);
						
					setInterval( function() {
						// Create a newDate() object and extract the hours of the current time on the visitor's
						var hours = new Date().getHours();
						// Add a leading zero to the hours value
						cj("#hours").html(( hours < 10 ? "0" : "" ) + hours);
					    }, 1000);	
					/************************************************/

					var csliderContainer = cj("#csliderContent");
					var containerLeft = csliderContainer.offset().left;
					var containerWidth = csliderContainer.width();
					cj("#register-panel").dialog(
							{
								autoOpen : false,
								show : {
									effect : 'drop',
									direction : "up"
								},
								hide : {
									effect : 'drop',
									direction : "up"
								},
								modal : true,
								closeOnEscape : true,
								minWidth : 300,
								position : [containerLeft + (containerWidth * 0.75), 150 ]
							});
					cj("#login-panel").dialog(
							{
								autoOpen : false,
								show : {
									effect : 'drop',
									direction : "up"
								},
								hide : {
									effect : 'drop',
									direction : "up"
								},
								modal : true,
								closeOnEscape : true,
								minWidth : 300,
								position : [containerLeft + (containerWidth * 0.75),150 ]
							});

					cj("#register-opener").click(function() {
						cj("#register-panel").dialog("open");
						return false;
					});
					cj("#login-opener").click(function() {
						cj("#login-panel").dialog("open");
						return false;
					});

					cj("#loginGo").click(function() {
						var panel = cj("#login-panel");
						panel.block({ css: { 
				            border: 'none', 
				            backgroundColor: 'rgba(0, 0, 0, 0.7)',
				            width: '100%',
				            height: '100%',
				            color: '#fff',
				        	},
							message: '<h2 style="padding-top:15%"><img src="/resources/style/img/loading-mask.gif" /> Waiting...</h2>'
						});
								cj.ajax({
									type : "POST",
									url : "/asyncUser/login",
									data : "name=" + cj("#login-username").val() + "&" + "password=" + cj("#login-password").val(),
									success : function(response) {
										var username = cj("#login-username");
										panel.unblock();
										if (response.status == "SUCCESS") {
											cj("#login-panel").dialog("close");
											window.location = "/users/dashboard";
										} else {
											username.parent().find("em").html(response.result);
											username.parent().find("em").animate({opacity : "show",top : "-75"}, "slow");
										}
									}
								});
							});

					cj("#login-username").blur(function() {
						mail = cj("#login-username");
						validation.checkEmail(mail);
					});
					
					cj("#back-dashboard").click(function(){
						window.location = "/users/dashboard";
					});

					cj("#registerGo").click(
							function() {
								var panel = cj("#register-panel");
								panel.block({ css: { 
						            border: 'none', 
						            backgroundColor: 'rgba(0, 0, 0, 0.7)',
						            width: '100%',
						            height: '100%',
						            color: '#fff',
						        	},
						        	message: '<h2 style="padding-top:15%"><img src="/resources/style/img/loading-mask.gif"/> Waiting...</h2>'
								});
								var regUserName = cj("#reg-username");
								var password = cj("#reg-password");
								var reppassword = cj("#reg-re-password");

								if (!validation.checkUserName(regUserName) 
										|| !validation.checkRegPassword(password) 
										|| !validation.checkRePassword(password, reppassword)) {
									return;
								}

								cj.ajax({
									type : "POST",
									url : "/asyncUser/register",
									data : "name=" + cj("#reg-username").val() + "&" + "password=" + cj("#reg-password").val(),
									success : function(response) {
										if (response.status == "SUCCESS") {
											var username = cj("#reg-username").val();
											username = username.substring(0, username.indexOf("@"));
											cj("#reg-username").val("");
											cj("#reg-password").val("");
											cj("#reg-re-password").val("");
											panel.unblock();
											cj("#register-panel > ul").fadeOut(1000,function() {
														cj("#welcomeRegister").fadeIn(1000);
													});
											setTimeout(function() {window.location = "/users/dashboard"}, 3000);
										} else {
											panel.unblock();
										}
									}
								});
							});

					cj("#reg-password").blur(
						function() {
							var password = cj("#reg-password");
							if (password.val() != "") {
								validation.checkRegPassword(password);
							}
						});

					cj("#reg-re-password").blur(function() {
						var password = cj("#reg-password");
						var reppassword = cj("#reg-re-password");
						if (reppassword.val() != "") {
							validation.checkRePassword(password, reppassword);
						}

					});

					cj("#reg-username").blur(function() {
						var regUserName = cj("#reg-username");

						if (cj.trim(regUserName.val()) != "") {
							validation.checkUserName(regUserName);
						}
					});
				});