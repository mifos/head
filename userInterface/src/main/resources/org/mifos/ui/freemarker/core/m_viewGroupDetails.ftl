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

[@widget.topNavigationNoSecurityMobile currentTab="ClientsAndAccounts" /]

<span id="page.id" title="ViewGroupDetails"></span>

<div class="content">
	<div>
		<span class="headingorange">
			${groupInformationDto.groupDisplay.displayName}
		</span>
		<span class="fontnormal"> <!-- Edit center status link --> 
			[#if groupInformationDto.groupDisplay.customerStatusId != CustomerStatus.GROUP_CLOSED.value]
			<a id="viewgroupdetails.link.editStatus" href="editCustomerStatusAction.do?method=loadStatus&customerId=${groupInformationDto.groupDisplay.customerId?c}&input=group&currentFlowKey=${Request.currentFlowKey}">
				[#assign arguments = ["${ConfigurationConstants.GROUP}"]/]
				[@spring.messageArgs "Group.editStatus" arguments/] 
			</a>
			[/#if]<br>
		</span>
	</div>
	<div>
		<font class="fontnormalRedBold">
			<span id="viewgroupdetails.error.message">
			</span>
		</font>
	</div>
	
	<div>
		<div class="fontnormalbold">
			<span class="fontnormal">
				[@mifostag.MifosImage id="${groupInformationDto.groupDisplay.customerStatusId}" moduleName="org.mifos.customers.util.resources.customerImages" /] 
				<span id="viewGroupDetails.text.status">
					${groupInformationDto.groupDisplay.customerStatusName}
				</span>
				[#list groupInformationDto.customerFlags as flagSet] 
					<span class="fontnormal"> 
						[#if groupInformationDto.groupDisplay.blackListed ]
							[@mifostag.MifosImage id="blackListed" moduleName="org.mifos.customers.client.util.resources.clientImages" /]
						[/#if] 
						${flagSet.statusFlagName} 
					</span>
				[/#list] 
			</span><br>
		[#-- System Id of the group --] 
		<span class="fontnormal">
			[@spring.message "Group.systemId" /]
		</span>
		<span id="viewGroupDetails.text.globalcustnum" class="fontnormal">
			${groupInformationDto.groupDisplay.globalCustNum}
		</span><br>
		[#-- Loan Officer of the group --] 
		<span class="fontnormal"> 
			[@spring.message "Group.loanofficer" /]
			${groupInformationDto.groupDisplay.loanOfficerName}
		</span><br>
	
		<br>
		<span class="fontnormalbold">
			[#assign arguments = ["${ConfigurationConstants.CLIENT}"]/]
			[@spring.messageArgs "Group.clientAssign" arguments/] 
		</span>
		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		[#if groupInformationDto.groupDisplay.customerStatusId != CustomerStatus.GROUP_CANCELLED.value && groupInformationDto.groupDisplay.customerStatusId != CustomerStatus.GROUP_CLOSED.value ]
			<span class="fontnormal"> 
				<a id="viewgroupdetails.link.add" href="clientCustAction.do?method=load&groupFlag=1&parentGroupId=${groupInformationDto.groupDisplay.customerId?c}&recordOfficeId=${groupInformationDto.groupDisplay.branchId}&recordLoanOfficerId=${groupInformationDto.groupDisplay.loanOfficerId}&randomNUm=${Session.randomNUm?c}">
	    			[#assign arguments = ["${ConfigurationConstants.CLIENT}"]/]
					[@spring.messageArgs "Group.Add" arguments/] 
				</a>
			</span>
		[/#if] <br>
		<span class="fontnormal"> 	
			[#assign arguments = ["${ConfigurationConstants.CLIENT}"]/]
			[@spring.messageArgs "Group.groupdetailViewMsg" arguments/] 
			<br>
		</span>
		
		<div id="Layer2" style="border: 1px solid #CECECE; height:100px; width:250px; overflow: auto; padding:6px; margin-top:5px;">
			<span class="fontnormal"> <!-- Display all client under this group -->
				[#if groupInformationDto.clientsOtherThanClosedAndCancelled?has_content]			
					[#if groupInformationDto.groupDisplay?has_content &&  groupInformationDto.groupDisplay.loanOfficerId?has_content]
						[#list groupInformationDto.clientsOtherThanClosedAndCancelled as client]
							<a id="viewgroupdetails.link.client"
								href="viewClientDetails.ftl?globalCustNum=${client.globalCustNum}&recordOfficeId=${groupInformationDto.groupDisplay.branchId}&recordLoanOfficerId=${groupInformationDto.groupDisplay.loanOfficerId}">
								${client.displayName} 
							</a>
							<br/>
						[/#list]
					[#else]
						[#list groupInformationDto.clientsOtherThanClosedAndCancelled as client]
							<a id="viewgroupdetails.link.client"
								href="viewClientDetails.ftl?globalCustNum=${client.globalCustNum}&recordOfficeId=${UserContext.branchId?c}&recordLoanOfficerId=${UserContext.id?c}">
								${client.displayName}
							</a>
							<br/>
						[/#list]
					[/#if]
				[#else]
					[#assign arguments = ["${ConfigurationConstants.CLIENT}"]/]
					[@spring.messageArgs "Group.noclientsavailable" arguments/]
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
					[@spring.message "Group.accountinformation" /]
				</span>
			</div>
			[#if groupInformationDto.groupDisplay.customerStatusId == CustomerStatus.GROUP_ACTIVE.value]
			<div>
				<span class="fontnormal"> 
					[@spring.message "Group.opennewaccount" /]
					&nbsp; 
					[#if isGroupLoanAllowed]
					<a id="viewgroupdetails.link.newLoanAccount" href="createLoanAccount.ftl?customerId=${groupInformationDto.groupDisplay.customerId?c}&recordOfficeId=${UserContext.branchId?c}&recordLoanOfficerId=${UserContext.id?c}">
						[@spring.message "${ConfigurationConstants.LOAN}" /]
					</a> &nbsp;|&nbsp;
          			[/#if] 
          			<a id="viewgroupdetails.link.newSavingsAccount"	href="createSavingsAccount.ftl?customerId=${groupInformationDto.groupDisplay.customerId?c}&recordOfficeId=${UserContext.branchId?c}&recordLoanOfficerId=${UserContext.id?c}">
						[@spring.message "${ConfigurationConstants.SAVINGS}.Savings" /]
					</a> 
				</span>
			</div>
			[/#if]
			[#if groupInformationDto.loanAccountsInUse?has_content ]
			<div>
				<div class="tableContentLightBlue" style="width: 325px">
					<div>
						<span class="fontnormalbold"> 
							[@spring.message "${ConfigurationConstants.LOAN}" /] 
						</span> 
					</div>

					[#list groupInformationDto.loanAccountsInUse as loan]
					<div>
						<span class="fontnormal"> 
							<a id="viewgroupdetails.link.viewLoanAccount"
								href="viewLoanAccountDetails.ftl?globalAccountNum=${loan.globalAccountNum}&customerId=${groupInformationDto.groupDisplay.customerId?c}&recordOfficeId=${UserContext.branchId?c}&recordLoanOfficerId=${UserContext.id?c}&randomNUm=${Session.randomNUm?c}">
								${loan.prdOfferingName}, [@spring.message "Group.acc" /] ${loan.globalAccountNum}
							</a> 
						</span>
						<br/>
						<span class="fontnormal">
							 [@mifostag.MifosImage id="${loan.accountStateId}" moduleName="org.mifos.accounts.loan.util.resources.loanImages" /]
							 ${loan.accountStateName} 
					 	</span>
					</div>
					<div>
						[#if loan.accountStateId==5 || loan.accountStateId==9 ]
						<span class="fontnormal"> 
							[@spring.message "loan.outstandingbalance"/] 
							${loan.outstandingBalance?number} <br>
							[@spring.message "loan.amount_due" /] 
							${loan.totalAmountDue?number} 
						</span>
						[/#if]
					</div>
					<div>
						<img src="pages/framework/images/trans.gif" width="5" height="20"></td>
					</div>
					[/#list]
				</div>
			</div>	
			[/#if]
			<div>
				<img src="pages/framework/images/trans.gif" width="10" height="10">
			</div>
			[#if groupInformationDto.savingsAccountsInUse?has_content ]
			<div class="tableContentLightBlue" style="width: 325px" >
				<div>
					<span class="fontnormalbold"> 
						[@spring.message "${ConfigurationConstants.SAVINGS}.Savings" /] 
					</span> 
				</div>
				
				[#list groupInformationDto.savingsAccountsInUse as savings ]
				<div>
					<span class="fontnormal"> 	
				 		<a id="viewgroupdetails.link.viewSavingsAccount"
							href="viewSavingsAccountDetails.ftl?globalAccountNum=${savings.globalAccountNum}&recordOfficeId=${UserContext.branchId?c}&recordLoanOfficerId=${UserContext.id?c}&randomNUm=${Session.randomNUm?c}">
							${savings.prdOfferingName}, [@spring.message "client.acc" /] ${savings.globalAccountNum}
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
					[#assign arguments = ["${ConfigurationConstants.GROUP}"]/]
					[@spring.messageArgs "Group.charges" arguments /] 
				</span>
				<div>
					<span class="fontnormal"> 
						<a id="viewgroupdetails.link.viewDetails" href="customerAccountAction.do?method=load&globalCustNum=${groupInformationDto.groupDisplay.globalCustNum}"/>
							[@spring.message "Group.viewdetails" /]
						</a> 
					</span>
					<br/>
					<span class="fontnormal">
						[@spring.message "Group.amountdue" /]
						<span id="viewgroupdetails.text.amountDue">
							${groupInformationDto.customerAccountSummary.nextDueAmount?number}
						</span>
					</span>
				</div>
				<div>
					<img src="pages/framework/images/trans.gif" width="5" height="5">
				</div>
			</div>			
			<div>
				<span class="fontnormal">
					[#if groupInformationDto.groupDisplay.customerStatusId !=7 && groupInformationDto.groupDisplay.customerStatusId !=8 ]
						<a id="viewgroupdetails.link.viewAllClosedAccounts" href="custAction.do?method=getClosedAccounts&customerId=${groupInformationDto.groupDisplay.customerId?c}&input=group&currentFlowKey=${Request.currentFlowKey}&randomNUm=${Session.randomNUm?c}">
	  						[@spring.message "Group.viewallclosedaccounts" /]
	  					</a>
	  				[/#if]
	  			</span>
			</div>			
		</div>
		<div>
			<img src="pages/framework/images/trans.gif" width="10" height="10">
		</div>
		<div>
			<div>
				<span class="headingorange">
					[#assign arguments = ["${ConfigurationConstants.GROUP}"]/]
					[@spring.messageArgs "Group.groupinformation" arguments /] 
				</span>
				<span class="fontnormal">				
					<a id="viewgroupdetails.link.editGroupInformation" href="groupCustAction.do?method=manage&currentFlowKey=${Request.currentFlowKey}&randomNUm=${Session.randomNUm?c}">
						[#assign arguments = ["${ConfigurationConstants.GROUP}"]/]
						[@spring.messageArgs "Group.editInformation" arguments /] 				
					</a>
				</span>
			</div>
			<div>
				<span class="fontnormal">	
					[#assign arguments = ["${ConfigurationConstants.GROUP}"]/]
					[@spring.messageArgs "Group.approvaldate" arguments /]
					[#if groupInformationDto.groupDisplay.customerActivationDate?? ] 	
						${i18n.date_formatter(groupInformationDto.groupDisplay.customerActivationDate, "dd/MM/yyyy", Application.LocaleSetting.locale)}			
					[/#if]
				</span> <br/>
				<span id="Group.ExternalId" class="fontnormal">
					[@spring.message "Group.ExternalId" /]:
					${groupInformationDto.groupDisplay.externalId}
				</span> <br/>
				<span class="fontnormal"> 
					[@spring.message "Group.FormedBy" /]
					${groupInformationDto.groupDisplay.customerFormedByDisplayName}
				</span>
			</div>
			<br/>
			<div id="Group.TrainedDate">
				<span class="fontnormalbold">
					[@spring.message "Group.trainingstatus" /]
				</span> <br/>
				<span class="fontnormal"> 
					[@spring.message "Group.trainedon" /] 
					[#if groupInformationDto.groupDisplay.trained ]
						${groupInformationDto.groupDisplay.trainedDate}
					[/#if] 
				</span>
			</div>
			<div>
				<img src="pages/framework/images/trans.gif" width="10" height="10">
			</div>	
			<div>
				<span class="fontnormalbold">
					[@spring.message "Group.officialtitlesassigned" /]
				</span> <br/>
				<span class="fontnormal">
					[#list groupInformationDto.customerPositions?if_exists as pos]
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
			<div id="Group.Address">
				<span class="fontnormalbold">
					[@spring.message "Group.address" /]
				</span> <br/>
				[#if groupInformationDto.address.displayAddress?has_content]
				<span class="fontnormal"> 
					${groupInformationDto.address.displayAddress}
				</span> <br/>
				[/#if]
				[#if groupInformationDto.address.city?has_content]
				<span class="fontnormal"> 
					${groupInformationDto.address.city}
				</span>
				[/#if]
				[#if groupInformationDto.address.state?has_content]
				<span class="fontnormal">
					${groupInformationDto.address.state}
				</span> <br/>
				[/#if]
				[#if groupInformationDto.address.country?has_content]
				<span class="fontnormal"> 
					${groupInformationDto.address.country}
				</span> <br/>
				[/#if]
				[#if groupInformationDto.address.zip?has_content]
				<span class="fontnormal"> 
					${groupInformationDto.address.zip}
				</span> <br/>
				[/#if]
			</div>
			<div>
				<img src="pages/framework/images/trans.gif" width="10" height="10">
			</div>	
			<div id="Group.PhoneNumber"> 
				[#if groupInformationDto.address.phoneNumber?has_content ]
					<span class="fontnormal">
						[@spring.message "Group.telephone" /]
						${groupInformationDto.address.phoneNumber}
					</span>
				[/#if] 
			</div>
			<div>
				<img src="pages/framework/images/trans.gif" width="10" height="10">
			</div>
			[#--Additional information custom fields --]
			[#if groupInformationDto.customFields?has_content]
			<div>
				<span class="fontnormalbold">
					[@spring.message "Group.additionalinformation" /]
				</span><br/>
				<span class="fontnormal">
					[#list groupInformationDto.customFields as customField]
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
				<div>
					<div> 
					[#if isCenterHierarchyExists ]
						<div>
							<span class="fontnormalbold">			
								[#assign arguments = ["${springMacroRequestContext.getMessage(ConfigurationConstants.CENTER)}"]/]
								[@spring.messageArgs "Group.centermembership&meetingdetails" arguments /]		
								<span class="fontnormalRed">
									<br/>
									[@spring.message "Group.meetings" /]&nbsp; 
									<span id="viewgroupdetails.text.meetingSchedule">
										${groupInformationDto.customerMeeting.meetingSchedule}
									</span>
								</span><br/>
							</span> 
							<span class="fontnormal"> 
								[#if groupInformationDto.customerMeeting.meetingPlace?has_content && groupInformationDto.customerMeeting.meetingPlace?has_content ]
									${groupInformationDto.customerMeeting.meetingPlace}
									<br/>
								[/#if] 
								${groupInformationDto.groupDisplay.parentCustomerDisplayName}<br/>
							</span>
						</div>
						<div class="fontnormal">
							<a id="viewgroupdetails.link.editCenterMembership" href="groupTransferAction.do?method=loadParents&currentFlowKey=${Request.currentFlowKey}&randomNUm=${Session.randomNUm?c}">
								[#assign arguments = ["${springMacroRequestContext.getMessage(ConfigurationConstants.CENTER)}"]/]
								[@spring.messageArgs "Group.editMembership" arguments /] 	
							</a>
						</div>
					[#else]
						<div>
							<span class="fontnormalbold">
								[@spring.message "Group.meetingdetails" /]
							</span><br/>
							<span class="fontnormalRed">
								[@spring.message "Group.meetings" /]
								<span id="viewgroupdetails.text.meetingSchedule">
									${groupInformationDto.customerMeeting.meetingSchedule}
								</span>
							</span><br/>
							<span class="fontnormal"> 
								${groupInformationDto.customerMeeting.meetingPlace}
							</span><br/>
							<span class="fontnormal"><br/>
								<a id="viewgroupdetails.link.editMeetingSchedule" href="meetingAction.do?method=edit&currentFlowKey=${Request.currentFlowKey}&randomNUm=${Session.randomNUm?c}&customerLevel=${groupInformationDto.groupDisplay.customerLevelId}">
									[@spring.message "Group.editmeetingchedule"/]
								</a><br/>
								<a id="viewgroupdetails.link.editOfficeMembership" href="groupTransferAction.do?method=loadBranches&currentFlowKey=${requestScope.currentFlowKey}&randomNUm=${sessionScope.randomNUm}"> 
									[@spring.message "Group.editOfficeMembership"/]
								</a>
							</span>
						</div>
					[/#if]
					</div> 
					<div>
						<img src="pages/framework/images/trans.gif" width="10" height="10">
					</div>
					<div>
						<span class="fontnormal">
                        	<a id="groupdetail.link.questionGroups" href="viewAndEditQuestionnaire.ftl?creatorId=${Session.UserContext.id?c}&entityId=${groupInformationDto.groupDisplay.customerId?c}&event=Create&source=Group&backPageUrl=${backPageUrl}%26recordOfficeId%3D${groupInformationDto.groupDisplay.branchId}%26recordLoanOfficerId%3D${groupInformationDto.groupDisplay.loanOfficerId}">
                            	[@spring.message "client.ViewQuestionGroupResponsesLink" /]
	                        </a>
	                        <br/>
							<a id="viewgroupdetails.link.viewHistoricalData" href="custHistoricalDataAction.do?method=getHistoricalData&globalCustNum=${groupInformationDto.groupDisplay.globalCustNum}&currentFlowKey=${Request.currentFlowKey}&randomNUm=${Session.randomNUm?c}">
								[@spring.message "Group.viewhistoricaldata" /]
							</a> 
							<br/>
							<a id="viewgroupdetails.link.viewChangeLog" href="groupCustAction.do?method=loadChangeLog&entityType=Group&entityId=${groupInformationDto.groupDisplay.customerId?c}&currentFlowKey=${Request.currentFlowKey}">
								[@spring.message "Group.viewchangelog" /]
							</a> 
							<br/>
						</span>
					</div>
				</div>
			<div>
		</div>
	</div>
</div>