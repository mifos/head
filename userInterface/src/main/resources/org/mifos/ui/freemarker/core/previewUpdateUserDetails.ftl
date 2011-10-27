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
[@adminLeftPaneLayout]
<div class="content">
    <div class="subcontent ">
        <p class="font15"><span class="fontBold">[@spring.message "systemUsers.preview.addanewuser"/]</span>&nbsp;-&nbsp;<span class="orangeheading">[@spring.message "reviewAndSubmit"/]</span></p>
        <div>[@spring.message "systemUsers.preview.reviewtheinformationbelow"/]</div>
        <p>&nbsp;</p>
        <p><span class="fontBold">[@spring.message "systemUsers.preview.office"/] </span><span>${userFormBean.officeName}</span></p>

        <div>
            <span class="orangeheading">[@spring.message "systemUsers.preview.userInformation"/] </span>
            <span>&nbsp;</span>
        </div>
        <form method="post" action="user.ftl?execution=${flowExecutionKey}">
            <div class="clear">&nbsp;</div>
            <div id="allErrorsDiv" class="allErrorsDiv">
                   [@form.showAllErrors "userFormBean.*"/]
            </div>
            <div class="prepend-1  span-21 last">

                <div class="span-20 ">
                    <span class="fontBold">[@spring.message "systemUsers.preview.firstName"/]&nbsp;</span>
                    <span>${userFormBean.firstName?if_exists}</span>
                  </div>
                <div class="span-20 ">
                    <span class="fontBold">[@spring.message "systemUsers.preview.middleName"/]&nbsp;</span>
                    <span>${userFormBean.middleName?if_exists}</span>
                  </div>
                <div class="span-20 ">
                    <span class="fontBold">[@spring.message "systemUsers.preview.secondLastName"/]&nbsp;</span>
                    <span>${userFormBean.secondLastName?if_exists}</span>
                </div>
                <div class="span-20 ">
                    <span class="fontBold">[@spring.message "systemUsers.preview.lastName"/]&nbsp;</span>
                    <span>${userFormBean.lastName?if_exists}</span>
                  </div>
                <div class="span-20 "><span class="fontBold">[@spring.message "systemUsers.preview.governmentID"/]&nbsp;</span><span>${userFormBean.governmentId?if_exists}</span>
                  </div>
                <div class="span-20 "><span class="fontBold">[@spring.message "systemUsers.preview.email"/]&nbsp;</span><span>${userFormBean.email?if_exists}</span>
                </div>
                <div class="span-20 "><span class="fontBold">[@spring.message "systemUsers.preview.dateofBirth"/]&nbsp;</span><span>${userFormBean.dateOfBirth?if_exists}</span>
                </div>
                <div class="span-20 "><span class="fontBold">[@spring.message "systemUsers.preview.age"/]&nbsp;</span><span>${userFormBean.age?if_exists}</span>
                </div>
                <div class="span-20 "><span class="fontBold">[@spring.message "systemUsers.preview.maritalStatus"/]&nbsp;</span><span>${userFormBean.maritalStatusName?if_exists}</span>
                  </div>
                <div class="span-20 "><span class="fontBold">[@spring.message "systemUsers.preview.gender"/]&nbsp;</span><span>${userFormBean.genderName?if_exists}</span>
                  </div>
            <div class="clear">&nbsp;</div>
            <div class="span-20 "><span class="fontBold">[@spring.message "systemUsers.preview.languagePreferred"/]&nbsp;</span><span>${userFormBean.preferredLanguageName?if_exists}</span>
              </div>
            <div class="span-20 "><span class="fontBold">[@spring.message "systemUsers.preview.mFIJoiningDate"/]:&nbsp;</span><span>${userFormBean.mfiJoiningDate?if_exists}</span>
              </div>
            <div class="clear">&nbsp;</div>
            <div class="clear">&nbsp;</div>
            <div class="span-20 "><span class="fontBold">[@spring.message "systemUsers.preview.address"/]&nbsp;</span><span>${userFormBean.address.displayAddress?if_exists}</span></div>
            <div class="span-20 "><span class="fontBold">[@spring.message "systemUsers.preview.city"/]&nbsp;</span><span>${userFormBean.address.cityDistrict?if_exists}</span></div>
            <div class="span-20 "><span class="fontBold">[@spring.message "systemUsers.preview.state"/]&nbsp;</span><span>${userFormBean.address.state?if_exists}</span></div>
            <div class="span-20 "><span class="fontBold">[@spring.message "systemUsers.preview.country"/]&nbsp;</span><span>${userFormBean.address.country?if_exists}</span></div>
            <div class="span-20 "><span class="fontBold">[@spring.message "systemUsers.preview.postalcode"/]&nbsp;</span><span>${userFormBean.address.postalCode?if_exists}</span></div>
            <div class="clear">&nbsp;</div>
            <div class="span-20 "><span class="fontBold">[@spring.message "systemUsers.preview.telephone"/]&nbsp;</span><span>${userFormBean.address.telephoneNumber?if_exists}</span>
            </div>
            <div class="clear">&nbsp;</div>
            <div class="span-20 "><span class="fontBold">[@spring.message "systemUsers.preview.officeAndPermissions"/]</span></div>

            <div class="span-20 "><span class="fontBold">[@spring.message "systemUsers.preview.userTitle"/]&nbsp;</span><span>${userFormBean.userTitleName?if_exists}</span>
            </div>
            <div class="span-20 "><span class="fontBold">[@spring.message "systemUsers.preview.userHierarchy"/]&nbsp;</span><span>${userFormBean.userHierarchyName?if_exists}</span>
            </div>
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
            <div class="span-20 "><span class="fontBold">[@spring.message "systemUsers.preview.loginInformation"/]</span>
            </div>
            <div class="span-20 "><span class="fontBold">[@spring.message "systemUsers.preview.userName"/]&nbsp;</span><span>${userFormBean.username}</span>
            </div>
             <div class="clear">&nbsp;</div>
            <div class="span-20 "><span class="fontBold">[@spring.message "systemUsers.preview.additionalInformation"/]</span></div>

            [#list userFormBean.customFields as additional]
                   <div class="span-20 ">
                       <span class="fontBold">${additional.label}:</span>
                       <span>${additional.fieldValue}</span>
                   </div>
            [/#list]

            <div class="clear">&nbsp;</div>
                <input class="buttn2" type="submit" name="_eventId_reedit" value="[@spring.message "systemUsers.preview.edituser"/]" />
            </div>

            <div class="clear">&nbsp;</div>
            <hr />
            <div class="clear">&nbsp;</div>
            <div class="prepend-9">
                  <input class="buttn" type="submit" name="_eventId_submit" value="[@spring.message "submit"/]" />
                  <input class="buttn2" type="submit" name="_eventId_cancel" value="[@spring.message "cancel"/]" />
            </div>
        </form>
      </div>
    </div>
  <!--Main Content Ends-->
 [/@adminLeftPaneLayout]