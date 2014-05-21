<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">  
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<meta http-equiv="pragma" content="no-cache" />
	<meta http-equiv="Cache-Control" content="no-cache" />
	<link rel="stylesheet" type="text/css" href="/resources/style/css/common.css?4945">
	<link rel="stylesheet" type="text/css" href="/resources/style/css/home.css?4946">
	<link rel="stylesheet" type="text/css" href="/resources/style/css/jqueryui/jquery.ui.all.css?4945">
	<link rel="stylesheet" type="text/css" href="/resources/style/css/digital-clock.css?49411">
	<title>Collaboration</title>
</head>
<body>
<div class="home-container">


<div id="cslider">
    <div id="csliderContent">
    	<div class="home-optbar">
    		<label class="opt-title">INNOVATION COLLEBORATION</label>
				<#if !currentUser??>
					<div id="register-opener" class="opt-btn">JOIN US</div>
					<div id="login-opener" class="opt-btn">SIGN IN</div>
				<#else>
					<div id="back-dashboard" class="opt-btn">DASHBOARD</div>
				</#if>
		</div>

		<#include "user/login-dialog.tpl">
		<#include "user/register-dialog.tpl">
		<div id="clock-container">
		<div class="clock">
		   <div id="Date"></div>
		      <ul>
		          <li id="hours"></li>
		          <li id="point">:</li>
		          <li id="min"></li>
		          <li id="point">:</li>
		          <li id="sec"></li>
		      </ul>
		</div>
		</div>
		
        <div class="csliderImage">
            <img src="/resources/style/img/home_bg1.jpg">
            <span class="csliderTextBottom csliderTextBottomOffset">We make what you need before you think, and beyond you think!</span>
        </div>
        <div class="csliderImage">
            <img src="/resources/style/img/home_bg2.jpg">
            <span class="csliderTextBottom csliderTextBottomOffset">Your text comes here</span>
        </div>
        <div class="csliderImage">
            <img src="/resources/style/img/home_bg3.jpg">
            <span class="csliderTextBottom csliderTextBottomOffset">Your text comes here</span>
        </div>
        <div class="clear csliderImage"></div>
    </div>
</div>
</div>

<script src="/resources/script/js/jquery/jquery-1.8.1.js?4945" type="text/javascript"></script>
<script src="/resources/script/js/jqueryui/jquery-ui-1.8.23.custom.js?4945" type="text/javascript"></script>
<script src="/resources/script/js/jqueryui/jquery.effects.slide.js?4945" type="text/javascript"></script>
<script src="/resources/script/js/jqueryui/jquery.effects.core.js?4945" type="text/javascript"></script>
<script src="/resources/script/js/jqueryui/jquery.effects.bounce.js?4945" type="text/javascript"></script>
<script src="/resources/script/js/jqueryui/jquery.ui.widget.js?4945" type="text/javascript"></script>
<script src="/resources/script/js/jqueryui/jquery.ui.mouse.js?4945" type="text/javascript"></script>
<script src="/resources/script/js/jqueryui/jquery.ui.sortable.js?4945" type="text/javascript"></script>
<script src="/resources/script/js/jqueryui/jquery.ui.accordion.js?4945" type="text/javascript"></script>

<script src="/resources/script/js/jqueryui/jquery.ui.draggable.js"></script>
<script src="/resources/script/js/jqueryui/jquery.ui.position.js"></script>
<script src="/resources/script/js/jqueryui/jquery.ui.resizable.js"></script>
<script src="/resources/script/js/jqueryui/jquery.ui.dialog.js"></script>

<script src="/resources/script/js/components/jquery.blockUI.js" type="text/javascript"></script>
<script src="/resources/script/js/components/cSlider.js" type="text/javascript"></script>

<script src="/resources/script/js/common.js?4945" type="text/javascript"></script>
<script src="/resources/script/js/home.js" type="text/javascript"></script>
</body>
</html>