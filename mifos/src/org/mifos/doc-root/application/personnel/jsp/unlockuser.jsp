<!-- /**
 
 * unlockuser.jsp    version: 1.0
 
 
 
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
 
 */-->
<%@ taglib uri="http://struts.apache.org/tags-html-el" prefix="html-el"%>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="/tags/mifos-html" prefix = "mifos"%>

<script language="javascript">
  function goToCancelPage(){
	personnelActionForm.action="PersonnelAction.do?method=cancel";
	personnelActionForm.submit();
  }
</script>
<tiles:insert definition=".view">
 <tiles:put name="body" type="string">
 <SCRIPT SRC="pages/framework/js/CommonUtilities.js" ></SCRIPT>
<html-el:form action="PersonnelAction.do?method=unLockUserAccount" onsubmit="func_disableSubmitBtn('submitBtn')">

   <table width="95%" border="0" cellpadding="0" cellspacing="0">
        <tr>
          <td class="bluetablehead05">
          	<span class="fontnormal8pt">
          	<a href="AdminAction.do?method=load">
	           <mifos:mifoslabel name="Personnel.Admin" bundle="PersonnelUIResources"></mifos:mifoslabel>             	
           	</a>
			 / 
			<a href="PersonnelAction.do?method=loadSearch">
					<mifos:mifoslabel name="Personnel.ViewEditUsers" bundle="PersonnelUIResources"></mifos:mifoslabel>
			</a> /
          	 <a href="OfficeAction.do?method=get&officeId=<c:out value="${requestScope.PersonnelVO.office.officeId}"/>">
	           <c:out value="${requestScope.PersonnelVO.office.officeName}"/>            	
           	</a>/
           	<a href="PersonnelAction.do?method=get&globalPersonnelNum=<c:out value="${sessionScope.personnelNotesActionForm.globalPersonnelNum}"/>">
           		<c:out value="${requestScope.PersonnelVO.displayName}"/>
           	</a>
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
	                	<c:out value="${requestScope.PersonnelVO.displayName}"/>
                	 - </span><mifos:mifoslabel name="Personnel.UnlockUserConfirmations" bundle="PersonnelUIResources"></mifos:mifoslabel></td>
              </tr>
            </table>
            <br>
            <table width="95%" border="0" cellpadding="3" cellspacing="0">
              <tr class="fontnormal">
                <td width="94%"><span class="fontnormal">
                	<mifos:mifoslabel name="Personnel.UserRecordLocked" bundle="PersonnelUIResources"></mifos:mifoslabel>
                	<c:out value="${requestScope.loginAttemptsCount}"/>
                	<mifos:mifoslabel name="Personnel.LoginAttempts" bundle="PersonnelUIResources"></mifos:mifoslabel>
                    <br>
                    <br>
                    <mifos:mifoslabel name="Personnel.AreYouSure" bundle="PersonnelUIResources"></mifos:mifoslabel>
                    <mifos:mifoslabel name="Personnel.SumbitOrCancel" bundle="PersonnelUIResources"></mifos:mifoslabel>
                    <br>
                </span></td>
              </tr>
            </table>
            <table width="95%" border="0" cellpadding="0" cellspacing="0">
              <tr>
                <td align="center" class="blueline">&nbsp;</td>
              </tr>
            </table>
            <br>
            <table width="95%" border="0" cellpadding="0" cellspacing="0">
              <tr>
                <td align="center">
                  <html-el:submit styleClass="buttn" property="submitBtn" style="width:70px;">
					<mifos:mifoslabel name="button.submit" bundle="PersonnelUIResources"></mifos:mifoslabel>
				   </html-el:submit>
                    &nbsp;&nbsp;
                    <html-el:button property="cancelBtn"  styleClass="cancelbuttn" style="width:70px" onclick="goToCancelPage()">
	                    <mifos:mifoslabel name="button.cancel" bundle="PersonnelUIResources"></mifos:mifoslabel>
                    </html-el:button>
                </td></tr>
            </table>
            <br>
          </td>
        </tr>
      </table>
<html-el:hidden property="input" value="UnLockUser"/>       
</html-el:form>
</tiles:put>
</tiles:insert>
