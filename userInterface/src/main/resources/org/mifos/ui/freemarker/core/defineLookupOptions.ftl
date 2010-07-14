[#ftl]
[#import "spring.ftl" as spring]
[#import "blueprintmacros.ftl" as mifos]

[@mifos.header "title" /]
  [@mifos.topNavigationNoSecurity currentTab="Admin" /]
  <div class="sidebar ht1500">
  [#include "adminLeftPane.ftl" ]
  </div>
  <div class="content leftMargin180">
  <div class="bluedivs paddingLeft"><a href="admin.ftl">Admin</a>&nbsp;/&nbsp;<span class="fontBold">Define Lookup Options</span></div>
  	<form method="" action="" name="formname">
    <div class="span-21">  		
        <div class="clear">&nbsp;</div>
        <div ><span class="orangeheading">[@spring.message "defineLookupOptions" /]</span></div>
        <p class="error"></p>
        <div class="prepend-1 span-19 last">
        	<div class="span-19 marginBottom"><span class="span-4 rightAlign">[@spring.message "salutation" /]&nbsp;:</span>
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
                     <span class="span-3"><br /><input type="button" class="buttn2" value="[@spring.message "add"/]" onclick="location.href='salutation.html'"/><br /><input type="button" class="buttn2" value="[@spring.message "edit"/]" onclick="location.href='salutation.html'"/></span>
  			</div>
            <div class="clear">&nbsp;</div>	
            <div class="span-19 marginBottom"><span class="span-4 rightAlign">[@spring.message "userTitle"/]&nbsp;:</span>
    				<span class="span-5"><select name="userTitle" size="5" style="width:140px;">
                    		<option>Officer</option>
                            <option>Manager</option>
                            <option>Director</option>
                            <option>Loan officer</option>
                        </select></span>
                     <span class="span-3"><br /><input type="button" class="buttn2" value="[@spring.message "add"/]" onclick="location.href='userTitle.html'"/><br /><input type="button" class="buttn2" value="[@spring.message "edit"/]" onclick="location.href='userTitle.html'" /></span>
  			</div>
             <div class="clear">&nbsp;</div>
            <div class="span-19 marginBottom"><span class="span-4 rightAlign">[@spring.message "maritalStatus"/]&nbsp;:</span>
    				<span class="span-5"><select name="maritalstatus" size="5" style="width:140px;">
                    		<option>Single</option>
                            <option>Married</option>
                            <option>Divorced</option>
                            <option>Widowed</option>
                            <option>Seperated</option>
                            <option>Mrs</option>
                        </select></span>
                     <span class="span-3"><br /><input type="button" class="buttn2" value="[@spring.message "add"/]" onclick="location.href='maritalstatus.html'"/><br /><input type="button" class="buttn2" value="[@spring.message "edit"/]" onclick="location.href='maritalstatus.html'"/></span>
  			</div>
             <div class="clear">&nbsp;</div>
            <div class="span-19 marginBottom"><span class="span-4 rightAlign">[@spring.message "ethnicity"/]&nbsp;:</span>
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
                     <span class="span-3"><br /><input type="button" class="buttn2" value="[@spring.message "add"/]" onclick="location.href='ethnicity.html'"/><br /><input type="button" class="buttn2" value="[@spring.message "edit"/]" onclick="location.href='ethnicity.html'"/></span>
  			</div>
             <div class="clear">&nbsp;</div>
            <div class="span-19 marginBottom"><span class="span-4 rightAlign">[@spring.message "educationlevel"/]&nbsp;:</span>
    				<span class="span-5"><select name="educationLevel" size="5" style="width:140px;">
                    		<option>None</option>
                            <option>Primary Education</option>
                            <option>Secondary Education</option>
                            <option>High School Graduate</option>
                            <option>College Degree</option>
                            <option>M.A</option>
                            <option>PhD</option>
                        </select></span>
                     <span class="span-3"><br /><input type="button" class="buttn2" value="[@spring.message "add"/]" onclick="location.href='educationLevel.html'"/><br /><input type="button" class="buttn2" value="[@spring.message "edit"/]" onclick="location.href='educationLevel.html'"/></span>
  			</div>
             <div class="clear">&nbsp;</div>
            <div class="span-19 marginBottom"><span class="span-4 rightAlign">[@spring.message "citizenship"/]&nbsp;:</span>
    				<span class="span-5"><select name="citizenship" size="5" style="width:140px;">
                    		<option>Natural Born Citizen</option>
                            <option>Immigrant with Citizenship</option>
                            <option>Non-Citizen</option>
                        </select></span>
                     <span class="span-3"><br /><input type="button" class="buttn2" value="[@spring.message "add"/]" onclick="location.href='citizenship.html'"/><br /><input type="button" class="buttn2" value="[@spring.message "edit"/]" onclick="location.href='citizenship.html'"/></span>
  			</div>
             <div class="clear">&nbsp;</div>
            <div class="span-19 marginBottom"><span class="span-4 rightAlign">[@spring.message "businessActivity"/]&nbsp;:</span>
    				<span class="span-5"><select name="businessActivity" size="5" style="width:140px;">
                    		<option>Daily Labor</option>
                            <option>Agriculture</option>
                            <option>Animal Husbandry</option>
                            <option>Micro Enterprise</option>
                            <option>Production</option>
                            <option>Hawking</option>
                            <option>Small shop</option>
                        </select></span>
                     <span class="span-3"><br /><input type="button" class="buttn2" value="[@spring.message "add"/]" onclick="location.href='businessActivity.html'"/><br /><input type="button" class="buttn2" value="[@spring.message "edit"/]" onclick="location.href='businessActivity.html'"/></span>
  			</div>
             <div class="clear">&nbsp;</div>
            <div class="span-19 marginBottom"><span class="span-4 rightAlign">[@spring.message "purposeofLoan"/]&nbsp;:</span>
    				<span class="span-5"><select name="purposeOfLoan" size="5" style="width:140px;">
                    		<option>Animal Husbandry</option>
                            <option>Animal Purchase</option>
                            <option>Equipment Purchase</option>
                            <option>Materials Purchase</option>
                        </select></span>
                     <span class="span-3"><br /><input type="button" class="buttn2" value="[@spring.message "add"/]" onclick="location.href='purposeOfLoan.html'"/><br /><input type="button" class="buttn2" value="[@spring.message "edit"/]" onclick="location.href='purposeOfLoan.html'"/></span>
  			</div>
             <div class="clear">&nbsp;</div>
            <div class="span-19 marginBottom"><span class="span-4 rightAlign">[@spring.message "handicapped" /]&nbsp;:</span>
    				<span class="span-5"><select name="handicapped" size="5" style="width:140px;">
                    		<option>Yes</option>
                            <option>No</option>
                        </select></span>
                     <span class="span-3"><br /><input type="button" class="buttn2" value="[@spring.message "add"/]" onclick="location.href='handicapped.html'"/><br /><input type="button" class="buttn2" value="[@spring.message "edit"/]" onclick="location.href='handicapped.html'"/></span>
  			</div>
             <div class="clear">&nbsp;</div>
            <div class="span-19 marginBottom"><span class="span-4 rightAlign">[@spring.message "collateralType"/]&nbsp;:</span>
    				<span class="span-5"><select name="collateralType" size="5" style="width:140px;">
                    		<option>None</option>
                            <option>Savings Fund</option>
                            <option>Equipment</option>
                            <option>sf</option>
                        </select></span>
                     <span class="span-3"><br /><input type="button" class="buttn2" value="[@spring.message "add"/]" onclick="location.href='collateralType.html'"/><br /><input type="button" class="buttn2" value="[@spring.message "edit"/]" onclick="location.href='collateralType.html'"/></span>
  			</div>
             <div class="clear">&nbsp;</div>
            <div class="span-19 marginBottom"><span class="span-4 rightAlign">[@spring.message "officerTitle"/]&nbsp;:</span>
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
                     <span class="span-3"><br /><input type="button" class="buttn2" value="[@spring.message "add"/]" onclick="location.href='officerTitle.html'"/><br /><input type="button" class="buttn2" value="[@spring.message "edit"/]" onclick="location.href='officerTitle.html'"/></span>
  			</div>
             <div class="clear">&nbsp;</div>
            <div class="span-19 marginBottom"><span class="span-4 rightAlign">[@spring.message "paymentModes"/]&nbsp;:</span>
    				<span class="span-5"><select name="paymentMode" size="5" style="width:140px;">
                    		<option>cash</option>
                            <option>Voucher</option>
                            <option>Cheque</option>
                        </select></span>
                     <span class="span-3"><br /><input type="button" class="buttn2" value="[@spring.message "add"/]" onclick="location.href='paymentMode.html'"/><br /><input type="button" class="buttn2" value="Edit" onclick="location.href='paymentMode.html'"/></span>
  			</div>
        </div>
        <div class="clear">&nbsp;</div>
        <hr />
	</div>
   	</form> 
  </div>
  [@mifos.footer /]