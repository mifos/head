<!-- 

/**

 * CreateSavingsProduct.jsp    version: 1.0

 

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
<%@taglib uri="/tags/date" prefix="date"%>
<%@taglib uri="/tags/mifos-html" prefix="mifos"%>
<%@taglib uri="http://struts.apache.org/tags-html-el" prefix="html-el"%>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="/sessionaccess" prefix="session"%>
<tiles:insert definition=".create">
	<tiles:put name="body" type="string">
		<script>
		<!--
			function fnCancel(form) {
				form.method.value="cancelCreate";
				form.action="savingsproductaction.do";
				form.submit();
			}
			function fnCheckRecMand() {
				if(document.getElementsByName("savingsType")[0].value==1) {
					document.getElementsByName("mandamnt")[0].style.display = "block";
					document.getElementsByName("recamnt")[0].style.display = "none";			
				}
				else {
					document.getElementsByName("mandamnt")[0].style.display = "none";
					document.getElementsByName("recamnt")[0].style.display = "block";	
				}
			}
			
			function fnCheckAppliesTo() {
				if(document.getElementsByName("prdApplicableMaster")[0].value==2) {
					document.getElementsByName("recommendedAmntUnit")[0].disabled=false;
				}
				else {
					document.getElementsByName("recommendedAmntUnit")[0].selectedIndex=0;
					document.getElementsByName("recommendedAmntUnit")[0].disabled=true;
				}
			}
		//-->
		</script>
		<script src="pages/framework/js/date.js"></script>
		<html-el:form action="/savingsproductaction" onsubmit="return (validateMyForm(startDate,startDateFormat,startDateYY) && 
				validateMyForm(endDate,endDateFormat,endDateYY))" focus="prdOfferingName">
			<table width="90%" border="0" align="center" cellpadding="0" cellspacing="0">
				<tr>
					<td align="center" class="heading">
						&nbsp;
					</td>
				</tr>
			</table>
			<table width="90%" border="0" align="center" cellpadding="0" cellspacing="0">
				<tr>
					<td class="bluetablehead">
						<table width="100%" border="0" cellspacing="0" cellpadding="0">
							<tr>
								<td width="27%">
									<table border="0" cellspacing="0" cellpadding="0">
										<tr>
											<td>
												<img src="pages/framework/images/timeline/bigarrow.gif" width="17" height="17">
											</td>
											<td class="timelineboldorange">
												<mifos:mifoslabel name="${ConfigurationConstants.SAVINGS}" bundle="ProductDefUIResources" />
												<mifos:mifoslabel name="product.productinfo" bundle="ProductDefUIResources" />
											</td>
										</tr>
									</table>
								</td>
								<td width="73%" align="right">
									<table border="0" cellspacing="0" cellpadding="0">
										<tr>
											<td>
												<img src="pages/framework/images/timeline/orangearrow.gif" width="17" height="17">
											</td>
											<td class="timelineboldorangelight">
												<mifos:mifoslabel name="product.review" bundle="ProductDefUIResources" />
											</td>
										</tr>
									</table>
								</td>
							</tr>
						</table>
					</td>
				</tr>
			</table>
			<table width="90%" border="0" align="center" cellpadding="0" cellspacing="0" class="bluetableborder">
				<tr>
					<td align="left" valign="top" class="paddingleftCreates">
						<table width="93%" border="0" cellpadding="3" cellspacing="0">
							<tr>
								<td class="headingorange">
									<span class="heading"> <mifos:mifoslabel name="product.addnew" bundle="ProductDefUIResources" /> <mifos:mifoslabel name="${ConfigurationConstants.SAVINGS}" bundle="ProductDefUIResources" /> <mifos:mifoslabel name="product.product"
											bundle="ProductDefUIResources" /> - </span>
									<mifos:mifoslabel name="product.enter" bundle="ProductDefUIResources" />
									<mifos:mifoslabel name="${ConfigurationConstants.SAVINGS}" bundle="ProductDefUIResources" />
									<mifos:mifoslabel name="product.productinfo" bundle="ProductDefUIResources" />
								</td>
							</tr>
							<tr>
								<td class="fontnormal">
									<mifos:mifoslabel name="product.compfields" bundle="ProductDefUIResources" />
									<mifos:mifoslabel name="product.clickpreview" bundle="ProductDefUIResources" />
									<mifos:mifoslabel name="product.clickcancinfo" bundle="ProductDefUIResources" />
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
									<mifos:mifosalphanumtext property="prdOfferingName" />
								</td>
							</tr>
							<tr class="fontnormal">
								<td align="right">
									<mifos:mifoslabel name="product.shortname" mandatory="yes" bundle="ProductDefUIResources" />
									:
								</td>
								<td valign="top">
									<mifos:mifosalphanumtext property="prdOfferingShortName" maxlength="4" size="4" />
								</td>
							</tr>
							<tr class="fontnormal">
								<td align="right" valign="top">
									<span class="mandatorytext"></span>
									<mifos:mifoslabel name="product.desc" bundle="ProductDefUIResources" />
									:
								</td>
								<td valign="top">
									<html-el:textarea property="description" style="width:320px; height:110px;">
									</html-el:textarea>
								</td>
							</tr>
							<tr class="fontnormal">
								<td align="right">
									<mifos:mifoslabel name="product.prodcat" mandatory="yes" bundle="ProductDefUIResources" />
									:
								</td>
								<td valign="top">
									<mifos:select property="prdCategory" style="width:136px;">
										<c:forEach items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'SavingsProductCategoryList')}" var="category">
											<html-el:option value="${category.productCategoryID}">${category.productCategoryName}</html-el:option>
										</c:forEach>
									</mifos:select>
								</td>
							</tr>
							<tr class="fontnormal">
								<td align="right">
									<mifos:mifoslabel name="product.startdate" mandatory="yes" bundle="ProductDefUIResources" />
									:
								</td>
								<td valign="top">
									<date:datetag property="startDate" />
								</td>
							</tr>
							<tr class="fontnormal">
								<td align="right">
									<span class="mandatorytext"></span>
									<mifos:mifoslabel name="product.enddate" bundle="ProductDefUIResources" />
									:
								</td>
								<td valign="top">
									<date:datetag property="endDate" />
								</td>
							</tr>
							<tr class="fontnormal">
								<td align="right">
									<mifos:mifoslabel mandatory="yes" name="product.applfor" bundle="ProductDefUIResources" />
									:
								</td>
								<td valign="top">
									<mifos:select property="prdApplicableMaster" style="width:136px;" onchange="fnCheckAppliesTo();">
										<c:forEach items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'SavingsApplForList')}" var="prdAppl">
											<html-el:option value="${prdAppl.id}">${prdAppl.name}</html-el:option>
										</c:forEach>
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
									<mifos:select property="savingsType" style="width:136px;" onchange="fnCheckRecMand();">
										<c:forEach items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'SavingsTypesList')}" var="type">
											<html-el:option value="${type.id}">${type.name}</html-el:option>
										</c:forEach>
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
									<mifos:mifosdecimalinput property="recommendedAmount" />
								</td>
							</tr>
							<script type="text/javascript">
							fnCheckRecMand();
						</script>
							<tr class="fontnormal" id="appliesto">
								<td align="right">
									<mifos:mifoslabel name="product.recamtappl" bundle="ProductDefUIResources" />
									:
								</td>
								<td valign="top">
									<mifos:select property="recommendedAmntUnit" style="width:136px;">
										<c:forEach items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'RecAmntUnitList')}" var="recAmntUnit">
											<html-el:option value="${recAmntUnit.id}">${recAmntUnit.name}</html-el:option>
										</c:forEach>
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
									<mifos:mifosdecimalinput property="maxAmntWithdrawl" />
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
							<tr class="fontnormal">
								<td align="right">
									<mifos:mifoslabel mandatory="yes" name="${ConfigurationConstants.SERVICE_CHARGE}" bundle="ProductDefUIResources" />
									<mifos:mifoslabel name="product.prdrate" bundle="ProductDefUIResources" />
									:
								</td>
								<td width="70%" valign="top">
									<mifos:mifosdecimalinput property="interestRate" max="100" min="0" decimalFmt="10.5" />
									<mifos:mifoslabel name="product.savingsintrateperc" bundle="ProductDefUIResources" />
								</td>
							</tr>
							<tr class="fontnormal">
								<td align="right">
									<span class="mandatorytext"> <mifos:mifoslabel mandatory="yes" name="product.balusedfor" bundle="ProductDefUIResources" /> <mifos:mifoslabel name="${ConfigurationConstants.SERVICE_CHARGE}" bundle="ProductDefUIResources" /> <mifos:mifoslabel
											name="product.calc" bundle="ProductDefUIResources" />: 
								</td>
								<td valign="top">
									<mifos:select property="interestCalcType" style="width:136px;">
										<c:forEach items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'IntCalcTypesList')}" var="intCalc">
											<html-el:option value="${intCalc.id}">${intCalc.name}</html-el:option>
										</c:forEach>
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
								<td valign="top">
									<mifos:mifosnumbertext property="timeForInterestCacl" size="3" maxlength="3" />
									<html-el:select property="recurTypeFortimeForInterestCacl" style="width:80px;">
										<c:forEach items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'SavingsRecurrenceTypeList')}" var="recType">
											<html-el:option value="${recType.recurrenceId}">${recType.recurrenceName}</html-el:option>
										</c:forEach>
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
								<td valign="top">
									<mifos:mifosnumbertext property="freqOfInterest" size="3" maxlength="3" />
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
									<mifos:mifosdecimalinput property="minAmntForInt" />
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
								<td align="right" valign="top" style="padding-top:8px;">
									<mifos:mifoslabel mandatory="yes" name="product.glcodedep" bundle="ProductDefUIResources" />
									:
								</td>
								<td width="70%" valign="top">
									<mifos:select property="depositGLCode" style="width:136px;">
										<c:forEach items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'depositGLCodes')}" var="glCodes">
											<html-el:option value="${glCodes.glcodeId}">${glCodes.glcode}</html-el:option>
										</c:forEach>

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
									<mifos:select property="interestGLCode" style="width:136px;">
										<c:forEach items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'interestGLCodes')}" var="glCodes">
											<html-el:option value="${glCodes.glcodeId}">${glCodes.glcode}</html-el:option>
										</c:forEach>
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
						<br>
						<table width="93%" border="0" cellpadding="0" cellspacing="0">
							<tr>
								<td align="center">
									<html-el:submit styleClass="buttn" style="width:70px">
										<mifos:mifoslabel name="product.preview" bundle="ProductDefUIResources" />
									</html-el:submit>
									&nbsp;
									<html-el:button property="cancel" styleClass="cancelbuttn" style="width:70px" onclick="javascript:fnCancel(this.form)">
										<mifos:mifoslabel name="product.cancel" bundle="ProductDefUIResources" />
									</html-el:button>
								</td>
							</tr>
						</table>
						<br>
					</td>
				</tr>
			</table>
			<html-el:hidden property="method" value="preview" />
			<html-el:hidden property="currentFlowKey" value="${requestScope.currentFlowKey}" />
		</html-el:form>
	</tiles:put>
</tiles:insert>
