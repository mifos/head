<%-- 
Copyright (c) 2005-2008 Grameen Foundation USA
All rights reserved.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
implied. See the License for the specific language governing
permissions and limitations under the License.

See also http://www.apache.org/licenses/LICENSE-2.0.html for an
explanation of the license and how it is applied.
--%>
<!-- editLoanAccount.jsp -->

<%@taglib uri="/tags/struts-html" prefix="html"%>
<%@taglib uri="/tags/struts-bean" prefix="bean"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@taglib uri="/tags/mifos-html" prefix="mifos"%>
<%@taglib uri="/tags/struts-html-el" prefix="html-el"%>
<%@ taglib uri="/tags/struts-tiles" prefix="tiles"%>
<%@ taglib uri="/mifos/customtags" prefix="mifoscustom"%>
<%@ taglib uri="/loan/loanfunctions" prefix="loanfn"%>
<%@taglib uri="/tags/date" prefix="date"%>
<%@ taglib uri="/mifos/custom-tags" prefix="customtags"%>
<%@ taglib uri="/sessionaccess" prefix="session"%>
<tiles:insert definition=".clientsacclayoutsearchmenu">
	<tiles:put name="body" type="string">
		<SCRIPT SRC="pages/application/loan/js/EditLoanAccount.js"></SCRIPT>
		<SCRIPT SRC="pages/application/loan/js/CreateLoanAccountPreview.js"></SCRIPT>
		<SCRIPT SRC="pages/framework/js/CommonUtilities.js"></SCRIPT>
		<script src="pages/framework/js/conversion.js"></script>
		<script src="pages/framework/js/con_en.js"></script>
		<script src="pages/framework/js/con_${sessionScope["UserContext"].currentLocale}.js"></script>
		
		<SCRIPT SRC="pages/framework/js/date.js"></SCRIPT>

	    <script type="text/javascript">
					    function addEvent(obj, evType, fn){ 
								 if (obj.addEventListener){ 
								 		obj.addEventListener(evType, fn, false); 
								 		return true; 
								 } else if (obj.attachEvent){ 
								 		var r = obj.attachEvent("on"+evType, fn); 
								 		return r; 
								 } else { 
								 		return false; 
								 } 
						}
	    </script>

				<script>
			function selectAll(x) {
				for(var i=0,l=x.form.length; i<l; i++)
				{
					if(x.form[i].type == 'checkbox' && x.form[i].name != 'selectAll1'){
						x.form[i].checked=x.checked
					}
				}
			}
	
			function selectAllCheck(x){
				var checked = true;
				for(var i=0,l=x.form.length; i<l; i++){
					if(x.form[i].type == 'checkbox' && x.form[i].name != 'selectAll1'){
						if(x.form[i].checked == false){
							checked = false;
						}
					}
				}
				for(var i=0,l=x.form.length; i<l; i++){
					if(x.form[i].type == 'checkbox' && x.form[i].name == 'selectAll1'){
						x.form[i].checked = checked;
					}
				}
			}
			function fun_saveForLater(form) {
				form.method.value="create";
				form.stateSelected.value="1";
				form.action="multipleloansaction.do";
				form.submit();
				func_disableSubmitBtn("saveForLaterButton");
			}
			function fun_submitForApproval(form) {
				form.method.value="create";
				form.stateSelected.value="2";
				form.action="multipleloansaction.do";
				form.submit();
				func_disableSubmitBtn("submitForApprovalButton");
				
			}
			function fun_approved(form) {
				form.method.value="create";
				form.stateSelected.value="3";
				form.action="multipleloansaction.do";
				form.submit();
				func_disableSubmitBtn("approvedButton");
				
			}
            function fun_cancel(form) {
		    	form.action="loanAccountAction.do?method=cancel";
				form.submit();
            }
            
 		</script>

        <fmt:setLocale value='${sessionScope["LOCALE"]}'/>
		<fmt:setBundle basename="org.mifos.config.localizedResources.LoanUIResources"/>
		
		<html-el:form action="/loanAccountAction.do?method=managePreview"
			onsubmit="return (validateMyForm(disbursementDate,disbursementDateFormat,disbursementDateYY));">
			<c:set value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'BusinessKey')}" var="BusinessKey" />
			<c:set value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'loanIndividualMonitoringIsEnabled')}"	var="loanIndividualMonitoringIsEnabled" />
			<c:set value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'loanaccountownerisagroup')}" var="loanaccountownerisagroup" />
			<c:set value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'repaymentSchedulesIndependentOfMeetingIsEnabled')}" var="repaymentSchedulesIndependentOfMeetingIsEnabled" />
			<c:set value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'recurrenceId')}" var="recurrenceId" />
			<c:set value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'recurrenceName')}" var="recurrenceName" />
			<c:set value="${requestScope['accountState']}" var="accountState" />
			<c:set value="${requestScope['frequency']}" var="frequency" />

			<html-el:hidden property="currentFlowKey" value="${requestScope.currentFlowKey}" />

			<td height="200" align="left" valign="top" bgcolor="#FFFFFF"
				class="paddingleftmain">
			<table width="95%" border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td class="bluetablehead05"><span class="fontnormal8pt"> <customtags:headerLink />
					</span></td>
				</tr>
			</table>
			<table width="95%" border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td align="left" valign="top" class="paddingL15T15">
					<table width="95%" border="0" cellpadding="0" cellspacing="0">
						<tr>
							<td width="83%" class="headingorange"><span class="heading"> <c:out
								value="${BusinessKey.loanOffering.prdOfferingName}" />&nbsp;#
							<c:out value="${BusinessKey.globalAccountNum}" />-&nbsp;
							</span> <mifos:mifoslabel name="loan.edit_loan_acc" /></td>
						</tr>
						<tr>
							<td class="fontnormal"><mifos:mifoslabel name="loan.edit_info" />
							<br>
							<font color="#FF0000">*</font> <mifos:mifoslabel
								name="loan.asterisk" /></td>
						</tr>
					</table>
					<br>
					<font class="fontnormalRedBold"> <html-el:errors
						bundle="loanUIResources" /> </font>
					<table width="95%" border="0" cellpadding="3" cellspacing="0">
						<tr>
							<td colspan="2" class="fontnormalbold">
								<fmt:message key="loan.loanAccountDetails">
									<fmt:param><mifos:mifoslabel
										name="${ConfigurationConstants.LOAN}" /></fmt:param>
								</fmt:message><br>
							<br>
							</td>
						</tr>
							<c:if test="${loanIndividualMonitoringIsEnabled == '1'}">
								<c:if test="${loanaccountownerisagroup == 'yes'}">
									<tiles:insert definition=".individualLoansForm" flush="false">
									</tiles:insert>
							    </c:if>
							</c:if>
