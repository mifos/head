<!--
/**

* editCenterDetails    version: 1.0



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

*/
 -->
<%@taglib uri="/tags/mifos-html" prefix="mifos"%>
<%@taglib uri="/tags/date" prefix="date"%>
<%@taglib uri="http://struts.apache.org/tags-html-el" prefix="html-el"%>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@taglib uri="http://struts.apache.org/tags-bean-el" prefix="bean-el"%>
<%@ taglib uri="/mifos/custom-tags" prefix="customtags"%>
<%@ taglib uri="/sessionaccess" prefix="session"%>

<tiles:insert definition=".detailsCustomer">
	<tiles:put name="body" type="string">
		<script language="javascript" SRC="pages/framework/js/date.js"></script>
		<script>
	function meetingpopup(){
		window.open("schedulemeetingpopup.htm",null,"height=400,width=800,status=yes,scrollbars=yes,toolbar=no,menubar=no,location=no");
	}
	function goToCancelPage(){
	centerCustActionForm.action="centerCustAction.do?method=cancel";
	centerCustActionForm.submit();
  }
  function chkForValidDates(){

	  		var mfiJoiningDate = document.getElementById("mfiJoiningDate");
	  	 	var mfiJoiningDateFormat = document.getElementById("mfiJoiningDateFormat");
	  		var mfiJoiningDateYY = document.getElementById("mfiJoiningDateYY");
			if(! (validateMyForm(mfiJoiningDate,mfiJoiningDateFormat,mfiJoiningDateYY)))
				return false;

		<%--if (centerCustActionForm.fieldTypeList.length!= undefined && centerCustActionForm.fieldTypeList.length!= null){
				for(var i=0; i <=centerCustActionForm.fieldTypeList.length;i++){
					if (centerCustActionForm.fieldTypeList[i]!= undefined){
						if(centerCustActionForm.fieldTypeList[i].value == "3"){
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
		 	for(var i=0; i <=centerCustActionForm.customFields.length;i++){
		 		if(centerCustActionForm.customField[i].fieldType == 3){
			 		var customFieldDate = document.getElementById("customField["+i+"].fieldValue");
					var customFieldDateFormat = document.getElementById("customField["+i+"].fieldValueFormat");
			  	 	var customFieldDateYY = document.getElementById("customField["+i+"].fieldValueYY");
					var dateValue = customFieldDate.value;
					if(!(validateMyForm(customFieldDate,customFieldDateFormat,customFieldDateYY)))
						return false;
		 		}
		 	} --%>

	  }
	</script>

		<html-el:form action="centerCustAction.do?method=editPreview" onsubmit="return chkForValidDates()">
		<c:set
		value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'BusinessKey')}"
		var="BusinessKey" />
		<html-el:hidden property="input" value="manage" />
				<table width="95%" border="0" cellpadding="0" cellspacing="0">
					<tr>
						<td class="bluetablehead05">
							<span class="fontnormal8pt">
							<customtags:headerLink/>
						</span>

						</td>
					</tr>
				</table>
				<table width="95%" border="0" cellpadding="0" cellspacing="0">
					<tr>
						<td align="left" valign="top" class="paddingL15T15">
							<table width="93%" border="0" cellpadding="3" cellspacing="0">
								<tr>
									<td class="headingorange">
										<span class="heading"><c:out value="${BusinessKey.displayName}" /> - </span>
										<mifos:mifoslabel name="Center.Edit" bundle="CenterUIResources" />
										<mifos:mifoslabel name="${ConfigurationConstants.CENTER}" />
										<mifos:mifoslabel name="Center.Information" bundle="CenterUIResources" />
									</td>
								</tr>
								<tr>
									<td class="fontnormal">
										<mifos:mifoslabel name="Center.CreatePageInstruction" bundle="CenterUIResources"></mifos:mifoslabel>
										<mifos:mifoslabel name="Center.EditPageCancelInstruction1" bundle="CenterUIResources" />
										<mifos:mifoslabel name="${ConfigurationConstants.CENTER}" />
										<mifos:mifoslabel name="Center.EditPageCancelInstruction2" bundle="CenterUIResources" />
										<span class="mandatorytext"><font color="#FF0000">*</font></span>
										<mifos:mifoslabel name="Center.FieldInstruction" bundle="CenterUIResources"></mifos:mifoslabel>
									</td>
								</tr>
							</table>
							<br>






							<table width="93%" border="0" cellpadding="3" cellspacing="0">
								<font class="fontnormalRedBold"><html-el:errors bundle="CenterUIResources" /> </font>
								<tr>
									<td colspan="2" class="fontnormalbold">
										<mifos:mifoslabel name="${ConfigurationConstants.CENTER}" />
										<c:out value=" " />
										<mifos:mifoslabel name="Center.details" bundle="CenterUIResources" />
										<br>
										<br>
									</td>
								</tr>

								<tr class="fontnormal">
									<td width="26%" align="right">
										<mifos:mifoslabel name="Center.Name" bundle="CenterUIResources"></mifos:mifoslabel>
									</td>
									<td width="74%">
										<c:out value="${BusinessKey.displayName}" />
									</td>
								</tr>

								<tr class="fontnormal">
									<td align="right">
										<mifos:mifoslabel name="Center.LoanOfficer" mandatory="yes" bundle="CenterUIResources"></mifos:mifoslabel>
									</td>
									<td>
										<mifos:select property="loanOfficerId" size="1">
											<c:forEach var="loanOfficer" items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'loanOfficers')}">
												<html-el:option value="${loanOfficer.personnelId}">
													<c:out value="${loanOfficer.displayName}" />
												</html-el:option>
											</c:forEach>
										</mifos:select>
									</td>
								</tr>


								<tr class="fontnormal" id="Center.ExternalId">
									<td align="right" class="fontnormal">
										<mifos:mifoslabel keyhm="Center.ExternalId" isColonRequired="Yes" name="${ConfigurationConstants.EXTERNALID}" />
									</td>
									<td>
										<table width="95%" border="0" cellspacing="0" cellpadding="0">
											<tr>
												<td width="29%">
													<mifos:mifosalphanumtext keyhm="Center.ExternalId" property="externalId" maxlength="50"/>
												</td>
												<td width="71%" class="fontnormal8pt">
													<mifos:mifoslabel keyhm="Center.ExternalId" name="Center.ExternalIdInfo" bundle="CenterUIResources"></mifos:mifoslabel>
												</td>
											</tr>
										</table>
									</td>
								</tr>


								<!-- MFI Joining Date -->
								<tr class="fontnormal">
									<td align="right">
										<mifos:mifoslabel name="Center.MfiJoiningDate" bundle="CenterUIResources" mandatory="yes" isColonRequired="yes"></mifos:mifoslabel>
									</td>
									<td>
										<date:datetag property="mfiJoiningDate" renderstyle="simple"/>
									</td>
								</tr>
								<tr class="fontnormal">
									<td align="right" valign="top" class="fontnormal" style="padding-top:8px;">
										<mifos:mifoslabel name="Center.OfficialTitlesHeading" bundle="CenterUIResources"></mifos:mifoslabel>
										:
									</td>
									<td>
										<table width="80%" border="0" cellspacing="0" cellpadding="3">
											<c:forEach var="pos" items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'positions')}" varStatus="loopStatus">
												<bean:define id="ctr">
													<c:out value="${loopStatus.index}" />
												</bean:define>
												<tr class="fontnormal">
													<td width="17%">
														<c:out value="${pos.name}" />
														:
													</td>
													<td width="83%">
														<mifos:select name="centerCustActionForm" property='customerPosition[${ctr}].customerId' size="1">
															<c:forEach var="clientColList" items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'clients')}" >
																<html-el:option value="${clientColList.customerId}">${clientColList.displayName}</html-el:option>
															</c:forEach>
														</mifos:select>
														<html-el:hidden property='customerPosition[${ctr}].positionId' value="${pos.id}"></html-el:hidden>
													</td>
												</tr>
											</c:forEach>
										</table>
									</td>
								</tr>
							</table>








							<br>
							<!-- Address Fields -->
							<table width="93%" border="0" cellpadding="3" cellspacing="0">
								<tr>
									<td colspan="2" class="fontnormalbold">
										<mifos:mifoslabel name="Center.AddressHeading" bundle="CenterUIResources"></mifos:mifoslabel>
										<br>
										<br>
									</td>
								</tr>
								<tr class="fontnormal">
									<td width="26%" align="right">
										<mifos:mifoslabel keyhm="Center.Address1" isColonRequired="Yes" name="${ConfigurationConstants.ADDRESS1}" />
									</td>
									<td width="74%">
										<mifos:mifosalphanumtext keyhm="Center.Address1" name="centerCustActionForm" property="address.line1"  maxlength="200" />
									</td>
								</tr>
								<tr class="fontnormal">
									<td align="right">
										<mifos:mifoslabel keyhm="Center.Address2" isColonRequired="Yes" name="${ConfigurationConstants.ADDRESS2}" />
									</td>
									<td>
										<mifos:mifosalphanumtext keyhm="Center.Address2" name="centerCustActionForm" property="address.line2"  maxlength="200" />
									</td>
								</tr>
								<tr class="fontnormal">
									<td align="right">
										<mifos:mifoslabel keyhm="Center.Address3" name="${ConfigurationConstants.ADDRESS3}" />
									</td>
									<td>
										<mifos:mifosalphanumtext keyhm="Center.Address3" name="centerCustActionForm" property="address.line3" maxlength="200" />
									</td>
								</tr>
								<tr class="fontnormal">
									<td align="right">
										<mifos:mifoslabel keyhm="Center.City" isColonRequired="Yes" name="${ConfigurationConstants.CITY}" />
									</td>
									<td>
										<mifos:mifosalphanumtext keyhm="Center.City" name="centerCustActionForm" property="address.city"  maxlength="100" />
									</td>
								</tr>
								<tr class="fontnormal">
									<td align="right">
										<mifos:mifoslabel keyhm="Center.State" isColonRequired="Yes" name="${ConfigurationConstants.STATE}" />
									</td>
									<td>
										<mifos:mifosalphanumtext keyhm="Center.State" name="centerCustActionForm" property="address.state"  maxlength="100" />
									</td>
								</tr>
								<tr class="fontnormal">
									<td align="right">
										<mifos:mifoslabel keyhm="Center.Country" name="Center.Country" bundle="CenterUIResources"></mifos:mifoslabel>
									</td>
									<td>
										<mifos:mifosalphanumtext keyhm="Center.Country" name="centerCustActionForm" property="address.country"  maxlength="100" />
									</td>
								</tr>
								<tr class="fontnormal">
									<td align="right">
										<mifos:mifoslabel keyhm="Center.PostalCode" name="${ConfigurationConstants.POSTAL_CODE}" />
									</td>
									<td>
										<mifos:mifosalphanumtext keyhm="Center.PostalCode" name="centerCustActionForm" property="address.zip"  maxlength="20" />
									</td>
								</tr>
								<tr class="fontnormal">
									<td align="right">
										<mifos:mifoslabel keyhm="Center.PhoneNumber" name="Center.Telephone" bundle="CenterUIResources" />
									</td>
									<td>
										<mifos:mifosalphanumtext keyhm="Center.PhoneNumber" name="centerCustActionForm" property="address.phoneNumber"  maxlength="20" />
									</td>
								</tr>
							</table>
							<br>
							<!-- Custom Fields -->
							<c:if test="${!empty session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'customFields')}">
							<table width="93%" border="0" cellpadding="3" cellspacing="0">
								<tr>
									<td colspan="2" class="fontnormalbold">
										<mifos:mifoslabel name="Center.AdditionalInformationHeading" bundle="CenterUIResources"></mifos:mifoslabel>
										<br>
										<br>
									</td>
								</tr>

								<!-- For each custom field definition in the list custom field entity is passed as key to mifos label -->
								<c:forEach var="customFieldDef" items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'customFields')}" varStatus="loopStatus">
									<bean:define id="ctr">
										<c:out value="${loopStatus.index}" />
									</bean:define>
									<c:forEach var="cf" items="${sessionScope.centerCustActionForm.customFields}">
										<c:if test="${customFieldDef.fieldId==cf.fieldId}">
											<tr class="fontnormal">
												<td width="26%" align="right">
													<mifos:mifoslabel name="${customFieldDef.lookUpEntity.entityType}" mandatory="${customFieldDef.mandatoryStringValue}" bundle="CenterUIResources"></mifos:mifoslabel>
													:
												</td>
												<td width="74%">
													<html-el:hidden property='customField[${ctr}].fieldId' value="${cf.fieldId}"></html-el:hidden>
													<html-el:hidden property='fieldTypeList' value='${customFieldDef.fieldType}' />
													<html-el:hidden property='customField[${ctr}].fieldType' value='${customFieldDef.fieldType}' />
													<%--<html-el:hidden property='customField[${ctr}].locale' value="${sessionScope.UserContext.mfiLocale}"></html-el:hidden>--%>
													<c:if test="${customFieldDef.fieldType == CustomFieldType.NUMERIC.value}">
														<mifos:mifosnumbertext name="centerCustActionForm" property='customField[${ctr}].fieldValue' value="${cf.fieldValue}" maxlength="200" />
													</c:if>
													<c:if test="${customFieldDef.fieldType == CustomFieldType.ALPHA_NUMERIC.value}">
														<mifos:mifosalphanumtext name="centerCustActionForm" property='customField[${ctr}].fieldValue' value="${cf.fieldValue}" maxlength="200" />
													</c:if>
													<c:if test="${customFieldDef.fieldType == CustomFieldType.DATE.value}">
														<date:datetag property="customField[${ctr}].fieldValue" />
													</c:if>
												<td>
											</tr>
										</c:if>
									</c:forEach>
								</c:forEach>
							</table>
							</c:if>
							<!--Custom Fields end  -->
							<table width="93%" border="0" cellpadding="0" cellspacing="0">
								<tr>
									<td align="center" class="blueline">
										&nbsp;
									</td>
								</tr>
							</table>
							<br>
							<table width="93%" border="0" cellpadding="0" cellspacing="0">
								<tr>
									<td align="center">
										<html-el:submit styleClass="buttn" style="width:70px;">
											<mifos:mifoslabel name="button.preview" bundle="CenterUIResources"></mifos:mifoslabel>
										</html-el:submit>
										&nbsp; &nbsp;
										<html-el:button onclick="goToCancelPage();" property="cancelButton" styleClass="cancelbuttn" style="width:70px">
											<mifos:mifoslabel name="button.cancel" bundle="CenterUIResources"></mifos:mifoslabel>
										</html-el:button>

									</td>
								</tr>
							</table>
						</td>
					</tr>
				</table>
				<br>
			<%--</td>
			</tr>
			</table>--%>
		<html-el:hidden property="currentFlowKey" value="${requestScope.currentFlowKey}" />
		</html-el:form>
	</tiles:put>
</tiles:insert>
