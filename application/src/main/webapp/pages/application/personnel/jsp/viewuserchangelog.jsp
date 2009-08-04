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
<%@ taglib uri="/mifos/customtags" prefix="mifoscustom" %>
<%@ taglib uri="/userlocaledate" prefix="userdatefn"%>

<tiles:insert definition=".view">
 <tiles:put name="body" type="string">
 <span id="page.id" title="viewuserchangelog" />
 
<script language="javascript">

  function goToCancelPage(){
	personnelActionForm.action="PersonnelAction.do?method=cancel";
	personnelActionForm.submit();
  }
 
</script>

<html-el:form action="PersonnelAction.do?method=preview">  
 
    
      <table width="95%" border="0" cellpadding="0" cellspacing="0">
      <tr>
        <td class="bluetablehead05">
        	<span class="fontnormal8pt">
				<a id="viewuserchangelog.link.admin" href="AdminAction.do?method=load">
		           <mifos:mifoslabel name="Personnel.Admin" bundle="PersonnelUIResources"></mifos:mifoslabel>             	
	           	</a>
			 	/ 
				<a id="viewuserchangelog.link.viewUsers" href="PersonnelAction.do?method=loadSearch">
					<mifos:mifoslabel name="Personnel.ViewUsers" bundle="PersonnelUIResources"></mifos:mifoslabel>
				</a> / 
				<a id="viewuserchangelog.link.viewUser" href="PersonnelAction.do?method=get&globalPersonnelNum=<c:out value="${requestScope.PersonnelVO.globalPersonnelNum}"/>">
		           <c:out value="${requestScope.PersonnelVO.displayName}"/>            	
	           	</a>
            </span>
       </td>
      </tr>
    </table>
      <table width="95%" border="0" cellpadding="0" cellspacing="0">
        <tr>
          <td width="70%" height="24" align="left" valign="top" class="paddingL15T15"><table width="96%" border="0" cellpadding="3" cellspacing="0">
              <tr>
                <td class="headingorange">
                	<span class="heading">
                		<c:out value="${requestScope.PersonnelVO.displayName}"/>
                     - </span>
                <mifos:mifoslabel name="Personnel.ChangeLog" bundle="PersonnelUIResources"></mifos:mifoslabel>
                </td>
              </tr>
              <tr>
                <td class="fontnormal"><br>
                  <mifos:mifoslabel name="Personnel.RecordCreationDate" bundle="PersonnelUIResources"/>
                  <c:out value="${userdatefn:getUserLocaleDate(sessionScope.UserContext.preferredLocale,requestScope.PersonnelVO.personnelDetails.dateOfJoiningBranch)}" /> 
                  </td>
              </tr>
            </table>
            <br>    
            <mifoscustom:mifostabletag moduleName="personnel" scope="request" source="UserChangeLogList" xmlFileName="UserChangeLog.xml" passLocale="true"/>        
            <table width="96%" border="0" cellpadding="0" cellspacing="0">
              <tr>
                <td align="center">
               
                <html-el:button styleId="viewuserchangelogs.button.back" property="btn"  styleClass="buttn" onclick="goToCancelPage()">
	                    <mifos:mifoslabel name="Personnel.BackToDetailsPage" bundle="PersonnelUIResources"></mifos:mifoslabel>
                </html-el:button>

                </td>
              </tr>
            </table></td>
        </tr>
      </table>      <br>
<html-el:hidden property="input" value="UserChangeLog"/> 
</html-el:form>
</tiles:put>
</tiles:insert>
