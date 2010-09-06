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
  	<form method="" action="" name="formname">
    <div class="span-24">
  		<div class="bluedivs paddingLeft"><a href="admin.ftl">[@spring.message "admin" /]</a>&nbsp;/&nbsp;<a href="viewOffices.ftl">[@spring.message "viewOffices" /]</a>&nbsp;/&nbsp;<span class="fontBold">[@spring.message "OfficeName" /]</span></div>
        <div class="clear">&nbsp;</div>
    	<div class="span-24 ">
        	<div class="span-19 ">
            	<span class="orangeheading">[@spring.message "OfficeName" /]</span><br /><br />
                <span><span><img src="pages/framework/images/status_activegreen.gif" /></span>&nbsp;<span>[@spring.message "active" /]</span><br />
                <span><span >[@spring.message "officeshortname" /]</span>&nbsp;<span id="viewOfficeDetails.text.shortName">&nbsp;</span><br />
                <span><span >[@spring.message "officetype" /]</span>&nbsp;<span id="viewOfficeDetails.text.officeLevel">&nbsp;</span><br />
                <span><span>[@spring.message "parentoffice" /]</span>&nbsp;<br />
        	</div>
            <span class="span-4 rightAlign"><a href="editOfficeInformation.ftl" id="viewOfficeDetails.link.editOfficeInformation">[@spring.message "editofficeinformation" /]</a></span>
        </div>
        <div class="clear">&nbsp;</div>
        <div class="span-24 ">
			<span class="fontBold">[@spring.message "additionalinformation" /] </span><br />
            <span>[@spring.message "externalId" /]</span><br />
            <span>[@spring.message "numberofClientsperGroup" /] </span><br />
            <span>[@spring.message "numberofClientsperCenter" /]</span><br />
            <span>[@spring.message "distancefromHOtoBOforoffice" /] </span><br />
                                    
        </div>
	</div>
   	</form> 
  </div>
  <!--Main Content Ends-->
[/@adminLeftPaneLayout]