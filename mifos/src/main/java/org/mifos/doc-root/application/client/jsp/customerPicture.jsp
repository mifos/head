<!--
/**

* createClientPersonalInfo.jsp    version: 1.0



* Copyright (c) 2005-2006 Grameen Foundation USA

* 1029 Vermont Avenue, NW, Suite 400, Washington DC 20005

* All rights reserved.



* Apache License
* Copyright (c) 2005-2006 Grameen Foundation USA
*

* Licensed under the Apache License, Version 2.0 (the "License"); you may
* not use this file except in compliance with the License. You may obtain
* a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
*

* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and limitations under the

* License.
*
* See also http://www.apache.org/licenses/LICENSE-2.0.html for an explanation of the license

* and how it is applied.

*

*/
 -->
<%@taglib uri="/tags/mifos-html" prefix = "mifos"%>
<%@taglib uri="/tags/date" prefix="date"%>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles" %>
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@taglib uri="http://struts.apache.org/tags-bean-el" prefix="bean-el"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://struts.apache.org/tags-html-el" prefix="html-el"%>
<%@ taglib uri="/sessionaccess" prefix="session"%>
<html>
	<head>
		<title>Mifos </title>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
		<link href="pages/framework/css/cssstyle.css" rel="stylesheet" type="text/css">
		<script>
			function resizeWindow(){
				window.moveTo(0, 0);
				window.resizeTo(250,200);
		  	}
		  	function goToGetPage(){
				clientCustActionForm.action="clientCustAction.do?method=get";
				clientCustActionForm.submit();
			  }
		</script>
	</head>
	
	<body>
	<script language="javascript">
	
	function goToCancelPage(){
		//clientCustActionForm.action="clientCustAction.do?method=get";
		//clientCustActionForm.submit();
		window.close()
		
	  }
	  
	  
	  
	</script>
		<html-el:form action="clientCustAction.do?method=get" >
			<c:set value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'BusinessKey')}"
			   var="BusinessKey" />
			<html-el:hidden property="currentFlowKey" value="${requestScope.currentFlowKey}" />
			<table width="100%" border="0" cellspacing="0" cellpadding="0">
			  <tr>
			    <td width="188" colspan="2" class="bgorange"><img src="pages/framework/images/trans.gif" width="6" height="6"></td>
			  </tr>
			  <tr>
			    <td colspan="2" class="bgwhite"><img src="pages/framework/images/trans.gif" width="100" height="2"></td>
			  </tr>
			</table>
			<table width="100%" border="0" cellspacing="0" cellpadding="0">
			  <tr>
			    <td align="left" valign="top" bgcolor="#FFFFFF">      <table width="90%" border="0" align="center" cellpadding="0" cellspacing="0">
			      <tr>
			        <td align="center" class="heading"><img src="pages/framework/images/trans.gif" width="10" height="10"></td>
			      </tr>
			      <tr>
			        <td align="center" class="heading"><c:out value="${BusinessKey.displayName}"/></td>
			      </tr>
			      <tr>
			        <td><img src="pages/framework/images/trans.gif" width="10" height="10"></td>
			      </tr>
			    </table>              
			    <table width="90%" border="0" align="center" cellpadding="0" cellspacing="0">
			        <tr>
			          <td align="center" valign="top">
			          	<img src="/mifos/clientCustAction.do?method=retrievePicture&currentFlowKey=${requestScope.currentFlowKey}"
											height="100" width="150" />
			           <%--	<img src="/mifos/clientCustAction.do?method=retrievePicture&customerId=${requestScope.clientVO.customerId}" height= "150" width="150"/> --%></td>
                    </tr>
			        <tr>
			          <td align="center" valign="top" class="paddingleft">
			  		  	<html-el:button styleId="customerPicture.button.cancel" onclick="goToCancelPage()" property = "cancelButton" styleClass="cancelbuttn">
					  	<mifos:mifoslabel name="button.close" bundle="ClientUIResources"></mifos:mifoslabel></html-el:button>
			 
			  			<br>
			  			<br>
			  		  </td>
			  		</tr>
			     </table>      
			     <br>
			   </td>
			 </tr>
		   </table>
	    </html-el:form>
	</body>
</html>
