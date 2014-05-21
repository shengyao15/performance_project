<table border = 1>
<tr>
    <th>Feature Name</th>
    <th>Report Date</th>
    <th>Error Name</th>
    <th>Error Amount</th>
  </tr>
<#list StatisticErrorBeanList as list>
<tr>
<td>${list.featureName}</td>
<td>${list.collectDate}</td>	
<#list list.errorDetails?keys as key>
<td>${key}</td>
<td>${list.errorDetails[key]}</td>
</tr>
</#list>
</#list>
</table> 