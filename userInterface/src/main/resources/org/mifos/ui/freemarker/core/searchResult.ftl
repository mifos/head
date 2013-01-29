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
[#include "layout.ftl"]
[@clientLeftPane "ClientsAndAccounts"]
[#if customerHierarchy??]
	[@widget.mainCustomerSearchResultDataTable customerHierarchy.size?c /]
[#else]
	[@widget.mainCustomerSearchResultDataTable 0 /]
[/#if]
<script>
	$(document).ready(function(){
		var searchString = $('input[name="searchString"]').val();
		var searchResultTable = $('#mainCustomerSearchResultDataTable').dataTable();
		
		$('input[name="searchString"]').keyup(function(){
		    if ( searchString.length > 0 ){
		    	searchResultTable.fnFilter("");
		    }   
		});
		$('select[name="officeId"]').change(function(){
		    searchResultTable.fnFilter("");  
		});
	});
</script>
<span id="page.id" title="MainSearchResults"></span>
<div class="content">
	<div class="fontnormal">
		<form method="GET" action="searchResult.ftl">
			[@form.showAllErrors "customerSearch.*"/]
			[@spring.message "CustomerSearch.searchFor" /]:
			[@spring.bind "customerSearch.searchString" /]
			<input type="text" name="${spring.status.expression}" value="${spring.status.value?if_exists}" maxlength="200"/>
			[@form.singleSelectWithNested "customerSearch.officeId", customerSearch.offices ]
				<option value="0">
					[@spring.message "CustomerSearch.all" /]&nbsp; 
					[@spring.message "${ConfigurationConstants.BRANCHOFFICE}" /]
					[@spring.message "CustomerSearch.s" /]
				</option>
			[/@form.singleSelectWithNested]
			<input type="submit" value="Search" class="buttn"/>
		</form>
	</div>
	<div>
		<img src="pages/framework/images/trans.gif" width="15" height="15">
	</div>
	<div class="search-results">
		<table id="mainCustomerSearchResultDataTable" class="datatable">
			<thead>
				<tr>
					<th style="display: none;">Index</th>
					<th style="display: none;">Customer</th>
				</tr>
			</thead>
			[#if customerHierarchy?has_content]
			<tbody>
				
				[#assign index = startIndex /]
				[#list customerHierarchy.clients as client]
					<tr>
						<td>
							[#assign index = index + 1 /]
							${index}.
						</td>
						<td>
							<div>
								[@spring.message '${ConfigurationConstants.CLIENT}' /]
								<span class='headingblue' style='font-size: 12px;'>
									<a href='viewClientDetails.ftl?globalCustNum=${client.clientGlobalCustNum}'>
										${client.clientName}[@spring.message 'label.colon' /] [@spring.message 'ID' /] ${client.clientGlobalCustNum}
									</a>
								</span>
							</div>
							<div>
								<span class='fontnormalbold'>
									[@spring.message 'accounts.status' /]
								</span>
								<span>
									[@mifostag.MifosImage id='${client.customerStatusId}' moduleName='org.mifos.customers.util.resources.customerImages' /]
								</span>
								<span class='fontnormalbold'>
									[@spring.message 'client.LoanOff' /]
								</span>
								<span>
									${client.loanOfficerName?if_exists}
								</span>
							</div>
							[#if client.savingsGlobalAccountNum?has_content]
							<div>
								<span class='fontnormalbold'>
									[@spring.message '${ConfigurationConstants.SAVINGS}.Savings' /]:
								</span>
								<span>
								[#list client.savingsGlobalAccountNum as saving]
									<span>
										<a href='viewSavingsAccountDetails.ftl?globalAccountNum=${saving}&recordOfficeId=${client.branchId}&recordLoanOfficerId=${client.loanOfficerId?if_exists}&randomNUm=${Session.randomNUm?if_exists?c}'>
										[@mifostag.MifosImage id='${client.savingsGlobalAccountStateIds[saving]}' moduleName='org.mifos.accounts.util.resources.accountsImages' /]Account # ${saving}</a>
									</span>
								[/#list]
								</span>
							</div>
							[/#if]
							[#if client.loanGlobalAccountNum?has_content]
							<div>
								<span class='fontnormalbold'>
									[@spring.message '${ConfigurationConstants.LOAN}' /]: 
								</span>
								<span>
								[#list client.loanGlobalAccountNum as loan]
									<span>
										<a href='viewLoanAccountDetails.ftl?globalAccountNum=${loan}&recordOfficeId=${client.branchId}&recordLoanOfficerId=${client.loanOfficerId?if_exists}&randomNUm=${Session.randomNUm?if_exists?c}'>
										[@mifostag.MifosImage id='${client.loanGlobalAccountStateIds[loan]}' moduleName='org.mifos.accounts.util.resources.accountsImages' /]Account # ${loan}</a>
									</span>
								[/#list]
								</span>
							</div>
							[/#if]
							[#if client.groupLoanGlobalAccountNum?has_content]
							<div>
								<span class='fontnormalbold'>
									[@spring.message '${ConfigurationConstants.GROUP}' /] [@spring.message '${ConfigurationConstants.LOAN}' /]: 
								</span>
								<span>
								[#list client.groupLoanGlobalAccountNum as gLoan]
									<span>
									
										<a href='viewLoanAccountDetails.ftl?globalAccountNum=${gLoan}&recordOfficeId=${client.branchId}&recordLoanOfficerId=${client.loanOfficerId?if_exists}&randomNUm=${Session.randomNUm?if_exists?c}'>
										[@mifostag.MifosImage id='${client.groupLoanGlobalAccountStateIds[gLoan]}' moduleName='org.mifos.accounts.util.resources.accountsImages' /]Account # ${gLoan}</a>
									</span>
								[/#list]
								</span>
							</div>
							[/#if]
							<div>
								<span>
									[@spring.message 'alsosee' /]:
								</span>
								<span>
									<a href='clientsAndAccounts.ftl?officeId=${client.branchId}&randomNUm=${Session.randomNUm?if_exists?c}'>
										${client.branchName}
									</a>
									[#if client.centerGlobalCustNum?has_content]
									 /
									<a href='viewCenterDetails.ftl?globalCustNum=${client.centerGlobalCustNum}&recordOfficeId=${client.branchId}&recordLoanOfficerId=${client.loanOfficerId?if_exists}&randomNUm=${Session.randomNUm?if_exists?c}'>
										${client.centerName}
									</a>
									[/#if]
									[#if client.groupGlobalCustNum?has_content]
									 /
									<a href='viewGroupDetails.ftl?globalCustNum=${client.groupGlobalCustNum}&recordOfficeId=${client.branchId}&recordLoanOfficerId=${client.loanOfficerId?if_exists}&randomNUm=${Session.randomNUm?if_exists?c}'>
										${client.groupName}
									</a>
									[/#if]
								</span>
							</div>
						</td>
					</tr>
				[/#list]
				[#list customerHierarchy.groups as group]
					<tr>
						<td>
							[#assign index = index + 1 /]
							${index}.
						</td>
						<td>
							<div>
								[@spring.message '${ConfigurationConstants.GROUP}' /]
								<span class='headingblue' style='font-size: 12px;'>
								
									<a href='viewGroupDetails.ftl?globalCustNum=${group.groupGlobalCustNum}&recordOfficeId=${group.branchId}&recordLoanOfficerId=${group.loanOfficerId?if_exists}&randomNUm=${Session.randomNUm?if_exists?c}'>
										${group.groupName}[@spring.message 'label.colon' /] [@spring.message 'ID' /] ${group.groupGlobalCustNum}
									</a>
								</span>
							</div>
							<div>
								<span class='fontnormalbold'>
									[@spring.message 'accounts.status' /]
								</span>
								<span>
									[@mifostag.MifosImage id='${group.customerStatusId}' moduleName='org.mifos.customers.util.resources.customerImages' /]
								</span>
								<span class='fontnormalbold'>
									[@spring.message 'client.LoanOff' /]
								</span>
								<span>
									${group.loanOfficerName?if_exists}
								</span>
							</div>
							[#if group.savingsGlobalAccountNum?has_content]
							<div>
								<span class='fontnormalbold'>
									[@spring.message '${ConfigurationConstants.SAVINGS}.Savings' /]:
								</span>
								<span>
								[#list group.savingsGlobalAccountNum as saving]
									<span>
										<a href='viewSavingsAccountDetails.ftl?globalAccountNum=${saving}&method=get&recordOfficeId=${group.branchId}&recordLoanOfficerId=${group.loanOfficerId?if_exists}&randomNUm=${Session.randomNUm?if_exists?c}'>
										[@mifostag.MifosImage id='${group.savingsGlobalAccountStateIds[saving]}' moduleName='org.mifos.accounts.util.resources.accountsImages' /]Account # ${saving}</a>
									</span>
								[/#list]
								</span>
							</div>
							[/#if]
							[#if group.loanGlobalAccountNum?has_content]
							<div>
								<span class='fontnormalbold'>
									[@spring.message '${ConfigurationConstants.LOAN}' /]:
								</span>
								<span>
								[#list group.loanGlobalAccountNum as loan]
									<span>
										<a href='viewLoanAccountDetails.ftl?globalAccountNum=${loan}&recordOfficeId=${group.branchId}&recordLoanOfficerId=${group.loanOfficerId?if_exists}&randomNUm=${Session.randomNUm?if_exists?c}'>
										[@mifostag.MifosImage id='${group.loanGlobalAccountStateIds[loan]}' moduleName='org.mifos.accounts.util.resources.accountsImages' /]Account # ${loan}</a>
									</span>
								[/#list]
								</span>
							</div>
							[/#if]
							[#if group.groupLoanGlobalAccountNum?has_content]
							<div>
								<span class='fontnormalbold'>
									[@spring.message '${ConfigurationConstants.GROUP}' /] [@spring.message '${ConfigurationConstants.LOAN}' /]:  
								</span>
								<span>
								[#list group.groupLoanGlobalAccountNum as gLoan]
									<span>
									<a href='viewLoanAccountDetails.ftl?globalAccountNum=${gLoan}&recordOfficeId=${group.branchId}&recordLoanOfficerId=${group.loanOfficerId?if_exists}&randomNUm=${Session.randomNUm?if_exists?c}'>
										[@mifostag.MifosImage id='${group.groupLoanGlobalAccountStateIds[gLoan]}' moduleName='org.mifos.accounts.util.resources.accountsImages' /]Account # ${gLoan}</a>
									</span>
								[/#list]
								</span>
							</div>
							[/#if]
							<div>
								<span>
									[@spring.message 'alsosee' /]:
								</span>
								<span>
									<a href='clientsAndAccounts.ftl?officeId=${group.branchId}&randomNUm=${Session.randomNUm?if_exists?c}'>
										${group.branchName}
									</a> 
									[#if group.centerGlobalCustNum?has_content]
									 /
									<a href='viewCenterDetails.ftl?globalCustNum=${group.centerGlobalCustNum}&recordOfficeId=${group.branchId}&recordLoanOfficerId=${group.loanOfficerId?if_exists}&randomNUm=${Session.randomNUm?if_exists?c}'>
										${group.centerName}
									</a>
									[/#if]
								</span>
							</div>
						</td>
					</tr>
				[/#list]
				[#list customerHierarchy.centers as center]
					<tr>
						<td>
							[#assign index = index + 1 /]
							${index}.
						</td>
						<td>
							<div>
								[@spring.message '${ConfigurationConstants.CENTER}' /]
								<span class='headingblue' style='font-size: 12px;'>
									<a href='viewCenterDetails.ftl?globalCustNum=${center.centerGlobalCustNum}&recordOfficeId=${center.branchId}&
											 recordLoanOfficerId=${center.loanOfficerId?if_exists}&randomnum=${Session.randomNUm?if_exists?c}'>
										${center.centerName}[@spring.message 'label.colon' /] [@spring.message 'ID' /] ${center.centerGlobalCustNum}
									</a>
								</span>
							</div>
							<div>
								<span class='fontnormalbold'>
									[@spring.message 'accounts.status' /]
								</span>
								<span>
									[@mifostag.MifosImage id='${center.customerStatusId}' moduleName='org.mifos.customers.util.resources.customerImages' /]
								</span>
								<span class='fontnormalbold'>
									[@spring.message 'client.LoanOff' /]
								</span>
								<span>
									${center.loanOfficerName?if_exists}
								</span>
							</div>
							[#if center.savingsGlobalAccountNum?has_content]
							<div>
								<span class='fontnormalbold'>
									[@spring.message '${ConfigurationConstants.SAVINGS}.Savings' /]:
								</span>
								<span>
								[#list center.savingsGlobalAccountNum as saving]
									<span>
										<a href='viewSavingsAccountDetails.ftl?globalAccountNum=${saving}&recordOfficeId=${center.branchId}&recordLoanOfficerId=${center.loanOfficerId?if_exists}&randomNUm=${Session.randomNUm?if_exists?c}'>
										[@mifostag.MifosImage id='${center.savingsGlobalAccountStateIds[saving]}' moduleName='org.mifos.accounts.util.resources.accountsImages' /]Account # ${saving}</a>
									</span>
								[/#list]
								</span>
							</div>
							[/#if]
							<div>
								<span>
									[@spring.message 'alsosee' /]:
								</span>
								<span>
									<a href='clientsAndAccounts.ftl?officeId=${center.branchId}&randomNUm=${Session.randomNUm?if_exists?c}'>
										${center.branchName}
									</a>
								</span>
							</div>
						</td>
					</tr>
				[/#list]
				[#if customerHierarchy.loan?has_content]
					<tr>
						<td>
							[#assign loan = customerHierarchy.loan /]
							[#assign index = index + 1 /]
							${index}.
						</td>
						<td>
							<div>
								<span>
									[@spring.message '${ConfigurationConstants.LOAN}' /]
								</span>
								<span class='headingblue' style='font-size: 12px;'>
								<!-- client new glim -->
								[#if loan.accountStatusId == 5 && loan.clientGlobalCustNum?has_content]
									<a href='groupIndividualLoanAccountAction.do?method=get&globalAccountNum=${loan.loanGlobalAccountNum}&recordOfficeId=${loan.branchId}&
										 recordLoanOfficerId=${loan.loanOfficerId?if_exists}&randomnum=${Session.randomNUm?if_exists?c}'>
									Account # ${loan.loanGlobalAccountNum}
								</a>
								<!-- group new glim-->
								[#elseif loan.accountStatusId == 5 && !loan.clientGlobalCustNum?has_content]
								<a href='viewLoanAccountDetails.ftl?globalAccountNum=${loan.loanGlobalAccountNum}&recordOfficeId=${loan.branchId}&
										 recordLoanOfficerId=${loan.loanOfficerId?if_exists}&randomnum=${Session.randomNUm?if_exists?c}'>
									Account # ${loan.loanGlobalAccountNum}
								</a>
								[#else]
								<!-- old accounts-->
								<a href='viewLoanAccountDetails.ftl?globalAccountNum=${loan.loanGlobalAccountNum}&recordOfficeId=${loan.branchId}&
										 recordLoanOfficerId=${loan.loanOfficerId?if_exists}&randomnum=${Session.randomNUm?if_exists?c}'>
									Account # ${loan.loanGlobalAccountNum}
								</a>
								[/#if]
								</span>
							</div>
							<div>
								<span class='fontnormalbold'>
									[@spring.message 'accounts.status' /]
								</span>
								<span>
									[@mifostag.MifosImage id='${loan.accountStatusId}' moduleName='org.mifos.accounts.util.resources.accountsImages' /]
								</span>
								<span class='fontnormalbold'>
									[@spring.message 'client.LoanOff' /]
								</span>
								<span>
									${loan.loanOfficerName?if_exists}
								</span>
							</div>
							<div>
								[#if loan.clientGlobalCustNum?has_content]
								<span class='fontnormal'>
									[@spring.message '${ConfigurationConstants.CLIENT}' /]
								</span>
								<span>
									<a href='viewClientDetails.ftl?globalCustNum=${loan.clientGlobalCustNum}&recordOfficeId=${loan.branchId}&recordLoanOfficerId=${loan.loanOfficerId?if_exists}&randomNUm=${Session.randomNUm?if_exists?c}'>
										${loan.clientName}: ${loan.clientGlobalCustNum}
									</a>
								</span>
								[#elseif loan.groupGlobalCustNum?has_content]
								<span class='fontnormal'>
									[@spring.message '${ConfigurationConstants.GROUP}' /]
								</span>
								<span>
									<a href='viewGroupDetails.ftl?globalCustNum=${loan.groupGlobalCustNum}&recordOfficeId=${loan.branchId}&recordLoanOfficerId=${loan.loanOfficerId?if_exists}&randomNUm=${Session.randomNUm?if_exists?c}'>
										${loan.groupName}: ${loan.groupGlobalCustNum}
									</a>
								</span>
								[/#if]
							</div>
							<div>
								<span>
									[@spring.message 'alsosee' /]:
								</span>
								<span>
									<a href='clientsAndAccounts.ftl?officeId=${loan.branchId}&randomNUm=${Session.randomNUm?if_exists?c}'>
										${loan.branchName}
									</a>
								</span>
								[#if loan.centerGlobalCustNum?has_content]
								 /
								<a href='viewCenterDetails.ftl?globalCustNum=${loan.centerGlobalCustNum}&recordOfficeId=${loan.branchId}&recordLoanOfficerId=${loan.loanOfficerId?if_exists}&randomNUm=${Session.randomNUm?if_exists?c}'>
									${loan.centerName}
								</a>
								[/#if]
								[#if loan.groupGlobalCustNum?has_content && loan.clientGlobalCustNum?has_content]
								 /
								<a href='viewGroupDetails.ftl?globalCustNum=${loan.groupGlobalCustNum}&recordOfficeId=${loan.branchId}&recordLoanOfficerId=${loan.loanOfficerId?if_exists}&randomNUm=${Session.randomNUm?if_exists?c}'>
									${loan.groupName}
								</a>
								[/#if]
							</div>					
						</td>
					</tr>
				[/#if]
				[#if customerHierarchy.savings?has_content]
					<tr>
						<td>
							[#assign savings = customerHierarchy.savings /]
							[#assign index = index + 1 /]
							${index}.
						</td>
						<td>
							<div>
								<span>
									[@spring.message '${ConfigurationConstants.SAVINGS}.Savings' /]
								</span>
								<span class='headingblue' style='font-size: 12px;'>
									<a href='viewSavingsAccountDetails.ftl?globalAccountNum=${savings.savingsGlobalAccountNum}&recordOfficeId=${savings.branchId}&
											 recordLoanOfficerId=${savings.loanOfficerId?if_exists}&randomnum=${Session.randomNUm?if_exists?c}'>
										Account # ${savings.savingsGlobalAccountNum}
									</a>
								</span>
							</div>
							<div>
								<span class='fontnormalbold'>
									[@spring.message 'accounts.status' /]
								</span>
								<span>
									[@mifostag.MifosImage id='${savings.accountStatusId}' moduleName='org.mifos.accounts.util.resources.accountsImages' /]
								</span>
								<span class='fontnormalbold'>
									[@spring.message 'client.LoanOff' /]
								</span>
								<span>
									${savings.loanOfficerName?if_exists}
								</span>
							</div>
							<div>
								[#if savings.clientGlobalCustNum?has_content]
								<span class='fontnormal'>
									[@spring.message '${ConfigurationConstants.CLIENT}' /]:
								</span>
								<span>
									<a href='viewClientDetails.ftl?globalCustNum=${savings.clientGlobalCustNum}&recordOfficeId=${savings.branchId}&recordLoanOfficerId=${savings.loanOfficerId?if_exists}&randomNUm=${Session.randomNUm?if_exists?c}'>
										${savings.clientName}: ${savings.clientGlobalCustNum}
									</a>
								</span>
								[#elseif savings.groupGlobalCustNum?has_content]
								<span class='fontnormal'>
									[@spring.message '${ConfigurationConstants.GROUP}' /]:
								</span>
								<span>
									<a href='viewGroupDetails.ftl?globalCustNum=${savings.groupGlobalCustNum}&recordOfficeId=${savings.branchId}&recordLoanOfficerId=${savings.loanOfficerId?if_exists}&randomNUm=${Session.randomNUm?if_exists?c}'>
										${savings.groupName}: ${savings.groupGlobalCustNum}
									</a>
								</span>
								[#elseif savings.centerGlobalCustNum?has_content]
								<span class='fontnormal'>
									[@spring.message '${ConfigurationConstants.CENTER}' /]:
								</span>
								<span>
									<a href='viewCenterDetails.ftl?globalCustNum=${savings.centerGlobalCustNum}&recordOfficeId=${savings.branchId}&recordLoanOfficerId=${savings.loanOfficerId?if_exists}&randomNUm=${Session.randomNUm?if_exists?c}'>
										${savings.centerName}: ${savings.centerGlobalCustNum}
									</a>
								</span>
								[/#if]
							</div>
							<div>
								<span>
									[@spring.message 'alsosee' /]:
								</span>
								<span>
									<a href='clientsAndAccounts.ftl?officeId=${savings.branchId}&randomNUm=${Session.randomNUm?if_exists?c}'>
										${savings.branchName}
									</a>
								</span>
								[#if savings.centerGlobalCustNum?has_content && savings.groupGlobalCustNum?has_content]
								 /
								<a href='viewCenterDetails.ftl?globalCustNum=${savings.centerGlobalCustNum}&recordOfficeId=${savings.branchId}&recordLoanOfficerId=${savings.loanOfficerId?if_exists}&randomNUm=${Session.randomNUm?if_exists?c}'>
									${savings.centerName}
								</a>
								[/#if]
								[#if savings.groupGlobalCustNum?has_content && savings.clientGlobalCustNum?has_content]
								 /
								<a href='viewGroupDetails.ftl?globalCustNum=${savings.groupGlobalCustNum}&recordOfficeId=${savings.branchId}&recordLoanOfficerId=${savings.loanOfficerId?if_exists}&randomNUm=${Session.randomNUm?if_exists?c}'>
									${savings.groupName}
								</a>
								[/#if]
							</div>
						</td>
					</tr>	
				[/#if]
			</tbody>
			[/#if]
		</table>
	</div>
</div>
[/@clientLeftPane]
