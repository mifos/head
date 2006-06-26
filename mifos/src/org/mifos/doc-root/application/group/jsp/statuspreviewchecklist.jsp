<!-- /**
 
 * statuspreviewchecklist.jsp    version: 1.0
 
 
 
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
<%@ taglib uri="/userlocaledate" prefix="userdatefn"%>
<tiles:insert definition=".clientsacclayoutsearchmenu">
 <tiles:put name="body" type="string">
<SCRIPT SRC="pages/framework/js/CommonUtilities.js"></SCRIPT>
<script language="javascript">

function goToCancelPage(){
	groupActionForm.action="GroupAction.do?method=cancel";
	groupActionForm.submit();
  }
 
<%--  function checkSelectedChoices(){


  	var checkCounter = 0;
if(groupActionForm.selectedItems != undefined){
  	if(groupActionForm.selectedItems.length != undefined){
		for (i=0;i<groupActionForm.selectedItems.length; i++)
		{
			if (groupActionForm.selectedItems[i].checked)
				checkCounter = checkCounter + 1;
		}
	}
	else{
		if(groupActionForm.selectedItems.checked){
			  checkCounter = checkCounter + 1;
			
		}
	}	
	if (checkCounter != groupActionForm.chklistSize.value)
		{
			alert("Please select all the checklists to change the status");
			return (false);
		}	
}

	groupActionForm.submit();

 }	--%>
  

  function GoToEditPage(){
	groupActionForm.action="GroupAction.do?method=previous";
	groupActionForm.submit();
  }

  
</script>

<html-el:form action="GroupAction.do?method=updateStatus" onsubmit="func_disableSubmitBtn('submitBtn')">
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
      
          <td align="left" valign="top" class="paddingL15T15" >
            <table width="95%" border="0" cellpadding="0" cellspacing="0">
              <tr>
                <td width="83%" class="headingorange"><span class="heading">
                	<c:out value="${requestScope.GroupVO.displayName}"/>  -</span> 
                	<mifos:mifoslabel name="Group.confirmstatuschange" bundle="GroupUIResources"></mifos:mifoslabel></td>
              </tr>
              <tr>
                <td class="headingorange" height="25">
                	<span class="fontnormalbold">
                		<mifos:mifoslabel name="Group.newstatus" bundle="GroupUIResources"></mifos:mifoslabel>
                	</span>
                	<span class="fontnormal">
					<c:choose>
					<%-- Partial Application --%>
					<c:when test="${requestScope.GroupVO.statusId == 7}">
						<mifos:MifosImage id="partial" moduleName="customer.group"/>
					</c:when>
					<%-- Pending Approval --%>
					<c:when test="${requestScope.GroupVO.statusId == 8}">
						<mifos:MifosImage id="pending" moduleName="customer.group"/>
					</c:when>
					<%-- Active --%>
					<c:when test="${requestScope.GroupVO.statusId == 9}">
						<mifos:MifosImage id="active" moduleName="customer.group"/>
					</c:when>
					<%-- On Hold --%>
					<c:when test="${requestScope.GroupVO.statusId == 10}">
						<mifos:MifosImage id="hold" moduleName="customer.group"/>
					</c:when>
					<%-- Cancelled --%>
					<c:when test="${requestScope.GroupVO.statusId == 11}">
						<mifos:MifosImage id="cancelled" moduleName="customer.group"/>
					</c:when>
					<%-- Closed --%>
					<c:when test="${requestScope.GroupVO.statusId == 12}">
						<mifos:MifosImage id="closed" moduleName="customer.group"/>
					</c:when>

				<c:otherwise>	  </c:otherwise>
				</c:choose>
                     <c:out value="${requestScope.newStatus}"/> 
                     <c:if test="${!empty requestScope.newFlag}">
                     	- <c:out value="${requestScope.newFlag}"/>
                     </c:if>
                     </span></td>
              </tr>
              <tr>
              <logic:messagesPresent>
	   		<td><br><font class="fontnormalRedBold"><html-el:errors bundle="GroupUIResources"/></font></td>
	   		</logic:messagesPresent>
			</tr>
	        
	        <!-- when group status is being changed to active and it has no clients assigned. Display a warning msg-->
   				<c:if test="${requestScope.GroupPerformanceVO.clientCount == 0 and requestScope.GroupVO.statusId == 9}">
   					<tr>
	        			<td class="fontnormal">
		   					<font class="fontnormalRedBold">
		   					<mifos:mifoslabel name="warning.noclientsassigned1" bundle="GroupUIResources"/>
		   					<mifos:mifoslabel name="${ConfigurationConstants.GROUP}"/>		   				
		   					<mifos:mifoslabel name="warning.noclientsassigned2" bundle="GroupUIResources"/>	
		   					<mifos:mifoslabel name="${ConfigurationConstants.CLIENT}"/>		   				
		   					<mifos:mifoslabel name="warning.noclientsassigned3" bundle="GroupUIResources"/>	
		   					<mifos:mifoslabel name="${ConfigurationConstants.GROUP}"/>		   				
		   					<mifos:mifoslabel name="Group.dot" bundle="GroupUIResources"/>			   							   							   							   						   						
		   					</font>
   						</td>
	        		</tr>
   				</c:if>
              <tr>
                <td class="fontnormal"><br><span class="fontnormalbold">
                  <c:out value="${userdatefn:getUserLocaleDate(sessionScope.UserContext.pereferedLocale,requestScope.GroupVO.customerNote.commentDate)}" />
                  	<%--<c:out value="${requestScope.GroupVO.customerNote.commentDate}"/>--%>
                  </span>
                  <span class="fontnormal"><br>
                  	<c:out value="${requestScope.GroupVO.customerNote.comment}"/>
                  </span>
                  	- <em><c:out value="${requestScope.GroupVO.customerNote.personnel.displayName}"/></em>
                  </td>
              </tr>
              <c:if test="${!empty requestScope.checklist}">
              <tr>
                <td class="blueline"><img src="pages/framework/images/trans.gif" width="10" height="12"></td>
              </tr>
              <tr>
                <td class="fontnormal">&nbsp;</td>
              </tr>
              
              <tr>
                <td class="fontnormal">
                <mifos:mifoslabel name="Group.statuspreviewMsg1" bundle="GroupUIResources"></mifos:mifoslabel>
                <mifos:mifoslabel name="Group.statuspreviewMsg2" bundle="GroupUIResources"></mifos:mifoslabel>
                <mifos:mifoslabel name="Group.statuspreviewMsg3" bundle="GroupUIResources"></mifos:mifoslabel>
				<mifos:mifoslabel name="${ConfigurationConstants.GROUP}"/>
                <mifos:mifoslabel name="Group.statuspreviewMsg4" bundle="GroupUIResources"></mifos:mifoslabel>
                <mifos:mifoslabel name="${ConfigurationConstants.GROUP}"/>
                <mifos:mifoslabel name="Group.dot" bundle="GroupUIResources"></mifos:mifoslabel>				 
                  </td>
              </tr>
              </c:if>
            </table>
            <br>
            <c:if test="${!empty requestScope.checklist}">
            <table width="95%" border="0" cellpadding="3" cellspacing="0">
				 <bean:size collection="${requestScope.checklist}" id="listSize"/>
				 <html-el:hidden property="chklistSize" value="${pageScope.listSize}" />  
					<c:forEach var="chklist" items="${requestScope.checklist}" varStatus="loopStatus">
					 <bean:define id="ctr">
                		<c:out value="${loopStatus.index}"/>
               		 </bean:define>
						<tr class="fontnormal">
        	        		<html-el:multibox name ="groupActionForm" property="selectedItems">
								<td width="2%" valign="top">
									<c:out value="${ctr}"/>
								</td>
							</html-el:multibox>
							<c:out value="${chklist.checkListName}"/>
						</tr>
					</c:forEach>
            </table>
            </c:if>
            <table width="95%" border="0" cellpadding="0" cellspacing="0">
              <tr>
                <td style="padding-top:5px;">
                 <html-el:button property="editInfo" value="Edit Status" styleClass="insidebuttn" style="width:65px;" onclick="GoToEditPage()">
                 <mifos:mifoslabel name="button.editstatus" bundle="GroupUIResources"></mifos:mifoslabel>
                </html-el:button>
                </td>
              </tr>
              <tr>
                <td align="center" class="blueline">&nbsp;</td>
              </tr>
            </table>
            <br>
            <table width="95%" border="0" cellpadding="0" cellspacing="0">
              <tr>
                <td align="center">
	                <html-el:submit property="submitBtn" styleClass="buttn" style="width:70px;" >
		                <mifos:mifoslabel name="button.submit" bundle="GroupUIResources"></mifos:mifoslabel>
	                </html-el:submit>
        	            &nbsp;&nbsp;
    	             <html-el:button property="cancelBtn"  styleClass="cancelbuttn" style="width:70px" onclick="goToCancelPage()">
	                    <mifos:mifoslabel name="button.cancel" bundle="GroupUIResources"></mifos:mifoslabel>
                    </html-el:button>
                </td>
              </tr>
            </table>
            <br>
          </td>
        
      </table>
 
<html-el:hidden property="input" value="PreviewChangeGroupStatus"/> 
<html-el:hidden property="customerId" value="${requestScope.GroupVO.customerId}"/> 
<html-el:hidden property="globalCustNum" value="${requestScope.GroupVO.globalCustNum}"/> 
<html-el:hidden property="versionNo" value="${requestScope.GroupVO.versionNo}"/> 
<html-el:hidden property="displayName" value="${requestScope.GroupVO.displayName}"/> 
</html-el:form>
</tiles:put>
</tiles:insert>
