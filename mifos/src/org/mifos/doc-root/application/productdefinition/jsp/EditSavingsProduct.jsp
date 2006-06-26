<!-- 

/**

 * EditSavingsProduct.jsp    version: 1.0

 

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
		<!-- html:javascript formName="/savingsprdaction"
			bundle="ProductDefUIResources" /-->
		<script>
			function showMeetingFrequency(){
				if (document.savingsprdactionform.freqOfInterest[0].checked == true){
					document.getElementById("dayDIV").style.display = "block";
					document.getElementById("weekDIV").style.display = "none";
					document.getElementById("monthDIV").style.display = "none";
					}
				else if (document.savingsprdactionform.freqOfInterest[1].checked == true){
					document.getElementById("dayDIV").style.display = "none";
					document.getElementById("weekDIV").style.display = "block";
					document.getElementById("monthDIV").style.display = "none";
					}
				else if (document.savingsprdactionform.freqOfInterest[2].checked == true){
					document.getElementById("dayDIV").style.display = "none";
					document.getElementById("weekDIV").style.display = "none";
					document.getElementById("monthDIV").style.display = "block";
					if(document.savingsprdactionform.monthType[0].checked == false && 
						document.savingsprdactionform.monthType[1].checked == false)
						document.savingsprdactionform.monthType[0].checked = true;
					}
				else {
					document.savingsprdactionform.freqOfInterest[1].checked = true;
					document.getElementById("dayDIV").style.display = "none";
					document.getElementById("weekDIV").style.display = "block";
					document.getElementById("monthDIV").style.display = "none";
				}
			}
			
			function showMeetingFrequency1(){
				if (document.savingsprdactionform.timeForInterestCacl[0].checked == true){
					document.getElementById("dayDIV1").style.display = "block";
					document.getElementById("weekDIV1").style.display = "none";
					document.getElementById("monthDIV1").style.display = "none";
					}
				else if (document.savingsprdactionform.timeForInterestCacl[1].checked == true){
					document.getElementById("dayDIV1").style.display = "none";
					document.getElementById("weekDIV1").style.display = "block";
					document.getElementById("monthDIV1").style.display = "none";
					}
				else if (document.savingsprdactionform.timeForInterestCacl[2].checked == true){
					document.getElementById("dayDIV1").style.display = "none";
					document.getElementById("weekDIV1").style.display = "none";
					document.getElementById("monthDIV1").style.display = "block";
					if(document.savingsprdactionform.intmonthType[0].checked == false && 
						document.savingsprdactionform.intmonthType[1].checked == false)
						document.savingsprdactionform.intmonthType[0].checked = true;
					}
				else {
					document.savingsprdactionform.timeForInterestCacl[1].checked = true;
					document.getElementById("dayDIV1").style.display = "none";
					document.getElementById("weekDIV1").style.display = "block";
					document.getElementById("monthDIV1").style.display = "none";
				}
			}
			function fnCancel(form) {
					form.method.value="cancel";
					form.action="savingsprdaction.do";
					form.submit();
				}
			function fnSearch(form) {
				form.method.value="search";
				form.action="savingsprdaction.do";
				form.submit();
			}
			function fnGet() {
				savingsprdactionform.method.value="get";
				savingsprdactionform.action="savingsprdaction.do";
				savingsprdactionform.submit();
			}
			function fnCancel() {
				savingsprdactionform.method.value="cancel";
				savingsprdactionform.input.value="admin";
				savingsprdactionform.action="savingsprdaction.do";
				savingsprdactionform.submit();
			}
			function fnCheckRecMand() {
				if(document.getElementById("savingsType.savingsTypeId").value==1) {
					document.getElementById("mandamnt").style.display = "block";
					document.getElementById("recamnt").style.display = "none";			
				}
				else {
					document.getElementById("mandamnt").style.display = "none";
					document.getElementById("recamnt").style.display = "block";	
				}
			}
			function fnCheckAppliesTo() {
				if(document.getElementById("prdApplicableMaster.prdApplicableMasterId").value==2) {
					//document.getElementById("appliesto").style.display = "block";
					document.getElementById("recommendedAmntUnit.recommendedAmntUnitId").disabled=false;
				}
				else {
					//document.getElementById("appliesto").style.display = "none";
					document.getElementById("recommendedAmntUnit.recommendedAmntUnitId").selectedIndex=0;
					document.getElementById("recommendedAmntUnit.recommendedAmntUnitId").disabled=true;
				}
			}
		</script>
		<script src="pages/framework/js/date.js"></script>
		<!--bug id 25520 added validateMyForm() -->
		<!-- html-el:form action="/savingsprdaction"
			onsubmit="return (validateSavingsprdactionform(this)&&
				validateMyForm(startDate,startDateFormat,startDateYY) && 
				validateMyForm(endDate,endDateFormat,endDateYY))"
			focus="prdOfferingName"-->
		<html-el:form action="/savingsprdaction" onsubmit="return (validateMyForm(startDate,startDateFormat,startDateYY) && 
				validateMyForm(endDate,endDateFormat,endDateYY))" focus="prdOfferingName">
			<c:choose>
				<c:when test="${param.prdOfferName == null}">
					<c:set var="prdOfferName" value="${requestScope.Context.valueObject.prdOfferingName}" />
				</c:when>
				<c:otherwise>
					<c:set var="prdOfferName" value="${param.prdOfferName}" />
				</c:otherwise>
			</c:choose>
			<table width="95%" border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td class="bluetablehead05">
						<span class="fontnormal8pt"> <html-el:link href="javascript:fnCancel()">
								<mifos:mifoslabel name="product.admin" bundle="ProductDefUIResources" />
							</html-el:link> / <html-el:link href="javascript:fnSearch(savingsprdactionform)">
								<mifos:mifoslabel name="product.savingsview" bundle="ProductDefUIResources" />
								<mifos:mifoslabel name="${ConfigurationConstants.SAVINGS}" bundle="ProductDefUIResources" />
								<mifos:mifoslabel name="product.products" bundle="ProductDefUIResources" />
							</html-el:link> / <html-el:link href="javascript:fnGet()">
								<c:out value="${prdOfferName}" />
							</html-el:link></span>
					</td>
				</tr>
			</table>
			<table width="95%" border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td width="70%" align="left" valign="top" class="paddingL15T15">
						<table width="93%" border="0" cellpadding="3" cellspacing="0">
							<tr>
								<td class="headingorange">
									<span class="heading"><c:out value="${prdOfferName}" /> - </span>
									<mifos:mifoslabel name="product.prdedit" bundle="ProductDefUIResources" />
									<mifos:mifoslabel name="${ConfigurationConstants.SAVINGS}" bundle="ProductDefUIResources" />
									<mifos:mifoslabel name="product.productinfo" bundle="ProductDefUIResources" />
								</td>
							</tr>
							<tr>
								<td class="fontnormal">
									<mifos:mifoslabel name="product.editfields" bundle="ProductDefUIResources" />
									<mifos:mifoslabel name="product.clickpreview" bundle="ProductDefUIResources" />
									<mifos:mifoslabel name="product.clickcancel" bundle="ProductDefUIResources" />
									<mifos:mifoslabel name="${ConfigurationConstants.SAVINGS}" bundle="ProductDefUIResources" />
									<mifos:mifoslabel name="product.withoutsubmit" bundle="ProductDefUIResources" />
									<br>
									<mifos:mifoslabel name="product.fieldsrequired" mandatory="yes" bundle="ProductDefUIResources" />
								</td>
							</tr>
						</table>
						<br>
						<font class="fontnormalRedBold"><html-el:errors bundle="ProductDefUIResources" /></font>
						<table width="93%" border="0" cellpadding="3" cellspacing="0">
							<tr>
								<td colspan="2" class="fontnormalbold">
									<mifos:mifoslabel name="${ConfigurationConstants.SAVINGS}" bundle="ProductDefUIResources" />
									<mifos:mifoslabel name="product.prddetails" bundle="ProductDefUIResources" />
									<br>
									<br>
								</td>
							</tr>
							<tr class="fontnormal">
								<td width="30%" align="right">
									<mifos:mifoslabel name="product.prodinstname" mandatory="yes" bundle="ProductDefUIResources" />
									:
								</td>
								<td width="70%" valign="top">
									<mifos:mifosalphanumtext property="prdOfferingName" value="${requestScope.Context.valueObject.prdOfferingName}" />
								</td>
							</tr>
							<tr class="fontnormal">
								<td align="right">
									<mifos:mifoslabel name="product.shortname" mandatory="yes" bundle="ProductDefUIResources" />
									:
								</td>
								<td valign="top">
									<mifos:mifosalphanumtext property="prdOfferingShortName" maxlength="4" value="${requestScope.Context.valueObject.prdOfferingShortName}" />
								</td>
							</tr>
							<tr class="fontnormal">
								<td align="right" valign="top">
									<span class="mandatorytext"></span>
									<mifos:mifoslabel name="product.desc" bundle="ProductDefUIResources" />
									:
								</td>
								<td valign="top">
									<html-el:textarea property="description" style="width:320px; height:110px;" value="${requestScope.Context.valueObject.description}">
									</html-el:textarea>
								</td>
							</tr>
							<tr class="fontnormal">
								<td align="right">
									<mifos:mifoslabel name="product.prodcat" mandatory="yes" bundle="ProductDefUIResources" />
									:
								</td>
								<td valign="top">
									<mifos:select property="prdCategory.productCategoryID" style="width:136px;" name="SavingsOffering">
										<html-el:options collection="SavingsProductCategoryList" property="productCategoryID" labelProperty="productCategoryName" />
									</mifos:select>
								</td>
							</tr>
							<tr class="fontnormal">
								<td align="right">
									<mifos:mifoslabel name="product.startdate" mandatory="yes" bundle="ProductDefUIResources" />
									:
								</td>
								<td valign="top">
									<date:datetag property="startDate" name="SavingsOffering" />
								</td>
							</tr>
							<tr class="fontnormal">
								<td align="right">
									<span class="mandatorytext"></span>
									<mifos:mifoslabel name="product.enddate" bundle="ProductDefUIResources" />
									:
								</td>
								<td valign="top">
									<date:datetag property="endDate" name="SavingsOffering" />
								</td>
							</tr>
							<tr class="fontnormal">
								<td align="right">
									<mifos:mifoslabel mandatory="yes" name="product.applfor" bundle="ProductDefUIResources" />
									:
								</td>
								<td valign="top">
									<mifos:select property="prdApplicableMaster.prdApplicableMasterId" style="width:136px;" name="SavingsOffering" onchange="fnCheckAppliesTo();">
										<html-el:options collection="SavingsApplForList" property="id" labelProperty="name" />
									</mifos:select>
								</td>
							</tr>
						</table>
						<br>
						<table width="93%" border="0" cellpadding="3" cellspacing="0">
							<tr>
								<td colspan="2" class="fontnormalbold">
									<mifos:mifoslabel name="product.tardepwidrest" bundle="ProductDefUIResources" />
									<br>
									<br>
								</td>
							</tr>
							<tr class="fontnormal">
								<td width="30%" align="right">
									<mifos:mifoslabel name="product.typeofdep" mandatory="yes" bundle="ProductDefUIResources" />
									:
								</td>
								<td width="70%" valign="top">
									<mifos:select property="savingsType.savingsTypeId" style="width:136px;" name="SavingsOffering" onchange="fnCheckRecMand();">
										<html-el:options collection="SavingsTypesList" property="id" labelProperty="name" />
									</mifos:select>
								</td>
							</tr>
							<tr class="fontnormal">
								<td align="right" id="recamnt">
									<mifos:mifoslabel name="product.recamtdep" bundle="ProductDefUIResources" />
									:
								</td>
								<td align="right" id="mandamnt">
									<mifos:mifoslabel name="product.mandamntdep" mandatory="yes" bundle="ProductDefUIResources" />
									:
								</td>
								<td valign="top">
									<mifos:mifosdecimalinput property="recommendedAmount" value="${requestScope.Context.valueObject.recommendedAmount}" />
								</td>
							</tr>
							<script type="text/javascript">
							fnCheckRecMand();
						</script>
							<tr class="fontnormal">
								<td align="right">
									<mifos:mifoslabel name="product.recamtappl" bundle="ProductDefUIResources" />
									:
								</td>
								<td valign="top">
									<mifos:select property="recommendedAmntUnit.recommendedAmntUnitId" style="width:136px;" name="SavingsOffering">
										<html-el:options collection="RecAmntUnitList" property="id" labelProperty="name" />
									</mifos:select>
								</td>
							</tr>
							<script type="text/javascript">
							fnCheckAppliesTo();
						</script>
							<tr class="fontnormal">
								<td align="right">
									<mifos:mifoslabel name="product.maxamtwid" bundle="ProductDefUIResources" />
									:
								</td>
								<td valign="top">
									<mifos:mifosdecimalinput property="maxAmntWithdrawl" value="${requestScope.Context.valueObject.maxAmntWithdrawl}" />
								</td>
							</tr>
						</table>
						<br>
						<table width="93%" border="0" cellpadding="3" cellspacing="0">
							<tr>
								<td colspan="2" class="fontnormalbold">
									<mifos:mifoslabel name="product.status" bundle="ProductDefUIResources" />
									<br>
									<br>
								</td>
							</tr>
							<tr class="fontnormal">
								<td width="30%" align="right">
									<mifos:mifoslabel mandatory="yes" name="product.chgStatus" bundle="ProductDefUIResources" />
									:
								</td>
								<td width="70%">
									<mifos:select property="prdStatus.offeringStatusId" style="width:136px;" name="SavingsOffering">
										<html-el:options collection="SavingsPrdStatusList" property="id" labelProperty="name" />
									</mifos:select>
								</td>
							</tr>
						</table>
						<br>
						<table width="93%" border="0" cellpadding="3" cellspacing="0">
							<tr>
								<td colspan="2" class="fontnormalbold">
									<mifos:mifoslabel name="${ConfigurationConstants.SERVICE_CHARGE}" bundle="ProductDefUIResources" />
									<mifos:mifoslabel name="product.prdrate" bundle="ProductDefUIResources" />
									<br>
									<br>
								</td>
							</tr>
							<!-- bug id 27509 changed maxValue from 999 to 100 and also changed the label -->
							<tr class="fontnormal">
								<td width="30%" align="right">
									<mifos:mifoslabel mandatory="yes" name="${ConfigurationConstants.SERVICE_CHARGE}" bundle="ProductDefUIResources" />
									<mifos:mifoslabel name="product.prdrate" bundle="ProductDefUIResources" />
									:
								</td>
								<td width="70%" valign="top">
									<mifos:mifosdecimalinput property="interestRate" max="100" min="0" decimalFmt="10.5" value="${requestScope.Context.valueObject.interestRate}" />
									<mifos:mifoslabel name="product.savingsintrateperc" bundle="ProductDefUIResources" />
								</td>
							</tr>
							<tr class="fontnormal">
								<td align="right">
									<mifos:mifoslabel mandatory="yes" name="product.balusedfor" bundle="ProductDefUIResources" />
									<mifos:mifoslabel name="${ConfigurationConstants.SERVICE_CHARGE}" bundle="ProductDefUIResources" />
									<mifos:mifoslabel name="product.calc" bundle="ProductDefUIResources" />
									:
								</td>
								<td valign="top">
									<mifos:select property="interestCalcType.interestCalculationTypeID" style="width:136px;" name="SavingsOffering">
										<html-el:options collection="IntCalcTypesList" property="id" labelProperty="name" />
									</mifos:select>
								</td>
							</tr>
							<tr class="fontnormal">
								<td align="right" valign="top">
									<mifos:mifoslabel mandatory="yes" name="product.timeper" bundle="ProductDefUIResources" />
									<mifos:mifoslabel name="${ConfigurationConstants.SERVICE_CHARGE}" bundle="ProductDefUIResources" />
									<mifos:mifoslabel name="product.calc" bundle="ProductDefUIResources" />
									:
								</td>
								<!--bug id 25496  added maxValue -->
								<td valign="top">
									<mifos:mifosnumbertext property="timeForInterestCacl" size="3" maxValue="32767" minValue="1" />
									<html-el:select property="recurTypeFortimeForInterestCacl" style="width:80px;">
										<html-el:options collection="SavingsRecurrenceTypeList" property="recurrenceId" labelProperty="recurrenceName" />
									</html-el:select>
								</td>
							</tr>
							<tr class="fontnormal">
								<td align="right" valign="top">
									<mifos:mifoslabel mandatory="yes" name="product.freq" bundle="ProductDefUIResources" />
									<mifos:mifoslabel name="${ConfigurationConstants.SERVICE_CHARGE}" bundle="ProductDefUIResources" />
									<mifos:mifoslabel name="product.postacc" bundle="ProductDefUIResources" />
									:
								</td>
								<!--bug id 25496  added maxValue -->
								<td valign="top">
									<mifos:mifosnumbertext property="freqOfInterest" size="3" maxValue="32767" minValue="1" />
									<mifos:mifoslabel name="product.month" bundle="ProductDefUIResources" />
								</td>
							</tr>
							<tr class="fontnormal">
								<td align="right">
									<mifos:mifoslabel name="product.minbalreq" bundle="ProductDefUIResources" />
									<mifos:mifoslabel name="${ConfigurationConstants.SERVICE_CHARGE}" bundle="ProductDefUIResources" />
									<mifos:mifoslabel name="product.calc" bundle="ProductDefUIResources" />
									:
								</td>
								<td valign="top">
									<mifos:mifosdecimalinput property="minAmntForInt" value="${requestScope.Context.valueObject.minAmntForInt}" />
								</td>
							</tr>
						</table>
						<br>
						<table width="93%" border="0" cellpadding="3" cellspacing="0">
							<tr>
								<td colspan="2" class="fontnormalbold">
									<mifos:mifoslabel name="product.accounting" bundle="ProductDefUIResources" />
									<br>
									<br>
								</td>
							</tr>
							<tr class="fontnormal">
								<td width="30%" align="right" valign="top" style="padding-top:8px;">
									<mifos:mifoslabel mandatory="yes" name="product.glcodedep" bundle="ProductDefUIResources" />
									:
								</td>
								<td width="70%" valign="top">
									<mifos:select property="depositGLCode.glcodeId" style="width:136px;" disabled="true" name="SavingsOffering">
										<html-el:options collection="depositGLCodes" property="glcodeId" labelProperty="glcode" />
									</mifos:select>
								</td>
							</tr>
							<tr class="fontnormal">
								<td align="right" valign="top" style="padding-top:8px;">
									<mifos:mifoslabel mandatory="yes" name="product.Glcodefor" bundle="ProductDefUIResources" />
									<mifos:mifoslabel name="${ConfigurationConstants.SERVICE_CHARGE}" bundle="ProductDefUIResources" />
									:
								</td>
								<td valign="top">
									<mifos:select property="interestGLCode.glcodeId" style="width:136px;" disabled="true" name="SavingsOffering">
										<html-el:options collection="interestGLCodes" property="glcodeId" labelProperty="glcode" />
									</mifos:select>
								</td>
							</tr>
						</table>
						<table width="93%" border="0" cellpadding="0" cellspacing="0">
							<tr>
								<td align="center" class="blueline">
									&nbsp;
								</td>
							</tr>
						</table>
						<html-el:hidden property="interestGLCode.glcodeId" value="${requestScope.Context.valueObject.interestGLCode.glcodeId}" />
						<html-el:hidden property="depositGLCode.glcodeId" value="${requestScope.Context.valueObject.depositGLCode.glcodeId}" />
						<html-el:hidden property="method" value="preview" />
						<html-el:hidden property="input" value="details" />
						<html-el:hidden property="prdOfferingId" value="${requestScope.Context.valueObject.prdOfferingId}" />
						<html-el:hidden property="prdOfferName" value="${prdOfferName}" />
						<html-el:hidden property="searchNode(search_name)" value="SavingsProducts" />
						<br>
						<table width="93%" border="0" cellpadding="0" cellspacing="0">
							<tr>
								<td align="center">
									<html-el:submit styleClass="buttn" style="width:70px">
										<mifos:mifoslabel name="product.preview" bundle="ProductDefUIResources" />
									</html-el:submit>
									&nbsp;
									<html-el:button property="cancel" styleClass="cancelbuttn" style="width:70px" onclick="javascript:fnGet()">
										<mifos:mifoslabel name="product.cancel" bundle="ProductDefUIResources" />
									</html-el:button>
								</td>
							</tr>
						</table>
						<br>
					</td>
				</tr>
			</table>
		</html-el:form>
	</tiles:put>
</tiles:insert>

