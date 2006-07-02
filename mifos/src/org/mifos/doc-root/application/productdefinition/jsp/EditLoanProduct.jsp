<!-- 

/**

 * EditLoanProduct.jsp    version: 1.0

 

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

<%@taglib uri="http://struts.apache.org/tags-html" prefix="html"%>
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="/tags/mifos-html" prefix="mifos"%>
<%@taglib uri="/tags/date" prefix="date"%>
<%@taglib uri="http://struts.apache.org/tags-html-el" prefix="html-el"%>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles"%>

<tiles:insert definition=".view">
	<tiles:put name="body" type="string">
		<!-- html:javascript formName="/loanprdaction"
			bundle="ProductDefUIResources" /-->
		<script language="javascript">
		<!--
			function fnCancel(form) {
				form.method.value="cancel";
				form.action="loanprdaction.do";
				form.submit();
			}
			function fnSearch(form) {
				form.method.value="search";
				form.action="loanprdaction.do";
				form.submit();
			}
			function fnGet() {
				loanprdactionform.method.value="get";
				loanprdactionform.action="loanprdaction.do";
				loanprdactionform.submit();
			}
			function fnCancel() {
				loanprdactionform.method.value="cancel";
				loanprdactionform.input.value="admin";
				loanprdactionform.action="loanprdaction.do";
				loanprdactionform.submit();
			}
			function showMeetingFrequency(){
				if (document.loanprdactionform.freqOfInstallments[1].checked == true){
					document.getElementById("weekDIV").style.display = "none";
					document.getElementById("monthDIV").style.display = "block";
				}
				else {
					document.getElementById("weekDIV").style.display = "block";
					document.loanprdactionform.freqOfInstallments[0].checked = true;
					document.getElementById("monthDIV").style.display = "none";
				}
			}
			function fnCheckInterestCalcRule() {
				if(document.getElementById("interestTypes.interestTypeId").value==2) {
					//document.getElementById("intcalcrulerow").style.display = "block";
				//	document.getElementById("interestCalcRule.interestCalcRuleId").disabled=false;
					document.getElementById("intDedDisbursementFlag").disabled=true;
					document.getElementById("gracePeriodType.gracePeriodTypeId").disabled=false;
					document.getElementById("gracePeriodDuration").disabled=false;
					document.getElementById("intDedDisbursementFlag").checked=false;
				}
				else if(document.getElementById("interestTypes.interestTypeId").value==1) {
					//document.getElementById("interestCalcRule.interestCalcRuleId").selectedIndex=0;
					//document.getElementById("interestCalcRule.interestCalcRuleId").disabled=true;
					document.getElementById("intDedDisbursementFlag").disabled=false;
				}
				else {
					//document.getElementById("intcalcrulerow").style.display = "none";
					//document.getElementById("interestCalcRule.interestCalcRuleId").selectedIndex=0;
					//document.getElementById("interestCalcRule.interestCalcRuleId").disabled=true;
					document.getElementById("intDedDisbursementFlag").disabled=true;
					document.getElementById("gracePeriodType.gracePeriodTypeId").disabled=false;
					document.getElementById("gracePeriodDuration").disabled=false;
					document.getElementById("intDedDisbursementFlag").checked=false;
				}
			}
			
			function fnIntDesbr() {
				if(document.getElementById("intDedDisbursementFlag").checked==true) {
					document.getElementById("gracePeriodType.gracePeriodTypeId").disabled=true;
					document.getElementById("gracePeriodType.gracePeriodTypeId").selectedIndex=0;
					document.getElementById("gracePeriodDuration").value="";
					document.getElementById("gracePeriodDuration").disabled=true;			
				}
				else {
					document.getElementById("gracePeriodType.gracePeriodTypeId").disabled=false;
					document.getElementById("gracePeriodDuration").disabled=false;
				}
			}
			function fnPenalty() {
				if(document.getElementById("penalty.penaltyID").selectedIndex==0) {
					document.getElementById("penaltyRate").value="";
					document.getElementById("penaltyRate").disabled=true;
				}
				else {
					document.getElementById("penaltyRate").disabled=false;
				}
			}
			function fnGracePeriod() {
				if(document.getElementById("gracePeriodType.gracePeriodTypeId").selectedIndex==0 ||
					document.getElementById("gracePeriodType.gracePeriodTypeId").value==1) {
					document.getElementById("gracePeriodDuration").value="";
					document.getElementById("gracePeriodDuration").disabled=true;
				}else {
					document.getElementById("gracePeriodDuration").disabled=false;
				}
			}
		//-->
		</script>
		<script src="pages/framework/js/date.js"></script>
		<!--bug id 25520 added validateMyForm() -->
		<!-- html-el:form action="/loanprdaction"
			onsubmit="return (validateLoanprdactionform(this)&&
				validateMyForm(startDate,startDateFormat,startDateYY) && 
				validateMyForm(endDate,endDateFormat,endDateYY))"
			focus="prdOfferingName"-->
		<html-el:form action="/loanprdaction"
			onsubmit="return (validateMyForm(startDate,startDateFormat,startDateYY) && 
				validateMyForm(endDate,endDateFormat,endDateYY))"
			focus="prdOfferingName">
			<c:choose>
				<c:when test="${param.prdOfferName == null}">
					<c:set var="prdOfferName"
						value="${requestScope.Context.valueObject.prdOfferingName}" />
				</c:when>
				<c:otherwise>
					<c:set var="prdOfferName" value="${param.prdOfferName}" />
				</c:otherwise>
			</c:choose>
			<table width="95%" border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td class="bluetablehead05"><span class="fontnormal8pt"> <html-el:link
						href="javascript:fnCancel()">
						<mifos:mifoslabel name="product.admin"
							bundle="ProductDefUIResources" />
					</html-el:link> / <html-el:link
						href="javascript:fnSearch(loanprdactionform)">
						<mifos:mifoslabel name="product.savingsview" bundle="ProductDefUIResources" />
					<mifos:mifoslabel name="${ConfigurationConstants.LOAN}" bundle="ProductDefUIResources" />
					<mifos:mifoslabel name="product.products" bundle="ProductDefUIResources" />
					</html-el:link> / <html-el:link href="javascript:fnGet()">
						<c:out value="${prdOfferName}" />
					</html-el:link></span></td>
				</tr>
			</table>
			<table width="95%" border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td width="70%" align="left" valign="top" class="paddingL15T15">
					<table width="93%" border="0" cellpadding="3" cellspacing="0">
						<tr>
							<td class="headingorange"><span class="heading"><c:out
								value="${prdOfferName}" /> - </span> 
								<mifos:mifoslabel name="produt.edit" bundle="ProductDefUIResources" />
								<mifos:mifoslabel name="${ConfigurationConstants.LOAN}" bundle="ProductDefUIResources" />
								<mifos:mifoslabel name="product.info" bundle="ProductDefUIResources" />				
								
							</td>
						</tr>
						<tr>
							<td class="fontnormal"><mifos:mifoslabel
								name="product.editfields" bundle="ProductDefUIResources" /> <mifos:mifoslabel
								name="product.clickpreview" bundle="ProductDefUIResources" /> <mifos:mifoslabel name="product.clickcancel" bundle="ProductDefUIResources" />
									<mifos:mifoslabel name="${ConfigurationConstants.LOAN}" bundle="ProductDefUIResources" />
									<mifos:mifoslabel name="product.withoutsubmit" bundle="ProductDefUIResources" />
							<br>
							<mifos:mifoslabel name="product.fieldsrequired" mandatory="yes"
								bundle="ProductDefUIResources" /></td>
						</tr>
					</table>
					<br>
					<font class="fontnormalRedBold"><html-el:errors
						bundle="ProductDefUIResources" /></font>
					<table width="93%" border="0" cellpadding="3" cellspacing="0">
						<tr>
							<td colspan="2" class="fontnormalbold">
							<mifos:mifoslabel name="${ConfigurationConstants.LOAN}" bundle="ProductDefUIResources" />
							<mifos:mifoslabel name="product.prddetails" bundle="ProductDefUIResources" /><br>
							<br>
							</td>
						</tr>
						<tr class="fontnormal">
							<td width="30%" align="right"><mifos:mifoslabel
								name="product.prodinstname" mandatory="yes"
								bundle="ProductDefUIResources" />:</td>
							<td width="70%" valign="top"><mifos:mifosalphanumtext
								property="prdOfferingName"
								value="${requestScope.Context.valueObject.prdOfferingName}" /></td>
						</tr>
						<tr class="fontnormal">
							<td align="right"><mifos:mifoslabel name="product.shortname"
								mandatory="yes" bundle="ProductDefUIResources" />:</td>
							<td valign="top"><mifos:mifosalphanumtext
								property="prdOfferingShortName" maxlength="4"
								value="${requestScope.Context.valueObject.prdOfferingShortName}" />
							</td>
						</tr>
						<tr class="fontnormal">
							<td align="right" valign="top"><span class="mandatorytext"></span><mifos:mifoslabel
								name="product.desc" bundle="ProductDefUIResources" />:</td>
							<td valign="top"><html-el:textarea property="description"
								style="width:320px; height:110px;"
								value="${requestScope.Context.valueObject.description}">
							</html-el:textarea></td>
						</tr>
						<tr class="fontnormal">
							<td align="right"><mifos:mifoslabel name="product.prodcat"
								mandatory="yes" bundle="ProductDefUIResources" />:</td>
							<td valign="top"><mifos:select
								property="prdCategory.productCategoryID" style="width:136px;"
								name="LoanOffering">
								<html-el:options collection="LoanProductCategoryList"
									property="productCategoryID"
									labelProperty="productCategoryName" />
							</mifos:select></td>
						</tr>
						<tr class="fontnormal">
							<td align="right"><mifos:mifoslabel name="product.startdate"
								mandatory="yes" bundle="ProductDefUIResources" />:</td>
							<td valign="top"><date:datetag property="startDate"
								name="LoanOffering" /></td>
						</tr>
						<tr class="fontnormal">
							<td align="right"><span class="mandatorytext"></span><mifos:mifoslabel
								name="product.enddate" bundle="ProductDefUIResources"/>:</td>
							<td valign="top"><date:datetag property="endDate"
								name="LoanOffering" /></td>
						</tr>
						<tr class="fontnormal">
							<td align="right"><mifos:mifoslabel mandatory="yes"
								name="product.applfor" bundle="ProductDefUIResources" />:</td>
							<td valign="top"><mifos:select
								property="prdApplicableMaster.prdApplicableMasterId"
								style="width:136px;" name="LoanOffering">
								<html-el:options collection="LoanApplForList" property="id"
									labelProperty="name" />
							</mifos:select></td>
						</tr>
						<tr class="fontnormal">
							<td align="right">
							<mifos:mifoslabel name="product.inclin" bundle="ProductDefUIResources" />
							<mifos:mifoslabel name="${ConfigurationConstants.LOAN}"	bundle="ProductDefUIResources" />
							<mifos:mifoslabel name="product.cyclecounter" bundle="ProductDefUIResources" />	:</td>
							<td valign="top"><mifos:select property="loanCounterFlag"
								style="width:136px;" name="LoanOffering">
								<html-el:options collection="YesNoMasterList" property="id"
									labelProperty="name" />
							</mifos:select></td>
						</tr>
						<tr class="fontnormal">
							<td align="right">
							<mifos:mifoslabel mandatory="yes" name="product.max" bundle="ProductDefUIResources" />
							<mifos:mifoslabel name="${ConfigurationConstants.LOAN}"	bundle="ProductDefUIResources" />
							<mifos:mifoslabel name="product.amount"	bundle="ProductDefUIResources" />:
							</td>
							<td valign="top"><mifos:mifosdecimalinput
								property="maxLoanAmount"
								value="${requestScope.Context.valueObject.maxLoanAmount}" /></td>
						</tr>
						<tr class="fontnormal">
							<td align="right">
							<mifos:mifoslabel mandatory="yes" name="product.min" bundle="ProductDefUIResources" />										
							<mifos:mifoslabel name="${ConfigurationConstants.LOAN}"	bundle="ProductDefUIResources" />
							<mifos:mifoslabel name="product.amount"	bundle="ProductDefUIResources" />:</td>
							<td valign="top"><mifos:mifosdecimalinput
								property="minLoanAmount"
								value="${requestScope.Context.valueObject.minLoanAmount}" /></td>
						</tr>
						
						<tr class="fontnormal">
							<td align="right"><mifos:mifoslabel name="product.default" bundle="ProductDefUIResources" />										
							<mifos:mifoslabel name="${ConfigurationConstants.LOAN}"	bundle="ProductDefUIResources" />									
							<mifos:mifoslabel name="product.amount"	bundle="ProductDefUIResources" />:</td>
							<td valign="top"><mifos:mifosdecimalinput
								property="defaultLoanAmount"
								value="${requestScope.Context.valueObject.defaultLoanAmount}" /></td>
						</tr>
					</table>
					<br>
					<table width="93%" border="0" cellpadding="3" cellspacing="0">
						<tr>
							<td colspan="2" class="fontnormalbold"><mifos:mifoslabel
								name="product.status" bundle="ProductDefUIResources" /><br>
							<br>
							</td>
						</tr>
						<tr class="fontnormal">
							<td width="30%" align="right"><mifos:mifoslabel mandatory="yes"
								name="product.chgStatus" bundle="ProductDefUIResources" />:</td>
							<td width="70%"><mifos:select
								property="prdStatus.offeringStatusId" style="width:136px;"
								name="LoanOffering">
								<html-el:options collection="LoanPrdStatusList" property="id"
									labelProperty="name" />
							</mifos:select></td>
						</tr>
					</table>
					<br>
					<table width="93%" border="0" cellpadding="3" cellspacing="0">
						<tr>
							<td colspan="2" class="fontnormalbold">
							<mifos:mifoslabel name="${ConfigurationConstants.SERVICE_CHARGE}" bundle="ProductDefUIResources"/>										
							<mifos:mifoslabel name="product.prdrate" bundle="ProductDefUIResources" /><br>
							<br>
							</td>
						</tr>
						<tr class="fontnormal">
							<td width="30%" align="right"><mifos:mifoslabel mandatory="yes" name="${ConfigurationConstants.SERVICE_CHARGE}" bundle="ProductDefUIResources"/>
							<mifos:mifoslabel name="product.prdrate" bundle="ProductDefUIResources" />
							<mifos:mifoslabel name="product.type" bundle="ProductDefUIResources"/>:</td>
							<td width="70%" valign="top"><mifos:select
								property="interestTypes.interestTypeId" style="width:136px;"
								name="LoanOffering" onchange="fnCheckInterestCalcRule();">
								<html-el:options collection="InterestTypesList" property="id"
									labelProperty="name" />
							</mifos:select></td>
						</tr>
						<tr class="fontnormal">
							<td align="right">
							<mifos:mifoslabel mandatory="yes" name="product.max" bundle="ProductDefUIResources" />
							<mifos:mifoslabel name="${ConfigurationConstants.SERVICE_CHARGE}" bundle="ProductDefUIResources" />									
							<mifos:mifoslabel name="product.prdrate" bundle="ProductDefUIResources" />:</td>
							<td valign="top"><mifos:mifosdecimalinput
								property="maxInterestRate" decimalFmt="10.5"
								value="${requestScope.Context.valueObject.maxInterestRate}"
								max="999" min="0" /> <mifos:mifoslabel name="product.rate"
								bundle="ProductDefUIResources" /></td>
						</tr>
						<tr class="fontnormal">
							<td align="right">
							<mifos:mifoslabel mandatory="yes" name="product.min" bundle="ProductDefUIResources" />
							<mifos:mifoslabel name="${ConfigurationConstants.SERVICE_CHARGE}" bundle="ProductDefUIResources" />									
							<mifos:mifoslabel name="product.prdrate" bundle="ProductDefUIResources" />:</td>
							<td valign="top"><mifos:mifosdecimalinput
								property="minInterestRate" decimalFmt="10.5"
								value="${requestScope.Context.valueObject.minInterestRate}"
								max="999" min="0" /> <mifos:mifoslabel name="product.rate"
								bundle="ProductDefUIResources" /></td>
						</tr>
						<tr class="fontnormal">
							<td align="right"><span class="mandatorytext"></span>
							<mifos:mifoslabel mandatory="yes" name="product.default" bundle="ProductDefUIResources" />
							<mifos:mifoslabel name="${ConfigurationConstants.SERVICE_CHARGE}" bundle="ProductDefUIResources" />									
							<mifos:mifoslabel name="product.prdrate" bundle="ProductDefUIResources" />:
								</td>
							<td valign="top"><mifos:mifosdecimalinput
								property="defInterestRate" decimalFmt="10.5"
								value="${requestScope.Context.valueObject.defInterestRate}"
								max="999" min="0" /> <mifos:mifoslabel name="product.rate"
								bundle="ProductDefUIResources" /></td>
						</tr>
<%-- Commented because this feature is not implemented now. But it may be required in fututre.

						<tr class="fontnormal" id="intcalcrulerow">
							<td align="right"><mifos:mifoslabel mandatory="yes"
								name="product.intratecalcpymt" bundle="ProductDefUIResources" />:</td>
							<td valign="top"><mifos:select
								property="interestCalcRule.interestCalcRuleId"
								style="width:136px;" name="LoanOffering">
								<html-el:options collection="InterestCalcRuleList" property="id"
									labelProperty="name" />
							</mifos:select></td>
						</tr>
--%>			
					</table>
					<br>
					<table width="93%" border="0" cellpadding="3" cellspacing="0">
						<tr>
							<td colspan="2" class="fontnormalbold"><mifos:mifoslabel
								name="product.repaysch" bundle="ProductDefUIResources" /><br>
							<br>
							</td>
						</tr>
						<tr class="fontnormal">
							<td width="30%" align="right" valign="top"><mifos:mifoslabel
								name="product.freqofinst" bundle="ProductDefUIResources" />:</td>
							<td width="70%" valign="top">
							<table width="90%" border="0" cellpadding="3" cellspacing="0">
								<tr class="fontnormal">
									<td align="left" valign="top"
										style="border-top: 1px solid #CECECE; border-left: 1px solid #CECECE; border-right: 1px solid #CECECE;">
									<table width="98%" border="0" cellspacing="0" cellpadding="2">
										<tr valign="top" class="fontnormal">
											<td width="24%"><html-el:radio property="freqOfInstallments"
												value="1" onclick="showMeetingFrequency();" /> <mifos:mifoslabel
												name="product.weeks" bundle="ProductDefUIResources" /></td>
											<td width="55%"><html-el:radio property="freqOfInstallments"
												value="2" onclick="showMeetingFrequency();" /> <mifos:mifoslabel
												name="product.months" bundle="ProductDefUIResources" /></td>
										</tr>
									</table>
									</td>
								</tr>
								<tr class="fontnormal">
									<td width="59%" align="left" valign="top"
										style="border: 1px solid #CECECE;">
									<div id="weekDIV" style="height:40px; width:380px;"><mifos:mifoslabel
										name="product.enterfoll" bundle="ProductDefUIResources" />:
									<table border="0" cellspacing="0" cellpadding="2">
										<tr class="fontnormal">
										<!--bug id 25799  used numbertexttag in place of html-el text tag -->
											<td colspan="4"><mifos:mifoslabel name="product.recur"
												bundle="ProductDefUIResources" /> <mifos:mifosnumbertext
												property="recurWeekDay" size="3" maxValue="32767" minValue="1"/> <mifos:mifoslabel
												name="product.week" bundle="ProductDefUIResources" /></td>
										</tr>
									</table>
									</div>
									<div id="monthDIV" style="height:40px; width:380px;"><mifos:mifoslabel
										name="product.enterfoll" bundle="ProductDefUIResources" />:<br>
									<table border="0" cellspacing="0" cellpadding="2">
										<tr class="fontnormal">
										<!--bug id 25799  used numbertexttag in place of html-el text tag -->
											<td><mifos:mifoslabel name="product.recur"
												bundle="ProductDefUIResources" /> <mifos:mifosnumbertext
												property="recurMonthDay" size="3" maxValue="32767" minValue="1"/> <mifos:mifoslabel
												name="product.month" bundle="ProductDefUIResources" /></td>
										</tr>
									</table>
									</div>
									</td>
								</tr>
							</table>
							<SCRIPT>
showMeetingFrequency();
</SCRIPT></td>
						</tr>
						<tr class="fontnormal">
							<td align="right"><mifos:mifoslabel mandatory="yes"
								name="product.maxinst" bundle="ProductDefUIResources" />:</td>
							<td valign="top"><mifos:mifosnumbertext
								property="maxNoInstallments"
								value="${requestScope.Context.valueObject.maxNoInstallments}" />
							</td>
						</tr>
						<tr class="fontnormal">
							<td align="right"><mifos:mifoslabel mandatory="yes"
								name="product.mininst" bundle="ProductDefUIResources" />:</td>
							<td valign="top"><mifos:mifosnumbertext
								property="minNoInstallments"
								value="${requestScope.Context.valueObject.minNoInstallments}" />
							</td>
						</tr>
						<tr class="fontnormal">
							<td align="right"><span class="mandatorytext"></span><mifos:mifoslabel
								name="product.definst" bundle="ProductDefUIResources"
								mandatory="yes" />:</td>
							<td valign="top"><mifos:mifosnumbertext
								property="defNoInstallments"
								value="${requestScope.Context.valueObject.defNoInstallments}" />
							</td>
						</tr>
						<tr class="fontnormal">
							<td align="right">
							<mifos:mifoslabel name="${ConfigurationConstants.SERVICE_CHARGE}" bundle="ProductDefUIResources"/>
							<mifos:mifoslabel name="product.deductedatdis" bundle="ProductDefUIResources" />:
							</td>
							<td valign="top"><html-el:checkbox
								property="intDedDisbursementFlag" value="1" name="LoanOffering"
								onclick="fnIntDesbr();" /></td>
						</tr>
						<tr class="fontnormal">
							<td align="right"><mifos:mifoslabel name="product.prinlastinst"
								bundle="ProductDefUIResources" />:</td>
							<td valign="top"><html-el:checkbox property="prinDueLastInstFlag"
								value="1" name="LoanOffering" /></td>
						</tr>
						<tr class="fontnormal">
							<td align="right"><mifos:mifoslabel name="product.gracepertype"
								bundle="ProductDefUIResources" />:</td>
							<td valign="top"><mifos:select
								property="gracePeriodType.gracePeriodTypeId"
								style="width:136px;" name="LoanOffering" onchange="fnGracePeriod();">
								<html-el:options collection="LoanGracePeriodTypeList"
									property="id" labelProperty="name" />
							</mifos:select></td>
						</tr>
						<tr class="fontnormal">
							<td align="right"><mifos:mifoslabel name="product.graceperdur"
								bundle="ProductDefUIResources" />:</td>
							<td valign="top"><mifos:mifosnumbertext
								property="gracePeriodDuration"
								value="${requestScope.Context.valueObject.gracePeriodDuration}" />
							<mifos:mifoslabel name="product.installments"
								bundle="ProductDefUIResources" /></td>
						</tr>
						<script type="text/javascript">
							fnCheckInterestCalcRule();
							fnIntDesbr();
							fnGracePeriod();
						</script>
					</table>
					<br>
					<table width="93%" border="0" cellpadding="3" cellspacing="0">
						<tr>
							<td colspan="2" class="fontnormalbold"><mifos:mifoslabel
								name="product.fees&pen" bundle="ProductDefUIResources" /><br>
							<br>
							</td>
						</tr>
						<tr class="fontnormal">
							<td align="right" valign="top"><mifos:mifoslabel
								name="product.attachfeestypes" bundle="ProductDefUIResources" />:</td>
							<td valign="top">
							<table width="80%" border="0" cellspacing="0" cellpadding="0">
								<tr>
									<td class="fontnormal"><mifos:mifoslabel
										name="product.clickfeetypes" bundle="ProductDefUIResources" />
									</td>
								</tr>
								<tr>
									<td><img src="pages/framework/images/trans.gif" width="1"
										height="1"></td>
								</tr>
							</table>
							
							<mifos:mifoscompositeselect output="prdOfferingFeesSet"
								input="LoanFeesList" property="prdOfferinFees" property1="feeId"
								property2="feeName" scope="request" formName="LoanOffering" 
								outputMethod="fees"  multiple="true"/> <br>
							</td>
						</tr>
						<tr class="fontnormal">
							<td width="30%" align="right" valign="top"
								style="padding-top:7px;"><mifos:mifoslabel
								name="product.penaltytype" bundle="ProductDefUIResources" />:</td>
							<td width="70%">
							<table width="100%" border="0" cellspacing="0" cellpadding="0">
								<tr class="fontnormal">
									<td width="32%" rowspan="2" valign="top"
										style="padding-top:4px;"><mifos:select
										property="penalty.penaltyID" style="width:136px;"
										name="LoanOffering" onchange="fnPenalty();">
										<html-el:options collection="PenaltyTypesList"
											property="penaltyID" labelProperty="penaltyType" />
									</mifos:select></td>
									<td width="8%"><input name="radiobutton" type="radio"
										value="radiobutton" checked></td>
									<td width="60%"><mifos:mifoslabel
										name="product.appchgnewopenacc" bundle="ProductDefUIResources" /></td>
								</tr>
								<tr class="fontnormal">
									<td><input type="radio" name="radiobutton" value="radiobutton"></td>
									<td><mifos:mifoslabel name="product.appchgnewacc"
										bundle="ProductDefUIResources" /></td>
								</tr>
							</table>
							</td>
						</tr>
						<tr class="fontnormal">
							<td align="right" valign="top" style="padding-top:7px;"><span
								class="mandatorytext"><mifos:mifoslabel
								name="product.penaltyrate" bundle="ProductDefUIResources" />:</td>
							<td>
							<table width="100%" border="0" cellspacing="0" cellpadding="0">
								<tr class="fontnormal">
									<td width="32%" rowspan="2" valign="top"
										style="padding-top:4px;"><mifos:mifosdecimalinput property="penaltyRate"
										value="${requestScope.Context.valueObject.penaltyRate}" max="999"/></td>
									<td width="8%"><input name="radiobutton1" type="radio"
										value="radiobutton" checked></td>
									<td width="60%"><mifos:mifoslabel
										name="product.appchgnewopenacc" bundle="ProductDefUIResources" />
									</td>
								</tr>
								<tr class="fontnormal">
									<td><input type="radio" name="radiobutton1" value="radiobutton">
									</td>
									<td><mifos:mifoslabel name="product.appchgnewacc"
										bundle="ProductDefUIResources" /></td>
								</tr>
							</table>
							<!-- bug id 26809 added javascript function fnPenalty() -->
							<script type="text/javascript">
								fnPenalty();
							</script>
							</td>
						</tr>
						<tr class="fontnormal">
							<td align="right"><mifos:mifoslabel 
								name="product.gracepenalty" bundle="ProductDefUIResources" />:</td>
							<td><mifos:mifosnumbertext property="penaltyGrace"
								value="${requestScope.Context.valueObject.penaltyGrace}" /> <mifos:mifoslabel
								name="product.days" bundle="ProductDefUIResources" /></td>
						</tr>
					</table>
					<br>
					<table width="93%" border="0" cellpadding="3" cellspacing="0">
						<tr>
							<td colspan="2" class="fontnormalbold"><mifos:mifoslabel
								name="product.accounting" bundle="ProductDefUIResources" /><br>
							<br>
							</td>
						</tr>
						<tr class="fontnormal">
							<td width="30%" align="right" valign="top"><mifos:mifoslabel
								name="product.srcfunds" bundle="ProductDefUIResources" />:</td>
							<td width="70%" valign="top">
							<table width="80%" border="0" cellspacing="0" cellpadding="0">
								<tr>
									<td class="fontnormal"><mifos:mifoslabel
										name="product.clickfunds" bundle="ProductDefUIResources" /></td>
								</tr>
								<tr>
									<td><img src="pages/framework/images/trans.gif" width="1"
										height="1"></td>
								</tr>
							</table>
							<mifos:mifoscompositeselect output="loanOffeingFundSet"
								input="SrcFundsList" property="loanOfferingFunds"
								property1="fundId" property2="fundName" outputMethod="fund" 
								multiple="true" scope="request" formName="LoanOffering" /></td>
						</tr>
						
						<tr class="fontnormal">
							<td align="right" valign="top" style="padding-top:8px;"><mifos:mifoslabel
								mandatory="yes" name="product.productglcode"
								bundle="ProductDefUIResources" />:</td>
							<td valign="top">
							<table width="80%" border="0" cellspacing="0" cellpadding="2">
								<tr class="fontnormal">
									<td width="15%">
									<mifos:mifoslabel name="${ConfigurationConstants.SERVICE_CHARGE}" bundle="ProductDefUIResources"/>:
									</td>
									<td width="85%">
									<mifos:select name="LoanOffering" property="interestGLCode.glcodeId" 
											style="width:136px;" disabled="true">
                						<html-el:options collection="interestGLCodes" property="glcodeId" labelProperty="glcode" />
                					</mifos:select>
									
									
								</td>
								</tr>
								<tr class="fontnormal">
									<td><mifos:mifoslabel name="product.principal"
										bundle="ProductDefUIResources" />:</td>
									<td>
									<mifos:select name="LoanOffering" property="principalGLCode.glcodeId" 
											style="width:136px;" disabled="true">
	                					<html-el:options collection="principalGLCodes" property="glcodeId" labelProperty="glcode"  />
           							</mifos:select>
									
									</td>
								</tr>
								<tr class="fontnormal">
									<td><mifos:mifoslabel name="product.penalties"
										bundle="ProductDefUIResources" />:</td>
									<td>
									<mifos:select name="LoanOffering" property="penaltyGLCode.glcodeId" 
											style="width:136px;" disabled="true">
	             						<html-el:options collection="penaltyGLCodes" property="glcodeId" labelProperty="glcode" />
          							</mifos:select>
									</td>
								</tr>
							</table>
							
							</td>
						</tr>

					<html-el:hidden property="interestGLCode.glcodeId" value="${requestScope.Context.valueObject.interestGLCode.glcodeId}"/>
					<html-el:hidden property="principalGLCode.glcodeId" value="${requestScope.Context.valueObject.principalGLCode.glcodeId}"/>
					<html-el:hidden property="penaltyGLCode.glcodeId" value="${requestScope.Context.valueObject.penaltyGLCode.glcodeId}"/>
					</table>
					<table width="93%" border="0" cellpadding="0" cellspacing="0">
						<tr>
							<td align="center" class="blueline">&nbsp;</td>
						</tr>
					</table>
					<html-el:hidden property="method" value="preview" /> <html-el:hidden
						property="input" value="details" /> <html-el:hidden
						property="prdOfferingId"
						value="${requestScope.Context.valueObject.prdOfferingId}" /> <html-el:hidden
						property="prdOfferName" value="${prdOfferName}" /> <html-el:hidden
						property="searchNode(search_name)" value="LoanProducts" /><br>
					<br>
					<table width="93%" border="0" cellpadding="0" cellspacing="0">
						<tr>
							<td align="center"><html-el:submit styleClass="buttn"
								style="width:70px"
								onclick="transferData(this.form.loanOfferingFunds);
										transferData(this.form.prdOfferinFees);">
								<mifos:mifoslabel name="product.preview"
									bundle="ProductDefUIResources" />
							</html-el:submit> &nbsp; <html-el:button property="cancel"
								styleClass="cancelbuttn" style="width:70px"
								onclick="javascript:fnGet()">
								<mifos:mifoslabel name="product.cancel"
									bundle="ProductDefUIResources" />
							</html-el:button></td>
						</tr>
					</table>
					<br>
					</td>
				</tr>
			</table>
		</html-el:form>
	</tiles:put>
</tiles:insert>
