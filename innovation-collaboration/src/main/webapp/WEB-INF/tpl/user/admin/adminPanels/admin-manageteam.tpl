<div id="team2">
	<fieldset class="adminFieldset">
		<legend class="adminFieldsetTitle">Manage Team Information</legend>
		<div><label for="targetTeam">Please specify a team to manage:</label></div>
		<div id="manageTeamMsg" class="adminMsg"></div>
		<input class="dialog-input-text" type="text" id="targetTeam" placeholder="eg:sopher">
		<select id="selManageParentTeam" class="selParentTeam"></select>
		<div id="teamInfoBlock">
			<ul>
				<li>
					<input class="dialog-input-text" type="text" id="manageTeamDisplayName" placeholder="Team Display Name">
					<div id="updateTeamBtn" class="dialog-btn"/>
				</li>
			</ul>
		</div>
	</fieldset>
	
	<div id="teamMemberBlock">
		<fieldset class="adminFieldset">
			<legend class="adminFieldsetTitle">Manage Team Members</legend>
			<span class="adminInlineBlock">
				<p>Current Members:</p>
				<select id="teamLeftBox" multiple="multiple" class="adminShuttleBox"></select>
			</span>
			<span class="adminInlineBlock">
				<span class="transferBtnContainer">
					<span class="transferBtn" id="teamMoveToRightBtn">&gt;&gt;</span><br>
					<span class="transferBtn" id="teamMoveToLeftBtn">&lt;&lt;</span>
				</span>
			</span>
			<span class="adminInlineBlock">
				<p>Available Members:</p>
				<select id="teamRightBox" multiple="multiple" class="adminShuttleBox"></select>
			</span>
		</fieldset>
	</div>
	
	<div id="subTeamsBlock">
		<fieldset class="adminFieldset">
			<legend class="adminFieldsetTitle">Current Team's Sub Teams</legend>
			<select id="subTeams" multiple="multiple" class="adminShuttleBox"></select>
		</fieldset>
	</div>
</div>