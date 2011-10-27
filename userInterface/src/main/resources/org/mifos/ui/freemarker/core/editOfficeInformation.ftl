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
[@adminLeftPaneLayout]   <!--  Main Content Begins-->
<script lanugage="javascript">
function getData(){
var officeLevel=document.getElementById("levelId").value;
if(document.getElementById("levelId").value == "1" || document.getElementById("levelId").value == "2" || document.getElementById("levelId").value == "3" || document.getElementById("levelId").value == "4" || document.getElementById("levelId").value == "5"){
return editOfficeInformation.submit();
}
}
</script>
  <div class=" content">
      <form method="POST" action="editOfficeInformation.ftl" name="editOfficeInformation" id="editOfficeInformation">
    <div class="span-24">
          [#-- <div class="bluedivs paddingLeft"><a href="admin.ftl" id="editoffice.link.admin">[@spring.message "admin" /]</a>&nbsp;/&nbsp;<a href="viewOffices.ftl" id="editoffice.link.viewOffices">[@spring.message "admin.viewOffices" /]</a>&nbsp;/&nbsp;<a href="" id="editoffice.link.viewOffice">[@spring.message "offices.editOfficeInformation.testAreaOffice" /]</a></div>--]
          [@spring.bind "officeFormBean.name"/]
          [#assign breadcrumb = {"admin":"AdminAction.do?method=load", "admin.viewOffices":"viewOffices.ftl",spring.status.value?default(""):"viewOfficeDetails.ftl?id=${officeFormBean.id}"}/]
        [@widget.editPageBreadcrumbs breadcrumb/]
        <div class="clear">&nbsp;</div>
        <div class="fontBold"><span>${spring.status.value?default("")}</span>&nbsp;-&nbsp;<span class="orangeheading">[@spring.message "offices.editOfficeInformation.editofficeinformation" /]</span></div>
        <div><span>[@spring.message "offices.editOfficeInformation.previewthefieldsbelow.ThenclickConfirm.ClickCanceltoreturntoOfficeDetailswithoutsubmittinginformation" /]</span></div>
        <div><span class="red"> * </span>[@spring.message "fieldsmarkedwithanasteriskarerequired." /]</div>
        <p class="error" id="editoffice.error.message">
        [#if showError == "true"]
        [@form.showAllErrors "officeFormBean.*"/]
        [/#if]
        </p>
        <p class="fontBold">[@spring.message "offices.editOfficeInformation.officedetails" /]</p>
        <p>&nbsp;&nbsp;</p>
        <div class="prepend-1  span-23 last">
            <div class="span-20"><span class="span-4 rightAlign" id="editoffice.label.officeName"><span class="red">* </span>[@spring.message "offices.editOfficeInformation.officeName" /]</span>
            [@spring.bind "officeFormBean.name"/]
                    <span class="span-3"><input name="${spring.status.expression}" type="text" id="editoffice.input.officeName" value="${spring.status.value?default("")}"/>
            [@spring.bind "officeFormBean.id"/]
            <input type="hidden" name="${spring.status.expression}" value="${spring.status.value?default("")}"/>
                    </span>
              </div>
            <div class="span-20"><span class="span-4 rightAlign" id="editoffice.label.shortName"><span class="red">* </span>[@spring.message "offices.editOfficeInformation.officeshortname" /]</span>
            [@spring.bind "officeFormBean.officeShortName"/]
                    <span class="span-3"><input name="${spring.status.expression}" type="text" size="5" id="editoffice.input.shortName" value="${spring.status.value?default("")}"/></span>
              </div>
            <div class="span-20 "><span class="span-4 rightAlign"><span class="red">* </span>[@spring.message "offices.editOfficeInformation.officetype" /]</span>
                       <span class="span-6">
                       [@spring.bind "officeFormBean.levelId"/]
                       <select id="${spring.status.expression}" name="${spring.status.expression}" [#if (officeFormBean.levelId?string == "1" || officeFormBean.levelId?string == "5") && view?if_exists="disable"]disabled[/#if] onChange="getData();">
                            <option value="">${springMacroRequestContext.getMessage("--Select--")}</option>
                            [#if officeTypes?is_hash]
                                [#list officeTypes?keys as value]
                                <option value="${value?html}" [@spring.checkSelected value/]>${officeTypes[value]?html}</option>
                                [/#list]
                            [#else]
                                [#list oficeTypes as value]
                                <option value="${value?html}" [@spring.checkSelected value/]>${officeTypes[value]?html}</option>
                                [/#list]
                            [/#if]
                    </select>
                    [#if (officeFormBean.levelId?string == "1" || officeFormBean.levelId?string == "5") && view?if_exists="disable"]<input type="hidden" value="${officeFormBean.levelId}" name="levelId"/>[/#if]
                       </span>
            </div>
            <div class="span-20 ">
                <span class="span-4 rightAlign"><span class="red">* </span>[@spring.message "offices.editOfficeInformation.parentOffice" /]</span>
                <span class="span-6">
                [@spring.bind "officeFormBean.parentId"/]
                [#if officeFormBean.levelId?string != "1"]
                       <select id="${spring.status.expression}" name="${spring.status.expression}">
                            <option value="">${springMacroRequestContext.getMessage("--Select--")}</option>
                            [#if parentOffices?exists]
                                [#if parentOffices?is_hash]
                                    [#list parentOffices?keys as value]
                                    <option value="${value?html}" [@spring.checkSelected value/]>${parentOffices[value]?html}</option>
                                    [/#list]
                                [#else]
                                    [#list parentOffices as value]
                                    <option value="${value?html}" [@spring.checkSelected value/]>${parentOffices[value]?html}</option>
                                    [/#list]
                                [/#if]
                            [/#if]
                    </select>
                [#else]
                [/#if]
                    </span>
            </div>
        </div>
        <p>&nbsp;&nbsp;</p>
        <p class="clear fontBold">[@spring.message "offices.editOfficeInformation.status" /]</p>
        <p>&nbsp;&nbsp;</p>
        <div class="prepend-1  span-23 last">
           <div class="span-20 "><span class="span-4 rightAlign">[@spring.message "offices.editOfficeInformation.status1" /]</span>
                <span class="span-6">
                    [#assign statusTypes={"Active":"active","Inactive":"inactive"}/]
                    [@spring.bind "officeFormBean.officeStatusName"/]
                    <select id="${spring.status.expression}" name="${spring.status.expression}">
                            [#if statusTypes?is_hash]
                                [#list statusTypes?keys as value]
                                <option value="${value?html}"[@spring.checkSelected value/]>${springMacroRequestContext.getMessage(statusTypes[value]?html)}</option>
                                [/#list]
                            [#else]
                                [#list statusTypes as value]
                                <option value="${value?html}"[@spring.checkSelected value/]>${springMacroRequestContext.getMessage(value?html)}</option>
                                [/#list]
                            [/#if]
                    </select>
                </span>
              </div>

        </div>
        <p>&nbsp;&nbsp;</p>
        <p class="fontBold">[@spring.message "offices.editOfficeInformation.officeaddress" /]</p>
        <p>&nbsp;&nbsp;</p>
        <div class="prepend-1  span-21 last">
            <div class="span-20 "><span class="span-4 rightAlign" id="editoffice.label.address1">[@spring.message "offices.editOfficeInformation.address1" /]</span>
            [#if officeFormBean.line1?has_content ]
            [@spring.bind "officeFormBean.line1"/]
            [/#if]
                    <span class="span-3"><input name="line1" type="text" id="editoffice.input.address1" value="[#if officeFormBean.line1?has_content ]${officeFormBean.line1?default("")}[/#if]" /></span>
              </div>
            <div class="span-20 "><span class="span-4 rightAlign" id="editoffice.label.address2">[@spring.message "offices.editOfficeInformation.address2" /]</span>
            [#if officeFormBean.line2?has_content ]
            [@spring.bind "officeFormBean.line2"/]
            [/#if]
                    <span class="span-3"><input id="address2" name="line2" value="[#if officeFormBean.line2?has_content ]${officeFormBean.line2?default("")}[/#if]" type="text" id="editoffice.input.address2"/></span>
              </div>
            <div class="span-20 "><span class="span-4 rightAlign" id="editoffice.label.address3">[@spring.message "offices.editOfficeInformation.address3" /]</span>
            [#if officeFormBean.line3?has_content ]
            [@spring.bind "officeFormBean.line3"/]
            [/#if]
                    <span class="span-3"><input id="address3" name="line3" value="[#if officeFormBean.line3?has_content ]${officeFormBean.line3?default("")}[/#if]" type="text" /></span>
              </div>
            <div class="span-20 "><span class="span-4 rightAlign" id="editoffice.label.city">[@spring.message "offices.editOfficeInformation.city/District" /]</span>
            [#if officeFormBean.city?has_content ]
            [@spring.bind "officeFormBean.city"/]
            [/#if]
                    <span class="span-3"><input id="cityDistrict" name="city" value="[#if officeFormBean.city?has_content ]${officeFormBean.city?default("")}[/#if]" type="text" /></span>
              </div>
            <div class="span-20 "><span class="span-4 rightAlign id="editoffice.label.state"">[@spring.message "offices.editOfficeInformation.state" /]</span>
            [#if officeFormBean.state?has_content ]
            [@spring.bind "officeFormBean.state"/]
            [/#if]
                    <span class="span-3"><input id="state" name="state" value="[#if officeFormBean.state?has_content ]${officeFormBean.state?default("")}[/#if]" type="text" /></span>
              </div>
            <div class="span-20 "><span class="span-4 rightAlign" id="editoffice.label.country">[@spring.message "offices.editOfficeInformation.country" /]</span>
            [#if officeFormBean.country?has_content ]
            [@spring.bind "officeFormBean.country"/]
            [/#if]
                    <span class="span-3"><input id="country" name="country" value="[#if officeFormBean.country?has_content ]${officeFormBean.country?default("")}[/#if]" type="text" /></span>
              </div>
            <div class="span-20 "><span class="span-4 rightAlign" id="editoffice.label.postalCode">[@spring.message "offices.editOfficeInformation.postalCode" /]</span>
            [#if officeFormBean.zip?has_content ]
            [@spring.bind "officeFormBean.zip"/]
            [/#if]
                    <span class="span-3"><input id="postalCode" name="zip" value="[#if officeFormBean.zip?has_content ]${officeFormBean.zip?default("")}[/#if]" type="text" /></span>
              </div>
            <div class="span-20 "><span class="span-4 rightAlign" id="editoffice.label.phoneNumber">[@spring.message "offices.editOfficeInformation.telephone" /]</span>
            [#if officeFormBean.phoneNumber?has_content ]
            [@spring.bind "officeFormBean.phoneNumber"/]
            [/#if]
                    <span class="span-3"><input id="telephone" name="phoneNumber" value="[#if officeFormBean.phoneNumber?has_content ]${officeFormBean.phoneNumber?default("")}[/#if]" type="text" /></span>

              </div>
          <p>&nbsp;&nbsp;</p>
        </div>
        <hr />
        <div class="prepend-9">
          <input class="buttn" type="submit" name="PREVIEW" id="editoffice.button.preview" value="[@spring.message "preview"/]"/>
          <input class="buttn2" type="submit" name="CANCEL" id="editoffice.button.cancel" value="[@spring.message "cancel"/]"/>
        </div>
    </div>
       </form>
  </div><!--Main Content Ends-->
  [/@adminLeftPaneLayout]