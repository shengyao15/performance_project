<div id="dashboardSummary" class="dashboard-summary">
	<div class="title">
		
	</div>
	
	<div class="content">
	<div id="accordion">
	<div class="group summary-container">
		<#assign userGender = currentUser.gender!"N/A">
		<h3><a href="#">My Profile</a></h3>
		<div class="summary-content">
			<img class="user-head-standard" src="${userHead}">
			<div class="user-profile">
			<ul>
				<li><span class="item">Name:</span> ${userName}</li>
			<#if currentUser.teams??>
				<li>
					<span class="item">Team:</span>
					<#list currentUser.teams as team>
						${team.teamName!""}
					</#list>
				</li>
			</#if>
				<li><span class="item">Gender:</span>
				<#if userGender = "m">
				<image class="gender-icon" src="/resources/style/img/male_icon.png">
				<#elseif userGender = "f">
				<image class="gender-icon" src="/resources/style/img/female_icon.png">
				<#else>
				${userGender}
				</#if>
				</li>
				<li><span class="item">Email:</span> ${currentUser.email!"N/A"}</li>
				<li><span class="item">Status:</span> ${currentUser.status?string!"Active"}</li>
			</ul>
			</div>
		</div>
	</div>
	<div class="group">
		<h3><a href="#">My Skills</a></h3>
		<div>
			<ul>
				<li>Java: x</li>
				<li>DB: x</li>
				<li>Portlet: x</li>
				<li>Freemarker: x</li>
				<li>Javascript: x</li>
			</ul>
		</div>
	</div>
	<div class="group">
		<h3><a href="#">My TODO List</a></h3>
		<div>
			<p>On the Insert tab, the galleries include items that are designed to coordinate with the overall look of your document. You can use these galleries to insert tables, headers, footers, lists, cover pages, and other document building blocks. When you create pictures, charts, or diagrams, they also coordinate with your current document look.
You can easily change the formatting of selected text in the document text by choosing a look for the selected text from the Quick Styles gallery on the Home tab. You can also format text directly by using the other controls on the Home tab. Most controls offer a choice of using the look from the current theme or using a format that you specify directly.
To change the overall look of your document, choose new Theme elements on the Page Layout tab. To change the looks available in the Quick Style gallery, use the Change Current Quick Style Set command. Both the Themes gallery and the Quick Styles gallery provide reset commands so that you can always restore the look of your document to the original contained in your current template.
			</p>
			<ul>
				<li>List item one</li>
				<li>List item two</li>
				<li>List item three</li>
			</ul>
		</div>
	</div>
	<div class="group">
		<h3><a href="#">My Records</a></h3>
		<div>
			<ul>
				<li>List item one</li>
				<li>List item two</li>
				<li>List item three</li>
			</ul>
		</div>
	</div>
	</div>
	</div>
</div>