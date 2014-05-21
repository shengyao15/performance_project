<div id="user1">	
	<fieldset class="adminFieldset">
		<legend class="adminFieldsetTitle">Manage User Information</legend>
		<div><label for="targetTeam">Please specify a user to manage:</label></div>
		<div id="manageUserMsg" class="adminMsg"></div>
		<input class="dialog-input-text" type="text" id="targetUser" placeholder="eg:tristan1@hp.com">
	</fieldset>
	
	
	<div id="userStatusBlock">
		<fieldset class="adminFieldset">
			<legend class="adminFieldsetTitle">User Status</legend>
			<input id="statusEnable" type="radio" name="status" value="1" /> Enable
			&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
			<input id="statusDisable" type="radio" name="status" value="0" /> Disable
		</fieldset>
	</div>
	
	<div id="userTeamBlock">
		<fieldset class="adminFieldset">
			<legend class="adminFieldsetTitle">Manage Team</legend>
			<span class="adminInlineBlock">
				<p>Current Team</p>
				<select id="userTeamLeftSelect" class="adminShuttleBox"  multiple="multiple" >
				</select>
			</span>
			<span class="adminInlineBlock">
				<span class="transferBtnContainer">
				    <span id="userTeamRemove" class="transferBtn">&gt;&gt;</span><br>
					<span id="userTeamAdd" class="transferBtn">&lt;&lt;</span>
				</span>
			</span>
			<span class="adminInlineBlock">
				<p>Available Team</p>
				<select id="userTeamRightSelect" class="adminShuttleBox"  multiple="multiple" >
				</select>
			</span>
		</fieldset>
	</div>
	
	<div id="userRoleBlock">
		<fieldset class="adminFieldset">
			<legend class="adminFieldsetTitle">Manage Role</legend>
			<span class="adminInlineBlock">
				<p>Current Role</p>
				<select id="userRoleLeftSelect" class="adminShuttleBox"  multiple="multiple" >
				</select>
			</span>
			<span class="adminInlineBlock">
				<span class="transferBtnContainer">
				    <span id="userRoleRemove" class="transferBtn">&gt;&gt;</span>
				    <br>
					<span id="userRoleAdd" class="transferBtn">&lt;&lt;</span>
				</span>
			</span>
			<span class="adminInlineBlock">
				<p>Available Role</p>
				<select id="userRoleRightSelect" class="adminShuttleBox" multiple="multiple" >
				</select>
			</span>
		</fieldset>
	</div>
</div>