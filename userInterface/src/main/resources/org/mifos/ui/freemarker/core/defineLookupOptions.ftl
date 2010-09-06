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
                    		<option>Ms</option>
                            <option>Sir</option>
                            <option>Mrs</option>
                            <option>Mr</option>
                            <option>Sri</option>
                            <option>Frank</option>
                            <option>Rev.</option>
                            <option>Madam</option>
                            <option>Cdr.</option>
                        </select></span>
                     <span class="span-3"><br /><input type="button" class="buttn2" value="[@spring.message "add"/]"/><br /><input type="button" class="buttn2" value="[@spring.message "edit"/]"/></span>
  			</div>
            <div class="clear">&nbsp;</div>	
            <div class="span-19 marginBottom"><span class="span-4 rightAlign">[@spring.message "dataDisplayAndRules.defineLookupOptions.userTitle"/]&nbsp;:</span>
    				<span class="span-5"><select name="userTitle" size="5" style="width:140px;">
                    		<option>Officer</option>
                            <option>Manager</option>
                            <option>Director</option>
                            <option>Loan officer</option>
                        </select></span>
                     <span class="span-3"><br /><input type="button" class="buttn2" value="[@spring.message "add"/]" /><br /><input type="button" class="buttn2" value="[@spring.message "edit"/]"  /></span>
  			</div>
             <div class="clear">&nbsp;</div>
            <div class="span-19 marginBottom"><span class="span-4 rightAlign">[@spring.message "dataDisplayAndRules.defineLookupOptions.maritalStatus"/]&nbsp;:</span>
    				<span class="span-5"><select name="maritalstatus" size="5" style="width:140px;">
                    		<option>Single</option>
                            <option>Married</option>
                            <option>Divorced</option>
                            <option>Widowed</option>
                            <option>Seperated</option>
                            <option>Mrs</option>
                        </select></span>
                     <span class="span-3"><br /><input type="button" class="buttn2" value="[@spring.message "add"/]" /><br /><input type="button" class="buttn2" value="[@spring.message "edit"/]" /></span>
  			</div>
             <div class="clear">&nbsp;</div>
            <div class="span-19 marginBottom"><span class="span-4 rightAlign">[@spring.message "dataDisplayAndRules.defineLookupOptions.ethnicity"/]&nbsp;:</span>
    				<span class="span-5"><select name="ethnicity" size="5" style="width:140px;">
                    		<option>Filipino</option>
                            <option>Indian</option>
                            <option>Tunisian</option>
                            <option>Kenyan</option>
                            <option>American</option>
                            <option>Dinka</option>
                            <option>Sudanese</option>
                            <option>Ghanaian</option>
                        </select></span>
                     <span class="span-3"><br /><input type="button" class="buttn2" value="[@spring.message "add"/]"/><br /><input type="button" class="buttn2" value="[@spring.message "edit"/]"/></span>
  			</div>
             <div class="clear">&nbsp;</div>
            <div class="span-19 marginBottom"><span class="span-4 rightAlign">[@spring.message "dataDisplayAndRules.defineLookupOptions.educationlevel"/]&nbsp;:</span>
    				<span class="span-5"><select name="educationLevel" size="5" style="width:140px;">
                    		<option>None</option>
                            <option>Primary Education</option>
                            <option>Secondary Education</option>
                            <option>High School Graduate</option>
                            <option>College Degree</option>
                            <option>M.A</option>
                            <option>PhD</option>
                        </select></span>
                     <span class="span-3"><br /><input type="button" class="buttn2" value="[@spring.message "add"/]"/><br /><input type="button" class="buttn2" value="[@spring.message "edit"/]"/></span>
  			</div>
             <div class="clear">&nbsp;</div>
            <div class="span-19 marginBottom"><span class="span-4 rightAlign">[@spring.message "dataDisplayAndRules.defineLookupOptions.citizenship"/]&nbsp;:</span>
    				<span class="span-5"><select name="citizenship" size="5" style="width:140px;">
                    		<option>Natural Born Citizen</option>
                            <option>Immigrant with Citizenship</option>
                            <option>Non-Citizen</option>
                        </select></span>
                     <span class="span-3"><br /><input type="button" class="buttn2" value="[@spring.message "add"/]"/><br /><input type="button" class="buttn2" value="[@spring.message "edit"/]"/></span>
  			</div>
             <div class="clear">&nbsp;</div>
            <div class="span-19 marginBottom"><span class="span-4 rightAlign">[@spring.message "dataDisplayAndRules.defineLookupOptions.businessActivity"/]&nbsp;:</span>
    				<span class="span-5"><select name="businessActivity" size="5" style="width:140px;">
                    		<option>Daily Labor</option>
                            <option>Agriculture</option>
                            <option>Animal Husbandry</option>
                            <option>Micro Enterprise</option>
                            <option>Production</option>
                            <option>Hawking</option>
                            <option>Small shop</option>
                        </select></span>
                     <span class="span-3"><br /><input type="button" class="buttn2" value="[@spring.message "add"/]"/><br /><input type="button" class="buttn2" value="[@spring.message "edit"/]" /></span>
  			</div>
             <div class="clear">&nbsp;</div>
            <div class="span-19 marginBottom"><span class="span-4 rightAlign">[@spring.message "dataDisplayAndRules.defineLookupOptions.purposeofLoan"/]&nbsp;:</span>
    				<span class="span-5"><select name="purposeOfLoan" size="5" style="width:140px;">
                    		<option>Animal Husbandry</option>
                            <option>Animal Purchase</option>
                            <option>Equipment Purchase</option>
                            <option>Materials Purchase</option>
                        </select></span>
                     <span class="span-3"><br /><input type="button" class="buttn2" value="[@spring.message "add"/]"/><br /><input type="button" class="buttn2" value="[@spring.message "edit"/]" /></span>
  			</div>
             <div class="clear">&nbsp;</div>
            <div class="span-19 marginBottom"><span class="span-4 rightAlign">[@spring.message "dataDisplayAndRules.defineLookupOptions.handicapped" /]&nbsp;:</span>
    				<span class="span-5"><select name="handicapped" size="5" style="width:140px;">
                    		<option>Yes</option>
                            <option>No</option>
                        </select></span>
                     <span class="span-3"><br /><input type="button" class="buttn2" value="[@spring.message "add"/]"/><br /><input type="button" class="buttn2" value="[@spring.message "edit"/]"/></span>
  			</div>
             <div class="clear">&nbsp;</div>
            <div class="span-19 marginBottom"><span class="span-4 rightAlign">[@spring.message "dataDisplayAndRules.defineLookupOptions.collateralType"/]&nbsp;:</span>
    				<span class="span-5"><select name="collateralType" size="5" style="width:140px;">
                    		<option>None</option>
                            <option>Savings Fund</option>
                            <option>Equipment</option>
                            <option>sf</option>
                        </select></span>
                     <span class="span-3"><br /><input type="button" class="buttn2" value="[@spring.message "add"/]"/><br /><input type="button" class="buttn2" value="[@spring.message "edit"/]"/></span>
  			</div>
             <div class="clear">&nbsp;</div>
            <div class="span-19 marginBottom"><span class="span-4 rightAlign">[@spring.message "dataDisplayAndRules.defineLookupOptions.officerTitle"/]&nbsp;:</span>
    				<span class="span-5"><select name="officerTitle" size="5" style="width:140px;">
                    		<option>President</option>
                            <option>Vice-President</option>
                            <option>Secretary</option>
                            <option>Treasurer</option>
                            <option>tresure</option>
                            <option>Co-secretary</option>
                            <option>Co-President</option>
                            <option>Credit officer</option>
                            <option>Financial Treasurer</option>
                        </select></span>
                     <span class="span-3"><br /><input type="button" class="buttn2" value="[@spring.message "add"/]"/><br /><input type="button" class="buttn2" value="[@spring.message "edit"/]"/></span>
  			</div>
             <div class="clear">&nbsp;</div>
            <div class="span-19 marginBottom"><span class="span-4 rightAlign">[@spring.message "dataDisplayAndRules.defineLookupOptions.paymentModes"/]&nbsp;:</span>
    				<span class="span-5"><select name="paymentMode" size="5" style="width:140px;">
                    		<option>cash</option>
                            <option>Voucher</option>
                            <option>Cheque</option>
                        </select></span>
                     <span class="span-3"><br /><input type="button" class="buttn2" value="[@spring.message "add"/]"/><br /><input type="button" class="buttn2" value="Edit"/></span>
  			</div>
        </div>
        <div class="clear">&nbsp;</div>
        <hr />
	</div>
   	</form> 
  </div>
[/@adminLeftPaneLayout]