<br>
<br>					
						<table width="93%" border="0" cellpadding="3" cellspacing="0">
							<tr>
								<td colspan="2" class="fontnormalbold"><mifos:mifoslabel
										name="loan.acc_details" /> <br>
								<br>
								</td>
							</tr>
 							<c:if test="${loanaccountownerisagroup != 'yes'}">											
							<tr class="fontnormal">
									<td width="30%" align="right" class="fontnormal">
										<font color="#FF0000">*</font>
										<fmt:message key="loan.loanAmount">
											<fmt:param><mifos:mifoslabel name="${ConfigurationConstants.LOAN}" /></fmt:param>
										</fmt:message>:&nbsp;</td>
									<td valign="top"><mifos:mifosdecimalinput property="loanAmount"
										value="${sessionScope.loanAccountActionForm.loanAmount}" /> <mifos:mifoslabel
										name="loan.allowed_amount" />&nbsp; <c:out
										value="${sessionScope.loanAccountActionForm.minLoanAmount}" />
									&nbsp; - &nbsp; <c:out
										value="${sessionScope.loanAccountActionForm.maxLoanAmount}" />)
									</td>
								</tr>
								
							</c:if>	
												
						<tr class="fontnormal">
							<td width="30%" align="right" class="fontnormal">
								<font color="#FF0000">*</font>
								<fmt:message key="loan.interestRate">
									<fmt:param><mifos:mifoslabel name="${ConfigurationConstants.INTEREST}" /></fmt:param>
								</fmt:message>:&nbsp;</td>
							<td width="70%" valign="top"><mifos:mifosdecimalinput
								property="interestRate" readonly="${loanfn:isDisabledWhileEditingGlim('interestRate',accountState)}"
								value="${sessionScope.loanAccountActionForm.interestRate}"
								decimalFmt="10.5" /> 
								<fmt:message key="loan.allowedInterest">
									<fmt:param><mifos:mifoslabel name="${ConfigurationConstants.INTEREST}" /></fmt:param>
								</fmt:message>&nbsp; <c:out
								value="${BusinessKey.loanOffering.minInterestRate}" />&nbsp;
							- &nbsp; <c:out
								value="${BusinessKey.loanOffering.maxInterestRate}" />
							%)</td>
						</tr>
						<tr class="fontnormal">
							<td align="right" class="fontnormal"><span class="mandatorytext"></span>
							<mifos:mifoslabel name="loan.no_of_inst" mandatory="yes" />:&nbsp;
							</td>
							<td valign="top"><mifos:mifosnumbertext
								name="loanAccountActionForm" property="noOfInstallments" 
								readonly="${loanfn:isDisabledWhileEditingGlim('noOfInstallments',accountState)}"
								value="${sessionScope.loanAccountActionForm.noOfInstallments}" />
							<mifos:mifoslabel name="loan.allowed_no_of_inst" />&nbsp; <c:out
								value="${sessionScope.loanAccountActionForm.minNoInstallments}" />&nbsp;
							- &nbsp; <c:out
								value="${sessionScope.loanAccountActionForm.maxNoInstallments}" />)
							</td>
						</tr>

						<tr class="fontnormal">
							<td align="right" class="fontnormal"><span class="mandatorytext"></span>
							<mifos:mifoslabel name="loan.proposed_date" mandatory="yes" />:&nbsp;
							</td>
							<c:set var="Yes" value="No"/>
							<c:if test="${loanfn:isDisabledWhileEditingGlim('disbursementDate',accountState)}">
							<c:set var="Yes"  value="Yes"/>
							</c:if>
							<td valign="top"><date:datetag isDisabled="${Yes}" property="disbursementDate" /></td>
						</tr>
						
						<!--  For Repayment day  -->
						<c:if
							test="${repaymentSchedulesIndependentOfMeetingIsEnabled == '1'}">
							<tr class="fontnormal">

								<td align="right" class="fontnormal"><span
									class="mandatorytext"></span> <mifos:mifoslabel
									name="loan.repayment_date" mandatory="yes" />:</td>
								<td valign="top">
								<c:if test="${recurrenceId == '2'}">
									<mifos:mifoslabel name="meeting.labelThe"
										bundle="MeetingResources" />
								<c:choose>
									 <c:when test="${loanfn:isDisabledWhileEditingGlim('ordinalOfMonth',accountState)}">									     
 									    <c:forEach var="weekRank"
												items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'WeekRankList')}">
 									         <c:if test="${weekRank.value == requestScope['ordinalOfMonth']}">
 									                   <c:out value="${weekRank.name}"/>
													<html-el:hidden value="${requestScope['ordinalOfMonth']}" property="monthRank" />
 									         </c:if>
 									     </c:forEach>
					       			</c:when>
									<c:otherwise>
										<mifos:select property="monthRank">
											<c:forEach var="weekRank"
												items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'WeekRankList')}">
												<html-el:option value="${weekRank.id}">${weekRank.name}</html-el:option>
											</c:forEach>
										</mifos:select>
									</c:otherwise>
								  </c:choose>	
								</c:if>
								
								<c:choose>
									 <c:when test="${loanfn:isDisabledWhileEditingGlim('weekDayId',accountState)}">									     
 									     <c:forEach var="weekDay"
										items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'WeekDayList')}">
 									         <c:if test="${weekDay.value == requestScope['weekDayId']}">
 									                   <c:out value="${weekDay.name}"/>
														<html-el:hidden
															value="${requestScope['weekDayId']}"
															property="monthWeek" />
 									         </c:if>
 									     </c:forEach>
					       			</c:when>
									<c:otherwise>
										<mifos:select property="monthWeek">
											<c:forEach var="weekDay"
												items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'WeekDayList')}">
												<html-el:option value="${weekDay.value}">${weekDay.name}</html-el:option>
											</c:forEach>
										</mifos:select> 
									</c:otherwise>
								  </c:choose>								
								<mifos:mifoslabel name="meeting.labelOfEvery" bundle="MeetingResources" />
								<mifos:mifosnumbertext property="recurMonth" size="3" maxlength="3" disabled="true" />
								<c:out value="${recurrenceName}" />
								</td>
							</tr>
						</c:if>
										
						<tr class="fontnormal">
							<td align="right" class="fontnormal"><span class="mandatorytext"></span>
							<mifos:mifoslabel name="loan.grace_period" />:&nbsp;</td>
							<td valign="top">
								<mifos:mifosnumbertext property="gracePeriod"  disabled="${loanfn:isDisabledWhileEditingGlim('gracePeriod',accountState)}" onchange="setGracePeriodDurationValue();"/></td>
						</tr>
						<html-el:hidden property="inheritedGracePeriodDuration"
								value="${sessionScope.loanAccountActionForm.gracePeriodDuration}" />
							<html-el:hidden property="gracePeriodDuration"/>
							<html-el:hidden property="gracePeriodTypeId" value="${BusinessKey.gracePeriodType.id}" />
						<script>//setIntrestAtDisb();</script>
						<c:if test="${loanaccountownerisagroup != 'yes'}">
							<tr class="fontnormal">
								<td align="right" class="fontnormal"><mifos:mifoslabel
									keyhm="Loan.PurposeOfLoan" name="Loan.PurposeOfLoan"
									bundle="loanUIResources" /><mifos:mifoslabel
									keyhm="Loan.PurposeOfLoan" name="${ConfigurationConstants.LOAN}"
									isColonRequired="yes" bundle="loanUIResources"
									isManadatoryIndicationNotRequired="yes" /></td>
								<td valign="top"><mifos:select keyhm="Loan.PurposeOfLoan"
									property="businessActivityId">
									<c:forEach var="BusinessActivity" items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'BusinessActivities')}" >
										<html-el:option value="${BusinessActivity.id}">${BusinessActivity.name}</html-el:option>
									</c:forEach>
								</mifos:select></td>
							</tr>
						</c:if>
						<tr class="fontnormal">
							<td align="right" class="fontnormal"><mifos:mifoslabel
								keyhm="Loan.CollateralType" name="Loan.CollateralType"
								isColonRequired="yes" bundle="loanUIResources" /></td>
							<td valign="top">
								<c:choose>
									 <c:when test="${loanfn:isDisabledWhileEditingGlim('collateralType',accountState)}">									     
 									     <c:forEach var="CollateralType" items="${requestScope['CollateralTypes']}" >
 									         <c:if test="${CollateralType.id  == requestScope['collateralTypeId']}">
 									                   <c:out value="${CollateralType.name}"/>
														<html-el:hidden
															value="${requestScope['collateralTypeId']}"
															property="collateralTypeId" />
 									         </c:if>
 									     </c:forEach>
					       			</c:when>
					       
									<c:otherwise>
										<mifos:select keyhm="Loan.CollateralType"
											property="collateralTypeId">
											<c:forEach var="CollateralType" items="${requestScope['CollateralTypes']}" >
												<html-el:option value="${CollateralType.id}">${CollateralType.name}</html-el:option>
											</c:forEach>
										</mifos:select>
									</c:otherwise>
								</c:choose>
							</td>
						</tr>
						<tr class="fontnormal">
							<td align="right" valign="top" class="fontnormal"><mifos:mifoslabel
								keyhm="Loan.CollateralNotes" name="Loan.CollateralNotes"
								isColonRequired="yes" bundle="loanUIResources" /></td>
							<td valign="top"><mifos:textarea keyhm="Loan.CollateralNotes" readonly="${loanfn:isDisabledWhileEditingGlim('collateralNotes',accountState)}"
								property="collateralNote" style="width:320px; height:110px;"></mifos:textarea></td>
						</tr>
					</table>
					<br>
					<c:if test="${!empty session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'customFields')}">
					<table width="93%" border="0" cellpadding="3" cellspacing="0">
						<tr>
						  <td colspan="2" class="fontnormalbold">
						     <mifos:mifoslabel name="loan.additionalInfo" bundle="loanUIResources"/></td>
						 </tr>
		                <c:forEach var="cf" items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'customFields')}" varStatus="loopStatus">
		              	 <bean:define id="ctr">
		                	<c:out value="${loopStatus.index}"/>
		                </bean:define>

						<tr class="fontnormal">
			                <td width="30%" align="right">
								<mifos:mifoslabel name="${cf.lookUpEntity.entityType}" mandatory="${cf.mandatoryStringValue}" bundle="loanUIResources"></mifos:mifoslabel>:
							</td>
			                <td width="70%">
								<c:if test="${cf.fieldType == MasterConstants.CUSTOMFIELD_NUMBER}">
				                	<mifos:mifosnumbertext  name = "loanAccountActionForm" property='customField[${ctr}].fieldValue' maxlength="200"  readonly="${loanfn:isDisabledWhileEditingGlim('customField',accountState)}"/>
				                </c:if>
				               	<c:if test="${cf.fieldType == MasterConstants.CUSTOMFIELD_ALPHANUMBER}">
				                	<mifos:mifosalphanumtext name = "loanAccountActionForm" property='customField[${ctr}].fieldValue' maxlength="200" readonly="${loanfn:isDisabledWhileEditingGlim('customField',accountState)}"/>
								</c:if>
				                <c:if test="${cf.fieldType == MasterConstants.CUSTOMFIELD_DATE}">
				                	<mifos:mifosalphanumtext name = "loanAccountActionForm" property='customField[${ctr}].fieldValue' maxlength="200" readonly="${loanfn:isDisabledWhileEditingGlim('customField',accountState)}"/>
				                </c:if>
				                <html-el:hidden property='customField[${ctr}].fieldId' value="${cf.fieldId}"></html-el:hidden>
				                <html-el:hidden property='customField[${ctr}].fieldType' value='${cf.fieldType}' />
		                	</td>
		           		</tr>
					     </c:forEach>
					</table>
					</c:if>
					<table width="95%" border="0" cellpadding="0" cellspacing="0">
						<tr>
							<td align="center" class="blueline">&nbsp;</td>
						</tr>
					</table>
					<br>
					<table width="95%" border="0" cellpadding="0" cellspacing="0">
						<tr>
							<td align="center"><html-el:submit property="editDetailsBtn"
								styleClass="buttn" >
								<mifos:mifoslabel name="loan.preview" />
							</html-el:submit> &nbsp; <html-el:button property="cancelButton"
								onclick="javascript:fun_cancel(this.form)"
								styleClass="cancelbuttn" >
								<mifos:mifoslabel name="loan.cancel" />
							</html-el:button></td>
						</tr>
					</table>
					<br>
					<br>
					</td>
				</tr>
			</table>
			</td>
			<html-el:hidden value="${BusinessKey.globalAccountNum}"
				property="globalAccountNum" />
			<html-el:hidden value="${BusinessKey.accountState.id}"
				property="accountStateId" />
			<html-el:hidden value="${recurrenceId}"
				property="recurrenceId" />			
		</html-el:form>
	</tiles:put>
</tiles:insert>
