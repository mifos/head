<!-- /**
 
 * groupdetailspreview.jsp    version: 1.0
 
 
 
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
<%@ taglib uri="/userlocaledate" prefix="userdatefn"%>
<tiles:insert definition=".clientsacclayoutsearchmenu">
<%@ taglib uri="/tags/mifos-html" prefix="mifos"%>
<%@ taglib uri="/sessionaccess" prefix="session"%>
<%@ taglib uri="/mifos/custom-tags" prefix="customtags"%>
 <tiles:put name="body" type="string">
 <SCRIPT SRC="pages/framework/js/CommonUtilities.js"></SCRIPT>
<script>
function meetingpopup(){
	window.open("schedulemeetingpopup.htm",null,"height=400,width=800,status=yes,scrollbars=yes,toolbar=no,menubar=no,location=no");
}
</script>
<body>
<script language="javascript">
  function GoToEditPage(){
	groupCustActionForm.action="groupCustAction.do?method=previousManage";
	groupCustActionForm.submit();
  }
  
</script>
<SCRIPT SRC="pages/application/group/js/groupcommon.js"></SCRIPT>
<html-el:form action="groupCustAction.do?method=update"  onsubmit="func_disableSubmitBtn('submitBtn')"> 
 <c:set value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'BusinessKey')}" var="BusinessKey" />
     <table width="95%" border="0" cellpadding="0" cellspacing="0">
		<tr>
			<td class="bluetablehead05"><span class="fontnormal8pt"> <customtags:headerLink/> </span>
		</tr>
	 </table>
      <table width="95%" border="0" cellpadding="0" cellspacing="0">
        <tr>
          <td align="left" valign="top" class="paddingL15T15" >          
            <table width="95%" border="0" cellpadding="3" cellspacing="0">
              <tr>
                <td width="64%" class="headingorange">
                <span class="heading">
                <c:out value="${sessionScope.groupCustActionForm.displayName}"/>
                     - 
                </span>
                 <mifos:mifoslabel name="Group.preview" bundle="GroupUIResources">  </mifos:mifoslabel>
                 <mifos:mifoslabel name="${ConfigurationConstants.GROUP}" ></mifos:mifoslabel>
                 <mifos:mifoslabel name="Group.groupinformation" bundle="GroupUIResources"></mifos:mifoslabel>                                  
                </td>
                </tr>
              <tr>
                <td class="fontnormal">
                <mifos:mifoslabel name="Group.previewcreatepagehead1" bundle="GroupUIResources"></mifos:mifoslabel>
                <mifos:mifoslabel name="Group.editpreviewMsg1" bundle="GroupUIResources"></mifos:mifoslabel>
                <mifos:mifoslabel name="${ConfigurationConstants.GROUP}"></mifos:mifoslabel>                
                <mifos:mifoslabel name="Group.editpreviewMsg2" bundle="GroupUIResources"></mifos:mifoslabel>                
                <mifos:mifoslabel name="Group.editMag2" bundle="GroupUIResources"></mifos:mifoslabel> 
				<mifos:mifoslabel name="${ConfigurationConstants.GROUP}"></mifos:mifoslabel>
				<mifos:mifoslabel name="Group.editMag3" bundle="GroupUIResources"></mifos:mifoslabel>
                 
                 </td>
              </tr>
              <tr>
   				<td>
   				<font class="fontnormalRedBold"><html-el:errors bundle="GroupUIResources"/></font>
				</td>
			</tr>
            </table>
            <br>
                        
            <table width="93%" border="0" cellpadding="0" cellspacing="0">
            
              <tr>
                <td width="100%" height="23" class="fontnormalboldorange">
				  <mifos:mifoslabel name="${ConfigurationConstants.GROUP}"></mifos:mifoslabel>                
                  <mifos:mifoslabel name="Group.groupinformation"  bundle="GroupUIResources"></mifos:mifoslabel>
                </td>
              </tr>
              
              <tr>
                <td height="23" class="fontnormalbold">
                 <mifos:mifoslabel name="Group.groupname" bundle="GroupUIResources"></mifos:mifoslabel>
                 <span class="fontnormal">
             	 <c:out value="${sessionScope.groupCustActionForm.displayName}"/>  <br>
                  </span><span class="fontnormal"> </span><span class="fontnormal"> </span>
                  <c:if test="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'CenterHierarchyExist') eq 'No'}">
                  <span class="fontnormalbold">
                  <mifos:mifoslabel name="Group.loanofficerassigned" bundle="GroupUIResources"></mifos:mifoslabel></span>
                  <span class="fontnormal">
                	<c:forEach var="LO" items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'loanOfficers')}">
						<c:if test = "${LO.personnelId == sessionScope.groupCustActionForm.loanOfficerId}">
							<c:out value="${LO.displayName}"/><br>
						</c:if>
					</c:forEach>
                 </span><span class="fontnormal"><br>
                    </span>
                   </c:if>
                  </td>
                  </tr>
                  <tr id="Group.Trained">
                <td class="fontnormalbold">  
                 <mifos:mifoslabel name="Group.grouptrained" bundle="GroupUIResources" keyhm="Group.Trained" isManadatoryIndicationNotRequired="yes"></mifos:mifoslabel>
              		<c:choose>
								<c:when test="${sessionScope.groupCustActionForm.trained eq '1'}">
									<span class="fontnormal"><mifos:mifoslabel
										name="Group.YesLabel" bundle="GroupUIResources"></mifos:mifoslabel><br>
									</span>
								</c:when>
								<c:otherwise>
									<span class="fontnormal"><mifos:mifoslabel
										name="Group.NoLabel" bundle="GroupUIResources"></mifos:mifoslabel><br>
									</span>
								</c:otherwise>
						</c:choose> 
	                    <mifos:mifoslabel name="Group.grouptrainedon" bundle="GroupUIResources" keyhm="Group.TrainedDate" isManadatoryIndicationNotRequired="yes"></mifos:mifoslabel>
	              		<span class="fontnormal"><c:out value="${sessionScope.groupCustActionForm.trainedDate}" /></span>
	              <br>      
      			<span class="fontnormal"></span><span class="fontnormal"> </span>
      		</td>
      		</tr>
      		
      		 <tr id="Group.ExternalId">
                <td height="23" class="fontnormalbold">      
     			 <mifos:mifoslabel name="${ConfigurationConstants.EXTERNALID}" isColonRequired="Yes" keyhm="Group.ExternalId" isManadatoryIndicationNotRequired="yes"></mifos:mifoslabel>
			      <span class="fontnormal">
			       	<c:out value="${sessionScope.groupCustActionForm.externalId}"/>
			       </span><br>
		     </td>
		     </tr>
		     
		      <tr>
                <td height="23" class="fontnormalbold">
      				 <mifos:mifoslabel name="Group.FormedBy" bundle="GroupUIResources"></mifos:mifoslabel>
	   			<span class="fontnormal"><c:out value="${BusinessKey.customerFormedByPersonnel.displayName}"/> </span><br>
       			 <br>
     			</td>
     			</tr>
     			
     			 <tr>
                <td height="23" class="fontnormalbold">
			      	<mifos:mifoslabel name="Group.officialtitlesassigned" bundle="GroupUIResources"></mifos:mifoslabel>
			      <span class="fontnormal"><br>
			      </span> 
			      	<c:forEach var="position" items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'positions')}">
						 <c:forEach var="cp" items="${sessionScope.groupCustActionForm.customerPositions}">
							<c:if test="${position.id==cp.positionId}">
								<c:out value="${position.name}"/>:
								<c:forEach var="client" items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'clients')}">
		    	            	 	<c:if test="${client.customerId==cp.customerId}">
			                	 		<span class="fontnormal"><c:out value="${client.displayName}"/></span>
									</c:if>
								</c:forEach>
							</c:if>
						</c:forEach><br>
    				  </c:forEach> 
					<br>
					<br>
				</td>
				</tr>
				
				<tr id="Group.Address">
                <td height="23" class="fontnormalbold">
				     	<mifos:mifoslabel name="Group.address" bundle="GroupUIResources" keyhm="Group.Address" isManadatoryIndicationNotRequired="yes"></mifos:mifoslabel>
					<span class="fontnormal"><span class="fontnormal"><br>
				</span></span>

				<%-- Group Adderss Detail --%>
				<%--<mifos:mifoslabel name="Group.address1"  bundle="GroupUIResources"></mifos:mifoslabel>--%>
				<span class="fontnormal">
					<c:out value="${sessionScope.groupCustActionForm.address.displayAddress}"/><br>
				</span>
				</td>
				</tr>
				<tr id="Group.City">
                <td class="fontnormalbold">
					<mifos:mifoslabel name="${ConfigurationConstants.CITY}" isColonRequired="yes" keyhm="Group.City" isManadatoryIndicationNotRequired="yes"/>
				<span class="fontnormal">
					<c:out value="${sessionScope.groupCustActionForm.address.city}"/>
				</span>
				</td>
				</tr>
				<tr id="Group.State">
                <td class="fontnormalbold">
					<mifos:mifoslabel name="${ConfigurationConstants.STATE}" isColonRequired="yes" keyhm="Group.State" isManadatoryIndicationNotRequired="yes"/>
				<span class="fontnormal"> 
					<c:out value="${sessionScope.groupCustActionForm.address.state}"/>
				</span>
				</td>
				</tr>
				<tr id="Group.Country">
                <td class="fontnormalbold">
				<mifos:mifoslabel name="Group.country" bundle="GroupUIResources" keyhm="Group.Country" isManadatoryIndicationNotRequired="yes"></mifos:mifoslabel>
				<span class="fontnormal">
					<c:out value="${sessionScope.groupCustActionForm.address.country}"/>
				</span>
				</td>
				</tr>
				<tr id="Group.PostalCode">
                <td class="fontnormalbold">
				<mifos:mifoslabel name="${ConfigurationConstants.POSTAL_CODE}" isColonRequired="yes" keyhm="Group.PostalCode" isManadatoryIndicationNotRequired="yes"/>
				<span class="fontnormal"> 
					<c:out value="${sessionScope.groupCustActionForm.address.zip}"/>
				</span>
				</td>
				</tr>
				<tr id="Group.PhoneNumber">
                <td class="fontnormalbold">
				<mifos:mifoslabel name="Group.telephone" bundle="GroupUIResources" keyhm="Group.PhoneNumber" isManadatoryIndicationNotRequired="yes"></mifos:mifoslabel>
				<span class="fontnormal"> 
					<c:out value="${sessionScope.groupCustActionForm.address.phoneNumber}"/><br>
				<br>
				</span>
				</td>
				</tr>

			<%-- Group Adderss Detail ends --%>

      		<%-- Custom Fields  Values --%>
      		 <tr>
                <td height="23" class="fontnormalbold">
				      <mifos:mifoslabel name="Group.additionalinformation" bundle="GroupUIResources"></mifos:mifoslabel>
					<br>
		
		      	  <c:forEach var="cf" items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'customFields')}">
						 <c:forEach var="customField" items="${sessionScope.groupCustActionForm.customFields}">
							<c:if test="${cf.fieldId==customField.fieldId}">
								<mifos:mifoslabel name="${cf.lookUpEntity.entityType}" bundle="CenterUIResources"></mifos:mifoslabel>: 
					         	<span class="fontnormal"><c:out value="${customField.fieldValue}"/></span><br>
							</c:if>
						</c:forEach>
    			</c:forEach> 
		      <br>
	<html-el:button property="editInfo" styleClass="insidebuttn" style="width:130px;" onclick="GoToEditPage()">
		<mifos:mifoslabel name="Group.edit" bundle="GroupUIResources"/><c:out value=" "/><mifos:mifoslabel name="${ConfigurationConstants.GROUP}"/><c:out value=" "/><mifos:mifoslabel name="Group.groupinformation" bundle="GroupUIResources"/>
	</html-el:button>
</td>
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
                <html-el:submit property="submitBtn" style="width:70px" styleClass="buttn">
			       <mifos:mifoslabel name="button.submit" bundle="GroupUIResources"></mifos:mifoslabel>
                </html-el:submit>
                <html-el:button property="cancelBtn"  styleClass="cancelbuttn" style="width:70px" onclick="goToCancelPage(this.form)">
	                   <mifos:mifoslabel name="button.cancel" bundle="GroupUIResources"></mifos:mifoslabel>
                </html-el:button>
                </td>
              </tr>
            </table>
            <br>
          </td>
        </tr>
      </table>

<br>
<html-el:hidden property="input" value="PreviewManageGroup"/> 
<html-el:hidden property="currentFlowKey" value="${requestScope.currentFlowKey}" />
</html-el:form>
</tiles:put>
</tiles:insert>
