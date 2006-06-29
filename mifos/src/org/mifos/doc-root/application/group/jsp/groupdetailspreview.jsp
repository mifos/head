<!-- /**
 
 * groupdetailspreview.jsp    version: 1.0
 
 
 
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
<tiles:insert definition=".clientsacclayoutsearchmenu">
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
	groupActionForm.action="GroupAction.do?method=previous";
	groupActionForm.submit();
  }
  
</script>
<SCRIPT SRC="pages/application/group/js/groupcommon.js"></SCRIPT>
<html-el:form action="GroupAction.do?method=update"  onsubmit="func_disableSubmitBtn('submitBtn')"> 
 
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
            <table width="95%" border="0" cellpadding="3" cellspacing="0">
              <tr>
                <td width="64%" class="headingorange">
                <span class="heading">
                <c:out value="${requestScope.GroupVO.displayName}"/>
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
             	 <c:out value="${requestScope.GroupVO.displayName}"/>  <br>
                  </span><span class="fontnormal"> </span><span class="fontnormal"> </span>
                  
                  <c:if test="${sessionScope.CenterHierarchyExist eq 'No'}">
                  <span class="fontnormalbold">
                  <mifos:mifoslabel name="Group.loanofficerassigned" bundle="GroupUIResources"></mifos:mifoslabel>
                  </span><span class="fontnormal"> 
                  <c:out value="${requestScope.GroupVO.personnel.displayName}"/> </span><span class="fontnormal"><br>
                    </span>
                    <%--
                    <span class="fontnormalbold">
                                   <mifos:mifoslabel name="Group.locationofthemeeting" bundle="GroupUIResources"></mifos:mifoslabel>
                    </span>
                    <span class="fontnormal">
                    <c:out value="${requestScope.GroupVO.customerMeeting.meetingPlace}"/> <br>
                    </span>--%>
                  </c:if>
                  
                  </td>
                  </tr>
                  
                <tr id="Group.Trained">
                <td class="fontnormalbold">  
                 <mifos:mifoslabel name="Group.grouptrained" bundle="GroupUIResources" keyhm="Group.Trained" isManadatoryIndicationNotRequired="yes"></mifos:mifoslabel>
              		<c:choose>
					<c:when test="${sessionScope.isTrained == 1}">
                    	<span class="fontnormal"> Yes</span><br>
                    	<mifos:mifoslabel name="Group.grouptrainedon" bundle="GroupUIResources" keyhm="Group.TrainedDate" isManadatoryIndicationNotRequired="yes"></mifos:mifoslabel>
	                  <span class="fontnormal">
    		              <c:out value="${userdatefn:getUserLocaleDate(sessionScope.UserContext.pereferedLocale,sessionScope.trainedDate)}" />
		    		   </span>
                    </c:when>
				    <c:otherwise>
				    	<c:choose>
				    	<c:when test="${requestScope.GroupVO.trained == 1}">
   		                    <span class="fontnormal"> Yes</span><br>
   		                    <mifos:mifoslabel name="Group.grouptrainedon" bundle="GroupUIResources" keyhm="Group.TrainedDate" isManadatoryIndicationNotRequired="yes"></mifos:mifoslabel>
		                	<span class="fontnormal">
	    		              <c:out value="${userdatefn:getUserLocaleDate(sessionScope.UserContext.pereferedLocale,requestScope.GroupVO.trainedDate)}" />
			    		    </span>
				    	</c:when>
				    	<c:otherwise>
	                    <span class="fontnormal"> No</span><br>
	                    <mifos:mifoslabel name="Group.grouptrainedon" bundle="GroupUIResources" keyhm="Group.TrainedDate" isManadatoryIndicationNotRequired="yes"></mifos:mifoslabel>
	                	<%--<span class="fontnormal">
    		              <c:out value="${userdatefn:getUserLocaleDate(sessionScope.UserContext.pereferedLocale,requestScope.GroupVO.trainedDate)}" />
		    		    </span>--%>
		    		    </c:otherwise>
		    		    </c:choose>
				    </c:otherwise>
			    </c:choose>
			    
                  <br>      
      			<span class="fontnormal"></span><span class="fontnormal"> </span>
      		</td>
      		</tr>
      		
      		 <tr id="Group.ExternalId">
                <td height="23" class="fontnormalbold">      
     			 <mifos:mifoslabel name="${ConfigurationConstants.EXTERNALID}" isColonRequired="Yes" keyhm="Group.ExternalId" isManadatoryIndicationNotRequired="yes"></mifos:mifoslabel>
			      <span class="fontnormal">
			       	<c:out value="${requestScope.GroupVO.externalId}"/>
			       </span><br>
		     </td>
		     </tr>
		     
		      <tr>
                <td height="23" class="fontnormalbold">
      				 <mifos:mifoslabel name="Group.FormedBy" bundle="GroupUIResources"></mifos:mifoslabel>
	   			<span class="fontnormal"><c:out value="${requestScope.GroupVO.customerFormedByPersonnel.displayName}"/> </span><br>
       
			       <%--collection sheet and programs 
			   <mifos:mifoslabel name="Group.collectionsheettype" bundle="GroupUIResources"></mifos:mifoslabel>
			   <span class="fontnormal"> Collection sheet type 1</span><br>
			      <br>
			      
			     <mifos:mifoslabel name="Group.programs" bundle="GroupUIResources"></mifos:mifoslabel>
			      <span class="fontnormal"><span class="fontnormal"><br>
			      <c:forEach var="program" items="${requestScope.customerPrograms}">
					<c:out value="${program.programName}"/><br>
				  </c:forEach>  </span><br>
			      <br>
			      </span>
			      --%>
     			 <br>
     			</td>
     			</tr>
     			
     			 <tr>
                <td height="23" class="fontnormalbold">
			      	<mifos:mifoslabel name="Group.officialtitlesassigned" bundle="GroupUIResources"></mifos:mifoslabel>
			      <span class="fontnormal"><br>
			      </span> 
			      	<c:forEach var="pos" items="${requestScope.positions}">
					      	<c:out value="${pos.positionName}"/>:
			      		<c:forEach var="cp" items="${requestScope.GroupVO.customerPositions}">
					      		<c:if test="${pos.positionId==cp.positionId}">
					      			<c:forEach var="client" items="${requestScope.clients}">
					      				<c:if test="${client.customerId==cp.customerId}">
						                 		<span class="fontnormal"><c:out value="${client.displayName}"/></span>
										</c:if>
									</c:forEach>
								</c:if>
						</c:forEach> 
						<br>
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
				<span class="fontnormal"><span class="fontnormal"> 
					<c:out value="${requestScope.GroupVO.displayAddress}"/><br>
				</span></span>
				</td>
				</tr>
				
				<tr id="Group.City">
                <td height="23" class="fontnormalbold">
					<mifos:mifoslabel name="${ConfigurationConstants.CITY}" isColonRequired="yes" keyhm="Group.City" isManadatoryIndicationNotRequired="yes"/>
				<span class="fontnormal"><span class="fontnormal">
					<c:out value="${requestScope.GroupVO.customerAddressDetail.city}"/><br>
				</span></span>
				</td>
				</tr>
				
				<tr id="Group.State">
                <td height="23" class="fontnormalbold">
					<mifos:mifoslabel name="${ConfigurationConstants.STATE}" isColonRequired="yes" keyhm="Group.State" isManadatoryIndicationNotRequired="yes"/>
				<span class="fontnormal"><span class="fontnormal"> 
					<c:out value="${requestScope.GroupVO.customerAddressDetail.state}"/><br>
				</span></span>
				</td>
				</tr>
				
				<tr id="Group.Country">
                <td height="23" class="fontnormalbold">
				<mifos:mifoslabel name="Group.country" bundle="GroupUIResources" keyhm="Group.Country" isManadatoryIndicationNotRequired="yes"></mifos:mifoslabel>
				<span class="fontnormal"><span class="fontnormal">
					<c:out value="${requestScope.GroupVO.customerAddressDetail.country}"/><br>
				</span></span>
				</td>
				</tr>
				
				<tr id="Group.PostalCode">
                <td height="23" class="fontnormalbold">
				<mifos:mifoslabel name="${ConfigurationConstants.POSTAL_CODE}" isColonRequired="yes" keyhm="Group.PostalCode" isManadatoryIndicationNotRequired="yes"/>
				<span class="fontnormal"> <span class="fontnormal">
					<c:out value="${requestScope.GroupVO.customerAddressDetail.zip}"/><br>
				<br>
				</span></span>
				</td>
				</tr>
				
				<tr id="Group.PhoneNumber">
                <td height="23" class="fontnormalbold">
				<mifos:mifoslabel name="Group.telephone" bundle="GroupUIResources" keyhm="Group.PhoneNumber" isManadatoryIndicationNotRequired="yes"></mifos:mifoslabel>
				<span class="fontnormal"> <span class="fontnormal">
					<c:out value="${requestScope.GroupVO.customerAddressDetail.phoneNumber}"/></span><br>
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
		
		      	  <c:forEach var="cfdef" items="${requestScope.customFields}">
						 <c:forEach var="cf" items="${requestScope.GroupVO.customFieldSet}">
							<c:if test="${cfdef.fieldId==cf.fieldId}">
								<mifos:mifoslabel name="${cfdef.lookUpEntity.entityType}" bundle="GroupUIResources"/>: 
				           	 	<span class="fontnormal"><c:out value="${cf.fieldValue}"/></span><br>
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
</html-el:form>
</tiles:put>
</tiles:insert>