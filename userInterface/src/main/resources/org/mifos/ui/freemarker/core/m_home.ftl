
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

[@widget.topNavigationNoSecurityMobile currentTab="Home" /]
<span id="page.id" title="Home"></span>
<div class="content">
	<span class="fontnormalboldorange" id="home.text.welcome">
		[@spring.message "CustomerSearch.welcome"/],&nbsp; ${Session.UserContext.name}
	</span>
	<br/>
	[#if Session.UserContext.lastLogin?exists && Session.UserContext.lastLogin?length != 0]
		<span class="fontnormal" id="home.text.lastLogin">
			[@spring.message "CustomerSearch.lastlog"/] ${Session.UserContext.lastLogin}
		</span>
	[/#if]
	<div style="width:100%"> 
		<form method="POST" action="searchResult.ftl">
			<div class="span-8 last" style="text-align:left;">
	        	<div class="error">
		            <span id="home.error.message">
		            </span>
		        </div>
		        <div>
		        	<span class="fontnormalbold">
		        		[@spring.message "CustomerSearch.navigate"/]
	        		</span>
					<table width="80%" border="0" cellspacing="0" cellpadding="3">
						<tr class="fontnormal">
							<td><img src="pages/framework/images/smallarrowdown.gif" width="11" height="11">
								[@spring.message "CustomerSearch.searchbelow"/]
							</td>
							<td><img src="pages/framework/images/smallarrowtop.gif" width="11" height="11">
								[@spring.message "CustomerSearch.tabsattop"/]
							</td>
						</tr>
					</table>
		        </div>
		        <div class="fontnormalbold">
					[@spring.message "CustomerSearch.quicklyfind"/]
					[@spring.message "${ConfigurationConstants.CLIENT}" /],
					[#if isCenterHierarchyExists ]
						[@spring.message "${ConfigurationConstants.CENTER}"/],&nbsp;
						[@spring.message "${ConfigurationConstants.GROUP}"/],
					[#else]
						[@spring.message "${ConfigurationConstants.GROUP}"/],
					[/#if]		
					[@spring.message "CustomerSearch.or"/]
					[@spring.message "CustomerSearch.youCan"/]...
		        </div>
		        <div>
					<span id="home.label.search">[@spring.message "CustomerSearch.searchby"/]</span>:
		        </div>
		        <div>
	                [@form.input "customerSearch.searchString" "home.input.search" "name='searchString' maxlength='200'" /]
		        </div>
		        <div>
		        	[#if Session.UserContext.officeLevelId == 5]
		        		<input type="hidden" name="officeId" value="${Session.UserContext.branchId}"/> 
		        	[#else]
		        		<input type="hidden" name="officeId" value="0"/>
		        	[/#if]
					<input type="hidden" name="officeId" value="0"/>
					<input type="hidden" name="method" value="mainSearch" />
					<input type="hidden" name="currentFlowKey" value="${Request.currentFlowKey}" />		
		        </div>
		        <div>
		        	<input type="submit" value="Search" id="home.button.search" name="searchButton" class="buttn"/>
		        </div>
	        </div>
		</form>
	</div>
	<!-- task-list MIFOS-5177 -->
	<div style="width:100%;float:left">
		[#if isLoanOfficer]
		<form method="GET" action="home.ftl">
			<table width="95%" border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td width="70%" height="24" align="left" valign="middle" class="paddingL10">
						<table width="96%" border="0" cellpadding="3" cellspacing="0">
							<tr>
								<td class="fontnormalbold">	
									[@spring.message "CustomerSearch.upcomingMeetings"/]:
									[@spring.formSingleSelect "customerSearch.selectedDateOption", nearestDates, "onchange='this.form.submit()'"  /]
								</td>
							<tr>
						</table>
					</td>
				</tr>
				<tr>
				[#if isCenterHierarchyExists]
				<td width="70%" height="24" align="left" valign="top" class="paddingL10">	 
             	 	[#list hierarchy.centers as center]
					<table width="90%" border="0" cellspacing="3" cellpadding="0" class="paddingL10">
						<tr class="fontnormal">
							<td width="1%"><img src="pages/framework/images/bullet_circle.gif" width="9" height="11"></td>
							<td width="99%">
								<a href="viewCenterDetails.ftl?globalCustNum=${center.globalCustNum}">
									${center.displayName}
								</a>
							</td>
						</tr>
						<tr>
							<td></td>
							<td>
								<ul>
									[#list center.groups as group]
										<li class="fontnormal" style="margin-bottom:3px;">
											<a href="viewGroupDetails.ftl?globalCustNum=${group.globalCustNum}">
												${group.displayName}
											</a>
										</li>
										<ul>
											[#list group.clients as client]
												<li class="fontnormal" style="margin-bottom:3px;">
													<a href="viewClientDetails.ftl?globalCustNum=${client.globalCustNum}">
														${client.displayName}
													</a>
												</li>
											[/#list]	
										</ul>
									[/#list]
								</ul>	
							<td>
						</tr>
					</table>
              		[/#list]
      			</td>	
				[#else]
				<td width="70%" height="24" align="left" valign="top" class="paddingL10">
					[#list hierarchy.groups as group]
						<table width="90%" border="0" cellspacing="3" cellpadding="0" class="paddingL10">
							<tr class="fontnormal">
								<td width="1%"><img src="pages/framework/images/bullet_circle.gif" width="9" height="11"></td>
								<td width="99%">
									<a href="viewGroupDetails.ftl?globalCustNum=${group.globalCustNum}">
										${group.displayName}
									</a>
								</td>
							</tr>
							<tr>
								<td></td>
								<td>
									<ul>
										[#list group.clients as cient]
											<li class="fontnormal" style="margin-bottom:3px;">
												<a href="viewClientDetails.ftl?globalCustNum=${client.globalCustNum}">
													${client.displayName}
												</a>
											</li>
										[/#list]
									</ul>	
								<td>
							</tr>
						</table>
              		[/#list]
          		</td>
				[/#if]
			</tr>
			</table>
		</form>	
		[/#if]
	</div>
</div>
[@layout.footer /]