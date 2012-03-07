[#ftl]
[#--
* Copyright (c) 2005-2011 Grameen Foundation USA
*  All rights reserved.
*
*  Licensed under the Apache License, Version 2.0 (the "License");
*  you may not use this file except in compliance with the License.
*  You may obtain a copy of the License at
*
*      http://www.apache.org/licenses/LICENSE-2.0
*
*  Unless required by applicable law or agreed to in writing, software
*  distributed under the License is distributed on an "AS IS" BASIS,
*  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
*  implied. See the License for the specific language governing
*  permissions and limitations under the License.
*
*  See also http://www.apache.org/licenses/LICENSE-2.0.html for an
*  explanation of the license and how it is applied.
--]
[@layout.header "mifos" /]
[#assign savingsoverdue=JspTaglibs["/WEB-INF/tlds/application/savings/savingsoverdueamounttag.tld"]]

[@widget.topNavigationNoSecurityMobile currentTab="ClientsAndAccounts" /]

<span id="page.id" title="savingsaccountdepositduedetails"></span>

<div class="content" style="width: 350px">
	<div>
		<span class="heading">
			${savingsAccountDetailDto.productDetails.productDetails.name} #&nbsp;${savingsAccountDetailDto.globalAccountNum} -
		</span>
		<span class="headingorange">
			[@spring.message "Savings.depositduedetails"/]
		</span>
	</div>
	<div>
		<table id="DepositDueDetailsTable" width="60%" border="0" cellpadding="3" cellspacing="0">
			<tr class="drawtablerowboldnoline">
				<td width="60%">
					&nbsp;
				</td>
				<td width="27%" align="right">
					[@spring.message "Savings.amount" /]
				</td>
				<td width="13%" align="right">
					&nbsp;
				</td>
			</tr>
			<tr>
				<td class="drawtablerow">
					[@spring.message "Savings.nextdeposit" /]
				</td>
				<td align="right" class="drawtablerow">
					${savingsAccountDetailDto.totalAmountDueForNextInstallment?number}
				</td>
				<td align="right" class="drawtablerow">
					[#if savingsAccountDetailDto.totalAmountDueForNextInstallment?number > 0]
						<a href="savingsAction.do?method=waiveAmountDue&currentFlowKey=${Request.currentFlowKey}&randomNUm=${Session.randomNUm?c}">
							[@spring.message "Savings.waive" /]
						</a>
					[#else]
						&nbsp;
					[/#if]
				</td>
			</tr>
			<tr>
				<td class="drawtablerow">
					[@spring.message "Savings.pastdepositsdue" /]
				</td>
				<td align="right" class="drawtablerow">
					&nbsp;
				</td>
				<td align="right" class="drawtablerow">
					&nbsp;
				</td>
			</tr>
			[@savingsoverdue.savingsoverdueamount /]
			<tr>
				<td class="drawtablerow">
					<em>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; [@spring.message "Savings.subtotal" /]</em>
				</td>
				<td align="right" class="drawtablerow">
					${savingsAccountDetailDto.totalAmountInArrears?number}
				</td>
				<td align="right" class="drawtablerow">
					[#if savingsAccountDetailDto.totalAmountInArrears?number > 0 ] 
							<a href="savingsAction.do?method=waiveAmountOverDue&currentFlowKey=${Request.currentFlowKey}&randomNUm=${session.randomNUm}">
								[@spring.message "Savings.waive" /]
							</a>
					[#else]
						&nbsp;
					[/#if]
				</td>
			</tr>
			<tr>
				<td class="drawtablerowbold">
					[@spring.message "Savings.totalAmountDue" /]
					[#if savingsAccountDetailDto.dueDate?has_content ]										
						[@spring.message "Savings.on" /]
						${i18n.date_formatter(savingsAccountDetailDto.dueDate, "dd/MM/yyyy", Application.LocaleSetting.locale)}
					[/#if]
				</td>
				<td align="right" class="drawtablerow">
					${savingsAccountDetailDto.totalAmountDue?number}
				</td>
				<td align="right" class="drawtablerow">
					&nbsp;
				</td>
			</tr>
			<tr>
				<td colspan="3">
					&nbsp;
				</td>
			</tr>
		</table>
	</div>
	<div>
		<form action="viewSavingsAccountDetails.ftl" method="get">
			 <input type="submit" value="[@spring.message "Savings.btnreturntodetails"/]" class="buttn" />
			 <input type="hidden" name="globalAccountNum" value="${savingsAccountDetailDto.globalAccountNum}"/>	
		</form>
	</div>
</div>