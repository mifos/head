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

<tiles:insert definition=".withoutmenu">
<tiles:put name="body" type="string" >  
<span id="page.id" title="CustomerList"></span>
<script src="pages/js/jquery/jquery-1.4.2.min.js"></script>
<!-- <script language="javascript" type="text/javascript" src="https://ajax.googleapis.com/ajax/libs/jquery/1.4.2/jquery.min.js"></script> -->
<script language="javascript">

	function SendValueToParent(val1,val2){
		    window.opener.getValueFromPopupWindow(val1,val2);
		    window.close();
		    return false;
	}   
			
			
	
	jQuery.expr[":"].containsNoCase = function(el, i, m) {
		var search = m[3];
		if (!search)
			return false;
		return eval("/" + search + "/i").test($(el).text());
	};
	jQuery(document)
			.ready(
					function() {
						// used for the first example in the blog post
						jQuery('li:contains(\'DotNetNuke\')').css('color',
								'#0000ff').css('font-weight', 'bold');
						// hide the cancel search image
						jQuery('#imgSearch').hide();
						// reset the search when the cancel image is clicked
						jQuery('#imgSearch').click(function() {
							resetSearch();
						});
						// cancel the search if the user presses the ESC key
						jQuery('#txtSearch').keyup(function(event) {
							if (event.keyCode == 27) {
								resetSearch();
							}
						});
						// execute the search
						//Mac Address Search
						jQuery('#txtSearch')
								.keyup(
										function() {
											// only search when there are 3 or more characters in the textbox
											if (jQuery('#txtSearch').val().length >= 1) {
												// hide all rows
												jQuery('#tblSearch tr').hide();
												// show the header row
												jQuery('#tblSearch tr:first')
														.show();
												// show the matching rows (using the containsNoCase from Rick Strahl)
											
												jQuery(
														'#tblSearch tr td:nth-child(2):containsNoCase(\''
																+ jQuery(
																		'#txtSearch')
																		.val()
																+ '\')')
														.parent().show();
												// show the cancel search image
												jQuery('#imgSearch').show();
											} else if (jQuery('#txtSearch')
													.val().length == 0) {
												// if the user removed all of the text, reset the search
												resetSearch();
											}
											// if there were no matching rows, tell the user
											if (jQuery('#tblSearch tr:visible').length == 1) {
												// remove the norecords row if it already exists
												jQuery('.norecords').remove();
												// add the norecords row
												jQuery('#tblSearch')
														.append(
																'<tr class="norecords"><td colspan="5" class="Normal">No records were found</td></tr>');
											}
										});
						
					});
	function resetSearch() {
		// clear the textbox
		jQuery('#txtSearch').val('');
		jQuery('#serialTxtSearch').val('');
		// show all table rows
		jQuery('#tblSearch tr').show();
		// remove any no records rows
		jQuery('.norecords').remove();
		// remove the cancel search image
		jQuery('#imgSearch').hide();
		// make sure we re-focus on the textbox for usability
		jQuery('#serialTxtSearch').focus();
	}
</script>
		
<fmt:setLocale value='${sessionScope["org.apache.struts.action.LOCALE"]}'/>
<fmt:setBundle basename="org.mifos.config.localizedResources.BulkEntryUIResources"/>
<%-- <html-el:form action="collectionsheetaction"> --%>


<fieldset style="width: 900px;">
<legend>Search</legend>
<p style="text-align: left;">
<span style="font-weight: normal"><mifos:mifoslabel name="bulkEntry.membName" ></mifos:mifoslabel></div></span>
<input type="text" id="txtSearch" name="txtSearch" style="width:272px" maxlength="60"/>
&#160;<img style="width: 14px; height: 12px" id="imgSearch" title="Cancel Search" alt="Cancel Search" src="images/cancel.gif" />
</p>
</fieldset>

<table id="tblSearch" border=1 class="pkgtable" border=0 width="100%" cellpadding="0" cellspacing="0">
<COL WIDTH="15%"><COL WIDTH="45%"><COL WIDTH="40%">
<tr class="pkgtableheaderrow">            
			<th align="left">Customer Id</th>
			<th align="left">Customer Name</th>                
			<th align="left">Global Customer No</th>			
</tr>
<c:forEach items="${sessionScope.GroupMemberList}" var="groupMembers">
<tr >
<td align="left">${groupMembers.customerId}</td>
<td align="left">${groupMembers.displayName}</td>
<td align="left">
	
	<a href="#" onclick="javascript:return SendValueToParent('${groupMembers.displayName}','${groupMembers.customerId}');" >
	${groupMembers.globalCustNum}</a>
</td>
</tr>
</c:forEach>

</table>
<%-- </html-el:form> --%>

</tiles:put>
</tiles:insert>