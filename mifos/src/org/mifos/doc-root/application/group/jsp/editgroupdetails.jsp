<!-- /**
 
 * editgroupdetails.jsp    version: 1.0
 
 
 
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
<%@ taglib uri="/tags/mifos-html" prefix="mifos"%>
<%@ taglib uri="/userlocaledate" prefix="userdatefn"%>
<%@ taglib uri="/tags/date" prefix="date"%>

<%@ taglib uri="/tags/mifos-html" prefix="mifos"%>
<tiles:insert definition=".clientsacclayoutsearchmenu">
	<tiles:put name="body" type="string">

		<script language="javascript">
	function chkForValidDates(){
		
	  		if(! chkForDateTrainedDate())
				return false;
	 		 	  	
			if (groupActionForm.fieldTypeList.length!= undefined && groupActionForm.fieldTypeList.length!= null){	  	
				for(var i=0; i <=groupActionForm.fieldTypeList.length;i++){
					if (groupActionForm.fieldTypeList[i]!= undefined){
						if(groupActionForm.fieldTypeList[i].value == "3"){
							var customFieldDate = document.getElementById("customField["+i+"].fieldValue");
							var customFieldDateFormat = document.getElementById("customField["+i+"].fieldValueFormat");	  
					  	 	var customFieldDateYY = document.getElementById("customField["+i+"].fieldValueYY");	  
							var dateValue = customFieldDate.value;
							if(!(validateMyForm(customFieldDate,customFieldDateFormat,customFieldDateYY)))
								return false;
						}
					}
		 		}
		 	 }
	  }
  function chkForDateTrainedDate(){
	 var trainedDate = document.getElementById("trainedDate");	  
  	 if (trainedDate!=undefined && trainedDate!=null){
  	 	var trainedDateFormat = document.getElementById("trainedDateFormat");	  
  	 	var trainedDateYY = document.getElementById("trainedDateYY");	  
	    return (validateMyForm(trainedDate,trainedDateFormat,trainedDateYY))
 	 }
 	 return true;
  }
</script>
		<SCRIPT SRC="pages/framework/js/date.js"></SCRIPT>
		<SCRIPT SRC="pages/application/group/js/groupcommon.js"></SCRIPT>
		<html-el:form action="GroupAction.do?method=preview" onsubmit="return chkForValidDates()">

			<table width="95%" border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td class="bluetablehead05">
						<span class="fontnormal8pt"> <a
							href="CustomerSearchAction.do?method=getOfficeHomePage&officeId=<c:out value="${sessionScope.linkValues.customerOfficeId}"/>&officeName=<c:out value="${sessionScope.linkValues.customerOfficeName}"/>&loanOfficerId=<c:out value="${requestScope.Context.userContext.id}"/>">
								<c:out value="${sessionScope.linkValues.customerOfficeName}" /> </a> / <c:if test="${!empty sessionScope.linkValues.customerParentName}">
								<a href="centerAction.do?method=get&globalCustNum=<c:out value="${sessionScope.linkValues.customerParentGCNum}"/>"> <c:out value="${sessionScope.linkValues.customerParentName}" /> </a> /  
		    </c:if> <a href="GroupAction.do?method=get&globalCustNum=<c:out value="${sessionScope.linkValues.globalCustNum}"/>"> <c:out value="${sessionScope.linkValues.customerName}" /> </a> </span>
					</td>
				</tr>
			</table>
			<table width="95%" border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td align="left" valign="top" class="paddingL15T15">
						<table width="95%" border="0" cellpadding="3" cellspacing="0">
							<tr>
								<td class="headingorange">
									<span class="heading"> <c:out value="${requestScope.GroupVO.displayName}" /> - </span>
									<mifos:mifoslabel name="Group.edit" bundle="GroupUIResources"></mifos:mifoslabel>
									<mifos:mifoslabel name="${ConfigurationConstants.GROUP}"></mifos:mifoslabel>
									<mifos:mifoslabel name="Group.groupinformation" bundle="GroupUIResources"></mifos:mifoslabel>

								</td>
							</tr>
							<tr>
								<td class="fontnormal">
									<mifos:mifoslabel name="Group.editMsg1" bundle="GroupUIResources"></mifos:mifoslabel>
									<mifos:mifoslabel name="Group.createpagehead2" bundle="GroupUIResources"></mifos:mifoslabel>
									<mifos:mifoslabel name="Group.editMag2" bundle="GroupUIResources"></mifos:mifoslabel>

									<span class="mandatorytext"><font color="#FF0000"><br> *</font></span>
									<mifos:mifoslabel name="Group.createpagehead4" bundle="GroupUIResources"></mifos:mifoslabel>
								</td>
							</tr>
							<tr>
								<td>
									<font class="fontnormalRedBold"><html-el:errors bundle="GroupUIResources" /></font>
								</td>
							</tr>
						</table>
						<br>
						<table width="95%" border="0" cellpadding="3" cellspacing="0">
							<tr>
								<td colspan="2" class="fontnormalbold">
									<mifos:mifoslabel name="${ConfigurationConstants.GROUP}">
									</mifos:mifoslabel>
									<mifos:mifoslabel name="Group.details" bundle="GroupUIResources"></mifos:mifoslabel>
									<br>
									<br>
								</td>
							</tr>
							<tr class="fontnormal">
								<td width="25%" align="right">
									<mifos:mifoslabel name="Group.groupname" mandatory="yes" bundle="GroupUIResources"></mifos:mifoslabel>
								</td>
								<td width="75%">

									<mifos:mifosalphanumtext property="displayName" value="${requestScope.GroupVO.displayName}" />
								</td>
							</tr>
							<c:if test="${sessionScope.CenterHierarchyExist eq 'No'}">
								<tr class="fontnormal">
									<td align="right" class="fontnormal">
										<mifos:mifoslabel name="Group.loanofficer" bundle="GroupUIResources"></mifos:mifoslabel>
									</td>
									<td>
										<mifos:select name="groupActionForm" property="loanOfficerId" size="1" value="${requestScope.GroupVO.personnel.personnelId}">
											<html-el:options collection="loanOfficers" property="personnelId" labelProperty="displayName" />
										</mifos:select>
									</td>
								</tr>
							</c:if>
							<tr class="fontnormal">
								<td align="right" class="fontnormal">
									<mifos:mifoslabel keyhm="Group.ExternalId" isColonRequired="Yes" name="${ConfigurationConstants.EXTERNALID}" />
								</td>
								<td>
									<table width="95%" border="0" cellspacing="0" cellpadding="0">
										<tr>
											<td width="28%">
												<mifos:mifosalphanumtext property="externalId" keyhm="Group.ExternalId" value="${requestScope.GroupVO.externalId}" />
											</td>
											<td width="72%" class="fontnormal8pt">
												<mifos:mifoslabel keyhm="Group.ExternalId" name="Center.ExternalIdInfo" />
											</td>
										</tr>
									</table>
								</td>
							</tr>
							<tr class="fontnormal">
								<td width="17%" height="23" align="right" class="fontnormal">
									<mifos:mifoslabel name="Group.FormedBy" bundle="GroupUIResources"></mifos:mifoslabel>
								</td>
								<td width="83%">
									<c:out value="${requestScope.GroupVO.customerFormedByPersonnel.displayName}" />
									<html-el:hidden property="customerFormedById" value="${requestScope.GroupVO.customerFormedByPersonnel.personnelId}" />
								</td>
							</tr>
							<%-- collection sheet
              <tr class="fontnormal">
                <td align="right" class="fontnormal">
	                <mifos:mifoslabel name="Group.collectionsheettype"  bundle="GroupUIResources"></mifos:mifoslabel>
                </td>
                <td> collection sheet type 1
                	<c:out value="${requestScope.GroupVO.collectionSheet.collectionSheetName}"/>
                </td>
              </tr>
              --%>
						</table>
						<br>
						<table width="95%" border="0" cellpadding="3" cellspacing="0">
							<tr>
								<td colspan="2" class="fontnormalbold">
									<mifos:mifoslabel name="Group.trainingstatus" bundle="GroupUIResources"></mifos:mifoslabel>
									<br>
									<br>
								</td>
							</tr>

							<c:choose>
								<c:when test="${sessionScope.isTrained == 1}">
									<tr class="fontnormal">
										<td width="25%" align="right" class="fontnormal">
											<mifos:mifoslabel keyhm="Group.Trained" name="Group.grouptrained" bundle="GroupUIResources"></mifos:mifoslabel>
										</td>
										<td width="75%">
											<mifos:checkbox keyhm="Group.Trained" name="GroupVO" property="trained" value="1" disabled="true" />
											<html-el:hidden property="trained" value="1"/>
										</td>
									</tr>
									<tr class="fontnormal">
										<td align="right" class="fontnormal">
											<mifos:mifoslabel name="Group.grouptrainedon" bundle="GroupUIResources"></mifos:mifoslabel>
										</td>
										<td>
											<c:out value="${userdatefn:getUserLocaleDate(sessionScope.UserContext.pereferedLocale,sessionScope.trainedDate)}" />
											<%--<c:out value="${requestScope.GroupVO.trainedDate}"/>--%>
										</td>
									</tr>
								</c:when>
								<c:otherwise>
									<tr class="fontnormal">
										<td width="25%" align="right" class="fontnormal">
											<mifos:mifoslabel keyhm="Group.Trained" name="Group.grouptrained" bundle="GroupUIResources"></mifos:mifoslabel>
										</td>
										<td width="75%">
											<mifos:checkbox keyhm="Group.Trained" property="trained" value="1" />
										</td>
									</tr>
									<tr class="fontnormal">
										<td align="right" class="fontnormal">
											<mifos:mifoslabel keyhm="Group.TrainedDate" name="Group.grouptrainedon" bundle="GroupUIResources"></mifos:mifoslabel>
										</td>
										<td>
											<date:datetag keyhm="Group.TrainedDate" property="trainedDate" />
										</td>
									</tr>
								</c:otherwise>
							</c:choose>
						</table>
						<br>
						<%-- programs
            <table width="95%" border="0" cellpadding="3" cellspacing="0">
              <tr>
                <td colspan="2" class="fontnormalbold">
                	<mifos:mifoslabel name="Group.programs" bundle="GroupUIResources"></mifos:mifoslabel><br>
                    <br>
                </td>
              </tr>
              <tr class="fontnormal">
                <td width="25%" align="right" valign="top" class="fontnormal">Programs:</td>
                <td width="75%">
                <table width="80%" border="0" cellspacing="0" cellpadding="0">
                  <tr>
                    <td class="fontnormal">
                    <mifos:mifoslabel name="Group.programMsg" bundle="GroupUIResources"></mifos:mifoslabel>
                    
                      </td>
                  </tr>
                  <tr>
                    <td><img src="pages/framework/images/trans.gif" width="10" height="10"></td>
                  </tr>
                </table>
                
                <table width="100%" border="0" cellspacing="0" cellpadding="0">
                    <tr>
                      <td>
                     <mifos:MifosSelect property="programs"  inputMap="programsMap"  outputMap="customerProgramsMap" multiple="true">
					  </mifos:MifosSelect>  

                      </td>
                    </tr>
                  </table>                
     			</td>
              </tr>
            </table>      
            --%>
						<br>
						<table width="95%" border="0" cellpadding="3" cellspacing="0">
							<tr>
								<td colspan="2" class="fontnormalbold">
									<mifos:mifoslabel name="Group.officialstatus" bundle="GroupUIResources"></mifos:mifoslabel>
									<br>
									<br>
								</td>
							</tr>
							<tr class="fontnormal">
								<td width="25%" align="right" valign="top" class="fontnormal" style="padding-top:8px;">
									<mifos:mifoslabel name="Group.assignclientstogrouptitles1" bundle="GroupUIResources"></mifos:mifoslabel>
									<mifos:mifoslabel name="${ConfigurationConstants.CLIENT}"></mifos:mifoslabel>
									<mifos:mifoslabel name="Group.assignclientstogrouptitles2" bundle="GroupUIResources"></mifos:mifoslabel>
									<mifos:mifoslabel name="${ConfigurationConstants.GROUP}"></mifos:mifoslabel>
									<mifos:mifoslabel name="Group.assignclientstogrouptitles3" bundle="GroupUIResources"></mifos:mifoslabel>

								</td>
								<td width="75%">
									<table width="80%" border="0" cellspacing="0" cellpadding="3">


										<%--<c:forEach var="pos" items="${requestScope.positions}" varStatus="loopStatus">
                <bean:define id="ctr">
                	<c:out value="${loopStatus.index}"/>
                </bean:define>
		          <tr class="fontnormal">
                    <td width="17%"><c:out value ="${pos.positionName}"/>:</td>
                    <td width="83%">
                    <c:set var="clientcol" scope="request" value="${requestScope.clients}"/> 
	                    <mifos:select name="groupActionForm" property='customerPosition[${ctr}].customerId' size="1" >
						  <html-el:options collection="clientcol" property="customerId" labelProperty="displayName"/>
				  	    </mifos:select>
				  	    <html-el:hidden property='customerPosition[${ctr}].positionId' value="${pos.positionId}"></html-el:hidden>
                    </td>
                  </tr>
               </c:forEach> --%>
										<c:forEach var="pos" items="${requestScope.customerPositions}" varStatus="loopStatus">
											<bean:define id="ctr">
												<c:out value="${loopStatus.index}" />
											</bean:define>
											<tr class="fontnormal">
												<td width="17%">
													<c:out value="${pos.positionName}" />
													:
												</td>
												<td width="83%">
													<c:set var="clientcol" scope="request" value="${requestScope.clients}" />
													<mifos:select name="groupActionForm" property='customerPosition[${ctr}].customerId' size="1" value="${pos.customerId}">
														<html-el:options collection="clientcol" property="customerId" labelProperty="displayName" />
													</mifos:select>
													<html-el:hidden property='customerPosition[${ctr}].positionId' value="${pos.positionId}"></html-el:hidden>
												</td>
											</tr>
										</c:forEach>
									</table>

								</td>
							</tr>
						</table>
						<br>
						<table width="95%" border="0" cellpadding="3" cellspacing="0">
							<tr>
								<td colspan="2" class="fontnormalbold">
									<mifos:mifoslabel name="Group.address" bundle="GroupUIResources"></mifos:mifoslabel>
									<br>
									<br>
								</td>
							</tr>

							<tr class="fontnormal">
								<td width="25%" align="right">
									<mifos:mifoslabel keyhm="Group.Address1" isColonRequired="Yes" name="${ConfigurationConstants.ADDRESS1}" />
								</td>
								<td width="75%">
									<mifos:mifosalphanumtext keyhm="Group.Address1" name="groupActionForm" property="customerAddressDetail.line1" value="${requestScope.GroupVO.customerAddressDetail.line1}" />
								</td>
							</tr>
							<tr class="fontnormal">
								<td align="right">
									<mifos:mifoslabel keyhm="Group.Address2" isColonRequired="Yes" name="${ConfigurationConstants.ADDRESS2}" />
								</td>
								<td>
									<mifos:mifosalphanumtext keyhm="Group.Address2" name="groupActionForm" property="customerAddressDetail.line2" value="${requestScope.GroupVO.customerAddressDetail.line2}" />
								</td>
							</tr>
							<tr class="fontnormal">
								<td align="right">
									<mifos:mifoslabel keyhm="Group.Address3" isColonRequired="Yes" name="${ConfigurationConstants.ADDRESS3}" />
								</td>
								<td>
									<mifos:mifosalphanumtext keyhm="Group.Address3" name="groupActionForm" property="customerAddressDetail.line3" value="${requestScope.GroupVO.customerAddressDetail.line3}" />
								</td>
							</tr>
							<tr class="fontnormal">
								<td align="right">
									<mifos:mifoslabel keyhm="Group.City" isColonRequired="Yes" name="${ConfigurationConstants.CITY}" />
								</td>
								<td>
									<mifos:mifosalphanumtext keyhm="Group.City" name="groupActionForm" property="customerAddressDetail.city" value="${requestScope.GroupVO.customerAddressDetail.city}" />
								</td>
							</tr>
							<tr class="fontnormal">
								<td align="right">
									<mifos:mifoslabel keyhm="Group.State" isColonRequired="Yes" name="${ConfigurationConstants.STATE}" />
								</td>
								<td>
									<mifos:mifosalphanumtext keyhm="Group.State" name="groupActionForm" property="customerAddressDetail.state" value="${requestScope.GroupVO.customerAddressDetail.state}" />
								</td>
							</tr>
							<tr class="fontnormal">
								<td align="right">
									<mifos:mifoslabel keyhm="Group.Country" name="Group.country" bundle="GroupUIResources" />
								</td>
								<td>
									<mifos:mifosalphanumtext keyhm="Group.Country" name="groupActionForm" property="customerAddressDetail.country" value="${requestScope.GroupVO.customerAddressDetail.country}" />
								</td>
							</tr>
							<tr class="fontnormal">
								<td align="right">
									<mifos:mifoslabel keyhm="Group.PostalCode" isColonRequired="Yes" name="${ConfigurationConstants.POSTAL_CODE}" />
								</td>
								<td>
									<mifos:mifosalphanumtext keyhm="Group.PostalCode" name="groupActionForm" property="customerAddressDetail.zip" value="${requestScope.GroupVO.customerAddressDetail.zip}" />
								</td>
							</tr>
							<tr class="fontnormal">
								<td align="right">
									<mifos:mifoslabel keyhm="Group.PhoneNumber" name="Group.telephone" bundle="GroupUIResources" />
								</td>
								<td>
									<mifos:mifosalphanumtext keyhm="Group.PhoneNumber" name="groupActionForm" property="customerAddressDetail.phoneNumber" value="${requestScope.GroupVO.customerAddressDetail.phoneNumber}" />
								</td>
							</tr>

						</table>


						<%--Custom Fields end  --%>
						<table width="95%" border="0" cellpadding="3" cellspacing="0">
							<tr>
								<td colspan="2" class="fontnormalbold">
									<mifos:mifoslabel name="Group.additionalinformation" bundle="GroupUIResources"></mifos:mifoslabel>
									<br>
									<br>
								</td>
							</tr>

							<!-- For each custom field definition in the list custom field entity is passed as key to mifos label -->
							<c:forEach var="cfdef" items="${requestScope.customFields}" varStatus="loopStatus">
								<bean:define id="ctr">
									<c:out value="${loopStatus.index}" />
								</bean:define>
								<c:forEach var="cf" items="${requestScope.GroupVO.customFieldSet}" varStatus="loopStatus">
									<c:if test="${cfdef.fieldId==cf.fieldId}">
										<tr class="fontnormal">
											<td width="25%" align="right">
												<mifos:mifoslabel name="${cfdef.lookUpEntity.entityType}" mandatory="${cfdef.mandatoryStringValue}" bundle="GroupUIResources"></mifos:mifoslabel>
												:
											</td>
											<td width="75%">
												<c:if test="${cfdef.fieldType == 1}">
													<mifos:mifosnumbertext name="groupActionForm" property='customField[${ctr}].fieldValue' value="${cf.fieldValue}" maxlength="200" />
												</c:if>
												<c:if test="${cfdef.fieldType == 2}">
													<mifos:mifosalphanumtext name="groupActionForm" property='customField[${ctr}].fieldValue' value="${cf.fieldValue}" maxlength="200" />
												</c:if>
												<c:if test="${cfdef.fieldType == 3}">
													<date:datetag property="customField[${ctr}].fieldValue" name="GroupVO" />
												</c:if>
												<html-el:hidden property='customField[${ctr}].fieldId' value="${cfdef.fieldId}"></html-el:hidden>
												<html-el:hidden property='fieldTypeList' value='${cfdef.fieldType}' />
											</td>
										</tr>
									</c:if>
								</c:forEach>
							</c:forEach>

						</table>
						<br>
						<%--Custom Fields end  --%>
						<table width="95%" border="0" cellpadding="0" cellspacing="0">
							<tr>
								<td align="center" class="blueline">
									&nbsp;
								</td>
							</tr>
						</table>
						<br>
						<table width="95%" border="0" cellpadding="0" cellspacing="0">
							<tr>
								<td align="center">
									<html-el:submit value="Preview" styleClass="buttn" style="width:70px;">
										<mifos:mifoslabel name="button.preview" bundle="GroupUIResources"></mifos:mifoslabel>
									</html-el:submit>
									&nbsp; &nbsp;
									<html-el:button property="cancelBtn" styleClass="cancelbuttn" style="width:70px" onclick="goToCancelPage(this.form)">
										<mifos:mifoslabel name="button.cancel" bundle="GroupUIResources"></mifos:mifoslabel>
									</html-el:button>
								</td>
							</tr>
						</table>
						<br>
					</td>
				</tr>
			</table>

			<html-el:hidden property="input" value="ManageGroup" />
			<html-el:hidden property="customerAddressDetail.customerAddressId" value="${requestScope.GroupVO.customerAddressDetail.customerAddressId}" />
			<html-el:hidden property="globalCustNum" value="${requestScope.GroupVO.globalCustNum}" />
			<html-el:hidden property="versionNo" value="${requestScope.GroupVO.versionNo}" />
			<html-el:hidden property="statusId" value="${requestScope.GroupVO.statusId}" />
			<html-el:hidden property="office.officeId" value="${requestScope.GroupVO.office.officeId}" />
			<html-el:hidden property="office.officeName" value="${requestScope.GroupVO.office.officeName}" />
			<html-el:hidden property="office.globalOfficeNum" value="${requestScope.GroupVO.office.globalOfficeNum}" />
			<html-el:hidden property="office.versionNo" value="${requestScope.GroupVO.office.versionNo}" />
		</html-el:form>
	</tiles:put>
</tiles:insert>

