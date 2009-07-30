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

<tiles:insert definition=".withmenu">
 <tiles:put name="body" type="string">
 <span style="display: none" id="page.id">ConfirmCenterMembership</span>

<SCRIPT SRC="pages/framework/js/CommonUtilities.js"></SCRIPT>
<script>
  function goToCancelPage(){
	groupTransferActionForm.action="groupTransferAction.do?method=cancel";
	groupTransferActionForm.submit();
  }
</script>
<fmt:setLocale value='${sessionScope["LOCALE"]}'/>
<fmt:setBundle basename="org.mifos.config.localizedResources.GroupUIResources"/>
<html-el:form action="/groupTransferAction.do?method=transferToCenter" onsubmit="func_disableSubmitBtn('submitBtn')">
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
            <td width="70%" align="left" valign="top" class="paddingL15T15"><table width="96%" border="0" cellpadding="3" cellspacing="0">
                <tr>
                  <td width="62%" class="headingorange">
                  	<span class="heading">
	                  <c:out value="${BusinessKey.displayName}"/> - 
    	            </span> 
        	            <fmt:message key="Group.changeMembership">
						<fmt:param><mifos:mifoslabel name="${ConfigurationConstants.CENTER}" /></fmt:param>
						</fmt:message>
        	            <br><br>
                    <span class="fontnormal">
                            <fmt:message key="Group.confirmcenterMsg1">
							<fmt:param><mifos:mifoslabel name="${ConfigurationConstants.CENTER}" /></fmt:param>
							<fmt:param><mifos:mifoslabel name="${ConfigurationConstants.GROUP}" /></fmt:param>
							</fmt:message>
                            <br>
		                    <mifos:mifoslabel name="meeting.msgUpdateMeeting" bundle="MeetingResources"/>
                    </span></td>
                </tr>
              </table>
              <table width="96%" border="0" cellpadding="3" cellspacing="0">
              <tr>
   					<td>
   					<font class="fontnormalRedBold"><span id="confirmcentermembership.error.message"><html-el:errors bundle="GroupUIResources"/></span></font>
					</td>
				</tr>
                <tr>
                  <td class="fontnormalbold"> <span class="fontnormal"><br>
                    </span>
                        <fmt:message key="Group.changeMembership">
						<fmt:param><mifos:mifoslabel name="${ConfigurationConstants.CENTER}" /></fmt:param>
						</fmt:message>
                    <span class="fontnormal"> 
	                  <c:out value="${sessionScope.groupTransferActionForm.centerName}"/>
             
                      <br>
                     </span></td>
                </tr>
              </table>
              <table width="96%" border="0" cellpadding="3" cellspacing="0">
                <tr>
                  <td class="blueline"><img src="pages/framework/images/trans.gif" width="10" height="5"></td>
                </tr>
              </table>
              <table width="96%" border="0" cellpadding="3" cellspacing="0">
                <tr class="fontnormal">
                  <td width="80%" align="center"><br>
                    <html-el:submit styleId="confirmcentermembership.button.submit" property="submitBtn" styleClass="buttn" >
		                <mifos:mifoslabel name="button.submit" bundle="GroupUIResources"></mifos:mifoslabel>
	                </html-el:submit>
        	        	&nbsp; &nbsp;
    	            <html-el:button styleId="confirmcentermembership.button.cancel" property="cancelBtn"  styleClass="cancelbuttn" onclick="goToCancelPage()">
	                    <mifos:mifoslabel name="button.cancel" bundle="GroupUIResources"></mifos:mifoslabel>
                    </html-el:button>
                  </td>
                </tr>
              </table>
            </td>
          </tr>
        </table>        
      <br>
</html-el:form>
</tiles:put>
</tiles:insert>
