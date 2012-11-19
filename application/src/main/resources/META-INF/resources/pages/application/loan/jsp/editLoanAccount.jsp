<%-- 
Copyright (c) 2005-2011 Grameen Foundation USA
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
		<span id="page.id" title="EditLoanAccount"></span>	
		<SCRIPT SRC="pages/application/loan/js/EditLoanAccount.js"></SCRIPT>
		<SCRIPT SRC="pages/application/loan/js/CreateLoanAccountPreview.js"></SCRIPT>
		<SCRIPT SRC="pages/framework/js/CommonUtilities.js"></SCRIPT>
		
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
						addEvent(window, "load", disableFields);
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
            function fun_cancel() {
            	goBackToLoanAccountDetails.submit();
            }

            function showMeetingFrequency(){
                if (document.loanAccountActionForm.frequency != undefined) {
                    if (document.loanAccountActionForm.frequency[1].checked == true) {
                        document.loanAccountActionForm.frequency[0].disabled=true;
                        document.getElementById("weekDIV").style.display = "none";
                        document.getElementById("monthDIV").style.display = "block";
                        document.getElementsByName("recurrenceId")[0].value = "2";
                        if(document.loanAccountActionForm.monthType[0].checked == false && document.loanAccountActionForm.monthType[1].checked == false)
                            document.getElementsByName("monthType")[0].checked=true;
                    } else {
                        document.loanAccountActionForm.frequency[0].checked=true;
                        document.loanAccountActionForm.frequency[1].disabled=true;
                        document.getElementById("weekDIV").style.display = "block";
                        document.getElementById("monthDIV").style.display = "none";
                        document.getElementsByName("recurrenceId")[0].value = "1";
                    }
                }
            }
 		</script>

        <fmt:setLocale value='${sessionScope["org.apache.struts.action.LOCALE"]}'/>
		<fmt:setBundle basename="org.mifos.config.localizedResources.LoanUIResources"/>
	    <c:set value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'BusinessKey')}" var="BusinessKey" scope="session" />    
		<form name="goBackToLoanAccountDetails" method="get" action ="viewLoanAccountDetails.ftl">
			<input type="hidden" name='globalAccountNum' value="${BusinessKey.globalAccountNum}"/>
		</form>   
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
					<font class="fontnormalRedBold"> <span id="editLoanAccount.error.message"><html-el:errors
						bundle="loanUIResources" /></span> </font>
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
										<span id="editLoanAccount.label.loanAmount">
											<mifos:mifoslabel name="loan.loanamount" />
										</span>:&nbsp;</td>
									<td valign="top"><html-el:text 
										styleId="editLoanAccount.input.loanAmount"
										property="loanAmount"
										value="${sessionScope.loanAccountActionForm.loanAmount}" /> <mifos:mifoslabel
										name="loan.allowed_amount" />&nbsp; <fmt:formatNumber
										value="${sessionScope.loanAccountActionForm.minLoanAmount}" />
									&nbsp; - &nbsp; <fmt:formatNumber
										value="${sessionScope.loanAccountActionForm.maxLoanAmount}" />)
									</td>
								</tr>
								
							</c:if>	
												
						<tr class="fontnormal">
							<td width="30%" align="right" class="fontnormal">
								<font color="#FF0000">*</font>
								<span id="editLoanAccount.label.interestRate">
								<fmt:message key="loan.interestRate">
									<fmt:param><mifos:mifoslabel name="${ConfigurationConstants.INTEREST}" /></fmt:param>
								</fmt:message></span>:&nbsp;</td>
							<td width="70%" valign="top"><html-el:text
								styleId="editLoanAccount.input.interestRate"
								property="interestRate" readonly="${loanfn:isDisabledWhileEditingGlim('interestRate',accountState)}"
								value="${sessionScope.loanAccountActionForm.interestRate}" /> 
								<fmt:message key="loan.allowedInterest">
									<fmt:param><mifos:mifoslabel name="${ConfigurationConstants.INTEREST}" /></fmt:param>
								</fmt:message>&nbsp; <fmt:formatNumber
								value="${sessionScope.loanAccountActionForm.minInterestRate}" />&nbsp;
							- &nbsp; <fmt:formatNumber
								value="${sessionScope.loanAccountActionForm.maxInterestRate}" />
							%)</td>
						</tr>
						<tr class="fontnormal">
							<td align="right" class="fontnormal"><span class="mandatorytext"></span>
							<span id="editLoanAccount.label.numberOfInstallments"><mifos:mifoslabel name="loan.no_of_inst" mandatory="yes" /></span>:&nbsp;
							</td>
							<td valign="top"><mifos:mifosnumbertext styleId="editLoanAccount.input.numberOfInstallments"
								name="loanAccountActionForm" property="noOfInstallments" 
								readonly="${loanfn:isDisabledWhileEditingGlim('noOfInstallments',accountState)}"
								value="${sessionScope.loanAccountActionForm.noOfInstallments}" />
							<mifos:mifoslabel name="loan.allowed_no_of_inst" />&nbsp; <fmt:formatNumber
								value="${sessionScope.loanAccountActionForm.minNoInstallments}" />&nbsp;
							- &nbsp; <fmt:formatNumber
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
									<td align="right" valign="top"><mifos:mifoslabel
										name="meeting.labelRepaymentDay" mandatory="yes"
										bundle="MeetingResources">
									</mifos:mifoslabel></td>
									<td align="left" valign="top"
										style="border-top: 1px solid #CECECE; border-left: 1px solid #CECECE; border-right: 1px solid #CECECE;">
									<table width="98%" border="0" cellspacing="0" cellpadding="2">
										<tr valign="top" class="fontnormal">

											<td width="24%"><html-el:radio styleId="loancreationdetails.input.frequencyWeeks" property="frequency" value="1"
												onclick="showMeetingFrequency();" /> <span id="loancreationdetails.label.frequencyWeeks"><mifos:mifoslabel
												name="meeting.labelWeeks" bundle="MeetingResources" /></span></td>
											<td width="55%"><html-el:radio styleId="loancreationdetails.input.frequencyMonths" property="frequency" value="2"
												onclick="showMeetingFrequency();" /> <span id="loancreationdetails.label.frequencyMonths"><mifos:mifoslabel
												name="meeting.labelMonths" bundle="MeetingResources" /></span></td>
										</tr>
									</table>
									</td>
								</tr>
							<tr class="fontnormal">
									<td width="22%" align="right" valign="top">&nbsp;</td>
									<td width="59%" align="left" valign="top"
										style="border: 1px solid #CECECE;">
									
									<div id="weekDIV" style="height:40px; width:380px; "><mifos:mifoslabel
										name="meeting.labelRecurWeeks" bundle="MeetingResources" />



									<table border="0" cellspacing="0" cellpadding="2">
										<tr class="fontnormal">
											<td colspan="4"><mifos:mifoslabel
												name="meeting.labelRecurEvery" bundle="MeetingResources" />


											<mifos:mifosnumbertext styleId="loancreationdetails.input.weekFrequency" property="recurWeek" size="3"  maxlength="3" disabled="${loanfn:isDisabledWhileEditingGlim('repaymentDay',accountState)}"/> <span id="loancreationdetails.label.weekFrequency"><mifos:mifoslabel
												name="meeting.labelWeeks" bundle="MeetingResources" /></span> 
												<mifos:select property="weekDay" disabled="${loanfn:isDisabledWhileEditingGlim('repaymentDay',accountState)}"> 
													<c:forEach var="weekDay" items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'WeekDayList')}" >
															<html-el:option value="${weekDay.value}">${weekDay.name}</html-el:option>
													</c:forEach>
												</mifos:select></td>
										</tr>
									</table>
									</div>
									<div id="monthDIV" style="height:65px; width:450px; "><mifos:mifoslabel
										name="meeting.labelRecurMonths" bundle="MeetingResources" />
									<br>
									<table border="0" cellspacing="0" cellpadding="2">
									<%-- TODO: add conditional around here to only show one monthType TD at a time? --%>
										<tr class="fontnormal">
											<td><html-el:radio styleId="loancreationdetails.input.monthType1" property="monthType" value="1" /></td>
											<td><span id="loancreationdetails.label.dayOfMonth"><mifos:mifoslabel name="meeting.labelDay"
												bundle="MeetingResources" /></span> <mifos:mifosnumbertext styleId="loancreationdetails.input.dayOfMonth"
												property="monthDay" size="3" onfocus="checkMonthType1()" maxlength="2"/> 
												<mifos:mifoslabel name="meeting.labelOfEvery"
												bundle="MeetingResources" /> <mifos:mifosnumbertext styleId="loancreationdetails.input.monthFrequency1"
												property="dayRecurMonth" size="3" onfocus="checkMonthType1()" maxlength="3" disabled="true"/> <span id="loancreationdetails.label.monthFrequency1"><mifos:mifoslabel
												name="meeting.labelMonths" bundle="MeetingResources" /></span></td>
										</tr>
										<tr class="fontnormal">
											<td><html-el:radio styleId="loancreationdetails.input.monthType2" property="monthType" value="2" /></td>
											<td><mifos:mifoslabel name="meeting.labelThe"
												bundle="MeetingResources" /> 
												<mifos:select	property="monthRank" onfocus="checkMonthType2()">
													<c:forEach var="weekRank" items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'WeekRankList')}" >
															<html-el:option value="${weekRank.value}">${weekRank.name}</html-el:option>
													</c:forEach>
											</mifos:select>
											<mifos:select property="monthWeek" onfocus="checkMonthType2()">
												<c:forEach var="weekDay" items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'WeekDayList')}" >
															<html-el:option value="${weekDay.value}">${weekDay.name}</html-el:option>
												</c:forEach>
											</mifos:select> <mifos:mifoslabel name="meeting.labelOfEvery"
												bundle="MeetingResources" /> <mifos:mifosnumbertext styleId="loancreationdetails.input.monthFrequency2"
												property="recurMonth" size="3" onfocus="checkMonthType2()" maxlength="3" disabled="true"/> 
												<span id="loancreationdetails.label.monthFrequency2"><mifos:mifoslabel
												name="meeting.labelMonths" bundle="MeetingResources" /></span></td>
										</tr>
									</table>
									</div>
								</td></tr>
						</c:if>
										
						<tr class="fontnormal">
							<td align="right" class="fontnormal"><span class="mandatorytext"></span>
							<span id="editLoanAccount.label.gracePeriod"><mifos:mifoslabel name="loan.grace_period" /></span>:&nbsp;</td>
							<td valign="top">
								<mifos:mifosnumbertext styleId="editLoanAccount.input.gracePeriod" property="gracePeriod"  disabled="${loanfn:isDisabledWhileEditingGlim('gracePeriod',accountState)}" onchange="setGracePeriodDurationValue();"/></td>
						</tr>
						<tr class="fontnormal">
							<td align="right" class="fontnormal"><mifos:mifoslabel
								keyhm="Loan.SourceOfFund"
								name="loan.source_fund" isColonRequired="yes"/></td>
							<td valign="top"><mifos:select keyhm="Loan.SourceOfFund" property="loanOfferingFund">
								<c:forEach var="loanfund"
									items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'loanfunds')}">
									<html-el:option value="${loanfund.fundId}">${loanfund.fundName}</html-el:option>
								</c:forEach>
								</mifos:select></td>
						</tr>
						<html-el:hidden property="inheritedGracePeriodDuration"
								value="${sessionScope.loanAccountActionForm.gracePeriodDuration}" />
							<html-el:hidden property="gracePeriodDuration"/>
							<html-el:hidden property="gracePeriodTypeId" value="${BusinessKey.gracePeriodType.id}" />
						<script>checkGracePeriod();</script>
						<c:if test="${loanaccountownerisagroup != 'yes'}">
							<tr class="fontnormal">
								<td align="right" class="fontnormal"><mifos:mifoslabel
									keyhm="Loan.PurposeOfLoan" name="Loan.PurposeOfLoan"
									bundle="loanUIResources" /><mifos:mifoslabel
									keyhm="Loan.PurposeOfLoan" name="${ConfigurationConstants.LOAN}"
									isColonRequired="yes" bundle="loanUIResources"
									isManadatoryIndicationNotRequired="yes" /></td>
								<td valign="top"><mifos:select keyhm="Loan.PurposeOfLoan"
									property="businessActivityId" styleId="editLoanAccount.input.purposeofloan">
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
											property="collateralTypeId" styleId="editLoanAccount.select.collateraltype">
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
								property="collateralNote" style="width:320px; height:110px;" styleId="editLoanAccount.textbox.collateralnotes"></mifos:textarea></td>
						</tr>
                        <tr class="fontnormal">
                            <td align="right" valign="top" class="fontnormal"><mifos:mifoslabel
                                keyhm="Loan.ExternalId" name="accounts.externalId"
                                isColonRequired="yes" bundle="accountsUIResources" /></td>
                            <td valign="top"><mifos:mifosalphanumtext keyhm="Loan.ExternalId" property="externalId" styleId="editLoanAccount.input.externalid">
                            </mifos:mifosalphanumtext></td>
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
								<span id="editLoanAccount.label.customField"><mifos:mifoslabel name="${cf.lookUpEntity.entityType}" mandatory="${cf.mandatoryStringValue}" bundle="loanUIResources"></mifos:mifoslabel></span>:
							</td>
			                <td width="70%">
								<c:if test="${cf.fieldType == MasterConstants.CUSTOMFIELD_NUMBER}">
				                	<mifos:mifosnumbertext styleId="editLoanAccount.input.customField"  name = "loanAccountActionForm" property='customField[${ctr}].fieldValue' maxlength="200"  readonly="${loanfn:isDisabledWhileEditingGlim('customField',accountState)}"/>
				                </c:if>
				               	<c:if test="${cf.fieldType == MasterConstants.CUSTOMFIELD_ALPHANUMBER}">
				                	<mifos:mifosalphanumtext styleId="editLoanAccount.input.customField" name = "loanAccountActionForm" property='customField[${ctr}].fieldValue' maxlength="200" readonly="${loanfn:isDisabledWhileEditingGlim('customField',accountState)}"/>
								</c:if>
				                <c:if test="${cf.fieldType == MasterConstants.CUSTOMFIELD_DATE}">
                                    <date:datetag property='customField[${ctr}].fieldValue' isDisabled="${loanfn:isDisabledWhileEditingGlim('customField',accountState)}"/>
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
							<td align="center"><html-el:submit styleId="editLoanAccount.button.preview" property="editDetailsBtn"
								styleClass="buttn" >
								<mifos:mifoslabel name="loan.preview" />
							</html-el:submit> &nbsp; <html-el:button styleId="editLoanAccount.button.cancel" property="cancelButton"
								onclick="javascript:fun_cancel()"
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
<script>
showMeetingFrequency();
</script>