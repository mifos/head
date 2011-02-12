[#ftl]
[#--
* Copyright Grameen Foundation USA
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
  <div class="content marginLeft30">
      <form method="" action="" name="formname">
    <div class="span-24">
          <div class="bluedivs paddingLeft"><a href="admin.ftl">[@spring.message "admin" /]</a>&nbsp;/&nbsp;<a href="viewOffices.ftl">[@spring.message "offices.viewoffices" /]</a>&nbsp;/&nbsp;<span class="fontBold">${officeFormBean.name}</span></div>
        <div class="clear">&nbsp;</div>
        <div class="span-24 marginLeft30">
            <div class="span-18 ">
                  <p><span class="orangeheading" style="font-size:11pt;">${officeFormBean.name}</span></p><br />
                  <div>
                  [#if officeFormBean.statusId == "1"]
                    <span><img src="pages/framework/images/status_activegreen.gif" /></span>&nbsp;<span>[@spring.message "active"/]</span>
                [#elseif officeFormBean.statusId == "2"]
                        <span><img src="pages/framework/images/status_closedblack.gif" /></span>&nbsp;<span>[@spring.message "inactive"/]</span>
                [/#if]
                </div>
                <span><span>[@spring.message "offices.viewOfficeDetails.officeshortname" /]&nbsp;:</span><span id="viewOfficeDetails.text.shortName">${officeFormBean.officeShortName}</span><br />
                <span><span>[@spring.message "offices.viewOfficeDetails.officetype" /]&nbsp;:</span><span id="viewOfficeDetails.text.officeLevel">${officeFormBean.officeLevelName}</span><br />
                <span><span>[@spring.message "offices.viewOfficeDetails.parentoffice" /]&nbsp;:</span><span id="viewOfficeDetails.text.parentOffice">${officeFormBean.parentOfficeName?default("")}</span><br />
            </div>
            <span class="span-4 rightAlign"><a href="editOfficeInformation.ftl?officeLevelId=${officeFormBean.id}" id="viewOfficeDetails.link.editOfficeInformation">[@spring.message "offices.viewOfficeDetails.editofficeinformation" /]</a></span>
        </div>
        <div class="clear">&nbsp;</div>
        <div class="span-24 ">
            <div><span class="fontBold span-3" >[@spring.message "offices.viewOfficeDetails.address" /]</span></div><br/>
            [#if officeAddress?has_content]
                    [#if officeFormBean.line1?has_content ]<span>${officeFormBean.line1}</span>,[/#if]
                    [#if officeFormBean.line2?has_content]<span>${officeFormBean.line2} </span>,[/#if]
                    [#if officeFormBean.line3?has_content]<span>${officeFormBean.line3}</span>[/#if]<br/>

                    <div>[#if officeFormBean.city?has_content]<span>${officeFormBean.city}</span><br/>[/#if]</div>
                    <div>[#if officeFormBean.state?has_content]<span>${officeFormBean.state}</span><br/>[/#if]</div>
                    <div>[#if officeFormBean.country?has_content]<span>${officeFormBean.country}</span><br/>[/#if]</div>
                    <div>[#if officeFormBean.zip?has_content]<span>${officeFormBean.zip}</span><br/>[/#if]</div><br />
                    <div>[#if officeFormBean.phoneNumber?has_content]<span>[@spring.message "offices.viewOfficeDetails.phoneNumber" /]&nbsp;:</span> <span>${officeFormBean.phoneNumber}</span><br/>[/#if]</div>
             [#else]
                    <br /><span>[@spring.message "offices.viewOfficeDetails.noAddressEntered" /] </span><br/>
             [/#if]
        </div>
    </div>
       </form>
  </div>
  <!--Main Content Ends-->
[/@adminLeftPaneLayout]