<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en">
<head>
	<style type='text/css'>
	body{background:#fff;color:#333;font-size:12px; margin-top:5px;font-family:"Futura Bk","Arial Narrow";}
	th, td { text-align:center; font-size: 14px}
	.summary_header { background-color: #555555; color: #FFFFFF; }
	.detail_header { background-color: #777777; color: #FFFFFF;}
	.summary_content { background-color: #37c4d0; color: #000000; }
	.detail_content { background-color: #FFFFFF; color: #000000; }
	.content { text-align:left;background-color: gray; color: #FFFFFF; font-size: 14px;}
	#statistics span { padding-left: 20px; padding-right: 20px; margin-right: 20px; }
	</style>
</head>
<body>
	<h2 align="center">uCMDB CI Processing Report & Product Registry Report - ${reportDate}</h2>
	
	<h3>uCMDB CI Processing Report:</h3>
	<table width="100%" border="0" cellpadding="1" cellspacing="2" bgcolor="#F0F0F0">
		<tr class="summary_header">
			<th width="11%" scope="col">#</th>
			<th width="13%" scope="col">File Name</th>
			<th width="11%" scope="col">Data Source</th>
			<th width="11%" scope="col">Process Start Date</th>
			<th width="11%" scope="col">Total Records</th>
			<th width="11%" scope="col">Status</th>
			<th width="11%" scope="col">Successful Records</th>
			<th width="11%" scope="col">Failed Records</th>
		</tr>
<#list adapterList as adapter>		
		<tr class="summary_content">
			<td width="12%" scope="col">${adapter_index}</th>
			<td width="16%" scope="col">${adapter.fileName}</th>
			<td width="12%" scope="col">${adapter.fileDataSource}</th>
			<td width="12%" scope="col">${adapter.processStartDate}</th>
			<td width="12%" scope="col">${adapter.recordCount}</th>
			<td width="12%" scope="col">${adapter.fileStatus}</th>
			<td width="12%" scope="col">${adapter.recordSuccess}</th>
			<td width="12%" scope="col">${adapter.recordsinError}</th>
		</tr>
<#if adapter.adapterDetailList?size != 0>
		<tr class="detail_header">
			<td width="12%" scope="col"></th>
			<td width="12%" scope="col">Seq No.</th>
			<td width="16%" scope="col">Attribute Name</th>
			<td width="12%" scope="col">Attribute Value</th>
			<td width="12%" scope="col">Error No</th>
			<td width="36%" scope="col" colspan="3">Error Description</th>
		</tr>
</#if>		
<#list adapter.adapterDetailList as adapterDetail>
		<tr class="detail_content">
			<td width="12%" scope="col"></th>
			<td width="12%" scope="col">${adapterDetail.recordSeqNo}</th>
			<td width="16%" scope="col">${adapterDetail.attributeName}</th>
			<td width="12%" scope="col">${adapterDetail.attributeValue}</th>
			<td width="12%" scope="col">${adapterDetail.errorNbr}</th>
			<td width="36%" scope="col" colspan="3">${adapterDetail.errorMessage}</th>
		</tr>
</#list>		
</#list>	
	</table>
	
	<h3>Product Registry Report:</h3>
	<table width="100%" border="0" cellpadding="1" cellspacing="2" bgcolor="#F0F0F0">
		<tr class="summary_header">
			<th width="11%" scope="col">#</th>
			<th width="11%" scope="col">File Name</th>
			<th width="11%" scope="col">Data Source</th>
			<th width="11%" scope="col">Process Start Date</th>
			<th width="11%" scope="col">Total Records</th>
			<th width="11%" scope="col">Status</th>
			<th width="11%" scope="col">Successful Records</th>
			<th width="11%" scope="col">Failed Records</th>
			<td width="11%" scope="col"></th>
		</tr>
<#list prList as pr>
		<tr class="summary_content">
			<td width="11%" scope="col">${pr_index}</th>
			<td width="11%" scope="col">${pr.fileName}</th>
			<td width="11%" scope="col">${pr.fileDataSource}</th>
			<td width="11%" scope="col">${pr.processStartDate}</th>
			<td width="11%" scope="col">${pr.recordCount}</th>
			<td width="11%" scope="col">${pr.fileStatus}</th>
			<td width="11%" scope="col">${pr.recordSuccess}</th>
			<td width="11%" scope="col">${pr.recordsinError}</th>
			<td width="11%" scope="col"></th>
		</tr>
<#if pr.prDetailList?size != 0>
		<tr class="detail_header">
			<td width="11%" scope="col"></th>
			<td width="11%" scope="col">Seq No.</th>
			<td width="11%" scope="col">CI Name</th>
			<td width="11%" scope="col">External ID</th>
			<td width="11%" scope="col">Original Brand</th>
			<td width="11%" scope="col">Original Model</th>
			<td width="11%" scope="col">PR Brand</th>
			<td width="11%" scope="col">PR Model</th>
			<td width="11%" scope="col">Record Status</th>
		</tr>
</#if>		
<#list pr.prDetailList as prDetail>
		<tr class="detail_content">
			<td width="11%" scope="col"></th>
			<td width="11%" scope="col">${prDetail.recordSeqNo}</th>
			<td width="11%" scope="col">${prDetail.name}</th>
			<td width="11%" scope="col">${prDetail.extId}</th>
			<td width="11%" scope="col">${prDetail.orgBrand}</th>
			<td width="11%" scope="col">${prDetail.orgModel}</th>
			<td width="11%" scope="col">${prDetail.prBrand}</th>
			<td width="11%" scope="col">${prDetail.prModel}</th>
			<td width="11%" scope="col">${prDetail.recordStatus}</th>
		</tr>
</#list>
</#list>	
	</table>
	
</body>
</html>