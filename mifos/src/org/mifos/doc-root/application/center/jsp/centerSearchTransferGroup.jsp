<!--
/**

* centerSearchTransferGroup.jsp    version: 1.0



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
<%@taglib uri="http://struts.apache.org/tags-html-el" prefix="html-el"%>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@taglib uri="http://struts.apache.org/tags-bean-el" prefix="bean-el"%>
<%@ taglib uri="/mifos/custom-tags" prefix="customtags"%>
<%@ taglib uri="/sessionaccess" prefix="session"%>

<script language="javascript">
  function goToCancelPage(){
	groupTransferActionForm.action="groupTransferAction.do?method=cancel";
	groupTransferActionForm.submit();
  }
</script>

<html-el:form method="post" action ="centerAction.do?method=search">
<c:set var="BusinessKey" value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'BusinessKey')}"/>
<html-el:hidden property="input" value="CenterSearch_TransferGroup"/> 
<html-el:hidden property="currentFlowKey" value="${requestScope.currentFlowKey}" />
	<table width="95%" border="0" cellpadding="0" cellspacing="0">
        <tr>
          <td class="bluetablehead05">
	          <span class="fontnormal8pt">
	            	<customtags:headerLink/>
	          </span>
          </td>
        </tr>
      </table>
      <table width="95%" border="0" cellpadding="0" cellspacing="0" >
        <tr>
          <td width="70%" align="left" valign="top" class="paddingL15T15">
          	<table width="96%" border="0" cellpadding="3" cellspacing="0">
          		 <tr>
                <td width="62%" class="headingorange"><span class="heading">
                	<c:out value="${BusinessKey.displayName}"/>-
                 	</span>
                 	<mifos:mifoslabel name="Center.change"  bundle="CenterUIResources"/>
                 	<mifos:mifoslabel name="${ConfigurationConstants.CENTER}"/>
                 	<mifos:mifoslabel name="Center.Membership"  bundle="CenterUIResources"/>
                </td>
              </tr>
              </table>
          	<table width="96%" border="0" cellspacing="3" cellpadding="3">
              
              <tr>
                <td class="fontnormalbold"> <span class="fontnormal">
                <mifos:mifoslabel name="Center.changeCenterMembershipInstruction1"  bundle="CenterUIResources"/>
                <mifos:mifoslabel name="${ConfigurationConstants.CENTER}"/>
                <mifos:mifoslabel name="Center.changeCenterMembershipInstruction2"  bundle="CenterUIResources"/>
                <mifos:mifoslabel name="${ConfigurationConstants.GROUP}"/>
                <mifos:mifoslabel name="Center.changeCenterMembershipInstruction3"  bundle="CenterUIResources"/>
                </span>
                  </td>
              </tr>
            </table>
            <table width="96%" border="0" cellspacing="0" cellpadding="3">
					 <font class="fontnormalRedBold"><html-el:errors bundle="CenterUIResources" /> </font><br>
					 <tr class="fontnormal">
                 	    <td align="right"><mifos:mifoslabel name="${ConfigurationConstants.CENTER}"/><c:out value=" "/>
							<mifos:mifoslabel name="Center.Name" bundle="CenterUIResources"></mifos:mifoslabel></td>
		                <td>
		                  <html-el:text property="searchNode(searchString)" maxlength = "200"/>
			              <html-el:hidden property="searchNode(search_name)" value="SearchCenter"/>
		                </td>
	                </tr>
	                <tr class="fontnormal">
                  		<td width="20%">&nbsp; </td>
                  		<td width="80%"><br>
	                     <html-el:submit styleClass="buttn" style="width:70px;"><mifos:mifoslabel name="button.Search" bundle ="CenterUIResources"></mifos:mifoslabel></html-el:submit>						  &nbsp;
	      				  <html-el:button property="cancelButton" onclick="goToCancelPage();" styleClass="cancelbuttn" style="width:70px;"><mifos:mifoslabel name="button.cancel" bundle ="CenterUIResources"></mifos:mifoslabel> </html-el:button>
                  			<br><br>
                  		</td>
                	</tr>
                	</table>
				
					         
              <br>
          </td>
        </tr>
      </table>
      <br>
</html-el:form>


