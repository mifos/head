<!-- /**
 
 * createuserconfirmation    version: 1.0
 
 
 
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
<tiles:insert definition=".view">
 <tiles:put name="body" type="string">
<html-el:form action="PersonnelAction.do?method=get">



     <table width="95%" border="0" cellpadding="0" cellspacing="0">
        <tr>
          <td width="70%" align="left" valign="top" class="paddingL15T15">
          <table width="98%" border="0" cellspacing="0" cellpadding="3">
            <tr>
              <td class="headingorange">
			<mifos:mifoslabel name="Personnel.UserAdded" bundle="PersonnelUIResources"></mifos:mifoslabel>
			<br><br>
   				<font class="fontnormalRedBold">
   					<html-el:errors bundle="PersonnelUIResources"/>
   				</font>
		     
              </td>
            </tr>
            <tr>
              <td class="fontnormalbold"> 
		<mifos:mifoslabel name="Personnel.PleaseNote" bundle="PersonnelUIResources"></mifos:mifoslabel>
		<span class="fontnormal"> 
		<c:out value="${requestScope.PersonnelVO.displayName}"/>
		 <mifos:mifoslabel name="Personnel.SystemIdAssigned" bundle="PersonnelUIResources"/>
		 </span> 
		<c:out value="${requestScope.PersonnelVO.globalPersonnelNum}"/>
		<span class="fontnormal"><br>    
                            <br>
                            </span>
			<a href="PersonnelAction.do?method=get&globalPersonnelNum=<c:out value="${requestScope.PersonnelVO.globalPersonnelNum}"/>">
			<mifos:mifoslabel name="Personnel.ViewUserdetailsNow" bundle="PersonnelUIResources"></mifos:mifoslabel>
			</a>
			<span class="fontnormal"><br>
                            <br>
                        </span>
			<span class="fontnormal">
			 <a href="PersonnelAction.do?method=chooseOffice">
			<mifos:mifoslabel name="Personnel.AddNewUser" bundle="PersonnelUIResources"></mifos:mifoslabel>
			</a>
			</span></td>
            </tr>
          </table>            <br>
          </td>
        </tr>
      </table>
      <br>
 
</html-el:form>
</tiles:put>
</tiles:insert>