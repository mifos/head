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
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic"%>
<%@ taglib uri="/sessionaccess" prefix="session"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>


<tiles:insert definition=".withoutmenu">
	<tiles:put name="body" type="string">
	<input type="hidden" id="page.id" value="GroupSearch"/>

<script type="text/javascript">

function goToCancelPage()
{
    groupCustActionForm.method.value="cancel";
    groupCustActionForm.input.value="createGroup";
    groupCustActionForm.submit();
}
</script>
<fmt:setLocale value='${sessionScope["LOCALE"]}'/>
<fmt:setBundle basename="org.mifos.config.localizedResources.GroupUIResources"/>
<html-el:form action="groupCustAction.do">

     <table width="90%" border="0" align="center" cellpadding="0" cellspacing="0">
      <tr>
        <td align="center" class="heading">&nbsp;</td>
      </tr>
    </table>      
      <table width="90%" border="0" align="center" cellpadding="0" cellspacing="0">
      <tr>
        <td class="bluetablehead">
        <table width="100%" border="0" cellspacing="0" cellpadding="0">
          <tr>
            <td width="25%"><table border="0" cellspacing="0" cellpadding="0">
              <tr>
                <td><img src="pages/framework/images/timeline/bigarrow.gif" width="17" height="17"></td>
                <td class="timelineboldorange">
                <fmt:message key="Group.selectOr">
							<fmt:param><mifos:mifoslabel name="${ConfigurationConstants.GROUP}" /></fmt:param>
							<fmt:param><mifos:mifoslabel name="${ConfigurationConstants.BRANCHOFFICE}" /></fmt:param>
						    </fmt:message></td>
              </tr>
            </table></td>
            <td width="25%"><table border="0" cellspacing="0" cellpadding="0">
              <tr>
                <td><img src="pages/framework/images/timeline/orangearrow.gif" width="17" height="17"></td>
                <td class="timelineboldorangelight"><mifos:mifoslabel name="Group.personnelinformation" /></td>
              </tr>
            </table></td>
            <td width="25%" align="center"><table border="0" cellspacing="0" cellpadding="0">
              <tr>
                <td><img src="pages/framework/images/timeline/orangearrow.gif" width="17" height="17"></td>
                <td class="timelineboldorangelight"><mifos:mifoslabel name="Group.mfiinformation" /></td>
              </tr>
            </table></td>
            <td width="25%" align="right"><table border="0" cellspacing="0" cellpadding="0">
              <tr>
                <td><img src="pages/framework/images/timeline/orangearrow.gif" width="17" height="17"></td>
                <td class="timelineboldorangelight"><mifos:mifoslabel name="Group.reviewandsubmit" /></td>
              </tr>
            </table></td>
            </tr>
        </table>
       </td>
      </tr>
    </table>
   
      <table width="90%" border="0" align="center" cellpadding="0" cellspacing="0" class="bluetableborder">
        <tr>
          <td width="70%" height="24" align="left" valign="top" class="paddingleftCreates"><table width="98%" border="0" cellspacing="0" cellpadding="3">
              <tr>
                <td class="headingorange"><span class="heading"><fmt:message key="Group.createnew">
				<fmt:param><mifos:mifoslabel name="${ConfigurationConstants.CLIENT}" /></fmt:param>
				</fmt:message> - </span> <fmt:message key="Group.select">
				<fmt:param><mifos:mifoslabel name="${ConfigurationConstants.GROUP}" /></fmt:param>
				</fmt:message></td>
              </tr>
            </table>
              <table width="98%" border="0" cellspacing="0" cellpadding="0">
              <tr><logic:messagesPresent>
					<td><br><font class="fontnormalRedBold"><span id="group_search.error.message"><html-el:errors
						bundle="GroupUIResources" /></span></font><br></td>
					</logic:messagesPresent>
				</tr>
               </table>                            
              <table width="98%" border="0" cellspacing="0" cellpadding="3">
                <tr>
                  <td class="fontnormalbold"> <span class="fontnormal"> 
                  <fmt:message key="Group.enterAndProceed">
				  <fmt:param><mifos:mifoslabel name="${ConfigurationConstants.GROUP}" /></fmt:param>
				  </fmt:message> 
                  <mifos:mifoslabel name="Group.createpagehead3" />                     
                  </span> </td>
                </tr>
              </table>
              <table width="98%" border="0" cellspacing="0" cellpadding="3">
                <tr class="fontnormal">
                  <td align="right">&nbsp;</td>
                  <td>&nbsp;</td>
                </tr>
                <tr class="fontnormal">
                  <td align="right">
                  	<span id="group_search.label.search">
                  	<fmt:message key="Group.groupname">
                  		<fmt:param><mifos:mifoslabel name="${ConfigurationConstants.GROUP}" /></fmt:param>
                  	</fmt:message></span></td>
                  <td>
                  	<html-el:text styleId="group_search.input.search" property="searchString" maxlength="200"/>
                  </td>
                </tr>
                <tr class="fontnormal">
                  <td width="28%">&nbsp; </td>
                  <td width="72%">      
      <html-el:submit  styleId="group_search.button.proceed" styleClass="buttn">
	      <mifos:mifoslabel name="button.proceed" bundle="GroupUIResources"></mifos:mifoslabel>
      </html-el:submit>
&nbsp;
      <html-el:button styleId="group_search.button.cancel" property="cancelBtn"  styleClass="cancelbuttn" onclick="goToCancelPage()">
            <mifos:mifoslabel name="button.cancel" bundle="GroupUIResources"></mifos:mifoslabel>
      </html-el:button>
                  </td>
                </tr>
                <tr class="fontnormal">
                  <td>&nbsp;</td>
                  <td>
                  							<c:set var="groupHierarchyRequired"
								value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'groupHierarchyRequired')}" />
                  
                  <c:if test="${groupHierarchyRequired eq 'No'}">
                   <a id="group_search.link.membershipNotRequired" href="clientCustAction.do?method=chooseOffice&amp;groupFlag=0&amp;currentFlowKey=${requestScope.currentFlowKey}"> 
                  <br>
                  	<fmt:message key="Group.membershipNotRequired">
                    	<fmt:param><mifos:mifoslabel name="${ConfigurationConstants.GROUP}" /></fmt:param> 
                    	<fmt:param><mifos:mifoslabel name="${ConfigurationConstants.CLIENT}" /></fmt:param>
                    </fmt:message></a>
                  </c:if>
                  </td>
                </tr>
              </table>              
              <br>
          </td>
        </tr>
      </table>
      <br>
      <html-el:hidden property="input" value="" />
<html-el:hidden property="method" value="search"/>       
<html-el:hidden property="currentFlowKey" value="${requestScope.currentFlowKey}" />
</html-el:form>
</tiles:put>
</tiles:insert>
