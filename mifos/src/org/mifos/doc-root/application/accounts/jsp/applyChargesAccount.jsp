<!-- 

/**

 * vieweditoffice.jsp    version: 1.0

 

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

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html"%>
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@taglib uri="http://struts.apache.org/tags-logic" prefix="logic"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="/tags/mifos-html" prefix="mifos"%>
<%@taglib uri="http://struts.apache.org/tags-html-el" prefix="html-el"%>
<%@ taglib uri="/tags/struts-tiles" prefix="tiles"%>
<%@ taglib uri="/mifos/customtags" prefix="mifoscustom"%>

<tiles:insert definition=".clientsacclayoutsearchmenu">
	<tiles:put name="body" type="string">

		<script>
				function fun_cancel(form)
					{
						form.action="AccountsApplyChargesAction.do?method=cancel";
						form.submit();
					}
				
				function fun_submit(){
					document.getElementsByName("chargeAmount")[0].value=document.getElementsByName("selectedChargeAmount")[0].value;
				}	
					
			function loadValues(current)
			
			{		
						if(current.selectedIndex >-1)
						{
						  	var amount =document.getElementsByName("amount");
						  	var formulaId =document.getElementsByName("formulaId");
							document.getElementsByName("selectedChargeAmount")[0].value=amount[current.selectedIndex].value;
							if(document.getElementsByName("selectedChargeAmount")[0].value ==null || document.getElementsByName("selectedChargeAmount")[0].value==""){
								document.getElementsByName("selectedChargeAmount")[0].disabled=false;
							}else{
								document.getElementsByName("selectedChargeAmount")[0].disabled=true;
							}
							var index=formulaId[current.selectedIndex].value;
							
							var formulaList= document.getElementsByName("formulaplaceHolder")[0];
							var meetingStringList = document.getElementsByName("periodicity");
							
							var span = document.getElementById("formula");
							
							var meetingString = meetingStringList[current.selectedIndex].value;
							if( index == ""  && meetingString =="" ||current.selectedIndex==0)
							{	
								span.style.display="none";
								span.innerHTML="";
							}
							else
							{
								span.style.display="block";
								if ( index!="" && meetingString !="")
								{
								  var formula=  formulaList[index].text;
								  
								  span.innerHTML = "Periodicity: Recur every "+meetingString+ "" + "<br>Formula: % of "+formula;
								}
								else if ( index!="" && meetingString =="")
								{
									var formula=  formulaList[index].text;
									span.innerHTML = "Formula: % of "+formula;
								}
								else if ( index=="" && meetingString !="")
								{
								  span.innerHTML = "Periodicity: Recur every "+meetingString;
								}
							}
							
							
						}
						
				
			}
		
			function ViewDetails(){
				closedaccsearchactionform.submit();
			}

	</script>
		<html-el:form method="post" action="AccountsApplyChargesAction.do?method=create" onsubmit="fun_submit()">
			<table width="95%" border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td class="bluetablehead05"><span class="fontnormal8pt">
						<mifoscustom:getLoanHeader loanHeader='${sessionScope.header_load}'/>
							<c:choose>
								<c:when test="${param.input=='ViewClientCharges'}">
									<html-el:link href="javascript:ViewDetails()">
									    <mifos:mifoslabel name="${ConfigurationConstants.CLIENT}" />
										<mifos:mifoslabel name="loan.charges" />

									</html-el:link> 
								</c:when>
								<c:when test="${param.input=='ViewGroupCharges'}">
									<html-el:link href="javascript:ViewDetails()">
									    <mifos:mifoslabel name="${ConfigurationConstants.GROUP}" />
										<mifos:mifoslabel name="loan.charges" />

									</html-el:link> 
								</c:when>
								<c:when test="${param.input=='ViewCenterCharges'}">
									<html-el:link href="javascript:ViewDetails()">
									    <mifos:mifoslabel name="${ConfigurationConstants.CENTER}" />
										<mifos:mifoslabel name="loan.charges" />

									</html-el:link> 
								</c:when>
								<c:otherwise>
									<html-el:link href="loanAction.do?method=get&globalAccountNum=${param.globalAccountNum}">
										<c:out value="${param.prdOfferingName}" />
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
									<c:when test="${param.input=='reviewTransactionPage'}">
										<c:out value="${param.prdOfferingName}" />&nbsp;#&nbsp;
										<c:out value="${param.globalAccountNum}" />
									</c:when>
									<c:otherwise>
										<c:out value="${param.prdOfferingName}" />
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
										<html-el:errors bundle="accountsUIResources" /> 
									</font></td></tr>
						</table><br>
					</logic:messagesPresent>
					<table width="93%" border="0" cellpadding="3" cellspacing="0">
						<tr>
							<td width="20%" align="right" class="fontnormal"><mifos:mifoslabel
								name="accounts.sel_charge_type" /></td>
							<td width="20%"  align="left" class="fontnormal"><mifos:select
								property="chargeType" style="width:136px;" onchange="loadValues(this)">
								<html-el:options collection="FeeMaster" property="feeId"
									labelProperty="feeName" />
							</mifos:select></td>
							<td width="10%" align="left" class="fontnormal"><mifos:mifoslabel
								name="accounts.amount_(Rs)"></mifos:mifoslabel></td>
							<td width="10%" class="fontnormal" align="left"><mifos:mifosdecimalinput
								property="selectedChargeAmount"></mifos:mifosdecimalinput></td>
							<td  width="40%" class="fontnormal" align="left">
							<SPAN id="formula"></SPAN>
							</td>	
									<html-el:hidden property="amount" value=""/>
									<html-el:hidden property="formulaId" value=""/>									
									<html-el:hidden property="periodicity" value=""/>	
							<c:forEach var="fee" items="${requestScope.FeeMaster}" >
									<html-el:hidden property="amount" value="${fee.rateOrAmount}"/>
									<html-el:hidden property="formulaId" value="${fee.formulaId}"/>
									<html-el:hidden property="periodicity" value="${fee.periodicity}"/>										
							</c:forEach>
					   <c:set var="formulaList" scope="request" value="${requestScope.formula.lookUpMaster}" />
						<mifos:select property="formulaplaceHolder" style="width:136px;display:none" >
					    	<html-el:options  style="{display:none}" property="id"
											labelProperty="lookUpValue"
											collection="formulaList" />
							</mifos:select>
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


							<td align="center"><html-el:submit styleClass="buttn"
								style="width:65px;">
								<mifos:mifoslabel name="accounts.submit"></mifos:mifoslabel>
							</html-el:submit> &nbsp; <html-el:cancel styleClass="cancelbuttn"
								style="width:65px;" onclick="javascript:fun_cancel(this.form)">
								<mifos:mifoslabel name="accounts.cancel"></mifos:mifoslabel>
							</html-el:cancel></td>
						</tr>
					</table>
					</td>
				</tr>
			</table>
			<br>
<html-el:hidden property="searchNode(search_name)" value="ClientChargesDetails"/>
<html-el:hidden property="prdOfferingName" value="${param.prdOfferingName}"/> 
<html-el:hidden property="globalAccountNum" value="${param.globalAccountNum}"/> 
<html-el:hidden property="accountId" value="${param.accountId}"/> 
<html-el:hidden property="accountType" value="${param.accountType}"/> 	
<html-el:hidden property="input" value="${param.input}"/> 		
<html-el:hidden property="chargeAmount" value="0"/>
<html-el:hidden property="statusId" value="${param.statusId}"/>  
</html-el:form>

<html-el:form  action="closedaccsearchaction.do?method=search">
<html-el:hidden property="searchNode(search_name)" value="ClientChargesDetails"/>
<html-el:hidden property="prdOfferingName" value="${param.prdOfferingName}"/> 
<html-el:hidden property="globalAccountNum" value="${param.globalAccountNum}"/> 
<html-el:hidden property="accountId" value="${param.accountId}"/> 
<html-el:hidden property="accountType" value="${param.accountType}"/> 
<html-el:hidden property="input" value="${param.input}"/> 
<html-el:hidden property="statusId" value="${param.statusId}"/> 
</html-el:form>

	</tiles:put>
</tiles:insert>
