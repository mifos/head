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
<%@ taglib uri="/mifos/custom-tags" prefix="customtags"%>
<%@ taglib uri="/sessionaccess" prefix="session"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<tiles:insert definition=".clientsacclayoutsearchmenu">
<tiles:put name="body" type="string">
<span style="display: none" id="page.id">center_search_results_transfer_group</span>
<fmt:setLocale value='${sessionScope["LOCALE"]}'/>
		<fmt:setBundle basename="org.mifos.config.localizedResources.CenterUIResources"/>
<script language="javascript">
  function goToCancelPage(){
	groupTransferActionForm.submit();
  }
</script>
<html-el:form method="post" action ="groupTransferAction.do?method=cancel">
	<html-el:hidden property="currentFlowKey" value="${requestScope.currentFlowKey}" />
</html-el:form>

<html-el:form action="centerCustAction.do?method=searchTransfer">
<c:set var="BusinessKey" value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'BusinessKey')}"/>
<html-el:hidden property="input" value="CenterSearch_TransferGroup"/> 
<html-el:hidden property="currentFlowKey" value="${param.currentFlowKey}" />
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
            <td width="70%" align="left" valign="top" class="paddingL15T15"><table width="96%" border="0" cellpadding="3" cellspacing="0">
                <tr>
                  <td width="62%" class="headingorange">
                  <span class="heading">
                  <c:out value="${BusinessKey.displayName}"/>
                  </span><mifos:mifoslabel name="Center.dash" bundle="CenterUIResources"/> <mifos:mifoslabel name="Center.change" bundle="CenterUIResources"/>
                  <mifos:mifoslabel name="${ConfigurationConstants.CENTER}" /><mifos:mifoslabel name="Center.Membership" bundle="CenterUIResources"/></td>
                </tr>
              </table>
              <table width="96%" border="0" cellpadding="3" cellspacing="0">
                <tr>
                  <td class="fontnormalbold"> <span class="fontnormal">
                  
                  <fmt:message key="Center.InstructionSet">
						<fmt:param><mifos:mifoslabel name="${ConfigurationConstants.CENTER}" /></fmt:param>
						<fmt:param><mifos:mifoslabel name="${ConfigurationConstants.CENTER}" /></fmt:param>
						<fmt:param><mifos:mifoslabel name="${ConfigurationConstants.GROUP}"/></fmt:param>
						<fmt:param><mifos:mifoslabel name="${ConfigurationConstants.CENTER}" /></fmt:param>
				  </fmt:message>
                 </td>
                </tr>
              </table>
              <table width="96%" border="0" cellpadding="3" cellspacing="0">
                <tr class="fontnormal">
                  <td colspan="2" align="right">&nbsp;</td>
                </tr>
                <tr class="fontnormal">
                  <td width="20%" align="right"><span id="center_search_results_transfer_group.label.search"><mifos:mifoslabel name="${ConfigurationConstants.CENTER}"/><c:out value=" "/>
							<mifos:mifoslabel name="Center.Name" bundle="CenterUIResources"></mifos:mifoslabel></span></td>
                  <td width="80%"><html-el:text styleId="center_search_results_transfer_group.input.search" property="searchString"/>
 	                  <html-el:submit styleId="center_search_results_transfer_group.button.search" styleClass="buttn">
	                  	<mifos:mifoslabel name="button.Search" bundle ="CenterUIResources"></mifos:mifoslabel>
	                  </html-el:submit>
				  </td>
                </tr>
              </table>
              <table width="96%" border="0" cellpadding="3" cellspacing="0">
                <tr>
   					<td>
   						<font class="fontnormalRedBold"><span id="center_search_results_transfer_group.error.message"><html-el:errors bundle="CenterUIResources"/></span></font>
					</td>
				</tr>
                <tr>
                  <td>
                 	   <mifos:mifostabletagdata name="groupTransfer" key="centerSearch" type="single" 
	                	className="CenterSearchResults" width="100%" border="0" cellspacing="0" cellpadding="0"/>
                  </td>
                  </tr>
              </table>
              <table width="96%" border="0" cellspacing="0" cellpadding="0">
                <tr>
                  <td align="center">
                    <html-el:button styleId="center_search_results_transfer_group.button.cancel" property="cancelButton" onclick="goToCancelPage();"  styleClass="cancelbuttn">
                    <mifos:mifoslabel name="button.cancel" bundle ="CenterUIResources"></mifos:mifoslabel> 
                    </html-el:button>
	    	        
                  </td>
                </tr>
              </table>             
          </td>
          </tr>
        </table>        

</html-el:form>
</tiles:put>
</tiles:insert>
