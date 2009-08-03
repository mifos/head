<%--
Copyright (c) 2005-2009 Grameen Foundation USA
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

<%@ taglib uri="http://struts.apache.org/tags-html-el" prefix="html-el"%>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="/tags/mifos-html" prefix = "mifos"%>
<%@ taglib uri="/mifos/officetags" prefix="office"%>
<%@ taglib uri="/mifos/custom-tags" prefix="customtags"%>
<%@ taglib uri="/sessionaccess" prefix="session"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>

<script language="javascript">
  function goToCancelPage(){
	groupTransferActionForm.submit();
  }
</script>

<tiles:insert definition=".clientsacclayoutsearchmenu">
 <tiles:put name="body" type="string">
 <span id="page.id" value="GroupChooseBranch" />
<fmt:setLocale value='${sessionScope["LOCALE"]}'/>
<fmt:setBundle basename="org.mifos.config.localizedResources.GroupUIResources"/>
<html-el:form action="groupTransferAction.do?method=cancel" >
<html-el:hidden property="currentFlowKey" value="${requestScope.currentFlowKey}" />
<c:set var="BusinessKey" value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'BusinessKey')}"/>
  <table width="95%" border="0" cellpadding="0" cellspacing="0">
        <tr>
          <td class="bluetablehead05">
             <span class="fontnormal8pt">
		   		<customtags:headerLink/>
            </span>
		  </td>
        </tr>
      </table>
      <table width="95%" border="0" cellpadding="0" cellspacing="0">
        <tr>
          <td align="left" valign="top" class="paddingL15T15" >
            <table width="95%" border="0" cellpadding="0" cellspacing="0">
              <tr>
                <td width="83%" class="headingorange">
                <span class="heading">
                	<c:out value="${BusinessKey.displayName}"/> 
                -</span> 
                <mifos:mifoslabel name="Group.editOfficeMembership" bundle="GroupUIResources"/>
                
                </td>
              </tr>
            </table>
            <br>
             
            <table width="95%" border="0" cellpadding="3" cellspacing="0">
              <tr class="fontnormal">
                <td width="94%">
                <span class="fontnormalbold">
                <fmt:message key="Group.choosebranch">
				<fmt:param><mifos:mifoslabel name="${ConfigurationConstants.BRANCHOFFICE}" /></fmt:param>
				</fmt:message>
                	 - 
                </span>
                <span class="fontnormal"> 
                  <fmt:message key="Group.transferbranchMsg1">
				  <fmt:param><mifos:mifoslabel name="${ConfigurationConstants.GROUP}" /></fmt:param>
				  <fmt:param><mifos:mifoslabel name="${ConfigurationConstants.BRANCHOFFICE}" /></fmt:param>
				  </fmt:message>
				  <fmt:message key="Group.transferbranchMsg2">
				  <fmt:param><mifos:mifoslabel name="${ConfigurationConstants.GROUP}" /></fmt:param>
				  </fmt:message>  
                </span></td>
              </tr>
            </table>
           
            <office:OfficeListTag methodName="previewBranchTransfer" actionName="groupTransferAction.do" onlyBranchOffices="yes" flowKey="${requestScope.currentFlowKey}"/>
            <table width="95%" border="0" cellpadding="0" cellspacing="0">
              <tr>
                <td align="center" class="blueline">&nbsp;</td>
              </tr>
            </table>
            <br>
            <table width="95%" border="0" cellpadding="0" cellspacing="0">
              <tr>
                <td align="center">
					  <html-el:submit   styleClass="cancelbuttn" >
						  <mifos:mifoslabel styleId="choosebranch.button.cancel" name="button.cancel" bundle="GroupUIResources"></mifos:mifoslabel>
					  </html-el:submit>
                </td></tr>
            </table>
            <br>
          </td>
        </tr>
    </table>
</html-el:form>
</tiles:put>
</tiles:insert>

