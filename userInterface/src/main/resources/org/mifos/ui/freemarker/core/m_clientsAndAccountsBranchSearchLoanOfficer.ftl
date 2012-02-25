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
<span id="page.id" title="BranchSearchLoanofficer"></span>
<div class="content" style="width: 350px">
<div>
		<span>[@form.showAllErrors "customerSearch.*"/]<span>
		<span class="headingorange"> ${customerSearch.officeName}</span><br/>
		<span class="fontnormalbold"> 
			[@spring.message "CustomerSearch.revieweditinstruction1" /] 
			[@spring.message "${ConfigurationConstants.CLIENT}" /],
            [@spring.message "${ConfigurationConstants.GROUP}" /]
            [#if isCenterHierarchyExists ]
                , [@spring.message "${ConfigurationConstants.CENTER}" /]
            [/#if]
    		[@spring.message "CustomerSearch.revieweditinstruction2" /]
		 </span>
	</div>
	<br/>
	<div class="bglightblue">
		<span class="heading">
			[@spring.message "CustomerSearch.search" /] 
		</span>
	</div>
	<div>
		<span class="fontnormal" id="branch_search.label.search"> 
			[@spring.message "CustomerSearch.searchinstruction1" /]&nbsp;
			[@spring.message "${ConfigurationConstants.CLIENT}" /],
			[@spring.message "${ConfigurationConstants.GROUP}" /]
            [#if isCenterHierarchyExists ]
                , [@spring.message "${ConfigurationConstants.CENTER}" /]
            [/#if]
			[@spring.message "CustomerSearch.searchnamesysid" /]
		</span>
		<form method="POST" action="searchResult.ftl">
			[@form.input "customerSearch.searchString" "branch_search.input.search" "name='searchString' maxlength='200'" /]
			[@form.input "customerSearch.officeId" "" "" "hidden" /]
			<input type="submit" value="[@spring.message "CustomerSearch.search" /]" id="branch_search.input.search" name="searchButton" class="buttn"/>
		</form>
	</div>
	<div>
		<span class="headingorange">
			[@spring.message "CustomerSearch.or" /]
		</span>
	</div>
	<div>
		<table width="100%" border="0" cellspacing="0" cellpadding="4">
			<tr class="fontnormal">
				<td width="100%" colspan="2" class="bglightblue">
					<span class="heading"> 
						[@spring.message "CustomerSearch.selectLoanOfficer" /]
					</span>
				</td>
			</tr>
	        <tr class="fontnormal">
	            <td style="border: 1px solid #CECECE; height:100px; width:100%; padding:6px; margin-top:5px;">
		         <span class="fontnormal">
	                 [#list personnelDtoList as loanOfficer]
		                <a id="branch_search.link.selectLoanOfficer" href="clientsAndAccounts.ftl?officeId=${customerSearch.officeId}&loanOfficerId=${loanOfficer.personnelId}&currentFlowKey=${Request.currentFlowKey}">
			               ${loanOfficer.displayName}
		                </a>
		                <br>
	                 [/#list] 
				     [#if !personnelDtoList?has_content]
					      [@spring.message "CustomerSearch.noEntityAvailablePrefix"/]
					      [@spring.message "${ConfigurationConstants.BRANCHOFFICE}"/]
				          [@spring.message "CustomerSearch.noEntityAvailableSuffix"/]
				      [/#if]
		         </span>
	            </td>
	        </tr>
	     </table>
	</div>
	<div>
		<table width="100%" border="0" cellspacing="0" cellpadding="4">
			<tr class="fontnormal">
				<td width="100%" colspan="2" class="bglightblue">
					<span class="heading">
						[@spring.message "CustomerSearch.select" /] 
					[#if isCenterHierarchyExists ]
						[@spring.message "${ConfigurationConstants.CENTER}" /]
					[#else]
						[@spring.message "${ConfigurationConstants.GROUP}" /]
					[/#if]
					</span>
				</td>
			</tr>
	        <tr class="fontnormal">
	            <td style="border: 1px solid #CECECE; height:100px; width:100%; padding:6px; margin-top:5px;">
		           <span class="fontnormal">
                       [#list customerList as customer]
                       		[#if isCenterHierarchyExists ]
			                    <a id="branch_search_loanofficer.link.selectCenter"
			                       href='viewCenterDetails.ftl?customerId=${customer.customerId}&searchId=${customer.searchId}&globalCustNum=${customer.globalCustNum}&recordLoanOfficerId=${recordLoanOfficerId?if_exists}&recordOfficeId=${recordOfficeId?if_exists}'>
			                   		${customer.displayName}
			                    </a>
			                    <br/>
		                     [#else]
			                    <a id="branch_search_loanofficer.link.selectGroup"
				                       href='viewGroupDetails.ftl?customerId=${customer.customerId}&searchId=${customer.searchId}&globalCustNum=${customer.globalCustNum}&recordLoanOfficerId=${recordLoanOfficerId?if_exists}&recordOfficeId=${recordOfficeId?if_exists}'>
				                   	${customer.displayName}
			                    </a>
			                    <br>
                     		[/#if]
                       [/#list] 
	                   [#if !customerList?has_content ]
			   		   		[@spring.message "CustomerSearch.noEntityAvailablePrefix"/]
								[#if isCenterHierarchyExists ]
									[@spring.message "${ConfigurationConstants.CENTER}" /]
								[#else]
									[@spring.message "${ConfigurationConstants.GROUP}" /]
								[/#if]
							[@spring.message "CustomerSearch.noEntityAvailableSuffix"/]
							<br/>
		               [/#if]
	               </span>
	           </td>
	       </tr>
     	</table>
	</div>
</div>