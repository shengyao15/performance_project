<table border = 1>
<tr>
    <th>Count</th>
    <th>Report Date</th>
    <th>IP Address</th>
    <th>User Agent</th>
</tr>
<#list TopIpList as list>
<tr>
<td>${list.count}</td>
<td>${list.date}</td>	
<td>${list.ip}</td>
<td>${list.userAgent}</td>	

</tr>
</#list>
</#list>
</table> 