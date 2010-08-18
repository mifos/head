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
[@adminLeftPaneLayout] <!--  Main Content Begins-->  
  <div class=" content leftMargin180">
 <div class="bluedivs paddingLeft"><a href="admin.ftl">[@spring.message "admin"/]</a>&nbsp;/&nbsp;<span class="fontBold">[@spring.message "organizationPreferences.editchecklist.viewEditChecklists"/]</span></div>
       <p>&nbsp;&nbsp;</p>
  	<form method="" action="" name="formname">
    <div class="span-24">
  		
    	<div class="span-24 ">
        	<div class="span-15 ">
            	<div><span class="orangeheading">[@spring.message "organizationPreferences.editchecklist.checklistName" /]</span></div>
                <div><span><img src="pages/framework/images/status_activegreen.gif" /></span>&nbsp;<span>[@spring.message "active" /]</span></div><br />
                <div><span>[@spring.message "organizationPreferences.editchecklist.type" /]</span>&nbsp;<span>&nbsp;</span></div>
                <div><span>[@spring.message "organizationPreferences.editchecklist.status" /]</span>&nbsp;<span>&nbsp;</span></div>
                <div><span>[@spring.message "organizationPreferences.editchecklist.createdby" /]</span>&nbsp;<span>&nbsp;</span></div>
                <div><span>[@spring.message "organizationPreferences.editchecklist.createddate" /]</span>&nbsp;<span>&nbsp;</span></div>
        	</div>
            <span class="span-4 rightAlign"><a href="editChecklist.html">[@spring.message "organizationPreferences.editchecklist.editChecklistInformation" /]</a></span>
        </div>
        <div class="clear">&nbsp;</div>
        <p class="span-24 ">
			<span class="fontBold">[@spring.message "organizationPreferences.editchecklist.items" /]</span><br />
            <ul style="list-style:none">
            	<li>[@spring.message "organizationPreferences.editchecklist.item1" /]</li>
                <li>[@spring.message "organizationPreferences.editchecklist.item2" /]</li>
            </ul>
        </p>
	</div>
   	</form> 
  </div><!--Main Content Ends-->
   [/@adminLeftPaneLayout] 