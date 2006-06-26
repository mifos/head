<!-- /**
 
 * creategrouppreview.jsp    version: 1.0
 
 
 
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
<tiles:insert definition=".withoutmenu">
 <tiles:put name="body" type="string">
 <SCRIPT SRC="pages/framework/js/CommonUtilities.js"></SCRIPT>
<script language="javascript">
  function SetGroupToPartial(){
	disableButtons();
	document.getElementsByName("statusId")[0].value=1;
	groupActionForm.submit();
  }
  function SetGroupToPending(){
	disableButtons();
	document.getElementsByName("statusId")[0].value=2;
	groupActionForm.submit();
  }
  function SetGroupToActive(){
	disableButtons();
	document.getElementsByName("statusId")[0].value=3;
	groupActionForm.submit();
  }
  
	function disableButtons(){
		func_disableSubmitBtn("submitBtn1");
		func_disableSubmitBtn("submitBtn2");
	}  
  function GoToEditPage(){
	groupActionForm.action="GroupAction.do?method=previous";
	groupActionForm.submit();
  }
  function goToCancelPage(){
	groupActionForm.action="GroupAction.do?method=cancel";
	groupActionForm.submit();
  }
</script>

<html-el:form action="GroupAction.do?method=create" > 
 <html-el:hidden property="statusId" value="1" /> 
 <html-el:hidden property="input" value="PreviewCreateNewGroup"/> 

<table width="100%" border="0" cellspacing="0" cellpadding="0">
  <tr>
    <td height="350" align="left" valign="top" bgcolor="#FFFFFF"> 
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
                    <td><img src="pages/framework/images/timeline/tick.gif" width="17" height="17"></td>
                    <td class="timelineboldgray">
                    <c:choose>
						<c:when test="${sessionScope.CenterHierarchyExist eq 'Yes'}">
							<mifos:mifoslabel name="Group.select" bundle="GroupUIResources"></mifos:mifoslabel>
							<mifos:mifoslabel name="${ConfigurationConstants.CENTER}" ></mifos:mifoslabel>
						</c:when>
						<c:otherwise>
		                    <mifos:mifoslabel name="Group.choosebranch" bundle="GroupUIResources"></mifos:mifoslabel>
		                    <mifos:mifoslabel name="${ConfigurationConstants.BRANCHOFFICE}" ></mifos:mifoslabel>
	                   </c:otherwise>
	                 </c:choose>
                    </td>
                  </tr>
                </table>
              </td>
              <td width="34%" align="center">
                <table border="0" cellspacing="0" cellpadding="0">
                  <tr>
                    <td><img src="pages/framework/images/timeline/tick.gif" width="17" height="17"></td>
                    <td class="timelineboldgray">
                    	<mifos:mifoslabel name="${ConfigurationConstants.GROUP}" ></mifos:mifoslabel>
	                    <mifos:mifoslabel name="Group.groupinformation" bundle="GroupUIResources"></mifos:mifoslabel>
                    </td>
                  </tr>
                </table>
              </td>
              <td width="33%" align="right">
                <table border="0" cellspacing="0" cellpadding="0">
                  <tr>
                    <td><img src="pages/framework/images/timeline/bigarrow.gif" width="17" height="17"></td>
                    <td class="timelineboldorange">
                    	<mifos:mifoslabel name="Group.reviewandsubmit" bundle="GroupUIResources"></mifos:mifoslabel>
                    </td>
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
                  <td class="headingorange"><span class="heading">
                   	<mifos:mifoslabel name="Group.create" bundle="GroupUIResources"></mifos:mifoslabel>                  
                   	<mifos:mifoslabel name="Group.new" bundle="GroupUIResources"></mifos:mifoslabel>
                  	<mifos:mifoslabel name="${ConfigurationConstants.GROUP}" ></mifos:mifoslabel>
                  - </span>
                 	<mifos:mifoslabel name="Group.reviewandsubmit" bundle="GroupUIResources"></mifos:mifoslabel>
                 </td>
                </tr>
                <tr>
                  <td class="fontnormal"> 
                  <mifos:mifoslabel name="Group.previewcreatepagehead1" bundle="GroupUIResources"></mifos:mifoslabel>
                  <mifos:mifoslabel name="Group.previewcreatepagehead2" bundle="GroupUIResources"></mifos:mifoslabel>
                  <mifos:mifoslabel name="Group.createpagehead3" bundle="GroupUIResources"></mifos:mifoslabel>
                  </td>
                </tr>
                <tr>
   					<td>
   					<font class="fontnormalRedBold"><html-el:errors bundle="GroupUIResources"/></font>
					</td>
				</tr>
              </table>
              
		    <c:if test="${sessionScope.CenterHierarchyExist eq 'Yes'}">
		    <br>
		   	<table width="93%" border="0" cellspacing="0" cellpadding="3">
                <tr>
                  <td>
                  	<span class="fontnormalbold">
	                   <mifos:mifoslabel name="Group.loanofficerassigned" bundle="GroupUIResources"></mifos:mifoslabel>
                    </span>
                    <span class="fontnormal">
                  		<c:out value="${requestScope.GroupParent.personnel.displayName}"/>  <br>
              		</span>
              		<span class="fontnormalbold">
						 <mifos:mifoslabel name="${ConfigurationConstants.CENTER}" ></mifos:mifoslabel>              		
              			 <mifos:mifoslabel name="Group.assigned" bundle="GroupUIResources"></mifos:mifoslabel>
              		</span>
              		<span class="fontnormal">
              			<c:out value="${requestScope.GroupParent.displayName}"/><br>
              		</span>
              		<span class="fontnormalbold">
              			<mifos:mifoslabel name="Group.meetingschedule" bundle="GroupUIResources"></mifos:mifoslabel>
              		</span>
              		<span class="fontnormal">
	              		<c:out value="${requestScope.GroupParent.customerMeeting.meeting.meetingSchedule}"/>
              			<br>
              		</span>
              		<span class="fontnormalbold">
              			<mifos:mifoslabel name="Group.locationofthemeeting" bundle="GroupUIResources"></mifos:mifoslabel>
              		</span>
              		<span class="fontnormal">
              			<c:out value="${requestScope.GroupParent.customerMeeting.meeting.meetingPlace}"/>
              			<br>
              		</span>
              		</td>
                </tr>
              </table>              
             </c:if>

              <br>
              
              
              
              
              
              
              
              
              
              
              
              <table width="93%" border="0" cellpadding="0" cellspacing="0">
              
                <tr>
                  <td width="100%" height="23" class="fontnormalboldorange">
                  <mifos:mifoslabel name="${ConfigurationConstants.GROUP}" ></mifos:mifoslabel>
                  <mifos:mifoslabel name="Group.groupinformation" bundle="GroupUIResources"></mifos:mifoslabel>
                  </td>
                </tr>
                
                <tr>
                  <td class="fontnormalbold">
                  <mifos:mifoslabel name="Group.groupname" bundle="GroupUIResources"></mifos:mifoslabel>
                  <span class="fontnormal"> 
	               <c:out value="${requestScope.GroupVO.displayName}"/>  
                   <br>
                   </span>
                  </td>
                  </tr>
                  
                  <tr>
                  <td class="fontnormalbold">
                  <mifos:mifoslabel name="Group.FormedBy" bundle="GroupUIResources"></mifos:mifoslabel>
		          <span class="fontnormal"><c:out value="${requestScope.GroupVO.customerFormedByPersonnel.displayName}"/> </span><br>
                  </td>
                  </tr>
                 <%-- Show following only if center hierarchy does not exists --%>
                 
                 <tr>
                 <td class="fontnormalbold">
              	 <c:if test="${sessionScope.CenterHierarchyExist eq 'No'}">
                  <span class="fontnormalbold">
                  <mifos:mifoslabel name="Group.loanofficerassigned" bundle="GroupUIResources"></mifos:mifoslabel>
                  </span><span class="fontnormal"> 
                  <c:out value="${requestScope.GroupVO.personnel.displayName}"/> </span><span class="fontnormal"><br>
                    </span><span class="fontnormalbold">
                    
                    <mifos:mifoslabel name="Group.locationofthemeeting" bundle="GroupUIResources"></mifos:mifoslabel>
                    </span><span class="fontnormal">
                    <c:out value="${requestScope.GroupVO.customerMeeting.meeting.meetingPlace}"/><br>
                    </span>
                    <span class="fontnormalbold">
	                    <mifos:mifoslabel name="Group.meetingschedule" bundle="GroupUIResources"></mifos:mifoslabel>
                    </span>
                    <span class="fontnormal">
						<c:out value="${requestScope.GroupVO.customerMeeting.meeting.meetingSchedule}"/><br> 
					</span>
              </c:if>
              	</td>
              	</tr>
                  <%-- Show following always --%>
                  
                  <tr id="Group.Trained">
                  <td class="fontnormalbold">
                    <mifos:mifoslabel name="Group.grouptrained" bundle="GroupUIResources" keyhm="Group.Trained" isManadatoryIndicationNotRequired="yes"></mifos:mifoslabel>
                	<c:choose>
					<c:when test="${requestScope.GroupVO.trained == 1}">
                    	<span class="fontnormal"> Yes</span>
                    </c:when>
				    <c:otherwise>
	                    <span class="fontnormal"> No</span>
				    </c:otherwise>
			        </c:choose>
                    <span class="fontnormal"><br>
                    </span><span class="fontnormal">
					</span>
				</td>
				</tr>
				
				<tr id="Group.TrainedDate">
                  <td class="fontnormalbold">
					 <mifos:mifoslabel name="Group.grouptrainedon" bundle="GroupUIResources" keyhm="Group.TrainedDate" isManadatoryIndicationNotRequired="yes"></mifos:mifoslabel>
					<span class="fontnormal">
					<c:out value="${userdatefn:getUserLocaleDate(sessionScope.UserContext.pereferedLocale,requestScope.GroupVO.trainedDate)}" />
						<%--<c:out value="${requestScope.GroupVO.trainedDate}"/>--%>
					</span><br>
					<%-- <mifos:mifoslabel name="Group.dategroupstarted" bundle="GroupUIResources"></mifos:mifoslabel>
					<span class="fontnormal">
						<c:out value="${requestScope.GroupVO.createdDate}"/>
					</span><br>--%>
				</td>
				</tr>
				
				<tr id="Group.ExternalId">
                  <td class="fontnormalbold">
					<mifos:mifoslabel name="${ConfigurationConstants.EXTERNALID}" keyhm="Group.ExternalId" isColonRequired="yes" isManadatoryIndicationNotRequired="yes"/>
					<span class="fontnormal">
						 <c:out value="${requestScope.GroupVO.externalId}"/></span><br>					
					<%--  collection sheet and programs 
					<span class="fontnormal"> 
						<mifos:mifoslabel name="Group.collectionsheettype" bundle="GroupUIResources"></mifos:mifoslabel>
						Collection Sheet Type 1
							<c:out value="${requestScope.GroupVO.collectionSheet.collectionSheetName}"/></span><br>
						<br>
					 <mifos:mifoslabel name="Group.programs" bundle="GroupUIResources"></mifos:mifoslabel>
					 <span class="fontnormal"><span class="fontnormal"><br>
								<c:forEach var="program" items="${requestScope.customerPrograms}">
									<c:out value="${program.programName}"/><br>
								</c:forEach> 
					</span><br> 
					<br>
					</span>--%>
					<br> 
					</td>
					</tr>
					
				  <tr id="Group.Address">
                  <td class="fontnormalbold">
						<mifos:mifoslabel name="Group.address" bundle="GroupUIResources" keyhm="Group.Address" isManadatoryIndicationNotRequired="yes"></mifos:mifoslabel><br>
					
					<%--<mifos:mifoslabel name="Group.address1" bundle="GroupUIResources"></mifos:mifoslabel>--%>
					<span class="fontnormal"> 
						<c:out value="${requestScope.GroupVO.displayAddress}"/><br>
					</span>
				  </td>
				  </tr>
				  
				  <tr id="Group.City">
                  <td class="fontnormalbold">
					<mifos:mifoslabel name="${ConfigurationConstants.CITY}" isColonRequired="yes" keyhm="Group.City" isManadatoryIndicationNotRequired="yes"/>
					<span class="fontnormal">
						<c:out value="${requestScope.GroupVO.customerAddressDetail.city}"/><br>
					</span>
				</td>
				</tr>
				
				<tr id="Group.State">
                  <td class="fontnormalbold">
					<mifos:mifoslabel name="${ConfigurationConstants.STATE}" isColonRequired="yes" keyhm="Group.State" isManadatoryIndicationNotRequired="yes"/>
				<span class="fontnormal"> 
					<c:out value="${requestScope.GroupVO.customerAddressDetail.state}"/><br>
				</span>
				</td>
				</tr>
				
				<tr id="Group.Country">
                  <td class="fontnormalbold">
					<mifos:mifoslabel name="Group.country" bundle="GroupUIResources" keyhm="Group.Country" isManadatoryIndicationNotRequired="yes"></mifos:mifoslabel>
					<span class="fontnormal">
						<c:out value="${requestScope.GroupVO.customerAddressDetail.country}"/><br>
					</span>
				</td>
				</tr>
				
				<tr id="Group.PostalCode">
                  <td class="fontnormalbold">
					<mifos:mifoslabel name="${ConfigurationConstants.POSTAL_CODE}" isColonRequired="yes" keyhm="Group.PostalCode" isManadatoryIndicationNotRequired="yes"/>
					<span class="fontnormal">
						<c:out value="${requestScope.GroupVO.customerAddressDetail.zip}"/><br>
					<br>
					</span>
					</td>
					</tr>
					
				<tr id="Group.PhoneNumber">
                  <td class="fontnormalbold">
					<mifos:mifoslabel name="Group.telephone" bundle="GroupUIResources" keyhm="Group.PhoneNumber" isManadatoryIndicationNotRequired="yes"></mifos:mifoslabel>
					<span class="fontnormal">
						<c:out value="${requestScope.GroupVO.customerAddressDetail.phoneNumber}"/></span><br>
											
				</td>
				</tr>	
			
			<tr>
                  <td class="fontnormalbold">
                  <br>
					<mifos:mifoslabel name="Group.additionalinformation" bundle="GroupUIResources"></mifos:mifoslabel>
					<br>					
					<c:forEach var="cfdef" items="${requestScope.customFields}">
						 <c:forEach var="cf" items="${requestScope.GroupVO.customFieldSet}">
							<c:if test="${cfdef.fieldId==cf.fieldId}">
								<mifos:mifoslabel name="${cfdef.lookUpEntity.entityType}" bundle="GroupUIResources"></mifos:mifoslabel>: 
				           	 	<span class="fontnormal"><c:out value="${cf.fieldValue}"/></span><br>
							</c:if>
						</c:forEach>
		  		  </c:forEach>  
  		 			 <br>
  		 	</td>
  		 	</tr>
  		 	
  		 	<tr>
                  <td class="fontnormalbold">
					<mifos:mifoslabel name="Group.adminsetfess" bundle="GroupUIResources"></mifos:mifoslabel><br>
							<c:forEach var="adminFeeMaster" items="${requestScope.adminFeesList}" >
								<c:forEach var="adminFee" items="${sessionScope.groupActionForm.adminFeeList}">
								   <c:if test="${adminFeeMaster.feeId==adminFee.feeId}">
										<c:if test="${adminFee.checkedFee != 1}">
										   <c:out value="${adminFee.feeName}"/>:
										   <span class="fontnormal">
										   		<c:out value="${adminFee.rateOrAmount}"/> 
										   		<mifos:mifoslabel name="Group.periodicity" bundle="GroupUIResources" />
										   		<c:choose>
										   		<c:when test="${adminFeeMaster.feeFrequencyTypeId == 1}">
													<c:out value="${adminFeeMaster.feeMeeting.feeMeetingSchedule}"/>
												</c:when>
												<c:otherwise>
													<mifos:mifoslabel name="Fees.onetime"/>
												</c:otherwise>
												</c:choose>
										   </span><br>
										</c:if> 
									</c:if>
								</c:forEach> 
							</c:forEach>
					<br>
			</td>
			</tr>
			
			<tr>
                  <td class="fontnormalbold">		
						<mifos:mifoslabel name="Group.additionalfees" bundle="GroupUIResources"></mifos:mifoslabel><br>
						<c:forEach var="additionalFee" items="${requestScope.additionalFees}" >
				         		<c:out value="${additionalFee.feeName}"/>:
								<span class="fontnormal"><span class="fontnormal">
									<c:out value="${additionalFee.rateOrAmount}"/> 
									<mifos:mifoslabel name="Group.periodicity" bundle="GroupUIResources" />
									<c:choose>
										<c:when test="${additionalFee.feeFrequencyTypeId == 1}">
											<c:out value="${additionalFee.feeMeeting.feeMeetingSchedule}"/>
										</c:when>
										<c:otherwise>
											<mifos:mifoslabel name="Fees.onetime"/>
										</c:otherwise>
									</c:choose>
								</span><br></span>
						</c:forEach>
					<br>  <br>                  
                          
                  <html-el:button property="editInfo" styleClass="insidebuttn" style="width:130px;" onclick="GoToEditPage()">
						<mifos:mifoslabel name="Group.edit" bundle="GroupUIResources"/><c:out value=" "/><mifos:mifoslabel name="${ConfigurationConstants.GROUP}" /><c:out value=" "/><mifos:mifoslabel name="Group.groupinformation" bundle="GroupUIResources"></mifos:mifoslabel>
                  </html-el:button>
                  </td>
                </tr>
              </table>
              
              
              
              
              
              
              
              
              
              
              
              
              
              
              
              
              
              <table width="93%" border="0" cellpadding="0" cellspacing="0">
              	<tr>
                  <td align="center" class="blueline">&nbsp;</td>
                </tr>
                <tr>
                  <td align="center">&nbsp;</td>
                </tr>
                <tr>
                 <td align="center">
                  <html-el:submit property="submitBtn1" onclick="javascript:SetGroupToPartial()" style="width:100px" styleClass="buttn">
                  	<mifos:mifoslabel name="button.saveforlater" bundle="GroupUIResources"></mifos:mifoslabel>
                  </html-el:submit>
&nbsp;
			<c:choose>
		    	<c:when test="${sessionScope.IsPendingApprovalDefined eq 'Yes'}">
				 <html-el:button property="submitBtn2" styleClass="buttn"   onclick="SetGroupToPending()" style="width:130px">
				 	<mifos:mifoslabel name="button.submitforapproval" bundle="GroupUIResources"></mifos:mifoslabel>
				 </html-el:button>
				</c:when>
				<c:otherwise>	 
				 <html-el:button property="submitBtn2" styleClass="buttn"  onclick="SetGroupToActive()" style="width:80px">
				 	<mifos:mifoslabel name="button.approved" bundle="GroupUIResources"></mifos:mifoslabel>
				 </html-el:button>
				</c:otherwise>		
			</c:choose>					 
				 &nbsp;
                 <html-el:button property="cancelBtn"  styleClass="cancelbuttn" style="width:70px" onclick="goToCancelPage()">
	                    <mifos:mifoslabel name="button.cancel" bundle="GroupUIResources"></mifos:mifoslabel>
                 </html-el:button>
                  </td></tr>
            </table>
            <br></td>
          </tr>
        </table>
      <br></td>
  </tr>
</table>
</html-el:form>
</tiles:put>
</tiles:insert>

