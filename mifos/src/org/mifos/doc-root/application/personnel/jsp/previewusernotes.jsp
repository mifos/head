<!-- /**
 
 * previewusernotes    version: 1.0
 
 
 
 * Copyright © 2005-2006 Grameen Foundation USA
 
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
<%@ taglib uri="/userlocaledate" prefix="userdatefn"%>
<tiles:insert definition=".view">
 <tiles:put name="body" type="string">
 <script language="javascript">
  function goToEditPage(){
	personnelNotesActionForm.action="PersonnelNotesAction.do?method=previous";
	personnelNotesActionForm.submit();
  }
  
  function goToCancelPage(){
	personnelNotesActionForm.action="PersonnelNotesAction.do?method=cancel";
	personnelNotesActionForm.submit();
  }
  </script>
<html-el:form action="PersonnelNotesAction.do?method=create">


      <table width="95%" border="0" cellpadding="0" cellspacing="0">
        <tr>
          <td class="bluetablehead05">
		<span class="fontnormal8pt">
		<a href="AdminAction.do?method=load">
	           <mifos:mifoslabel name="Personnel.Admin" bundle="PersonnelUIResources"></mifos:mifoslabel>             	
           	</a>
		 / 
		<a href="PersonnelAction.do?method=loadSearch">
			<mifos:mifoslabel name="Personnel.ViewUsers" bundle="PersonnelUIResources"></mifos:mifoslabel>
		</a> / 
		<a href="OfficeAction.do?method=get&officeId=<c:out value="${sessionScope.personnelNotesActionForm.officeId}"/>">
	           <c:out value="${sessionScope.personnelNotesActionForm.officeName}"/>            	
           	</a>
		 / 
		<a href="PersonnelAction.do?method=get&globalPersonnelNum=<c:out value="${sessionScope.personnelNotesActionForm.globalPersonnelNum}"/>">
	           <c:out value="${sessionScope.personnelNotesActionForm.personnelName}"/>            	
           	</a>
		</span></td>
        </tr>
      </table>
      <table width="95%" border="0" cellpadding="0" cellspacing="0">
        <tr>
          <td width="70%" align="left" valign="top" class="paddingL15T15">              <table width="95%" border="0" cellpadding="3" cellspacing="0">
            <tr>
              <td width="83%" class="headingorange">
		<span class="heading">
		<c:out value="${sessionScope.personnelNotesActionForm.personnelName}"/> -
		</span> 
		<mifos:mifoslabel name="Personnel.PreviewNote" bundle="PersonnelUIResources"></mifos:mifoslabel></td>
            </tr>
          </table>
            <table width="95%" border="0" cellpadding="3" cellspacing="0">
                <tr>
                  <td><br>
                      <span class="fontnormal">
			<mifos:mifoslabel name="Personnel.ReviewText" bundle="PersonnelUIResources"></mifos:mifoslabel>
			<mifos:mifoslabel name="Personnel.Submit" bundle="PersonnelUIResources"></mifos:mifoslabel>
			<mifos:mifoslabel name="Personnel.Edit" bundle="PersonnelUIResources"></mifos:mifoslabel>
			<mifos:mifoslabel name="Personnel.ClickCancelToDetailsPage" bundle="PersonnelUIResources"></mifos:mifoslabel>
			</span></td>
                </tr>
                <tr>
                  <td class="blueline"><img src="pages/framework/images/trans.gif" width="10" height="5"></td>
                </tr>
                <tr>
		   		<td>
   					<font class="fontnormalRedBold"><html-el:errors bundle="PersonnelUIResources"/></font>
				</td>
				</tr>
              </table>
              <table width="95%" border="0" cellpadding="3" cellspacing="0">
                <tr>
                  <td align="left" valign="top"><img src="pages/framework/images/trans.gif" width="10" height="5"></td>
                </tr>
                <tr>
                  <td align="left" valign="top">
			<span class="fontnormalbold">
			<c:out value="${userdatefn:getUserLocaleDate(sessionScope.UserContext.pereferedLocale,requestScope.PersonnelNotesVO.commentDate)}"/>
			</span><br>
                      <span class="fontnormal">
			<c:out value="${requestScope.PersonnelNotesVO.comment}"/>	
                      - <em><c:out value="${requestScope.PersonnelNotesVO.officer.displayName}"/></em></span> </td>
                </tr>
                <tr>
                  <td align="left" valign="top">
                  <html-el:button property="btn" style="width:65px;" styleClass="insidebuttn" onclick="goToEditPage()">
                  <mifos:mifoslabel name="Personnel.EditLabel" bundle="PersonnelUIResources"></mifos:mifoslabel>
                  </html-el:button>
                  </td>
                </tr>
              </table>
              <table width="95%" border="0" cellpadding="0" cellspacing="0">
                <tr>
                  <td height="17" align="center" class="blueline">&nbsp;</td>
                </tr>
              </table>
              <br>
              <table width="95%" border="0" cellpadding="0" cellspacing="0">
                <tr>
                  <td align="center">
                  
				<html-el:submit styleClass="buttn" style="width:70px;">
					<mifos:mifoslabel name="button.submit" bundle="PersonnelUIResources"></mifos:mifoslabel>
				</html-el:submit>
                    &nbsp;&nbsp;
                    <html-el:button property="cancelBtn"  styleClass="cancelbuttn" style="width:70px" onclick="goToCancelPage()">
	                    <mifos:mifoslabel name="button.cancel" bundle="PersonnelUIResources"></mifos:mifoslabel>
                    </html-el:button>
				</td>
                </tr>
              </table>
              <br>
          </td>
        </tr>
      </table>
      <br>
  
</html-el:form>
</tiles:put>
</tiles:insert>

