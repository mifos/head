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

<script type="text/javascript">
function addOption(root, text, value)
{
  var newOpt = new Option(text, value);
  var rootLength = root.length;
  root.options[rootLength] = newOpt;
}

function deleteOption(root, index)
{
  var rootLength= root.length;
  if(rootLength>0)
  {
    root.options[index] = null;
  }
}

function moveOptions(root, destination)
{
  var rootLength= root.length;
  var rootText = new Array();
  var rootValues = new Array();
  var rootCount = 0;

  var i;
  for(i=rootLength-1; i>=0; i--)
  {
    if(root.options[i].selected)
    {
      rootText[rootCount] = root.options[i].text;
      rootValues[rootCount] = root.options[i].value;
      deleteOption(root, i);
      rootCount++;
    }
  }
  for(i=rootCount-1; i>=0; i--)
  {
    addOption(destination, rootText[i], rootValues[i]);
  }
}

function selectAllOptions(outSel)
{
    if(null != outSel) {
         var selLength =outSel.length;
        outSel.multiple=true;
        for(i=selLength-1; i>=0; i--)
        {
            outSel.options[i].selected=true;
        }
    }
}
</script>

[@layout.webflow currentTab="Admin"
                 currentState="user.flowState.enterAccountInfo" 
                 states=["user.flowState.chooseUserOffice", 
                         "user.flowState.enterAccountInfo", 
                         "user.flowState.reviewAndSubmit"]]
        <p class="font15"><span class="fontBold">[@spring.message "systemUsers.defineNewSystemUser.addanewuser"/]</span>&nbsp;-&nbsp;<span class="orangeheading">[@spring.message "systemUsers.defineNewSystemUser.enterUserInformation"/]</span></p>
        <p>[@spring.message "systemUsers.defineNewSystemUser.infomessage"/]</p>
         <p><span class="red">*</span><span>[@spring.message "manageProducts.defineProductmix.fieldsmarkedwithanasteriskarerequired"/]</span></p>

        <form name="enterUserDetails" method="post" action="user.ftl?execution=${flowExecutionKey}">
            <div id="allErrorsDiv" class="allErrorsDiv">
                [@form.showAllErrors "userFormBean.*"/]
            </div>
            [@spring.formHiddenInput "userFormBean.officeId" /]
            <div class="marginTop15"><span class="fontBold">[@spring.message "systemUsers.defineNewSystemUser.form.office" /]&nbsp;:</span><span>${userFormBean.officeName}</span></div>

            <div class="marginTop15"><span class="fontBold">[@spring.message "systemUsers.preview.userdetails" /]</span></div>
            <div class="prepend-1  span-21 last marginTop15">
                    <div class="span-21 prepend-2 ">
                        <span class="span-4 rightAlign">
                            <label for="firstName"><span class="red">*</span>[@spring.message "systemUsers.preview.firstName" /]&nbsp;:</label>
                        </span>
                        <span>&nbsp;[@spring.formInput "userFormBean.firstName" /]</span>
                      </div>

                    <div class="span-21 prepend-2 ">
                        <span class="span-4 rightAlign">
                            <label for="middleName">[@spring.message "systemUsers.preview.middleName" /]:</label>
                        </span>
                        <span>&nbsp;[@spring.formInput "userFormBean.middleName" /]</span>
                      </div>

                    <div class="span-21 prepend-2 ">
                        <span class="span-4 rightAlign">
                            <label for="secondLastName">[@spring.message "systemUsers.preview.secondLastName" /]:</label>
                        </span>
                        <span>&nbsp;[@spring.formInput "userFormBean.secondLastName" /]</span>
                      </div>

                    <div class="span-21 prepend-2 ">
                        <span class="span-4 rightAlign">
                            <label for="lastName"><span class="red">*</span>[@spring.message "systemUsers.preview.lastName" /]:</label>
                        </span>
                        <span>&nbsp;[@spring.formInput "userFormBean.lastName" /]</span>
                      </div>

                    <div class="span-21 prepend-2 ">
                        <span class="span-4 rightAlign">
                            <label for="governmentId">[@spring.message "systemUsers.preview.governmentID" /]:</label>
                        </span>
                        <span>&nbsp;[@spring.formInput "userFormBean.governmentId" /]</span>
                      </div>

                    <div class="span-21 prepend-2 ">
                        <span class="span-4 rightAlign">
                            <label for="email">[@spring.message "systemUsers.preview.email" /]:</label>
                        </span>
                        <span>&nbsp;[@spring.formInput "userFormBean.email" /]</span>
                      </div>

                    <div class="span-10 prepend-2 ">
                        <span class="span-4 rightAlign">
                            <span class="red">*</span>[@spring.message "systemUsers.preview.dateofBirth" /]&nbsp;:</span>
                            <span>&nbsp;[@spring.formInput "userFormBean.dateOfBirthDay", "size=1 maxlength=2" /]</span>
                            <span>[@spring.message "systemUser.enterUserDetails.DD"/]&nbsp;</span>
                            <span>&nbsp;[@spring.formInput "userFormBean.dateOfBirthMonth", "size=1 maxlength=2" /]</span>
                            <span>[@spring.message "systemUser.enterUserDetails.MM"/]&nbsp;</span>
                            <span>&nbsp;[@spring.formInput "userFormBean.dateOfBirthYear", "size=2 maxlength=4" /]</span>
                            <span>[@spring.message "systemUser.enterUserDetails.YYYY"/]&nbsp;</span>
                        </span>
                    </div>

                    <div class="span-21 prepend-2 ">
                        <span class="span-4 rightAlign">
                            <label for="selectedMaritalStatus">[@spring.message "systemUsers.preview.maritalStatus" /]&nbsp;:</label>
                        </span>
                        <span>
                            [@form.formSingleSelectWithPrompt "userFormBean.selectedMaritalStatus", userFormBean.maritalStatusOptions, "--selectone--" /]
                        </span>
                    </div>

                    <div class="span-21 prepend-2 ">
                        <span class="span-4 rightAlign">
                            <label for="selectedGender"><span class="red">*</span>[@spring.message "systemUsers.preview.gender" /]:</label>
                        </span>
                        <span>
                            [@form.formSingleSelectWithPrompt "userFormBean.selectedGender", userFormBean.genderOptions, "--selectone--" /]
                        </span>
                    </div>

                    <div class="span-21 prepend-2 ">
                        <span class="span-4 rightAlign">
                            <label for="selectedPreferredLanguage">[@spring.message "systemUsers.preview.languagePreferred" /]:</label>
                        </span>
                        <span>
                            [@form.formSingleSelectWithPrompt "userFormBean.selectedPreferredLanguage", userFormBean.preferredLanguageOptions, "--selectone--" /]
                        </span>
                    </div>

                     <div class="span-10 prepend-2 ">
                         <span class="span-4 rightAlign">
                            <span>[@spring.message "systemUsers.preview.mFIJoiningDate" /]:</span>
                        </span>
                        <span>
                            <span>[@spring.formInput "userFormBean.mfiJoiningDateDay", "size=1 maxlength=2" /]</span>
                        </span>
                        <span>
                            <span>[@spring.message "systemUser.enterUserDetails.DD"/]&nbsp;</span>
                        </span>
                        <span>
                            <span>[@spring.formInput "userFormBean.mfiJoiningDateMonth", "size=1 maxlength=2" /]</span>
                        </span>
                        <span>
                            <span>[@spring.message "systemUser.enterUserDetails.MM"/]&nbsp;</span>
                        </span>
                        <span>
                            <span>[@spring.formInput "userFormBean.mfiJoiningDateYear", "size=2 maxlength=4" /]</span>
                        </span>
                        <span>
                            <span>[@spring.message "systemUser.enterUserDetails.YYYY"/]&nbsp;</span>
                        </span>
                        </span>
                    </div>
            </div>

            <div class="fontBold marginTop15">[@spring.message "systemUsers.preview.address" /]</div>

            <div class="prepend-1  span-21 last marginTop15">
                        <div class="span-21 prepend-2 ">
                            <span class="span-4 rightAlign">
                                <label for="address.address1">
                                [#if userFormBean.address.address1Mandatory]
                                <span class="red">*</span>[/#if][@spring.message "systemUsers.preview.address1" /]:</label>
                            </span>
                            <span>
                                [@spring.formInput "userFormBean.address.address1" /]
                            </span>
                        </div>
                    [#if userFormBean.address.address2Hidden]
                        [@spring.formHiddenInput "userFormBean.address.address2" /]
                    [#else]
                        <div class="span-21 prepend-2 ">
                            <span class="span-4 rightAlign">
                                <label for="address.address2">[@spring.message "systemUsers.preview.address2" /]:</label>
                            </span>
                            <span>
                                [@spring.formInput "userFormBean.address.address2" /]
                            </span>
                        </div>
                    [/#if]
                     [#if userFormBean.address.address3Hidden]
                        [@spring.formHiddenInput "userFormBean.address.address3" /]
                    [#else]
                        <div class="span-21 prepend-2 ">
                            <span class="span-4 rightAlign">
                                <label for="address.address3">[@spring.message "systemUsers.preview.address3" /]:</label>
                            </span>
                            <span>
                                [@spring.formInput "userFormBean.address.address3" /]<br />
                            </span>
                        </div>
                    [/#if]

                    [#if userFormBean.address.cityDistrictHidden]
                        [@spring.formHiddenInput "userFormBean.address.cityDistrict" /]
                    [#else]
                        <div class="span-21 prepend-2 ">
                            <span class="span-4 rightAlign">
                                <label for="address.city">[@spring.message "systemUsers.preview.city" /]:</label>
                            </span>
                            <span>
                                [@spring.formInput "userFormBean.address.cityDistrict" /]
                            </span>
                        </div>
                    [/#if]

                    [#if userFormBean.address.stateHidden]
                        [@spring.formHiddenInput "userFormBean.address.state" /]
                    [#else]
                        <div class="span-21 prepend-2 ">
                            <span class="span-4 rightAlign">
                                <label for="address.state">[@spring.message "systemUsers.preview.state" /]:</label>
                            </span>
                            <span>
                                [@spring.formInput "userFormBean.address.state" /]
                            </span>
                        </div>
                    [/#if]

                    [#if userFormBean.address.countryHidden]
                        [@spring.formHiddenInput "userFormBean.address.country" /]
                    [#else]
                        <div class="span-21 prepend-2 ">
                            <span class="span-4 rightAlign">
                                <label for="address.country">[@spring.message "systemUsers.preview.country" /]:</label>
                            </span>
                            <span>
                                    [@spring.formInput "userFormBean.address.country" /]
                            </span>
                        </div>
                    [/#if]

                    [#if userFormBean.address.postalCodeHidden]
                        [@spring.formHiddenInput "userFormBean.address.postalCode" /]
                    [#else]
                        <div class="span-21 prepend-2 ">
                            <span class="span-4 rightAlign">
                                <label for="address.postalcode">[@spring.message "systemUsers.preview.postalcode" /]:</label>
                            </span>
                            <span>
                                [@spring.formInput "userFormBean.address.postalCode" /]
                            </span>
                        </div>
                    [/#if]
                        <div class="span-21 prepend-2 ">
                            <span class="span-4 rightAlign">
                                <label for="address.telephone">[@spring.message "systemUsers.preview.telephone" /]:</label>
                            </span>
                            <span>
                                [@spring.formInput "userFormBean.address.telephoneNumber" /]
                            </span>
                        </div>
            </div>

            <div class="fontBold marginTop15">[@spring.message "systemUsers.preview.permissions" /]</div>

            <div class="prepend-1  span-24 last marginTop15">
                        <div class="span-21 prepend-2 ">
                            <span class="span-4 rightAlign">
                                <label for="userTitle">[@spring.message "systemUsers.preview.userTitle" /]:</label>
                            </span>
                            <span>
                                [@form.formSingleSelectWithPrompt "userFormBean.selectedUserTitle", userFormBean.userTitleOptions, "--selectone--" /]
                            </span>
                          </div>
                        <div class="span-21 prepend-2 ">
                            <span class="span-4 rightAlign">
                                <label for="userTitle"><span class="red">*</span>[@spring.message "systemUsers.preview.userHierarchy" /]:</label>
                            </span>
                            <span>
                                [@form.formSingleSelectWithPrompt "userFormBean.selectedUserHierarchy", userFormBean.userHierarchyOptions, "--selectone--" /]
                            </span>
                          </div>
                          <div class="span-21 prepend-2 ">
                                  <span class="span-4 rightAlign">[@spring.message "systemUsers.preview.roles" /]:</span>
                                <span class="span-4">
                                    [@spring.formMultiSelect "userFormBean.availableRoles", userFormBean.availableRolesOptions, "class=listSize" /]
                                </span>
                                <span class="span-3">
                                    <br />
                                    <input class="buttn2" name="add" style="width:78px;" type="button" id="roles.button.add"  value="[@spring.message "add"/] >>" onclick="moveOptions(this.form.availableRoles, this.form.selectedRoles);" />
                                    <br /><br />
                                    <input class="buttn2" name="remove" type="button" value="<< [@spring.message "remove"/]" onclick="moveOptions(this.form.selectedRoles, this.form.availableRoles);" />
                                </span>
                                <span class="span-4">
                                    [@spring.formMultiSelect "userFormBean.selectedRoles", userFormBean.selectedRolesOptions, "class=listSize" /]
                                </span>
                           </div>
            </div>

            <div class="fontBold marginTop15">[@spring.message "systemUsers.preview.logininfo" /]</div>

            <div class="prepend-1  span-21 last marginTop15">
                <div class="span-21 prepend-2 ">
                    <span class="span-4 rightAlign">
                        <label for="username"><span class="red">*</span>[@spring.message "systemUsers.preview.userName" /]:</label>
                    </span>
                    <span>
                        [@spring.formInput "userFormBean.username" /]
                    </span>
                </div>

                <div class="span-21 prepend-2 ">
                    <span class="span-4 rightAlign">
                        <label for="password"><span class="red">*</span>[@spring.message "systemUsers.preview.password" /]:</label>
                    </span>
                    <span>
                        [@spring.formPasswordInput "userFormBean.password" /]
                    </span>
                </div>

                <div class="span-21 prepend-2 ">
                    <span class="span-4 rightAlign">
                        <label for="confirmedPassword"><span class="red">*</span>[@spring.message "systemUsers.preview.confirmedPassword" /]:</label>
                    </span>
                    <span>
                        [@spring.formPasswordInput "userFormBean.confirmedPassword" /]
                    </span>
                </div>

            </div>

            <div class="fontBold marginTop15">[@spring.message "systemUsers.preview.additionalInformation" /]</div>

            <div class="prepend-1  span-21 last marginTop15">
                <div class="span-21 prepend-2 ">
                    <span class="span-4 rightAlign">
                        [#assign fieldNumber = 0]
                        [#assign dateFieldNumber = 0]
                        [#list userFormBean.customFields as additional]
                            [#assign fieldlabel = "customFields[" + fieldNumber + "].fieldValue"]
                            [#assign fieldvalue = "userFormBean.customFields[" + fieldNumber + "].fieldValue"]
                            [#assign datefieldvalueday = "userFormBean.customDateFields[" + dateFieldNumber + "].day"]
                            [#assign datefieldvaluemonth = "userFormBean.customDateFields[" + dateFieldNumber + "].month"]
                            [#assign datefieldvalueyear = "userFormBean.customDateFields[" + dateFieldNumber + "].year"]
                            [#switch additional.fieldType]
                                [#case 0]
                                [#case 1]
                                [#case 2]
                                <label for="${fieldlabel}">[#if additional.mandatory]<span class="red">*</span>[/#if]${additional.label}:</label>
                                [@spring.formInput fieldvalue /]<br />
                                [#break]
                                [#case 3]
                                <div>
                                <label>[#if additional.mandatory]<span class="red">*</span>[/#if]${additional.label}:</label>
                                <span>[@spring.formInput datefieldvalueday "size=1 maxlength=2" /]DD</span>
                                  <span>[@spring.formInput datefieldvaluemonth "size=1 maxlength=2" /]MM</span>
                                  <span>[@spring.formInput datefieldvalueyear "size=2 maxlength=4" /]YYYY</span>
                                  </div>
                                  <br />
                                  [#assign dateFieldNumber = dateFieldNumber + 1]
                                [#break]
                            [/#switch]
                            [#assign fieldNumber = fieldNumber + 1]
                        [/#list]
                     </span>
                  </div>
            </div>

            <div class="clear">&nbsp;</div>
                <hr />
            <div class="clear">&nbsp;</div>

            <div class="prepend-8">
		<table align="93%" border="0" cellpadding="0" cellspacing="0">
                <tr>
                  <td align="center"> &nbsp;
              <div class="row centered">
                <input class="buttn" type="submit" id="preview" name="_eventId_preview" value="[@spring.message "preview"/]" onclick="selectAllOptions(this.form.selectedRoles);selectAllOptions(this.form.availableRoles);" />
                <input class="buttn2" type="submit" id="cancel" name="_eventId_cancel" value="[@spring.message "cancel"/]" />
				</div>
				</td>
				</tr>
				</table>
            </div>
        </form>
      [/@layout.webflow]