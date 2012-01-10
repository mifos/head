<%--
Copyright (c) 2005-2011 Grameen Foundation USA
All rights reserved.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
implied. See the License for the specific language governing
permissions and limitations under the License.

See also http://www.apache.org/licenses/LICENSE-2.0.html for an
explanation of the license and how it is applied.
--%>


<%@page import="java.math.BigDecimal"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="/tags/mifos-html" prefix="mifos"%>
<%@taglib uri="http://struts.apache.org/tags-html-el" prefix="html-el"%>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles"%>
<%@taglib uri="/tags/date" prefix="date"%>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic"%>
<%@ taglib uri="/sessionaccess" prefix="session"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>

<head>

<style>
tr.even {
  background-color: #ddd;
}
tr.odd {
  background-color: #eee;
}

div.scroll {
height: 80%;
width: 100%;
overflow: auto;
float:left;
padding: 0px;
position:relative;
}
table.pkgtable td
{
border-top: 1px dotted #6699CC;
}
table.pkgtable tr.pkgtableheaderrow
{
border-bottom: 2px solid #6699CC;
background-color: #BEC8D1;
text-align: center;
font-family: Verdana;
font-weight: bold;
font-size: 13px;
color: #404040;

</style>
</head>
<span id="page.id" title="CustomerList"></span>
<script src="pages/js/jquery/jquery-1.4.2.min.js"></script>
<!-- <script language="javascript" type="text/javascript" src="https://ajax.googleapis.com/ajax/libs/jquery/1.4.2/jquery.min.js"></script> -->
<script language="javascript">

/* function print() {
    alert("hello");	
	window.print();
	window.close();
} */

</script>
<fmt:setLocale value='${sessionScope["org.apache.struts.action.LOCALE"]}'/>
<fmt:setBundle basename="org.mifos.config.localizedResources.BulkEntryUIResources"/>
</head>
    <table align="center">
    <tr><td>
    <h3>Customer print receipt </h3></td></tr>
    </br></br></br> 
     </table>
	<table align="center">
	<tr>
      <th colspan="1">Organization Name :</th><td>Hugo technologies</td>
      </tr>
      <tr>
      <th colspan="1">Branch Info :</th><td>${sessionScope.branchId}</td>
      </tr>
      <tr>
      <th colspan="1">Group Info :</th><td>${sessionScope.groupId}</td>
      </tr>
  
      <tr>
      <th colspan="1">Loan Officer :</th><td>${sessionScope.loanoffId}</td>
      </tr>
     
    <tr>
     <th colspan="1">Member ID: </th><td>${sessionScope.customerId} </td> <th colspan="5">Member Name :</th><td>${sessionScope.membername}</td>
      
      </tr>
   <tr >
   <th colspan="1">Receipt Date :</th><td>${sessionScope.transactionDate }</td><th colspan="5">Amount Collected:</th><td>${sessionScope.amountTotal}</td>
   </tr>
   
 
   </table><br>
   <table align="center">
   <tr>
   <td><h4>Collections details</h4></td>
   </tr>
   </table>
   <table border='1' align="center">
   		<th width="25%">Product Details</th>
   		<th width="25%">principal</th>
   		<th width="25%">interest</th>
   		<th width="40%">Amount</th>
   		
      <c:forEach items="${sessionScope.accountPayements}" var="accountId">
      <tr>
      
        <td>${accountId.productName}</td>
       	<td align="right">${accountId.principal}</td>
       	<td align="right">${accountId.interest}</td>
       	<td align="right">${accountId.amount}</td>
       	
      </tr>
      </c:forEach>
      
   </tr>
   <tr>
   <td>Total:</td>
   <td align="right">${sessionScope.principalSum}</td>
  <td align="right">${sessionScope.interestSum}</td>
  <td align="right">${sessionScope.amountTotal}</td> 
   </tr>
</table>
<br>
	<table align="right"><tr><td>Authorized Signatory</td></tr></table>
										<table align="center"><tr>
												<!-- <td><input type="button" value="print priview" onclick='javascript:printerFormat();'/></td> -->
												<td><input type='button' value='print' onclick='window.print()'/></td>
												</tr>
												
										</table>
<%-- </html-el:form> --%>
