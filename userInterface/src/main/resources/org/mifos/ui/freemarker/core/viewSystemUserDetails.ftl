[#ftl]
[#--
* Copyright (c) 2005-2010 Grameen Foundation USA
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
[@adminLeftPaneLayout]
  <!--  Main Content Begins-->  
  <div class=" content">
	<div class="bluedivs paddingLeft">
		<a href="AdminAction.do?method=load">[@spring.message "admin"/]</a>&nbsp;/&nbsp;<a href="viewSystemUsers.ftl">[@spring.message "admin.viewsystemusers"/]</a>&nbsp;/&nbsp;<span class="fontBold">${userFormBean.displayName}</span>
	</div>
        
    <div class="span-22">
  		<div class="span-22">
       		<span class="span-10">
	           	<span class="orangeheading">${userFormBean.displayName}</span>
	        </span> 
	        <span class="span-5 rightAlign"><a href="editUser.ftl?id=${userFormBean.userId}">[@spring.message "systemUsers.preview.edituser" /]</a></span>
	        <div class="span-5 rightAlign">
	        	<div class="span-5">[@spring.message "recentnotes"/]</div>
	        	[#if userFormBean.recentNotesEmpty]
	        	<div class="span-5">[@spring.message "recentnotes.noneavailable"/]</div>
	        	[#else]
	        		[#list userFormBean.recentNotes as note]
	        		<div class="span-5">
	        		<span class="fontBold">${note.commentDateFormatted}:</span>
	        		<span> ${note.comment}</span>
	        		<span class="fontBold"> -${note.personnelName}</span>
	        		</div>
	        		[/#list]
				<div class="span-5 rightAlign"><a href="personnelNoteAction.do?method=search&personnelId=${userFormBean.userId}&currentFlowKey=123456789">[@spring.message "recentnotes.allnotes"/]</a></div>
	        	[/#if]
	        	<div class="span-5 rightAlign"><a href="personnelNoteAction.do?method=load&personnelId=${userFormBean.userId}&currentFlowKey=123456789">[@spring.message "recentnotes.addnote"/]</a></div>
	        </div>
        </div>
        <div class="span-10">
        	[#switch userFormBean.statusId]
	            	[#case 1]
	            	<span><img src="pages/framework/images/status_activegreen.gif" /></span>&nbsp;<span>[@spring.message "active" /]</span>
	            	[#break]
	            	[#case 2]
	            	<span><img src="pages/framework/images/status_closedblack.gif" /></span>&nbsp;<span>[@spring.message "inactive"/]</span>
	            	[#break]
	        [/#switch] 
        </div>
        <div class="clear">&nbsp;</div>
        
        <div class="span-22">
            <div class="span-20 "><span class="fontBold">[@spring.message "systemUsers.preview.governmentID"/]&nbsp;</span><span>${userFormBean.governmentId}</span> 
  			</div>
            <div class="span-20 "><span class="fontBold">[@spring.message "systemUsers.preview.email"/]&nbsp;</span><span>${userFormBean.email}</span>
            </div>
            <div class="span-20 "><span class="fontBold">[@spring.message "systemUsers.preview.dateofBirth"/]&nbsp;</span><span>${userFormBean.dateOfBirth}</span>
            </div>
            <div class="span-20 "><span class="fontBold">[@spring.message "systemUsers.preview.age"/]&nbsp;</span><span>${userFormBean.age}</span>
            </div>
            <div class="span-20 "><span class="fontBold">[@spring.message "systemUsers.preview.maritalStatus"/]&nbsp;</span><span>${userFormBean.maritalStatusName}</span>
  			</div>
            <div class="span-20 "><span class="fontBold">[@spring.message "systemUsers.preview.gender"/]&nbsp;</span><span>${userFormBean.genderName}</span>
  			</div>
            <div class="span-20 "><span class="fontBold">[@spring.message "systemUsers.preview.languagePreferred"/]&nbsp;</span><span>${userFormBean.preferredLanguageName}</span></div>
            <div class="span-20 "><span class="fontBold">[@spring.message "systemUsers.preview.mFIJoiningDate"/]:&nbsp;</span><span>${userFormBean.mfiJoiningDate}</span></div>

            <div class="clear">&nbsp;</div>

            <div class="span-20 "><span class="fontBold">[@spring.message "systemUsers.preview.address"/]&nbsp;</span><span>${userFormBean.address.displayAddress}</span></div>
            
            [#if userFormBean.address.cityDistrictHidden]
            [#else]
            	<div class="span-20 "><span class="fontBold">[@spring.message "systemUsers.preview.city"/]&nbsp;</span><span>${userFormBean.address.cityDistrict}</span></div>
            [/#if]
            
            [#if userFormBean.address.stateHidden]
            [#else]
            <div class="span-20 "><span class="fontBold">[@spring.message "systemUsers.preview.state"/]&nbsp;</span><span>${userFormBean.address.state}</span></div>
            [/#if]
            
            [#if userFormBean.address.countryHidden]
            [#else]
            <div class="span-20 "><span class="fontBold">[@spring.message "systemUsers.preview.country"/]&nbsp;</span><span>${userFormBean.address.country}</span></div>
            [/#if]
            
            [#if userFormBean.address.postalCodeHidden]
            [#else]
            <div class="span-20 "><span class="fontBold">[@spring.message "systemUsers.preview.postalcode"/]&nbsp;</span><span>${userFormBean.address.postalCode}</span></div>
            [/#if]
            
            [#if userFormBean.address.cityDistrictHidden]
            [#else]
            <div class="span-20 "><span class="fontBold">[@spring.message "systemUsers.preview.telephone"/]&nbsp;</span><span>${userFormBean.address.telephoneNumber}</span>
            [/#if]
            
            <div class="clear">&nbsp;</div>
            <div class="span-20 "><span class="fontBold">[@spring.message "systemUsers.preview.office&Permissions"/]</span></div>
            <div class="span-20 "><span class="fontBold">[@spring.message "systemUsers.preview.userTitle"/]&nbsp;</span><span>${userFormBean.userTitleName}</span></div>
            <div class="span-20 "><span class="fontBold">[@spring.message "systemUsers.preview.userHierarchy"/]&nbsp;</span><span>${userFormBean.userHierarchyName}</span></div>
            <div class="span-20 "><span class="fontBold">[@spring.message "systemUsers.preview.roles"/]&nbsp;</span>
	            <span>
	            <ol>
	            [#list userFormBean.selectedRoleNames as role]
	            	<li>${role}</li>
	            [/#list]
	            <ol>
	            </span>
            </div>
            
            <div class="clear">&nbsp;</div>
            <div class="span-20 "><span class="fontBold">[@spring.message "systemUsers.preview.loginInformation"/]</span></div>
            <div class="span-20 "><span class="fontBold">[@spring.message "systemUsers.preview.userName"/]&nbsp;</span><span>${userFormBean.username}</span></div>
         	
         	<div class="clear">&nbsp;</div>
            <div class="span-20 "><span class="fontBold">[@spring.message "systemUsers.preview.additionalInformation"/]</span></div>
            [#list userFormBean.customFields as additional]
           		<div class="span-20 ">
           			<span class="fontBold">${additional.label}:</span>
           			<span>${additional.fieldValue}</span>
           		</div>
			[/#list]
        </div>
        
        <div class="clear">&nbsp;</div> 
        <p class="span-22 ">
            <div class="span-22 "><a href="viewSystemUserChangeLog.ftl?id=${userFormBean.userId}">[@spring.message "manageLoanProducts.editloanproduct.viewChangeLog" /]</a></div>
        </p>
	</div>
  </div>
  <!--Main Content Ends-->
[/@adminLeftPaneLayout]