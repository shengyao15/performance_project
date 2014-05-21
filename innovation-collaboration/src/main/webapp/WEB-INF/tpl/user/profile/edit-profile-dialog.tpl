<div id="edit-my-profile" style="display:none;">
	<ul>
		<img class="user-head-standard" src="${userHead}">
		<li>
			<input class="dialog-input-text" id="profile-username" name="profile-username" type="text" placeholder='${currentUser.displayName!"Enter your display name!"}'/>
			<em style="top: -65px;">tips message</em>
		</li>
		<li>
			<input class="dialog-input-text" id="profile-gender" name="profile-gender" type="text" placeholder='${currentUser.gender!"Enter your gender!"}'/>
			<em style="top: -65px;">tips message</em>
		</li>
		<li>
			<input class="dialog-input-text" id="profile-email" name="profile-email" type="text" placeholder='${currentUser.email!"Enter your email!"}'/>
			<em style="top: -65px;">tips message</em>
			<div id="editProfileGo" class="dialog-btn"/>
		</li>
	</ul>
</div>