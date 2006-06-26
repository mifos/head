<!-- /**
 
 * changegroupstatus.jsp    version: 1.0
 
 
 
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
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic"%>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="/tags/mifos-html" prefix = "mifos"%>

<tiles:insert definition=".clientsacclayoutsearchmenu">
 <tiles:put name="body" type="string">

<script language="javascript">

function goToCancelPage(){
	groupActionForm.action="GroupAction.do?method=cancel";
	groupActionForm.submit();
  }
  
function manageFlag(i) {
   if(groupActionForm.flagId!=undefined){
		if(i==11 || i==12){
			groupActionForm.flagId.disabled=false;
		}else{
			groupActionForm.flagId.selectedIndex=0;
			groupActionForm.flagId.disabled=true;
		}
	}
  }
</script>

<html-el:form action="GroupAction.do?method=preview" >

  <table width="95%" border="0" cellpadding="0" cellspacing="0">
        <tr>
          <td class="bluetablehead05">
          <span class="fontnormal8pt">
           <a href="CustomerSearchAction.do?method=getOfficeHomePage&officeId=<c:out value="${sessionScope.linkValues.customerOfficeId}"/>&officeName=<c:out value="${sessionScope.linkValues.customerOfficeName}"/>&loanOfficerId=<c:out value="${requestScope.Context.userContext.id}"/>">
              <c:out value="${sessionScope.linkValues.customerOfficeName}"/>            	
           </a> /
           	<c:if test="${!empty sessionScope.linkValues.customerParentName}">
               	<a href="centerAction.do?method=get&globalCustNum=<c:out value="${sessionScope.linkValues.customerParentGCNum}"/>">
			       	<c:out value="${sessionScope.linkValues.customerParentName}"/>
		       	</a> /  
		    </c:if>
          <a href="GroupAction.do?method=get&globalCustNum=<c:out value="${sessionScope.linkValues.globalCustNum}"/>">
          	<c:out value="${sessionScope.linkValues.customerName}"/>
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
				<c:out value="${sessionScope.linkValues.customerName}"/> - 
              </span> <mifos:mifoslabel name="Group.changestatus" bundle="GroupUIResources"></mifos:mifoslabel> 
			</td>
              </tr>
              <tr>
                <td class="headingorange">
                	<span class="fontnormalbold">
                	<mifos:mifoslabel name="Group.currentstatus" bundle="GroupUIResources"></mifos:mifoslabel> 
                	
                	</span> 
                	<span class="fontnormal">
				<c:choose>
					<%-- Partial Application --%>
					<c:when test="${sessionScope.oldStatus == 7}">
						<mifos:MifosImage id="partial" moduleName="customer.group"/>
					</c:when>
					<%-- Pending Approval --%>
					<c:when test="${sessionScope.oldStatus == 8}">
						<mifos:MifosImage id="pending" moduleName="customer.group"/>
					</c:when>
					<%-- Active --%>
					<c:when test="${sessionScope.oldStatus == 9}">
						<mifos:MifosImage id="active" moduleName="customer.group"/>
					</c:when>
					<%-- On Hold --%>
					<c:when test="${sessionScope.oldStatus == 10}">
						<mifos:MifosImage id="hold" moduleName="customer.group"/>
					</c:when>
					<%-- Cancelled --%>
					<c:when test="${sessionScope.oldStatus == 11}">
						<mifos:MifosImage id="cancelled" moduleName="customer.group"/>
					</c:when>
					<%-- Closed --%>
					<c:when test="${sessionScope.oldStatus == 12}">
						<mifos:MifosImage id="closed" moduleName="customer.group"/>
					</c:when>

				<c:otherwise>	  </c:otherwise>
				</c:choose>
                		
                		 <c:out value="${requestScope.currentStatus}"/>
                	</span>
                </td>
              </tr>
              <tr><logic:messagesPresent>
	   		<td><br><font class="fontnormalRedBold"><html-el:errors bundle="GroupUIResources"/></font></td>
	   		</logic:messagesPresent>
			</tr>
              <tr>
                <td class="fontnormal"><br>
                <mifos:mifoslabel name="Group.statusMsg1" bundle="GroupUIResources"></mifos:mifoslabel>
                <mifos:mifoslabel name="Group.createpagehead2" bundle="GroupUIResources"></mifos:mifoslabel>
                <mifos:mifoslabel name="Group.editMag2" bundle="GroupUIResources"></mifos:mifoslabel>
                <mifos:mifoslabel name="${ConfigurationConstants.GROUP}" ></mifos:mifoslabel>
                <mifos:mifoslabel name="Group.editMag3" bundle="GroupUIResources"></mifos:mifoslabel><br>
                 <span class="mandatorytext">
                    <font color="#FF0000">*</font>
                 </span>
                
                 <mifos:mifoslabel name="Group.createpagehead4" bundle="GroupUIResources"></mifos:mifoslabel>
                   
                  
                    </td>
              </tr>
              
        	<tr>
              <tr>
                <td class="blueline"><img src="pages/framework/images/trans.gif" width="10" height="12"></td>
              </tr>
            </table>
            <br>
            <table width="95%" border="0" cellpadding="0" cellspacing="0">
              <tr>
                <td align="left" valign="top" class="fontnormalbold">
		<mifos:mifoslabel name="Group.status" mandatory="yes" bundle="GroupUIResources"></mifos:mifoslabel>
		</td>
                <td align="left" valign="top"><table width="95%" border="0" cellpadding="0" cellspacing="0">
                
           <c:forEach var="status" items="${requestScope.statusList}" varStatus="loopStatus">
	            <bean:define id="ctr">
                	<c:out value="${loopStatus.index}"/>
                </bean:define>
			  <tr class="fontnormal">
                    <td width="2%" align="center">
						<html-el:radio property="statusId" value="${status.statusId}" onclick="manageFlag(${status.statusId})"/> 
                    </td>
                   <td width="98%"><c:out value="${status.statusName}"/></td>
               </tr>
 		  </c:forEach> 
 		  <c:forEach var="status" items="${requestScope.statusList}">
		   <c:if test="${status.statusId == 11 or status.statusId==12}">
	 		  <tr class="fontnormal">
	                    <td align="center">&nbsp;</td>
	                    <td>
	                    <mifos:mifoslabel name="Group.statusMsg2" bundle="GroupUIResources"> </mifos:mifoslabel>
	                    <mifos:mifoslabel name="${ConfigurationConstants.GROUP}" ></mifos:mifoslabel>
	                    <mifos:mifoslabel name="Group.statusMsg3" bundle="GroupUIResources"></mifos:mifoslabel>
	                      </td>
	          </tr>
			 <tr class="fontnormal">
			 <td align="center">&nbsp;</td>
			 <td>
					   <c:set var="flags" scope="request" value="${status.flagList}"/>   	
					   <mifos:select name="groupActionForm" property="flagId" size="1" disabled="true">
						   <html-el:options collection="flags" property="flagId" labelProperty="flagName"/>
					   </mifos:select>
			  </td>
		 	</tr>
		 	</c:if>
		 </c:forEach> 
		 	
			<tr class="fontnormal">
                    <td align="center">&nbsp;</td>
                    <td>&nbsp;</td>
                  </tr>
         </table></td>
              </tr>
              <tr>
                <td width="7%" align="left" valign="top" class="fontnormalbold">

		<mifos:mifoslabel name="Group.note" mandatory="yes" bundle="GroupUIResources"></mifos:mifoslabel>
		</td>
                <td width="93%" align="left" valign="top" style="padding-left:4px;">
		<html-el:textarea property="customerNote.comment" style="width:320px; height:110px;"/>
                </td>
              </tr>
            </table>
            <table width="95%" border="0" cellpadding="0" cellspacing="0">
              <tr>
                <td height="25" align="center" class="blueline">&nbsp;</td>
              </tr>
            </table>
            <br>
            <table width="95%" border="0" cellpadding="0" cellspacing="0">
              <tr>
                <td align="center">
					<html-el:submit styleClass="buttn" style="width:70px;">
						<mifos:mifoslabel name="button.preview" bundle="GroupUIResources"></mifos:mifoslabel>
					</html-el:submit>
                    &nbsp;&nbsp;
                    <html-el:button property="cancelBtn"  styleClass="cancelbuttn" style="width:70px" onclick="goToCancelPage()">
	                    <mifos:mifoslabel name="button.cancel" bundle="GroupUIResources"></mifos:mifoslabel>
                    </html-el:button>
                </td>
              </tr>
            </table>
            <br>
            <br>
          </td>
        </tr>
      </table>

<html-el:hidden property="input" value="ChangeGroupStatus"/> 
<html-el:hidden property="customerId" value="${requestScope.GroupVO.customerId}"/> 
<html-el:hidden property="globalCustNum" value="${requestScope.GroupVO.globalCustNum}"/> 
<html-el:hidden property="versionNo" value="${requestScope.GroupVO.versionNo}"/> 
<html-el:hidden property="displayName" value="${requestScope.GroupVO.displayName}"/> 
</html-el:form>
<script language="javascript">
	if(groupActionForm.statusId.length != undefined){
		for(j=0;j<groupActionForm.statusId.length;j++){
			if (groupActionForm.statusId[j].checked) {
				manageFlag(groupActionForm.statusId[j].value);
			}
		}
	}else{
		manageFlag(groupActionForm.statusId.value);
	}
	
</script>
</tiles:put>
</tiles:insert>
