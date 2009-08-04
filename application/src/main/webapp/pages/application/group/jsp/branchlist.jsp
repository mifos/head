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
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<script language="javascript">
  function goToCancelPage(){
	groupCustActionForm.action="groupCustAction.do?method=cancel";
	groupCustActionForm.submit();
  }
</script>
<fmt:setLocale value='${sessionScope["LOCALE"]}'/>
<fmt:setBundle basename="org.mifos.config.localizedResources.GroupUIResources"/>

<tiles:insert definition=".withoutmenu">
 <tiles:put name="body" type="string">
 <span id="page.id" title="GroupBranchList" />

<html-el:form action="groupCustAction.do?method=preview">

   <table width="90%" border="0" align="center" cellpadding="0" cellspacing="0">
      <tr>
        <td align="center" class="heading">&nbsp;</td>
        </tr>
    </table>              
      <table width="90%" border="0" align="center" cellpadding="0" cellspacing="0">
          <tr>
            <td class="bluetablehead"><table width="100%" border="0" cellspacing="0" cellpadding="0">
              <tr>
                <td width="33%"><table border="0" cellspacing="0" cellpadding="0">
                    <tr>
                      <td><img src="pages/framework/images/timeline/bigarrow.gif" width="17" height="17"></td>
                      <td class="timelineboldorange">
                      <fmt:message key="Group.choosebranch">
						<fmt:param><mifos:mifoslabel name="${ConfigurationConstants.BRANCHOFFICE}" /></fmt:param>
						</fmt:message>
                      </td>
                    </tr>
                  </table>
                </td>
                <td width="34%" align="center">
                  <table border="0" cellspacing="0" cellpadding="0">
                    <tr>
                      <td><img src="pages/framework/images/timeline/orangearrow.gif" width="17" height="17"></td>
                      <td class="timelineboldorangelight">
                      <fmt:message key="Group.groupinformation">
				<fmt:param><mifos:mifoslabel name="${ConfigurationConstants.GROUP}" /></fmt:param>
				</fmt:message>
                      </td>
                    </tr>
                  </table>
                </td>
                <td width="33%" align="right">
                  <table border="0" cellspacing="0" cellpadding="0">
                    <tr>
                      <td><img src="pages/framework/images/timeline/orangearrow.gif" width="17" height="17"></td>
                      <td class="timelineboldorangelight"><mifos:mifoslabel name="Group.reviewandsubmit" bundle="GroupUIResources"/></td>
                    </tr>
                  </table>
                </td>
                </tr>
            </table></td>
          </tr>
        </table>
        <table width="90%" border="0" align="center" cellpadding="0" cellspacing="0" class="bluetableborder">
          <tr>
            <td align="left" valign="top" class="paddingleftCreates">  
                        <table width="93%" border="0" cellpadding="3" cellspacing="0">
                <tr>
                  <td class="headingorange">
                  <span class="heading">
                  <fmt:message key="Group.createnew">
				<fmt:param><mifos:mifoslabel name="${ConfigurationConstants.GROUP}" /></fmt:param>
				</fmt:message> - </span>
                  <fmt:message key="Group.choosebranch">
				<fmt:param><mifos:mifoslabel name="${ConfigurationConstants.BRANCHOFFICE}" /></fmt:param>
				</fmt:message>
                  </td>
                </tr>
                <tr>
                  <td class="fontnormal"><mifos:mifoslabel name="Group.clickoffice" bundle="GroupUIResources"/>
                  <mifos:mifoslabel name="Group.createpagehead3" bundle="GroupUIResources"/> </td>
                </tr>
              </table>
              <office:OfficeListTag methodName="load" actionName="groupCustAction.do" flowKey="${requestScope.currentFlowKey}" onlyBranchOffices="yes"/>
              <table width="93%" border="0" cellpadding="0" cellspacing="0">
                <tr>
                  <td align="center" class="blueline">&nbsp;</td>
                </tr>
              </table>
              <br>
              <table width="93%" border="0" cellpadding="0" cellspacing="0">
                <tr>
                  <td align="center">
                    <html-el:button styleId="branchlist.button.cancel" property="cancelBtn"  styleClass="cancelbuttn" onclick="goToCancelPage()">
	                    <mifos:mifoslabel name="button.cancel" bundle="GroupUIResources"></mifos:mifoslabel>
                    </html-el:button>
                  </td>
                </tr>
              </table>
            <br></td>
          </tr>
        </table>
      <br>
<html-el:hidden property="input" value="createGroup"/> 
<html-el:hidden property="currentFlowKey" value="${requestScope.currentFlowKey}" />
</html-el:form>

</tiles:put>
</tiles:insert>
