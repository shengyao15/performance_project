<div id="role3">
    <fieldset class="adminFieldset">
		<legend class="adminFieldsetTitle">Manage Role Information</legend>
		<div><label for="targetRole">Please specify a role to manage:</label></div>
		<div id="manageRoleMsg" class="adminMsg"></div>
		<input class="dialog-input-text" type="text" id="targetRole" placeholder="eg:admin">
		<div id="roleInfoBlock">
			<ul>
				<li>
					<input class="dialog-input-text" type="text" id="manageRoleDisplayName" placeholder="Role Display Name">
					<div id="updateRoleBtn" class="dialog-btn"/>
				</li>
			</ul>
		</div>
	</fieldset>
	
	<div id="roleMemberBlock">
		<fieldset class="adminFieldset">
			<legend class="adminFieldsetTitle">Manage Role Users</legend>
			<span class="adminInlineBlock">
				<p>Current Role Users:</p>
				<select id="roleLeftBox" multiple="multiple" class="adminShuttleBox"></select>
			</span>
			<span class="adminInlineBlock">
				<span class="transferBtnContainer">
					<span class="transferBtn" id="roleMoveToRightBtn">&gt;&gt;</span><br>
					<span class="transferBtn" id="roleMoveToLeftBtn">&lt;&lt;</span>
				</span>
			</span>
			<span class="adminInlineBlock">
				<p>Available Role Users:</p>
				<select id="roleRightBox" multiple="multiple" class="adminShuttleBox"></select>
			</span>
		</fieldset>
	</div>
</div>