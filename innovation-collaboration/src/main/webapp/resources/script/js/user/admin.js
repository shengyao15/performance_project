cj("#adminPanel").ready(function(){
	cj("#adminPanel").click(function(){
		cj("#dialog:ui-dialog" ).dialog( "destroy" );
		cj("#adminEntry").dialog({
			width: 1000,
			height:"auto",
			modal:true,
			show : {
				effect : 'drop',
				direction : "up"
			},
			hide : {
				effect : 'drop',
				direction : "up"
			}
		});
	});
});

cj("#adminEntry").ready(function() {
	/*------------------validation function for admin common use start--------------------*/
	var adminValidator = new Object();
	(function(v){
		v.validateName = function(name){
			var reg = /^\w+$/;
			return reg.test(name);
		};
	})(adminValidator);
	
	/*------------------validation function for admin common use end--------------------*/
	
	cj( "#accordionAdmin" ).accordion({
		fillSpace: false,
		autoHeight: false,
		navigation: true,
		collapsible: true
	});
	
	var currentContent = "";
	
	cj("#userSelect").selectable({
		stop: function() {
			cj( ".ui-selected", this ).each(function() {
				var index = cj( "#userSelect li" ).index( this );
				if(currentContent != ""){
					cj(currentContent).hide();
				}
				currentContent = "#user"+(index+1);
				if(currentContent == "#user"+(index+1)){
					cj(currentContent).slideDown(1000,function(){
						if(currentContent == "#user1"){
							loadManageUser();
						}
					});
				}
			});
		}
	});
	
	cj("#roleSelect").selectable({
		stop: function() {
			cj( ".ui-selected", this ).each(function() {			
				var index = cj( "#roleSelect li" ).index( this );
				if(currentContent != ""){
					cj(currentContent).hide();
				}
				currentContent = "#role"+(index+1);
				if(currentContent == "#role"+(index+1)){
					cj(currentContent).slideDown(1000,function(){
						if(currentContent == "#role1"){
							loadViewRole();			
						}
						else if(currentContent == "#role2"){
							loadAddRole();
						}
						else if(currentContent == "#role3"){
							loadManageRole();
						}
					});
				}
			});
		}
	});
	
	cj("#teamSelect").selectable({
		stop: function() {
			cj( ".ui-selected", this ).each(function() {
				var index = cj( "#teamSelect li" ).index( this );
				if(currentContent != ""){
					cj(currentContent).hide();
				}
				currentContent = "#team"+(index+1);
				if(currentContent == "#team"+(index+1)){
					cj(currentContent).slideDown(1000,function(){
						if(currentContent == "#team1"){
							loadAddTeam();
						}
						else if(currentContent == "#team2"){
							loadManageTeam();
						}
					});
				}
			});
		}
	});
	
	/*---------------------------------------------Admin Role Start---------------------------------------------*/
	function loadViewRole(){
		cj.ajax({
			type: "POST",
			url:"/asyncAdmin/retrieveAllRoles",
			dataType : "json",
			success:function(json){
				var oTable = cj('#example').dataTable( {
					"sScrollY": 200,
					"bJQueryUI": true,
					"bProcessing": true,
					"bRetrieve":true,
					"bDestroy":true,
					"aaData":json,
            		"aoColumns": [
	            		              { "mData": "name" },
	            		              { "mData": "roleName" },
									  { "mData": "createDate" },
									  { "mData": "updateDate" }
								  ]
				} );
			}
		});
		
	}
	
	function loadAddRole(){		
		bindCustomEventsForAddRole();
	}
	
	function bindCustomEventsForAddRole(){
		var addRoleBtn = cj("#addroleGo");
		addRoleBtn.unbind("click");
		addRoleBtn.click(function(){
			var name = cj("#roleName").val();
			var roleName = cj("#roleDisplayName").val();
			var addRoleMsg = cj("#addRoleMsg");
			if(adminValidator.validateName(name)){
				cj.ajax({
					type: "POST",
					url:"/asyncAdmin/addRole/",
					data:{name:name, roleName:roleName},
					success:function(response){
						if(response.status == "SUCCESS"){
							cj("#roleName").parent().find("em").html(response.result);
							cj("#roleName").parent().find("em").animate({opacity: "show", top: "90", left: "650",color:"#000000"}, "slow");
							addRoleMsg.text(response.result);
						}else{
							cj("#roleName").parent().find("em").html(response.result);
							cj("#roleName").parent().find("em").animate({opacity: "show", top: "90", left: "650",color:"#000000"}, "slow");
							addRoleMsg.text(response.result);
						}
					}
				});
			}
			else{
				addRoleMsg.text("Please specify role name correctly!");
			}
		});
		
		var addRoleMsg = cj("#addRoleMsg");
		var roleName = cj("#roleName");
		roleName.unbind("focus");
		roleName.focus(function(){
			addRoleMsg.empty();
			roleName.parent().find("em").hide();
		});
		
		var roleDisplayName = cj("#roleDisplayName");
		roleDisplayName.unbind("focus");
		roleDisplayName.focus(function(){
			addRoleMsg.empty();
			roleName.parent().find("em").hide();
		});
	}
	
	//manage role
	function loadManageRole(){
		retrieveRoleDataForManageRole();
	}
	
	function retrieveRoleDataForManageRole(){
		cj.ajax({
			type: "POST",
			url:"/asyncAdmin/retrieveAllRoleName",
			data:{},
			success:function(response){
				if(response.status == "SUCCESS"){
					var autocompletedata = response.result.split(",");
					cj("#targetRole").autocomplete({
						minLength:0,
						source: autocompletedata,
						select:function(event,ui){
							retrieveRoleInfoForManageRole(event,ui);
						},
						search:function(event,ui){
							cj("#manageRoleMsg").empty();
							hideManageRoleComponents(1000);
						}
					});
				}else{
					cj("#manageRoleMsg").text("Failed to load role data!");
				}
			}
		});
	}
	
	function retrieveRoleInfoForManageRole(event,ui){
		cj.ajax({
			type: "POST",
			url:"/asyncAdmin/getRoleInfoByRoleName",
			data:{name:ui.item.label},
			success:function(response){
				if(response.status == "SUCCESS"){
					var result = response.result.split("#");
					var roleName = "";
					var members = "";
					var availableMemebers = "";
					for(var i=0;i<result.length;i++){
						var ret = result[i].split("=");
						if(ret[0]=="teamName"){
							roleName = ret[1];
						}
						else if(ret[0]=="members"){
							members = ret[1];
						}
						else if(ret[0]=="availableMembers"){
							availableMemebers = ret[1];
						}
					}
					populateRoleInfoForManageRole(roleName,members,availableMemebers);
					bindCustomEventsForManageRole();
					showManageRoleComponents(1000);
				}else{
					cj("#manageRoleMsg").text("Failed to load role info!");
				}
			}
		});
	}
	
	function showManageRoleComponents(time){
		cj("#roleInfoBlock").slideDown(time);
		cj("#roleMemberBlock").slideDown(time);
	}
	
	function hideManageRoleComponents(time){
		cj("#roleInfoBlock").slideUp(time);
		cj("#roleMemberBlock").slideUp(time);
	}
	
	function populateRoleInfoForManageRole(roleName,members,availableMemebers){
		populateRoleDisplayNameForManageRole(roleName);
		populateCurrentMembersForManageRole(members);
		populateAvailableMembersForManageRole(availableMemebers);
	}
	
	function populateRoleDisplayNameForManageRole(roleName){
		cj("#manageRoleDisplayName").val(roleName);
	}
	
	function populateCurrentMembersForManageRole(members){
		var roleLeftBox = cj("#roleLeftBox");
		roleLeftBox.empty();
		if(members!=""){
			var memberArr = members.split(",");
			for(var i=0;i<memberArr.length;i++){
				var member = memberArr[i];
				roleLeftBox.append("<option value='"+member+"'>"+member+"</option>");
			}
		}
	}
	
	function populateAvailableMembersForManageRole(availableMemebers){
		var roleRightBox = cj("#roleRightBox");
		roleRightBox.empty();
		if(availableMemebers!=""){
			var availableMemberArr = availableMemebers.split(",");
			for(var i=0;i<availableMemberArr.length;i++){
				var availableMember = availableMemberArr[i];
				roleRightBox.append("<option value='"+availableMember+"'>"+availableMember+"</option>");
			}
		}
	}
	
	function bindCustomEventsForManageRole(){
		var manageRoleMsg = cj("#manageRoleMsg");
		
		var manageRoleDisplayName = cj("#manageRoleDisplayName");
		manageRoleDisplayName.unbind("focus");
		manageRoleDisplayName.focus(function(){
			manageRoleMsg.empty();
		});
		
		var updateRoleBtn = cj("#updateRoleBtn");
		updateRoleBtn.unbind("click");
		updateRoleBtn.click(function(){
			processUpdateRole();
		});

		var moveToRightBtn = cj("#roleMoveToRightBtn");
		moveToRightBtn.unbind("click");
		moveToRightBtn.click(function(){
			removeMembersForManageRole();
		});
		
		var moveToLeftBtn = cj("#roleMoveToLeftBtn");
		moveToLeftBtn.unbind("click");
		moveToLeftBtn.click(function(){
			addMembersForManageRole();
		});
	}
	
	function processUpdateRole(){
		var roleName = cj("#targetRole").val();
		var roleDisplayName = cj("#manageRoleDisplayName").val();
		cj.ajax({
			type: "POST",
			url:"/asyncAdmin/updateRoleInfo",
			data:{name:roleName, roleName:roleDisplayName},
			success:function(response){
				var manageRoleMsg = cj("#manageRoleMsg");
				if(response.status == "SUCCESS"){
					manageRoleMsg.text("update role successful!");
				}else{
					manageRoleMsg.text("update role fail!");
				}
			}
		});
	}
	
	function removeMembersForManageRole(){
		cj("#manageRoleMsg").empty();
		var v = cj("#roleLeftBox").val();
		if(v!=null){
			updateMembersForManageRole("remove");
		}
	}
	
	function addMembersForManageRole(){
		cj("#manageRoleMsg").empty();
		var v = cj("#roleRightBox").val();
		if(v!=null){
			updateMembersForManageRole("add");
		}
	}
	
	function updateMembersForManageRole(type){
		var roleName = cj("#targetRole").val();
		var members = "";
		if(type=="add"){
			cj("#roleRightBox").find("option:selected").each(function(){
				members+=cj(this).val()+",";
			});
		}
		else if(type=="remove"){
			cj("#roleLeftBox").find("option:selected").each(function(){
				members+=cj(this).val()+",";
			});
		}
		processUpdateMembersForManageRole(roleName,members,type);
	}
	
	function processUpdateMembersForManageRole(roleName,members,type){
		cj.ajax({
			type: "POST",
			url:"/asyncAdmin/updateRoleMembers",
			data:{name:roleName, members:members,type:type},
			success:function(response){
				var manageRoleMsg = cj("#manageRoleMsg");
				if(response.status == "SUCCESS"){
					if(type=="add"){
						cj("#roleRightBox").find("option:selected").appendTo("#roleLeftBox");
					}
					else if(type=="remove"){
						cj("#roleLeftBox").find("option:selected").appendTo("#roleRightBox");
					}
					manageRoleMsg.text("update role users successful!");
				}else{
					manageRoleMsg.text("update role users fail!");
				}
			}
		});
	}
	/*---------------------------------------------Admin Role End---------------------------------------------*/
	
	/*---------------------------------------------Admin Team Start---------------------------------------------*/
	/*-----------------Add Team Start-----------------*/
	function loadAddTeam(){
		loadParentTeamOptionsForAddTeam();
		bindCustomEventsForAddTeam();
	}
	
	function loadParentTeamOptionsForAddTeam(){
		cj.ajax({
			type: "POST",
			url:"/asyncAdmin/retrieveAllTeams",
			data:{},
			success:function(response){
				if(response.status == "SUCCESS"){
					var teamNames = response.result.split(",");
					var selParentTeam = cj("#selParentTeam");
					selParentTeam.empty();
					selParentTeam.append("<option value=''>Specify a parent team.</option>");
					for(var i=0;i<teamNames.length;i++){
						var teamName = teamNames[i];
						selParentTeam.append("<option value='"+teamName+"'>"+teamName+"</option>");
					}
				}else{
					cj("#addTeamMsg").text("Failed to load teams!");
				}
			}
		});
	}
	
	function processAddTeam(){
		var teamName = cj("#teamName").val();
		var teamDisplayName = cj("#teamDisplayName").val();
		var parentTeamName = cj("#selParentTeam").val();
		var addTeamMsg = cj("#addTeamMsg");
		if(adminValidator.validateName(teamName)){
			cj.ajax({
				type: "POST",
				url:"/asyncAdmin/addTeam",
				data:{name:teamName, teamName:teamDisplayName,parentTeamName:parentTeamName},
				success:function(response){
					if(response.status == "SUCCESS"){
						addTeamMsg.text("Add team successfully!");
					}else{
						addTeamMsg.text(response.result);
					}
				}
			});
		}
		else{
			addTeamMsg.text("Please specify team name correctly!");
		}
	}
	
	function bindCustomEventsForAddTeam(){
		var addTeamBtn = cj("#addTeamBtn");
		addTeamBtn.unbind("click");//avoid duplicate function call.
		addTeamBtn.click(function(){
			processAddTeam();
		});
		
		var addTeamMsg = cj("#addTeamMsg");
		var teamName = cj("#teamName");
		teamName.unbind("focus");
		teamName.focus(function(){
			addTeamMsg.empty();
		});
		
		var teamDisplayName = cj("#teamDisplayName");
		teamDisplayName.unbind("focus");
		teamDisplayName.focus(function(){
			addTeamMsg.empty();
		});
		
		var selParentTeam = cj("#selParentTeam");
		selParentTeam.unbind("change");
		selParentTeam.change(function(){
			addTeamMsg.empty();
		});
	}
	/*-----------------Add Team End-----------------*/
	
	/*-----------------Manage Team Start-----------------*/
	function loadManageTeam(){
		retrieveTeamDataForManageTeam();
	}
	
	function retrieveTeamDataForManageTeam(){
		cj.ajax({
			type: "POST",
			url:"/asyncAdmin/retrieveAllTeams",
			data:{},
			success:function(response){
				if(response.status == "SUCCESS"){
					var autocompletedata = response.result.split(",");
					cj("#targetTeam").autocomplete({
						minLength:0,
						source: autocompletedata,
						select:function(event,ui){
							retrieveTeamInfoForManageTeam(event,ui);
						},
						search:function(event,ui){
							cj("#manageTeamMsg").empty();
							hideManageTeamComponents(1000);
						}
					});
				}else{
					cj("#manageTeamMsg").text("Failed to load team data!");
				}
			}
		});
	}
	
	function retrieveTeamInfoForManageTeam(event,ui){
		cj.ajax({
			type: "POST",
			url:"/asyncAdmin/getTeamInfoByTeamName",
			data:{name:ui.item.label},
			success:function(response){
				if(response.status == "SUCCESS"){
					var result = response.result.split("#");
					var teamName = "";
					var members = "";
					var availableMemebers = "";
					var availableParentTeamNames = "";
					var parentTeamName = "";
					var subTeamNames = "";
					for(var i=0;i<result.length;i++){
						var ret = result[i].split("=");
						if(ret[0]=="teamName"){
							teamName = ret[1];
						}
						else if(ret[0]=="members"){
							members = ret[1];
						}
						else if(ret[0]=="availableMembers"){
							availableMemebers = ret[1];
						}
						else if(ret[0]=="availableParentTeamNames"){
							availableParentTeamNames = ret[1];
						}
						else if(ret[0]=="parentTeamName"){
							parentTeamName = ret[1];
						}
						else if(ret[0]=="subTeamNames"){
							subTeamNames = ret[1];
						}
					}
					populateTeamInfoForManageTeam(teamName,availableParentTeamNames,parentTeamName,members,availableMemebers,subTeamNames);
					bindCustomEventsForManageTeam();
					showManageTeamComponents(1000);
				}else{
					cj("#manageTeamMsg").text("Failed to load team info!");
				}
			}
		});
	}
	
	function showManageTeamComponents(time){
		cj("#selManageParentTeam").slideDown(time);
		cj("#teamInfoBlock").slideDown(time);
		cj("#teamMemberBlock").slideDown(time);
		cj("#subTeamsBlock").slideDown(time);
	}
	
	function hideManageTeamComponents(time){
		cj("#selManageParentTeam").slideUp(time);
		cj("#teamInfoBlock").slideUp(time);
		cj("#teamMemberBlock").slideUp(time);
		cj("#subTeamsBlock").slideUp(time);
	}
	
	function populateTeamInfoForManageTeam(teamName,availableParentTeamNames,parentTeamName,members,availableMemebers,subTeamNames){
		populateTeamDisplayNameForManageTeam(teamName);
		populateParentTeamOptionsForManageTeam(availableParentTeamNames,parentTeamName);
		populateCurrentMembersForManageTeam(members);
		populateAvailableMembersForManageTeam(availableMemebers);
		populateSubTeamsForManageTeam(subTeamNames);
	}
	
	function populateTeamDisplayNameForManageTeam(teamName){
		cj("#manageTeamDisplayName").val(teamName);
	}
	
	function populateParentTeamOptionsForManageTeam(availableParentTeamNames,parentTeamName){
		var selManageParentTeam = cj("#selManageParentTeam");
		selManageParentTeam.empty();
		selManageParentTeam.append("<option value=''>Update a parent team.</option>");
		if(availableParentTeamNames!=""){
			var availableParentTeamNameArr = availableParentTeamNames.split(",");
			for(var i=0;i<availableParentTeamNameArr.length;i++){
				var availableParentTeamName = availableParentTeamNameArr[i];
				selManageParentTeam.append("<option value='"+availableParentTeamName+"'>"+availableParentTeamName+"</option>");
			}
		}
		selManageParentTeam.val(parentTeamName);
	}
	
	function populateCurrentMembersForManageTeam(members){
		var teamLeftBox = cj("#teamLeftBox");
		teamLeftBox.empty();
		if(members!=""){
			var memberArr = members.split(",");
			for(var i=0;i<memberArr.length;i++){
				var member = memberArr[i];
				teamLeftBox.append("<option value='"+member+"'>"+member+"</option>");
			}
		}
	}
	
	function populateAvailableMembersForManageTeam(availableMemebers){
		var teamRightBox = cj("#teamRightBox");
		teamRightBox.empty();
		if(availableMemebers!=""){
			var availableMemberArr = availableMemebers.split(",");
			for(var i=0;i<availableMemberArr.length;i++){
				var availableMember = availableMemberArr[i];
				teamRightBox.append("<option value='"+availableMember+"'>"+availableMember+"</option>");
			}
		}
	}
	
	function populateSubTeamsForManageTeam(subTeamNames){
		var subTeams = cj("#subTeams");
		subTeams.empty();
		if(subTeamNames!=""){
			var subTeamNameArr = subTeamNames.split(",");
			for(var i=0;i<subTeamNameArr.length;i++){
				var subTeamName = subTeamNameArr[i];
				subTeams.append("<option value='"+subTeamName+"'>"+subTeamName+"</option>");
			}
		}
	}
	
	function bindCustomEventsForManageTeam(){
		var manageTeamMsg = cj("#manageTeamMsg");
		var selManageParentTeam = cj("#selManageParentTeam");
		selManageParentTeam.unbind("change");
		selManageParentTeam.change(function(){
			manageTeamMsg.empty();
		});
		
		var manageTeamDisplayName = cj("#manageTeamDisplayName");
		manageTeamDisplayName.unbind("focus");
		manageTeamDisplayName.focus(function(){
			manageTeamMsg.empty();
		});
		
		var updateTeamBtn = cj("#updateTeamBtn");
		updateTeamBtn.unbind("click");
		updateTeamBtn.click(function(){
			processUpdateTeam();
		});

		var moveToRightBtn = cj("#teamMoveToRightBtn");
		moveToRightBtn.unbind("click");
		moveToRightBtn.click(function(){
			removeMembersForManageTeam();
		});
		
		var moveToLeftBtn = cj("#teamMoveToLeftBtn");
		moveToLeftBtn.unbind("click");
		moveToLeftBtn.click(function(){
			addMembersForManageTeam();
		});
	}
	
	function processUpdateTeam(){
		var teamName = cj("#targetTeam").val();
		var teamDisplayName = cj("#manageTeamDisplayName").val();
		var parentTeamName = cj("#selManageParentTeam").val();
		cj.ajax({
			type: "POST",
			url:"/asyncAdmin/updateTeamInfo",
			data:{name:teamName, teamName:teamDisplayName, parentTeamName:parentTeamName},
			success:function(response){
				var manageTeamMsg = cj("#manageTeamMsg");
				if(response.status == "SUCCESS"){
					manageTeamMsg.text("update team successful!");
				}else{
					manageTeamMsg.text("update team fail!");
				}
			}
		});
	}
	
	function removeMembersForManageTeam(){
		cj("#manageTeamMsg").empty();
		var v = cj("#teamLeftBox").val();
		if(v!=null){
			updateMembersForManageTeam("remove");
		}
	}
	
	function addMembersForManageTeam(){
		cj("#manageTeamMsg").empty();
		var v = cj("#teamRightBox").val();
		if(v!=null){
			updateMembersForManageTeam("add");
		}
	}
	
	function updateMembersForManageTeam(type){
		var teamName = cj("#targetTeam").val();
		var members = "";
		if(type=="add"){
			cj("#teamRightBox").find("option:selected").each(function(){
				members+=cj(this).val()+",";
			});
		}
		else if(type=="remove"){
			cj("#teamLeftBox").find("option:selected").each(function(){
				members+=cj(this).val()+",";
			});
		}
		processUpdateMembersForManageTeam(teamName,members,type);
	}
	
	function processUpdateMembersForManageTeam(teamName,members,type){
		cj.ajax({
			type: "POST",
			url:"/asyncAdmin/updateTeamMembers",
			data:{name:teamName, members:members,type:type},
			success:function(response){
				var manageTeamMsg = cj("#manageTeamMsg");
				if(response.status == "SUCCESS"){
					if(type=="add"){
						cj("#teamRightBox").find("option:selected").appendTo("#teamLeftBox");
					}
					else if(type=="remove"){
						cj("#teamLeftBox").find("option:selected").appendTo("#teamRightBox");
					}
					manageTeamMsg.text("update team members successful!");
				}else{
					manageTeamMsg.text("update team members fail!");
				}
			}
		});
	}
	/*-----------------Manage Team End-----------------*/
	/*---------------------------------------------Admin Team End---------------------------------------------*/
	
	//admin-user
	function loadManageUser(){
		var targetUser = cj("#targetUser");
		cj.ajax({
			type: "POST",
			url:"/asyncAdmin/retrieveAllUsers",
			data:{},
			success:function(response){
				cj("#manageUserMsg").empty();
				var autocompletedata = response.result.split("#");
				
				targetUser.autocomplete({
					minLength:0,
					source: autocompletedata,
					select:function(event,ui){
						cj.ajax({
							type : "POST",
							url : "/asyncAdmin/retrieveTeamsPrivilege",
							data:{name:ui.item.label},
							success : function(response) {
								
								var availableTeams = response.availableTeams;
								var selectedTeams = response.selectedTeams;
								cj("#userTeamLeftSelect").empty();
								cj("#userTeamRightSelect").empty();
								for ( var key in availableTeams ){
									cj("<option value="+key+">"+availableTeams[key]+"</option>").appendTo(cj('#userTeamRightSelect'));
								} 
								for ( var key in selectedTeams ){
									cj("<option value="+key+">"+selectedTeams[key]+"</option>").appendTo(cj('#userTeamLeftSelect'));
								}
							}
						});
						
						cj.ajax({
							type : "POST",
							url : "/asyncAdmin/retrieveRolesPrivilege",
							data:{name:ui.item.label},
							success : function(response) {
								
								var availableRoles = response.availableRoles;
								var selectedRoles = response.selectedRoles;
								
								cj("#userRoleLeftSelect").empty();
								cj("#userRoleRightSelect").empty();
								
								for ( var key in availableRoles ){
									cj("<option value="+key+">"+availableRoles[key]+"</option>").appendTo(cj('#userRoleRightSelect'));
								} 
								for ( var key in selectedRoles ){
									cj("<option value="+key+">"+selectedRoles[key]+"</option>").appendTo(cj('#userRoleLeftSelect'));
								}
							}
						});
						
						cj.ajax({
							type : "POST",
							url : "/asyncAdmin/retrieveUserStatus",
							data:{name:ui.item.label},
							success : function(response) {
								
								var status = response.result;
								
								if(status == "1"){
									cj("#statusEnable").attr('checked', true);
								}else{
									cj("#statusDisable").attr('checked', true);
								}
							}
						});
						
						cj("#userStatusBlock").slideDown(1000);
						cj("#userTeamBlock").slideDown(1000);
						cj("#userRoleBlock").slideDown(1000);
					},
					search:function(event,ui){
						cj("#userStatusBlock").slideUp();
						cj("#userTeamBlock").slideUp();
						cj("#userRoleBlock").slideUp();
					}
				});
			}
		});

		var statusEnable = cj('#statusEnable');
		statusEnable.unbind("click");
		statusEnable.click(function() {
			userStatusSave();
		});
		
		var statusDisable = cj('#statusDisable');
		statusDisable.unbind("click");
		statusDisable.click(function() {
			userStatusSave();
		});
		
		var userTeamAdd = cj('#userTeamAdd');
		userTeamAdd.unbind("click");
		userTeamAdd.click(function() {
			cj('#userTeamRightSelect option:selected').appendTo('#userTeamLeftSelect');
			userTeamSave();
		});
		
		var userTeamRemove = cj('#userTeamRemove');
		userTeamRemove.unbind("click");
		userTeamRemove.click(function() {
			cj('#userTeamLeftSelect option:selected').appendTo('#userTeamRightSelect');
			userTeamSave();
		});
		
		var userRoleAdd = cj('#userRoleAdd');
		userRoleAdd.unbind("click");
		userRoleAdd.click(function() {
			cj('#userRoleRightSelect option:selected').appendTo('#userRoleLeftSelect');
			userRoleSave();
		});
		
		var userRoleRemove = cj('#userRoleRemove');
		userRoleRemove.unbind("click");
		userRoleRemove.click(function() {
			cj('#userRoleLeftSelect option:selected').appendTo('#userRoleRightSelect');
			userRoleSave();
		});
	}
	
	function userTeamSave(){
		cj("#manageUserMsg").empty();
		var selectedOpt = "";
		cj('#userTeamLeftSelect option').each(function(){
			selectedOpt += cj(this).val() + ",";
		});
		var userName = cj("#targetUser").val();
		cj.ajax({
			type : "POST",
			url : "/asyncAdmin/saveTeamsPrivilege",
			data : {name:userName,"selectedOpt":selectedOpt},
			success : function(response) {
				var msg = cj("#manageUserMsg");
				if(response.status == "SUCCESS"){
					msg.text("update team successful!");
				}else{
					msg.text("update team fail!");
				}
			}
		});
	}
	
	function userRoleSave(){
		cj("#manageUserMsg").empty();
		var selectedOpt = "";
		cj('#userRoleLeftSelect option').each(function(){
			selectedOpt += cj(this).val() + ",";
		});
		var userName = cj("#targetUser").val();
		cj.ajax({
			type : "POST",
			url : "/asyncAdmin/saveRolesPrivilege",
			data : {name:userName,"selectedOpt2":selectedOpt},
			success : function(response) {
				var msg = cj("#manageUserMsg");
				if(response.status == "SUCCESS"){
					msg.text("update role successful!");
				}else{
					msg.text("update role fail!");
				}
			}
		});
	}
	
	function userStatusSave(){
		cj("#manageUserMsg").empty();
		var status=cj('input:radio[name="status"]:checked').val();
		var userName = cj("#targetUser").val();
		cj.ajax({
			type : "POST",
			url : "/asyncAdmin/saveUserStatus",
			data : {name:userName,"status":status},
			success : function(response) {
				var msg = cj("#manageUserMsg");
				if(response.status == "SUCCESS"){
					msg.text("update status successful!");
				}else{
					msg.text("update status fail!");
				}
			}
		});
	}
	
	
});