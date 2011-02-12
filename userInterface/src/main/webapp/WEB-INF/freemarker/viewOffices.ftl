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
  <p class="bluedivs paddingLeft"><a href="admin.ftl">[@spring.message "admin"/]</a>&nbsp;/&nbsp;<span class="fontBold">[@spring.message "admin.viewOffices"/]</span></p>
      <form method="" action="" name="formname">
      <p>&nbsp;&nbsp;</p>
    <div class="orangeheading">[@spring.message "admin.viewOffices"/]</div>
    <p>[@spring.message "offices.viewoffices.clickonanofficebelowtoviewdetailsandmakechangesor"/] <a href="#">[@spring.message "offices.viewoffices.defineanewoffice"/]</a></p>
    <div class="span-23">
        <div class="span-23">
            <ul>
            [#list formBean.headOffices as headOffice]
                <li>
                    <span class="fontBold"><a href="viewOfficeDetails.ftl?id=${headOffice.id}">${headOffice.name}</a></span>
                </li>
            [/#list]
            </ul>
        </div>
        <div class="span-23">
            <div class="span-16">
                <div>
                <span class="fontBold">[@spring.message "offices.defineNewOffice.regionalOffice"/]</span>
                    <ul>
                    [#list formBean.regionalOffices as regionalOffice]
                        <li>
                            <a href="viewOfficeDetails.ftl?id=${regionalOffice.id}">${regionalOffice.name}</a>
                            [#if regionalOffice.statusId== 2]
                            <span><img src="pages/framework/images/status_closedblack.gif" /></span>&nbsp;<span>[@spring.message "inactive"/]</span>
                            [/#if]
                        </li>
                    [/#list]
                    </ul>
                </div>
            </div>
            <span class="span-5 rightAlign"><a href="#">[@spring.message "offices.viewoffices.definenewregionaloffice"/]</a></span>
        </div>
        <div class="span-23">
            <div class="span-16"><span class="fontBold">[@spring.message "offices.defineNewOffice.divisionalOffice"/]</span>
                <div>
                    <ul>
                    [#list formBean.divisionalOffices as divisionalOffices]
                        <li>
                            <a href="viewOfficeDetails.ftl?id=${divisionalOffices.id}">${divisionalOffices.name}</a>
                            [#if divisionalOffices.statusId== 2]
                            <span><img src="pages/framework/images/status_closedblack.gif" /></span>&nbsp;<span>[@spring.message "inactive"/]</span>
                            [/#if]
                        </li>
                    [/#list]
                    </ul>
                </div>
            </div>
            <span class="span-5 rightAlign"><a href="#">[@spring.message "offices.viewoffices.definenewDivisionalOffice"/]</a></span>
        </div>
        <div class="span-23">
            <div class="span-16"><span class="fontBold">[@spring.message "offices.defineNewOffice.areaOffice"/]</span>
                <div>
                    <ul>
                    [#list formBean.areaOffices as areaOffices]
                        <li>
                            <a href="viewOfficeDetails.ftl?id=${areaOffices.id}">${areaOffices.name}</a>
                            [#if areaOffices.statusId== 2]
                            <span><img src="pages/framework/images/status_closedblack.gif" /></span>&nbsp;<span>[@spring.message "inactive"/]</span>
                            [/#if]
                        </li>
                    [/#list]
                    </ul>
                </div>
            </div>
            <span class="span-5 rightAlign"><a href="#">[@spring.message "offices.viewoffices.definenewAreaOffice"/]</a></span>
        </div>
        <div class="span-23">
        [#assign parentOffice =""/]
        <div class="span-16">
            <span class="fontBold">[@spring.message "offices.defineNewOffice.branchOffice"/]</span>
            <div>
                <ul>
                    [#list formBean.branchOffices as branchOffice]
                    [#if parentOffice == branchOffice.parentOfficeName]
                    <li class="marginLeft30">
                        <a href="viewOfficeDetails.ftl?id=${branchOffice.id}">${branchOffice.name}</a>
                        [#if branchOffice.statusId== 2]
                        <span><img src="pages/framework/images/status_closedblack.gif" /></span>&nbsp;<span>[@spring.message "inactive"/]</span>
                        [/#if]
                    </li>
                    [#else]
                        ${branchOffice.parentOfficeName}
                        <li class="marginLeft30">
                        <a href="viewOfficeDetails.ftl?id=${branchOffice.id}">${branchOffice.name}</a>
                        [#if branchOffice.statusId== 2]
                        <span><img src="pages/framework/images/status_closedblack.gif" /></span>&nbsp;<span>[@spring.message "inactive"/]</span>
                        [/#if]
                        [#assign parentOffice = branchOffice.parentOfficeName/]
                    </li>
                    [/#if]
                    [/#list]
                </ul>
            </div>
         </div>
         <span class="span-5 rightAlign"><a href="#">[@spring.message "offices.viewoffices.definenewBranchOffice"/]</a></span>
    </div>
   </div>
       </form>
  </div><!--Main Content Ends-->
  [/@adminLeftPaneLayout]