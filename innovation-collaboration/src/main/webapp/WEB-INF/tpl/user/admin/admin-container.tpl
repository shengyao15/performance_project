<div class="admin-container">
<!--
<h3 class="docs" style="font-size: 20px">Admin Control Panel:</h3>
-->
	<div id="accordionResizer" class="ui-widget-content">
		<div id="accordionAdmin">
			<h3>
				<a href="#">Users</a>
			</h3>
			<div>
				<ol id="userSelect">
					<li class="ui-widget-content">Manage User</li>
				</ol>
			</div>


			<h3>
				<a href="#">Roles</a>
			</h3>
			<div>
				<ol id="roleSelect">
					<li class="ui-widget-content">View Role</li>
					<li class="ui-widget-content">Add Role</li>
					<li class="ui-widget-content">Manage Role</li>
				</ol>
			</div>

			<h3>
				<a href="#">Teams</a>
			</h3>
			<div>
				<ol id="teamSelect">
					<li class="ui-widget-content">Add Team</li>
					<li class="ui-widget-content">Manage Team</li>
				</ol>
			</div>

		</div>

		<span class="ui-icon ui-icon-grip-dotted-horizontal"
			style="margin: 2px auto;"></span>
	</div>
	<!-- End accordionResizer -->

	<div id="content" class="ui-widget-content">	
		<#include "adminPanels/admin-manageuser.tpl">
		
		<#include "adminPanels/admin-viewrole.tpl">
		<#include "adminPanels/admin-addrole.tpl">
		<#include "adminPanels/admin-managerole.tpl">
		
		<#include "adminPanels/admin-addteam.tpl">
		<#include "adminPanels/admin-manageteam.tpl">
	</div>

	<p style="clear:both;"></p>
</div>