<%--
Copyright (c) 2005-2009 Grameen Foundation USA
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

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html"%>
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@taglib uri="http://struts.apache.org/tags-logic" prefix="logic"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="/tags/mifos-html" prefix="mifos"%>
<%@taglib uri="http://struts.apache.org/tags-html-el" prefix="html-el"%>
<%@ taglib uri="/tags/struts-tiles" prefix="tiles"%>
<%@ taglib uri="/mifos/customtags" prefix="mifoscustom"%>
<%@ taglib uri="/mifos/custom-tags" prefix="customtags"%>
<%@ taglib uri="/sessionaccess" prefix="session"%>


<tiles:insert definition=".clientsacclayoutsearchmenu">
	<tiles:put name="body" type="string">
	<input type="hidden" id="page.id" value="ApplyCharges"/>

		<script src="pages/framework/js/conversion.js"></script>
		<script src="pages/framework/js/con_en.js"></script>
		<script src="pages/framework/js/con_${sessionScope['UserContext'].currentLocale}.js"></script>
		<script>
				function fun_cancel(form)
				{
						form.action="applyChargeAction.do?method=cancel";
						form.submit();
				}

				function fun_submit(){
					document.getElementsByName("charge")[0].value=document.getElementsByName("chargeAmount")[0].value;
				}

			function loadValues(current,passed)

			{
						if(current.selectedIndex >-1)
						{
						  	var amount =document.getElementsByName("amount")[current.selectedIndex].value;
						  	var changedAmount =document.getElementsByName("changedAmount")[current.selectedIndex].value;
						  	var formulaId =document.getElementsByName("formulaId")[current.selectedIndex].value;
						  	var periodicity=document.getElementsByName("periodicity")[current.selectedIndex].value;
						  	var paymentType=document.getElementsByName("paymentType")[current.selectedIndex].value;
							if(passed==1){
								document.getElementsByName("chargeAmount")[0].value=amount;
							}
							else{
								document.getElementsByName("chargeAmount")[0].value=changedAmount;
							}
							if(periodicity!=""){
								document.getElementsByName("chargeAmount")[0].disabled=true;
							}else{
								document.getElementsByName("chargeAmount")[0].disabled=false;
							}

							var span = document.getElementById("formula");

							if( formulaId == null  && periodicity == null)
							{
								span.style.display="none";
								span.innerHTML="";
							}
							else
							{
								span.style.display="block";
								if ( formulaId != null  && periodicity != null && periodicity!="")
								{
				                    span.innerHTML = periodicity +"<br>"+formulaId;
								}
								else if ( formulaId != null  && periodicity == null || periodicity=="")
								{
									document.getElementsByName("selectedChargeFormula")[0].value=formulaId;
									span.innerHTML = formulaId;
								}
								else if ( formulaId == null  && periodicity != null)
								{
								    document.getElementsByName("selectedChargeFormula")[0].value="";
								    span.innerHTML = periodicity;
								}
							}


						}


			}

	</script>
		<html-el:form method="post" action="applyChargeAction.do?method=update" onsubmit="fun_submit()">
			<c:set value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'BusinessKey')}" var="BusinessKey" />
			<html-el:hidden property="currentFlowKey" value="${requestScope.currentFlowKey}" />
			<html-el:hidden property="selectedChargeFormula" value="" />
			<table width="95%" border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td class="bluetablehead05">
						<span class="fontnormal8pt">
							<c:choose>
									<c:when test="${BusinessKey.accountType.accountTypeId==AccountTypes.LOANACCOUNT.value}">
										<customtags:headerLink/>
									</c:when>
									<c:otherwise>
										<customtags:headerLink selfLink="false"/>
										<html-el:link styleId="applyCharges.link.viewCharges" href="customerAccountAction.do?method=load">
								          	<c:if test="${param.input == 'ViewCenterCharges'}">
	        			  						<mifos:mifoslabel name="Center.CenterCharges" bundle="CenterUIResources"/>
	          								</c:if>
	          								<c:if test="${param.input == 'ViewGroupCharges'}">
					          					<mifos:mifoslabel name="Center.GroupCharges" bundle="CenterUIResources"/>
					          				</c:if>
					          				<c:if test="${param.input == 'ViewClientCharges'}">
	          									<mifos:mifoslabel name="Center.ClientCharges" bundle="CenterUIResources"/>
	          								</c:if>
								         </html-el:link>
									</c:otherwise>
							</c:choose>
	        			</span>
	        		</td>
				</tr>
			</table>
			<table width="95%" border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td width="70%" height="24" align="left" valign="top"
						class="paddingL15T15">
					<table width="96%" border="0" cellpadding="3" cellspacing="0">
						<tr>
							<td width="70%" class="headingorange"><span class="heading">

								<c:choose>
									<c:when test="${BusinessKey.accountType.accountTypeId==AccountTypes.LOANACCOUNT.value}">
										<c:out value="${BusinessKey.loanOffering.prdOfferingName}" />&nbsp;#&nbsp;
										<c:out value="${BusinessKey.globalAccountNum}" />
									</c:when>
									<c:otherwise>
										<c:out value="${BusinessKey.customer.displayName}" />
									</c:otherwise>
								</c:choose>
								&nbsp;-&nbsp; </span>
								<mifos:mifoslabel name="accounts.apply_charges" />
							</td>
						</tr>
					</table>
					<br>
					<logic:messagesPresent>
						<table width="93%" border="0" cellpadding="3" cellspacing="0"><tr><td>
									<font class="fontnormalRedBold">
										<span id="applyCharges.error.message"><html-el:errors bundle="accountsUIResources" /></span>
									</font></td></tr>
						</table><br>
					</logic:messagesPresent>
					<table width="93%" border="0" cellpadding="3" cellspacing="0">
						<tr>
							<td width="20%" align="right" class="fontnormal"><mifos:mifoslabel
								mandatory="yes" name="accounts.sel_charge_type" /></td>
							<td width="20%"  align="left" class="fontnormal">
							<mifos:select property="chargeType" style="width:136px;" onchange="loadValues(this,1)">
								<c:forEach var="applicableCharge" items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'applicableChargeList')}" >
									<html-el:option value="${applicableCharge.feeId}">${applicableCharge.feeName}</html-el:option>
								</c:forEach>

							</mifos:select>
							</td>
							<td width="10%" align="left" class="fontnormal"><span id="applyCharges.label.amount"><mifos:mifoslabel
								name="accounts.amount_(Rs)"></mifos:mifoslabel></span></td>
							<td width="10%" class="fontnormal" align="left"><mifos:mifosdecimalinput
								styleId="applyCharges.input.amount"
								property="chargeAmount"></mifos:mifosdecimalinput></td>
							<td  width="40%" class="fontnormal" align="left">
							<SPAN id="formula"></SPAN>
							</td>
									<html-el:hidden property="amount" value=""/>
									<html-el:hidden property="formulaId" value=""/>
									<html-el:hidden property="periodicity" value=""/>
									<html-el:hidden property="paymentType" value=""/>
									<html-el:hidden property="charge" value=""/>
									<html-el:hidden property="changedAmount" value=""/>
							<c:forEach var="fee" items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'applicableChargeList')}" >
									<html-el:hidden property="amount" value="${fee.amountOrRate}"/>
									<html-el:hidden property="changedAmount" value="${fee.amountOrRate}"/>
									<html-el:hidden property="formulaId" value="${fee.formula}"/>
									<html-el:hidden property="periodicity" value="${fee.periodicity}"/>
									<html-el:hidden property="paymentType" value="${fee.paymentType}"/>
							</c:forEach>
						</tr>
					</table>
					<table width="750" border="0" cellpadding="0" cellspacing="0">
						<tr>
							<td align="center" class="blueline">&nbsp;</td>
						</tr>
					</table>
					<br>
					<table width="95%" border="0" cellspacing="0" cellpadding="1">
						<tr>


							<td align="center"><html-el:submit styleId="applyCharges.button.submit" styleClass="buttn">
								<mifos:mifoslabel name="accounts.submit"></mifos:mifoslabel>
							</html-el:submit> &nbsp;
							 <html-el:button styleId="applyCharges.button.cancel" property="btn"  styleClass="cancelbuttn"
								onclick="javascript:fun_cancel(this.form)">
								<mifos:mifoslabel name="accounts.cancel"></mifos:mifoslabel>
							</html-el:button></td>
						</tr>
					</table>
					</td>
				</tr>
			</table>
			<br>
	<html-el:hidden property="accountId" value="${BusinessKey.accountId}"/>
	<html-el:hidden property="globalAccountNum" value="${BusinessKey.globalAccountNum}"/>
	<html-el:hidden property="globalCustNum" value="${BusinessKey.customer.globalCustNum}"/>
	</html-el:form>
	<script>
		document.getElementsByName("changedAmount")[document.getElementsByName("chargeType")[0].selectedIndex].value=document.getElementsByName("chargeAmount")[0].value
		loadValues(document.getElementsByName("chargeType")[0],0)
	</script>

	</tiles:put>
</tiles:insert>
