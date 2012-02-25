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
<span id="page.id" title="ClientsAccounts"></span>	
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
		<span class="fontnormal" id="clients_accounts.label.search"> 
			[@spring.message "CustomerSearch.searchstring" /]
		</span>
		<form method="POST" action="searchResult.ftl">
			[@form.input "customerSearch.searchString" "clients_accounts.input.search" "name='searchString' maxlength='200'" /]
			[@form.singleSelectWithNested "customerSearch.officeId", customerSearch.offices ]
				<option value="0">
					[@spring.message "CustomerSearch.all" /]&nbsp; 
					[@spring.message "${ConfigurationConstants.BRANCHOFFICE}" /]
					[@spring.message "CustomerSearch.s" /]
				</option>
			[/@form.singleSelectWithNested]
			<input type="submit" value="[@spring.message "CustomerSearch.search" /]" id="clients_accounts.button.search" name="searchButton" class="buttn"/>
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
						[@spring.message "CustomerSearch.select" /]&nbsp;[@spring.message "${ConfigurationConstants.BRANCHOFFICE}" /] 
					</span>
				</td>
			</tr>
	        <tr class="fontnormal">
	            <td style="border: 1px solid #CECECE; height:100px; width:100%; padding:6px; margin-top:5px;">
		         <span class="fontnormal">
	                 [#list customerSearch.offices?keys as officeId]
		                <a id="client_accounts.link.selectBranchOffice" href="clientsAndAccounts.ftl?officeId=${officeId}&currentFlowKey=${Request.currentFlowKey}">
			               ${customerSearch.offices[officeId]}
		                </a>
		                <br>
	                 [/#list] 
				     [#if !customerSearch.offices?has_content]
					      [@spring.message "CustomerSearch.noEntityAvailablePrefix"/]
					      [@spring.message "${ConfigurationConstants.BRANCHOFFICE}"/]
				          [@spring.message "CustomerSearch.noEntityAvailableSuffix"/]
				      [/#if]
		         </span>
	            </td>
	        </tr>
	     </table>
	</div>
</div>