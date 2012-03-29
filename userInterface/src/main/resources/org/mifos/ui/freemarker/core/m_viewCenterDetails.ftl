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
[#assign mifostag=JspTaglibs["/tags/mifos-html"]]
[#assign mifos=JspTaglibs["/tags/mifos-html"]]

[@widget.topNavigationNoSecurityMobile currentTab="ClientsAndAccounts" /]

<span id="page.id" title="ViewCenterDetails"></span>

<div class="content">
	<div>
		<span class="headingorange">
			${centerInformationDto.centerDisplay.displayName}
		</span>
		<span class="fontnormal"> <!-- Edit center status link --> 
			<a id="viewCenterDetails.link.edit" href="editCustomerStatusAction.do?method=loadStatus&customerId=${centerInformationDto.centerDisplay.customerId?c}&input=center&currentFlowKey=${Request.currentFlowKey}">
				[@spring.message "Center.Edit" /] [@spring.message "${ConfigurationConstants.CENTER}" /] [@spring.message "Center.Status1" /] 			
			</a>
		</span>
	</div>
	<div>
		<font class="fontnormalRedBold">
			<span id="viewCenterDetails.error.message">
			</span>
		</font>
	</div>
	<div>
	<div class="fontnormalbold">
		<span class="fontnormal">
			[@mifostag.MifosImage id="${centerInformationDto.centerDisplay.customerStatusId}" moduleName="org.mifos.customers.util.resources.customerImages" /] 
			<span id="viewCenterDetails.text.status">
				${centerInformationDto.centerDisplay.customerStatusName}
			</span>
		</span><br>
		<span class="fontnormal">
			[@spring.message "Center.SystemId" /]:
		</span>
		<span id="viewGroupDetails.text.globalcustnum" class="fontnormal">
			${centerInformationDto.centerDisplay.globalCustNum}
		</span><br>
		<span class="fontnormal"> 
			[@spring.message "Center.LoanOff" /]
			${centerInformationDto.centerDisplay.loanOfficerName}
		</span><br>
		
		<br>
		<span class="fontnormalbold">
			[@spring.message "${ConfigurationConstants.GROUP}" /][@spring.message "Center.s" /] [@spring.message "Center.Assigned" /]
		</span>
		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		[#if centerInformationDto.centerDisplay.customerStatusId != CustomerConstants.CENTER_INACTIVE_STATE ]
			<span class="fontnormal"> 
				<a id="viewCenterDetails.link.add" href="groupCustAction.do?method=load&centerSystemId=${centerInformationDto.centerDisplay.globalCustNum}&parentOfficeId=${centerInformationDto.centerDisplay.branchId}&recordOfficeId=${centerInformationDto.centerDisplay.branchId}&recordLoanOfficerId=${centerInformationDto.centerDisplay.loanOfficerId}&randomNUm=${Session.randomNUm?c}">
					[@spring.message "Center.Add" /] [@spring.message "${ConfigurationConstants.GROUP}" /]
				</a>
			</span>
		[/#if] <br>
		<span class="fontnormal">
			[@spring.message "Center.GroupsLink1" /] [@spring.message "${ConfigurationConstants.GROUP}" /] [@spring.message "Center.GroupsLink2" /] [@spring.message "${ConfigurationConstants.GROUP}" /] [@spring.message "Center.GroupsLink3" /]
			<br>
		</span>
		<div id="Layer2" style="border: 1px solid #CECECE; height:100px; width:250px; overflow: auto; padding:6px; margin-top:5px;">
			<span class="fontnormal"> <!-- Display all client under this group -->
				[#if centerInformationDto.groupsOtherThanClosedAndCancelled?has_content]			
					[#list centerInformationDto.groupsOtherThanClosedAndCancelled as group]
						<a id="viewCenterDetails.link.group"
							href="viewGroupDetails.ftl?globalCustNum=${group.globalCustNum}&recordOfficeId=${centerInformationDto.centerDisplay.branchId}&recordLoanOfficerId=${centerInformationDto.centerDisplay.loanOfficerId}">
							${group.displayName} 
						</a>
						<br/>
					[/#list]
				[#else]
					[@spring.message "Center.No" /] [@spring.message "${ConfigurationConstants.GROUP}" /] [@spring.message "Center.Available" /]
					<br>
				[/#if] 
			</span>
			<br>
		</div>
	</div>
	<div>
		<img src="pages/framework/images/trans.gif" width="5" height="5">
	</div>
	<div>
		<div>
			<div>
				<span class="headingorange">
					[@spring.message "Center.AccountHeading" /]
				</span>
			</div>
			[#if centerInformationDto.centerDisplay.customerStatusId == CustomerConstants.CENTER_ACTIVE_STATE]
			<div>
				<span class="fontnormal"> 
					[@spring.message "Center.AccountsLink" /]
					&nbsp; 
          			<a id="viewCenterDetails.link.newSavingsAccount" href="createSavingsAccount.ftl?customerId=${centerInformationDto.centerDisplay.customerId?c}&recordOfficeId=${UserContext.branchId?c}&recordLoanOfficerId=${UserContext.id?c}">
						[@spring.message "${ConfigurationConstants.SAVINGS}.Savings" /]
					</a> 
				</span>
			</div>
			[/#if]
			[#if centerInformationDto.savingsAccountsInUse?has_content ]
			<div class="tableContentLightBlue" style="width: 325px" >
				<div>
					<span class="fontnormalbold"> 
						[@spring.message "${ConfigurationConstants.SAVINGS}.Savings" /] 
					</span> 
				</div>
				[#list centerInformationDto.savingsAccountsInUse as savings ]
				<div>
					<span class="fontnormal"> 	
				 		<a id="viewCenterDetails.link.savingsAccount"
							href="viewSavingsAccountDetails.ftl?globalAccountNum=${savings.globalAccountNum}&recordOfficeId=${UserContext.branchId?c}&recordLoanOfficerId=${UserContext.id?c}&randomNUm=${Session.randomNUm?c}">
							${savings.prdOfferingName}, [@spring.message "Center.acc" /] ${savings.globalAccountNum}
						</a> 
					</span>
					<br/>
					<span class="fontnormal"> 
						[@mifostag.MifosImage id="${savings.accountStateId}" moduleName="org.mifos.accounts.savings.util.resources.savingsImages" /] 
						${savings.accountStateName}
					</span>
				</div>
				<div>
					<span class="fontnormal">
						[@spring.message "Client.balance" /] 
						${savings.savingsBalance?number} 
					</span>
				</div>
				<div>
					<img src="pages/framework/images/trans.gif" width="5" height="20"></td>
				</div>
				[/#list]
			</div>
			[/#if]
			<div>
				<img src="pages/framework/images/trans.gif" width="10" height="10">
			</div>
			<div class="tableContentLightBlue" style="width: 325px">
				<span class="fontnormalbold">		
					[@spring.message "${ConfigurationConstants.CENTER}" /]
					[@spring.message "Center.Charges" /] 
				</span>
				<div>
					<span class="fontnormal"> 
						<a id="viewCenterDetails.link.viewDetails" href="customerAccountAction.do?method=load&globalCustNum=${centerInformationDto.centerDisplay.globalCustNum}"/>
							[@spring.message "Center.Viewdetails" /]
						</a> 
					</span>
					<br/>
					<span class="fontnormal">
						[@spring.message "Center.AmountDueColon" /]
						<span id="viewCenterDetails.text.amountDue">
							${centerInformationDto.customerAccountSummary.nextDueAmount?number}
						</span>
					</span>
				</div>
				<div>
					<img src="pages/framework/images/trans.gif" width="5" height="5">
				</div>
			</div>			
			<div>
				<span class="fontnormal">
					<a id="viewCenterDetails.link.viewAllClosedAccounts" href="custAction.do?method=getClosedAccounts&customerId=${centerInformationDto.centerDisplay.customerId?c}&input=center&currentFlowKey=${Request.currentFlowKey}&randomNUm=${Session.randomNUm?c}">
  						[@spring.message "Group.viewallclosedaccounts" /]
  					</a>
	  			</span>
			</div>			
		</div>
	</div>
	<div>
		<div>
			<img src="pages/framework/images/trans.gif" width="10" height="10">
		</div>	
		<div>
			<div>
				<span class="headingorange">
					[@spring.message "${ConfigurationConstants.CENTER}" /] [@spring.message "Center.Information" /] 	
				</span>
				<span class="fontnormal">				
					<a id="viewCenterDetails.link.editCenterDetails" href="centerCustAction.do?method=manage&currentFlowKey=${Request.currentFlowKey}&randomNUm=${Session.randomNUm?c}">
						[@spring.message "Center.Edit" /] [@spring.message "${ConfigurationConstants.CENTER}" /] [@spring.message "Center.details" /] 					
					</a>
				</span>
			</div>
			<div>
				<span class="fontnormal">	
					[@spring.message "Center.MfiJoiningDate" /]: 	
					${i18n.date_formatter(centerInformationDto.centerDisplay.mfiJoiningDate, "dd/MM/yyyy", Application.LocaleSetting.locale)}			
				</span>
			</div>
			<div>
				<span class="fontnormal">
					[@spring.message "${ConfigurationConstants.CENTER}"/] [@spring.message "Center.CenterStartDate" /]:
					${i18n.date_formatter(centerInformationDto.centerDisplay.createdDate, "dd/MM/yyyy", Application.LocaleSetting.locale)}			
				</span>
			</div>
			<div id="Center.ExternalId" />
				<span class="fontnormal">
					[@mifos.mifoslabel name="${ConfigurationConstants.EXTERNALID}" bundle="CenterUIResources" keyhm="Center.ExternalId" isManadatoryIndicationNotRequired="yes" isColonRequired="yes"/]
					${centerInformationDto.centerDisplay.externalId}
				</span> 
			</div>
		</div>
		<div>
			<img src="pages/framework/images/trans.gif" width="10" height="10">
		</div>	
		<div>
			<span class="fontnormalbold">
				[@spring.message "Center.OfficialTitlesHeading" /]
			</span><br/>
			<span class="fontnormal">
				[#list centerInformationDto.customerPositions?if_exists as pos]
					[#if pos.positionName?has_content ]
						${pos.positionName}: ${pos.customerDisplayName}
						<br/>
					[/#if]
				[/#list]
			</span>
		</div>
		<div>
			<img src="pages/framework/images/trans.gif" width="10" height="10">
		</div>
		<div>
			<span class="fontnormalbold">			
				[@spring.message "Center.MeetingsHeading" /]		
				<span class="fontnormalRed">
					<br/>
					[@spring.message "Center.MeetingsSubHeading" /]&nbsp; 
					<span id="viewCenterDetails.text.meetingSchedule">
						${centerInformationDto.customerMeeting.meetingSchedule}
					</span>
				</span><br/>
			</span> 
			<span class="fontnormal" id="viewCenterDetails.meeting.text.meetingplace"> 
				${centerInformationDto.customerMeeting.meetingPlace}
			</span>
		</div>
		<div class="fontnormal">
			<a id="viewCenterDetails.link.meetings" href="meetingAction.do?method=edit&customerLevel=${centerInformationDto.centerDisplay.customerLevelId}&currentFlowKey=${Request.currentFlowKey}&randomNUm=${Session.randomNUm?c}">
				[@spring.message "Center.MeetingsLink" /] 	
			</a>
		</div>
		[#if centerInformationDto.address.phoneNumber?has_content ||
					 centerInformationDto.address.displayAddress?has_content ||
					 centerInformationDto.address.city?has_content	 ||
					 centerInformationDto.address.state?has_content	 ||
					 centerInformationDto.address.country?has_content ||
					 centerInformationDto.address.zip?has_content
		 ]
		<div id="Center.Address">
			<span class="fontnormalbold">
				<br>
				[@spring.message "client.Address" /]
			 	<span class="fontnormal">
			 		<br>
			 	</span> 
			 	[#if centerInformationDto.address.displayAddress?has_content]
					<span class="fontnormal">
						${centerInformationDto.address.displayAddress} 
					</span>
				[/#if]
			</span>
		</div>
		<div id="Center.City">
			[#if centerInformationDto.address.city?has_content ]
				<span class="fontnormal">
					${centerInformationDto.address.city}
				</span>
			[/#if]
		</div>
		<div id="Center.State">
			[#if centerInformationDto.address.state?has_content]
				<span class="fontnormal">
					${centerInformationDto.address.state}
				</span>
			[/#if]
		</div>
		<div id="Center.Country">
			[#if centerInformationDto.address.country?has_content]
				<span class="fontnormal">
					${centerInformationDto.address.country}
				</span>
			[/#if]
		</div>
		<div id="Center.PostalCode">
			[#if centerInformationDto.address.zip?has_content]
				<span class="fontnormal">
					${centerInformationDto.address.zip}
				</span>
			[/#if]
		</div>
		<div id="Center.PhoneNumber">
			[#if centerInformationDto.address.phoneNumber?has_content ]
				<span class="fontnormal">
						<br>
						[@spring.message "client.Telephone" /]
						${centerInformationDto.address.phoneNumber}
				</span> 
			[/#if]
			<br>
		</div>
		[/#if]
		[#--Additional information custom fields --]
		[#if centerInformationDto.customFields?has_content]
		<div>
			<span class="fontnormalbold">
				[@spring.message "Center.AdditionalInformationHeading" /]
			</span><br/>
			<span class="fontnormal">
				[#list centerInformationDto.customFields as customField]
					[@spring.message "${customField.lookUpEntityType}" /]:
					[#if customField.fieldType == 3 ]
					${i18n.date_formatter(customField.fieldValue, "dd/MM/yyyy", Application.LocaleSetting.locale)}	
					[#else]
					${customField.fieldValue}
					[/#if]
					<br/>
				[/#list] 
			</span>
		</div>
		[/#if]
		<div>
			<span class="fontnormal">
	        	<a id="groupdetail.link.questionGroups" href="viewAndEditQuestionnaire.ftl?creatorId=${Session.UserContext.id?c}&entityId=${centerInformationDto.centerDisplay.customerId?c}&event=Create&source=Group&backPageUrl=${backPageUrl}%26recordOfficeId%3D${centerInformationDto.centerDisplay.branchId}%26recordLoanOfficerId%3D${centerInformationDto.centerDisplay.loanOfficerId}">
	            	[@spring.message "client.ViewQuestionGroupResponsesLink" /]
	            </a>
	            <br/>
				<a id="viewCenterDetails.link.viewChangeLog" href="centerCustAction.do?method=loadChangeLog&entityType=Center&entityId=${centerInformationDto.centerDisplay.customerId?c}&currentFlowKey=${Request.currentFlowKey}">
					[@spring.message "Center.ChangeLogLink" /]
				</a> 
				<br/>
			</span>
		</div>			
	</div>	
</div>