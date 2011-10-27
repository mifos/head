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
[@layout.header "title" /]
  [@widget.topNavigationNoSecurity currentTab="Admin" /]
  <script language="javascript">
  function getData(){
  if(document.getElementById("levelId").value == "1" || document.getElementById("levelId").value == "2" || document.getElementById("levelId").value == "3" || document.getElementById("levelId").value == "4" || document.getElementById("levelId").value == "5"){
  defineNewOffice.submit();
  }
  }
  </script>
 <!--  Main Content Begins-->
  <div class="content definePageMargin">
      <div class="borders span-23">
          <div class="borderbtm span-23">
            <p class="span-18 arrowIMG orangeheading">[@spring.message "offices.defineNewOffice.officeinformation" /]</p>
            <p class="span-3 arrowIMG1 orangeheading last width130px">[@spring.message "reviewAndSubmit" /]</p>
        </div>
        <div class="subcontent ">
            <form method="POST" action="defineNewOffice.ftl" name="defineNewOffice" id="defineNewOffice">
            <div class="fontBold">
                [@spring.message "offices.defineNewOffice.addanewoffice" /]&nbsp;--&nbsp;<span class="orangeheading">[@spring.message "offices.defineNewOffice.enterofficeInformation" /]</span>
            </div>
            <div>
                [@spring.message "offices.defineNewOffice.completethefieldsbelow.ThenclickPreview.ClickCanceltoreturntoAdminwithoutsubmittinginformation" /]
            </div>
            <div>
                <span class="red">* </span>
                [@spring.message "fieldsmarkedwithanasteriskarerequired." /]
            </div>
            <p class="error" id="CreateNewOffice.error.message">
            [#if showError == "true"]
            [@form.showAllErrors "officeFormBean.*"/]
            [/#if]
            </p>
            <p>&nbsp;&nbsp;</p>
            <p class="fontBold">[@spring.message "offices.defineNewOffice.officedetails" /]</p>
            <div class="prepend-3  span-23 last">
                <div class="span-24">
                    <span class="span-4 rightAlign" id="CreateNewOffice.label.officeName">
                        <span class="red">* </span>[@spring.message "offices.defineNewOffice.officeName" /]&nbsp;:
                    </span>
                    [@spring.bind "officeFormBean.name"/]
                    <span class="span-3">
                        <input name="${spring.status.expression}" type="text" id="editoffice.input.officeName" value="${spring.status.value?default("")}"/>
                    </span>
                </div>
                <div class="span-24">
                    <span class="span-4 rightAlign" id="CreateNewOffice.label.shortName">
                        <span class="red">* </span>[@spring.message "offices.defineNewOffice.officeshortname" /]&nbsp;:
                    </span>
                    [@spring.bind "officeFormBean.officeShortName"/]
                    <input id="CreateNewOffice.input.shortName" name="${spring.status.expression}" value="${spring.status.value?if_exists}" type="text" size="5" />
                  </div>
                  <div class="span-24">
                      <span class="span-4 rightAlign" id="CreateNewOffice.label.officeLevel">
                          <span class="red">* </span>[@spring.message "offices.defineNewOffice.officetype" /]&nbsp;:
                      </span>
                      [@spring.bind "officeFormBean.levelId"/]
                       <select id="${spring.status.expression}" name="${spring.status.expression}" onchange="getData();">
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
                   </div>
                   <div class="span-24">
                       <span class="span-4 rightAlign" id="CreateNewOffice.label.parentOffice">
                           <span class="red">* </span>[@spring.message "offices.defineNewOffice.parentOffice" /]&nbsp;:
                       </span>
                       [@spring.bind "officeFormBean.parentId"/]
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
                   </div>
               </div>
               <p>&nbsp;&nbsp;</p>
               <p class="fontBold">[@spring.message "offices.defineNewOffice.officeaddress" /]</p>
               <p>&nbsp;&nbsp;</p>
               <div class="prepend-3  span-23 last">
                   <div class="span-24">
                       <span class="span-4 rightAlign" id="CreateNewOffice.label.address1">
                           [@spring.message "offices.defineNewOffice.address1" /]&nbsp;:
                       </span>
                       [@spring.bind "officeFormBean.line1"/]
                       <input id="CreateNewOffice.input.address1" name="${spring.status.expression}" value="${spring.status.value?if_exists}" type="text" />
                   </div>
                   <div class="span-24">
                       <span class="span-4 rightAlign" id="CreateNewOffice.label.address2">
                           [@spring.message "offices.defineNewOffice.address2" /]&nbsp;:
                       </span>
                       [@spring.bind "officeFormBean.line2"/]
                       <input id="CreateNewOffice.input.address2" name="${spring.status.expression}" value="${spring.status.value?if_exists}" type="text" />
                   </div>
                   <p>&nbsp;&nbsp;</p>
                   <div class="span-20 ">
                       <span class="span-4 rightAlign" id="CreateNewOffice.label.address3">
                           [@spring.message "offices.defineNewOffice.address3" /]&nbsp;:
                       </span>
                       [@spring.bind "officeFormBean.line3"/]
                       <input id="address3" name="${spring.status.expression}" value="${spring.status.value?if_exists}" type="text" />
                   </div>
                   <div class="span-20 ">
                       <span class="span-4 rightAlign" id="CreateNewOffice.label.city">
                           [@spring.message "offices.defineNewOffice.city/District" /]&nbsp;:
                       </span>
                       [@spring.bind "officeFormBean.city"/]
                       <input id="cityDistrict" name="${spring.status.expression}" value="${spring.status.value?if_exists}" type="text" />
                   </div>
                   <div class="span-20 ">
                       <span class="span-4 rightAlign" id="CreateNewOffice.label.state">
                           [@spring.message "offices.defineNewOffice.state" /]&nbsp;:
                       </span>
                       [@spring.bind "officeFormBean.state"/]
                       <input id="state" name="${spring.status.expression}" value="${spring.status.value?if_exists}" type="text" />
                   </div>
                   <div class="span-20 ">
                       <span class="span-4 rightAlign" id="CreateNewOffice.label.country">
                           [@spring.message "offices.defineNewOffice.country" /]&nbsp;:
                       </span>
                       [@spring.bind "officeFormBean.country"/]
                       <input id="country" name="${spring.status.expression}" value="${spring.status.value?if_exists}" type="text" />
                   </div>
                   <div class="span-20 ">
                       <span class="span-4 rightAlign" id="CreateNewOffice.label.postalCode">
                           [@spring.message "offices.defineNewOffice.postalCode" /]&nbsp;:
                       </span>
                       [@spring.bind "officeFormBean.zip"/]
                       <input id="postalCode" name="${spring.status.expression}" value="${spring.status.value?if_exists}" type="text" />
                   </div>
                   <div class="span-20 ">
                       <span class="span-4 rightAlign" id="CreateNewOffice.label.phoneNumber">
                           [@spring.message "offices.defineNewOffice.telephone" /]&nbsp;:
                       </span>
                    [@spring.bind "officeFormBean.phoneNumber"/]
                    <input id="telephone" name="${spring.status.expression}" value="${spring.status.value?if_exists}" type="text" id="CreateNewOffice.input.phoneNumber" />
                </div>
            </div>
            <div class="clear">&nbsp;</div>
            <hr />
            <div class="prepend-9">
                <input class="buttn" id="CreateNewOffice.button.preview" type="submit" name="PREVIEW" value="[@spring.message "preview"/]"/>
                <input class="buttn2" type="button" id="CreateNewOffice.button.cancel" name="CANCEL" value="[@spring.message "cancel"/]"/>
            </div>
            <div class="clear">&nbsp;</div>
        </form>
    </div><!--Subcontent Ends-->
</div>
</div>
<!--Main Content Ends-->
[@layout.footer/]