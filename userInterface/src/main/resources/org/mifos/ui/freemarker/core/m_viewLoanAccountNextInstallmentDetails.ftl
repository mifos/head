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

[@widget.topNavigationNoSecurityMobile currentTab="ClientsAndAccounts" /]

<span id="page.id" title="NextPaymentLoanAccount"></span>

<div class="content" style="width: 350px">
	<div>
		<span class="heading">
			${loanInformationDto.prdOfferingName} #&nbsp;${loanInformationDto.globalAccountNum} -
		</span>
		<span class="headingorange">
			[@spring.message "loan.next_install_details" /]
		</span>
	</div>
	<div>
		<table id="loanInstallmentTable" width="60%" border="0" cellpadding="3" cellspacing="0">
			<tr class="drawtablerowboldnoline">
				<td width="60%">&nbsp;</td>
				<td width="27%" align="right">
				[@spring.message "loan.amount" /]
				</td>							
				<td width="13%" align="right">&nbsp;</td>
			</tr>
			<tr>
                <td class="drawtablerowbold">
                [@spring.message "loan.current_installment" /]
                </td>
                <td align="right" class="drawtablerow">&nbsp;</td>
                <td align="right" class="drawtablerow">&nbsp;</td>
              </tr>
              
			<tr>
				<td class="drawtablerow">
					[@spring.message "loan.principal" /]
				</td>
				<td align="right" class="drawtablerow">
					${loanInstallmentDetailsDto.upcomingInstallmentDetails.principal?if_exists?number}
				</td>
				<td align="right" class="drawtablerow">&nbsp;</td>
			</tr>
			 
			<tr>
				<td class="drawtablerow">
				[@spring.message "${ConfigurationConstants.INTEREST}" /]
				</td>
				<td align="right" class="drawtablerow">${loanInstallmentDetailsDto.upcomingInstallmentDetails.interest?if_exists?number}</td>
				<td align="right" class="drawtablerow">&nbsp;</td>
			</tr>
			<tr>
				<td class="drawtablerow">[@spring.message "loan.fees" /]</td>
				<td align="right" class="drawtablerow">	${loanInstallmentDetailsDto.upcomingInstallmentDetails.fees?if_exists?number}</td>
				<td align="right" class="drawtablerow">	&nbsp;
				[#if loanInstallmentDetailsDto.upcomingInstallmentDetails.fees != '0.0' ]					 
				<a id="nextPayment_loanAccount.link.waiveFeeDue" href="loanAccountAction.do?method=waiveChargeDue&prdOfferingName=${loanInformationDto.prdOfferingName}&accountId=${loanInformationDto.accountId?c}&WaiveType=fees&type=LoanAccount&input=LoanAccount&globalAccountNum=${loanInformationDto.globalAccountNum}&randomNUm=${Session.randomNUm?c}&currentFlowKey=${Request.currentFlowKey}">
					[@spring.message "loan.waive" /]
				</a>
				[/#if]
				</td>
			</tr>
			<tr>
				<td class="drawtablerow">[@spring.message "loan.penalty" /]</td>
				<td align="right" class="drawtablerow">${loanInstallmentDetailsDto.upcomingInstallmentDetails.penalty?if_exists?number}</td>
				<td align="right" class="drawtablerow">&nbsp;
				[#if loanInstallmentDetailsDto.upcomingInstallmentDetails.penalty != '0.0' ]						 
				<a id="nextPayment_loanAccount.link.waivePenaltyDue" href="loanAccountAction.do?method=waiveChargeDue&accountId=${loanInformationDto.accountId?c}&WaiveType=penalty&type=LoanAccount&input=LoanAccount&globalAccountNum=${loanInformationDto.globalAccountNum}&prdOfferingName=${loanInformationDto.prdOfferingName}&randomNUm=${Session.randomNUm?c}&currentFlowKey=${Request.currentFlowKey}">
					[@spring.message "loan.waive" bundle="loanUIResources" /]
				</a>
				[/#if]
				</td>
			</tr>
			<tr>
				<td class="drawtablerow">
				<em>
				[@spring.message "loan.subTotal" /]
				</em>
				</td>
				<td align="right" class="drawtablerow">
					${loanInstallmentDetailsDto.upcomingInstallmentDetails.subTotal?if_exists?number}
				</td>
				<td align="right" class="drawtablerow">&nbsp;</td>
			</tr>						
								
			<tr>
				<td class="drawtablerowbold">[@spring.message "loan.overdueamount" /]</td>
				<td align="right" class="drawtablerow">
				&nbsp;
				</td>
				<td align="right" class="drawtablerow">&nbsp;</td>
			</tr>						
			
			<tr>
				<td class="drawtablerow">[@spring.message "loan.principal"/]</td>
				<td align="right" class="drawtablerow">${loanInstallmentDetailsDto.overDueInstallmentDetails.principal?if_exists?number}</td>
				<td align="right" class="drawtablerow">&nbsp;</td>
			</tr>
			<tr>
				<td class="drawtablerow">[@spring.message "${ConfigurationConstants.INTEREST}" /]</td>
				<td align="right" class="drawtablerow">${loanInstallmentDetailsDto.overDueInstallmentDetails.interest?if_exists?number}</td>
				<td align="right" class="drawtablerow">&nbsp;</td>
			</tr>
			<tr>
				<td class="drawtablerow">
				[@spring.message "loan.fees" /]</td>
				<td align="right" class="drawtablerow">${loanInstallmentDetailsDto.overDueInstallmentDetails.fees?if_exists?number}</td>
				<td align="right" class="drawtablerow">&nbsp;
				[#if loanInstallmentDetailsDto.overDueInstallmentDetails.fees != '0.0' ]	
				<a id="nextPayment_loanAccount.link.waiveFeeOverDue" href="loanAccountAction.do?method=waiveChargeOverDue&accountId=${loanInformationDto.accountId?c}&WaiveType=fees&type=LoanAccount&input=LoanAccount&globalAccountNum=${loanInformationDto.globalAccountNum}&prdOfferingName=${loanInformationDto.prdOfferingName}&randomNUm=${Session.randomNUm?c}&currentFlowKey=${Request.currentFlowKey}">
					[@spring.message "loan.waive" /]
				</a>
				[/#if]
				</td>
			</tr>
			<tr>
				<td class="drawtablerow">
				[@spring.message "loan.penalty" /]</td>
				<td align="right" class="drawtablerow">${loanInstallmentDetailsDto.overDueInstallmentDetails.penalty?if_exists?number}</td>
				<td align="right" class="drawtablerow">&nbsp;
				[#if loanInstallmentDetailsDto.overDueInstallmentDetails.penalty != '0.0' ]
				<a id="nextPayment_loanAccount.link.waivePenaltyOverDue" href="loanAccountAction.do?method=waiveChargeOverDue&accountId=${loanInformationDto.accountId?c}&WaiveType=penalty&type=LoanAccount&input=LoanAccount&globalAccountNum=${loanInformationDto.globalAccountNum}&prdOfferingName=${loanInformationDto.prdOfferingName}&randomNUm=${Session.randomNUm?c}&currentFlowKey=${Request.currentFlowKey}">
					[@spring.message "loan.waive" /]
				</a>
				[/#if]
				</td>
			</tr>
			
			<tr>
				<td class="drawtablerow">
				<em>
				[@spring.message "loan.subTotal" /]
				</em>
				</td>
				<td align="right" class="drawtablerow">
					${loanInstallmentDetailsDto.overDueInstallmentDetails.subTotal?if_exists?number}
				</td>
				<td align="right" class="drawtablerow">&nbsp;</td>
			</tr>	
			
			<tr>
				<td class="drawtablerowbold">
					[@spring.message "loan.totalDueOn" /]
					${i18n.date_formatter(loanInstallmentDetailsDto.nextMeetingDate, "dd/MM/yyyy", Application.LocaleSetting.locale)}
				</td>
				<td align="right" class="drawtablerow">&nbsp;
					${loanInstallmentDetailsDto.totalAmountDue}
				</td>
				
				<td align="right" class="drawtablerow">&nbsp;</td>
			</tr>
			<tr>
				<td colspan="3">&nbsp;</td>
			</tr>
		</table>
	</div>
	<div>
		<form action="viewLoanAccountDetails.ftl" method="get">
			 <input type="submit" value="[@spring.message "loan.returnToAccountDetails"/]" class="buttn" id="nextPayment_loanAccount.button.back" />
			 <input type="hidden" name="globalAccountNum" value="${loanInformationDto.globalAccountNum}"/>	
		</form>
	</div>
</div>