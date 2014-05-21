cj("#editProfile").ready(function (){
	
	cj("#edit-my-profile").dialog(
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
			minWidth : 600,
			position : "center"
		}
	);
	
	cj("#editProfile").click(function(){
		cj("#edit-my-profile").dialog("open");
	});
});