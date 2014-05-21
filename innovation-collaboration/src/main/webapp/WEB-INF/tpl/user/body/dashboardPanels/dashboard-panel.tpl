<#switch panel.type>
<#case "myprofile">
<#include "dashboard-myprofile.tpl">
<#break>
<#case "todoreminder">
<#include "dashboard-todoreminder.tpl">
<#break>
<#case "skillmatrix">
<#include "dashboard-skillmatrix.tpl">
<#break>
<#case "ad1">
<#include "dashboard-ad1.tpl">
<#break>
<#case "ad2">
<#include "dashboard-ad2.tpl">
<#break>
<#case "ad3">
<#include "dashboard-ad3.tpl">
<#break>
<#case "admin">
<#include "dashboard-admin.tpl">
<#break>
<#case "placeholder1">
<#include "dashboard-placeholder1.tpl">
<#break>
<#case "placeholder2">
<#include "dashboard-placeholder2.tpl">
<#break>
<#case "placeholder3">
<#include "dashboard-placeholder3.tpl">
<#break>
<#default>
<#include "dashboard-ad1.tpl">
</#switch>