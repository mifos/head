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
  <div class="content">
  <div class="bluedivs paddingLeft"><a href="admin.ftl">Admin</a>&nbsp;/&nbsp;<span class="fontBold">Define Lookup Options</span></div>
      <form method="" action="" name="formname">
    <div class="span-21">
        <div class="clear">&nbsp;</div>
        <div ><span class="orangeheading">[@spring.message "dataDisplayAndRules.defineLookupOptions.defineLookupOptions" /]</span></div>
        <p class="error"></p>
        <div class="prepend-1 span-19 last">
            <div class="span-19 marginBottom"><span class="span-4 rightAlign">[@spring.message "dataDisplayAndRules.defineLookupOptions.salutation" /]&nbsp;:</span>
                    <span class="span-5"><select name="salutation" size="5" style="width:140px;">
                            <option>[@spring.message "dataDisplayAndRules.defineLookupOptions.ms"/]</option>
                            <option>[@spring.message "dataDisplayAndRules.defineLookupOptions.sir"/]</option>
                            <option>[@spring.message "dataDisplayAndRules.defineLookupOptions.mrs"/]</option>
                            <option>[@spring.message "dataDisplayAndRules.defineLookupOptions.mr"/]</option>
                            <option>[@spring.message "dataDisplayAndRules.defineLookupOptions.sri"/]</option>
                            <option>[@spring.message "dataDisplayAndRules.defineLookupOptions.frank"/]</option>
                            <option>[@spring.message "dataDisplayAndRules.defineLookupOptions.rev"/]</option>
                            <option>[@spring.message "dataDisplayAndRules.defineLookupOptions.madam"/]</option>
                            <option>[@spring.message "dataDisplayAndRules.defineLookupOptions.cdr"/]</option>
                        </select></span>
                     <span class="span-3"><br /><input type="button" class="buttn2" value="[@spring.message "add"/]"/><br /><input type="button" class="buttn2" value="[@spring.message "edit"/]"/></span>
              </div>
            <div class="clear">&nbsp;</div>
            <div class="span-19 marginBottom"><span class="span-4 rightAlign">[@spring.message "dataDisplayAndRules.defineLookupOptions.userTitle"/]&nbsp;:</span>
                    <span class="span-5"><select name="userTitle" size="5" style="width:140px;">
                            <option>[@spring.message "dataDisplayAndRules.defineLookupOptions.officer"/]</option>
                            <option>[@spring.message "dataDisplayAndRules.defineLookupOptions.manager"/]</option>
                            <option>[@spring.message "dataDisplayAndRules.defineLookupOptions.director"/]</option>
                            <option>[@spring.message "dataDisplayAndRules.defineLookupOptions.loanOfficer"/]</option>
                        </select></span>
                     <span class="span-3"><br /><input type="button" class="buttn2" value="[@spring.message "add"/]" /><br /><input type="button" class="buttn2" value="[@spring.message "edit"/]"  /></span>
              </div>
             <div class="clear">&nbsp;</div>
            <div class="span-19 marginBottom"><span class="span-4 rightAlign">[@spring.message "dataDisplayAndRules.defineLookupOptions.maritalStatus"/]&nbsp;:</span>
                    <span class="span-5"><select name="maritalstatus" size="5" style="width:140px;">
                            <option>[@spring.message "dataDisplayAndRules.defineLookupOptions.single"/]</option>
                            <option>[@spring.message "dataDisplayAndRules.defineLookupOptions.married"/]</option>
                            <option>[@spring.message "dataDisplayAndRules.defineLookupOptions.divorced"/]</option>
                            <option>[@spring.message "dataDisplayAndRules.defineLookupOptions.widowed"/]</option>
                            <option>[@spring.message "dataDisplayAndRules.defineLookupOptions.seperated"/]</option>
                            <option>[@spring.message "dataDisplayAndRules.defineLookupOptions.mrs"/]</option>
                        </select></span>
                     <span class="span-3"><br /><input type="button" class="buttn2" value="[@spring.message "add"/]" /><br /><input type="button" class="buttn2" value="[@spring.message "edit"/]" /></span>
              </div>
             <div class="clear">&nbsp;</div>
            <div class="span-19 marginBottom"><span class="span-4 rightAlign">[@spring.message "dataDisplayAndRules.defineLookupOptions.ethnicity"/]&nbsp;:</span>
                    <span class="span-5"><select name="ethnicity" size="5" style="width:140px;">
                            <option>[@spring.message "dataDisplayAndRules.defineLookupOptions.filipino"/]</option>
                            <option>[@spring.message "dataDisplayAndRules.defineLookupOptions.indian"/]</option>
                            <option>[@spring.message "dataDisplayAndRules.defineLookupOptions.tunisian"/]</option>
                            <option>[@spring.message "dataDisplayAndRules.defineLookupOptions.kenyan"/]</option>
                            <option>[@spring.message "dataDisplayAndRules.defineLookupOptions.american"/]</option>
                            <option>[@spring.message "dataDisplayAndRules.defineLookupOptions.dinka"/]</option>
                            <option>[@spring.message "dataDisplayAndRules.defineLookupOptions.sudanese"/]</option>
                            <option>[@spring.message "dataDisplayAndRules.defineLookupOptions.ghanaian"/]</option>
                        </select></span>
                     <span class="span-3"><br /><input type="button" class="buttn2" value="[@spring.message "add"/]"/><br /><input type="button" class="buttn2" value="[@spring.message "edit"/]"/></span>
              </div>
             <div class="clear">&nbsp;</div>
            <div class="span-19 marginBottom"><span class="span-4 rightAlign">[@spring.message "dataDisplayAndRules.defineLookupOptions.educationlevel"/]&nbsp;:</span>
                    <span class="span-5"><select name="educationLevel" size="5" style="width:140px;">
                            <option>[@spring.message "dataDisplayAndRules.defineLookupOptions.none"/]</option>
                            <option>[@spring.message "dataDisplayAndRules.defineLookupOptions.primaryEducation"/]</option>
                            <option>[@spring.message "dataDisplayAndRules.defineLookupOptions.secondaryEducation"/]</option>
                            <option>[@spring.message "dataDisplayAndRules.defineLookupOptions.highSchoolGraduate"/]</option>
                            <option>[@spring.message "dataDisplayAndRules.defineLookupOptions.collegeDegree"/]</option>
                            <option>[@spring.message "dataDisplayAndRules.defineLookupOptions.ma"/]</option>
                            <option>[@spring.message "dataDisplayAndRules.defineLookupOptions.phD"/]</option>
                        </select></span>
                     <span class="span-3"><br /><input type="button" class="buttn2" value="[@spring.message "add"/]"/><br /><input type="button" class="buttn2" value="[@spring.message "edit"/]"/></span>
              </div>
             <div class="clear">&nbsp;</div>
            <div class="span-19 marginBottom"><span class="span-4 rightAlign">[@spring.message "dataDisplayAndRules.defineLookupOptions.citizenship"/]&nbsp;:</span>
                    <span class="span-5"><select name="citizenship" size="5" style="width:140px;">
                            <option>[@spring.message "dataDisplayAndRules.defineLookupOptions.naturalBornCitizen"/]</option>
                            <option>[@spring.message "dataDisplayAndRules.defineLookupOptions.immigrantWithCitizenship"/]</option>
                            <option>[@spring.message "dataDisplayAndRules.defineLookupOptions.nonCitizen"/]</option>
                        </select></span>
                     <span class="span-3"><br /><input type="button" class="buttn2" value="[@spring.message "add"/]"/><br /><input type="button" class="buttn2" value="[@spring.message "edit"/]"/></span>
              </div>
             <div class="clear">&nbsp;</div>
            <div class="span-19 marginBottom"><span class="span-4 rightAlign">[@spring.message "dataDisplayAndRules.defineLookupOptions.businessActivity"/]&nbsp;:</span>
                    <span class="span-5"><select name="businessActivity" size="5" style="width:140px;">
                            <option>[@spring.message "dataDisplayAndRules.defineLookupOptions.dailyLabor"/]</option>
                            <option>[@spring.message "dataDisplayAndRules.defineLookupOptions.agriculture"/]</option>
                            <option>[@spring.message "dataDisplayAndRules.defineLookupOptions.animalHusbandry"/]</option>
                            <option>[@spring.message "dataDisplayAndRules.defineLookupOptions.microEnterprise"/]</option>
                            <option>[@spring.message "dataDisplayAndRules.defineLookupOptions.production"/]</option>
                            <option>[@spring.message "dataDisplayAndRules.defineLookupOptions.hawking"/]</option>
                            <option>[@spring.message "dataDisplayAndRules.defineLookupOptions.smallShop"/]</option>
                        </select></span>
                     <span class="span-3"><br /><input type="button" class="buttn2" value="[@spring.message "add"/]"/><br /><input type="button" class="buttn2" value="[@spring.message "edit"/]" /></span>
              </div>
             <div class="clear">&nbsp;</div>
            <div class="span-19 marginBottom"><span class="span-4 rightAlign">[@spring.message "dataDisplayAndRules.defineLookupOptions.purposeofLoan"/]&nbsp;:</span>
                    <span class="span-5"><select name="purposeOfLoan" size="5" style="width:140px;">
                            <option>[@spring.message "dataDisplayAndRules.defineLookupOptions.animalHusbandry"/]</option>
                            <option>[@spring.message "dataDisplayAndRules.defineLookupOptions.animalPurchase"/]</option>
                            <option>[@spring.message "dataDisplayAndRules.defineLookupOptions.equipmentPurchase"/]</option>
                            <option>[@spring.message "dataDisplayAndRules.defineLookupOptions.materialsPurchase"/]</option>
                        </select></span>
                     <span class="span-3"><br /><input type="button" class="buttn2" value="[@spring.message "add"/]"/><br /><input type="button" class="buttn2" value="[@spring.message "edit"/]" /></span>
              </div>
             <div class="clear">&nbsp;</div>
            <div class="span-19 marginBottom"><span class="span-4 rightAlign">[@spring.message "dataDisplayAndRules.defineLookupOptions.handicapped" /]&nbsp;:</span>
                    <span class="span-5"><select name="handicapped" size="5" style="width:140px;">
                            <option>[@spring.message "boolean.yes"/]</option>
                            <option>[@spring.message "boolean.no"/]</option>
                        </select></span>
                     <span class="span-3"><br /><input type="button" class="buttn2" value="[@spring.message "add"/]"/><br /><input type="button" class="buttn2" value="[@spring.message "edit"/]"/></span>
              </div>
             <div class="clear">&nbsp;</div>
            <div class="span-19 marginBottom"><span class="span-4 rightAlign">[@spring.message "dataDisplayAndRules.defineLookupOptions.collateralType"/]&nbsp;:</span>
                    <span class="span-5"><select name="collateralType" size="5" style="width:140px;">
                            <option>[@spring.message "dataDisplayAndRules.defineLookupOptions.none"/]</option>
                            <option>[@spring.message "dataDisplayAndRules.defineLookupOptions.savingsFund"/]</option>
                            <option>[@spring.message "dataDisplayAndRules.defineLookupOptions.equipment"/]</option>
                            <option>[@spring.message "dataDisplayAndRules.defineLookupOptions.sf"/]</option>
                        </select></span>
                     <span class="span-3"><br /><input type="button" class="buttn2" value="[@spring.message "add"/]"/><br /><input type="button" class="buttn2" value="[@spring.message "edit"/]"/></span>
              </div>
             <div class="clear">&nbsp;</div>
            <div class="span-19 marginBottom"><span class="span-4 rightAlign">[@spring.message "dataDisplayAndRules.defineLookupOptions.officerTitle"/]&nbsp;:</span>
                    <span class="span-5"><select name="officerTitle" size="5" style="width:140px;">
                            <option>[@spring.message "dataDisplayAndRules.defineLookupOptions.president"/]</option>
                            <option>[@spring.message "dataDisplayAndRules.defineLookupOptions.vicePresident"/]</option>
                            <option>[@spring.message "dataDisplayAndRules.defineLookupOptions.secretary"/]</option>
                            <option>[@spring.message "dataDisplayAndRules.defineLookupOptions.treasurer"/]</option>
                            <option>[@spring.message "dataDisplayAndRules.defineLookupOptions.tresure"/]</option>
                            <option>[@spring.message "dataDisplayAndRules.defineLookupOptions.coSecretary"/]</option>
                            <option>[@spring.message "dataDisplayAndRules.defineLookupOptions.coPresident"/]</option>
                            <option>[@spring.message "dataDisplayAndRules.defineLookupOptions.creditOfficer"/]</option>
                            <option>[@spring.message "dataDisplayAndRules.defineLookupOptions.financialTreasurer"/]</option>
                        </select></span>
                     <span class="span-3"><br /><input type="button" class="buttn2" value="[@spring.message "add"/]"/><br /><input type="button" class="buttn2" value="[@spring.message "edit"/]"/></span>
              </div>
             <div class="clear">&nbsp;</div>
            <div class="span-19 marginBottom"><span class="span-4 rightAlign">[@spring.message "dataDisplayAndRules.defineLookupOptions.paymentModes"/]&nbsp;:</span>
                    <span class="span-5"><select name="paymentMode" size="5" style="width:140px;">
                            <option>[@spring.message "OrganizationPreferences.defineAcceptedPaymentTypes.cash"/]</option>
                            <option>[@spring.message "OrganizationPreferences.defineAcceptedPaymentTypes.voucher"/]</option>
                            <option>[@spring.message "OrganizationPreferences.defineAcceptedPaymentTypes.cheque"/]</option>
                        </select></span>
                     <span class="span-3"><br /><input type="button" class="buttn2" value="[@spring.message "add"/]"/><br /><input type="button" class="buttn2" value="[@spring.message "edit"/]"/></span>
              </div>
        </div>
        <div class="clear">&nbsp;</div>
        <hr />
    </div>
       </form>
  </div>
[/@adminLeftPaneLayout]