[#ftl]
[#import "spring.ftl" as spring]
[#import "blueprintmacros.ftl" as mifos]
[#import "macros.ftl" as mifosmacros]

[@mifos.header "title" /]
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

[@mifos.topNavigationNoSecurity currentTab="Admin" /]
 <!--  Main Content Begins-->
  <div class="content marginAuto">
    <div class="borders span-22">
      <div class="borderbtm span-22">
        <p class="span-6 arrowIMG1 orangeheading ">[@spring.message "systemUsers.defineNewSystemUser.chooseOffice"/]</p>
        <p class="span-6 arrowIMG orangeheading ">[@spring.message "systemUsers.defineNewSystemUser.userInformation"/]</p>
        <p class="span-3 arrowIMG1 orangeheading last">[@spring.message "review&Submit"/]</p>
      </div>
      <div class="subcontent">
        <p class="font15"><span class="fontBold">[@spring.message "systemUsers.defineNewSystemUser.addanewuser"/]</span>&nbsp;-&nbsp;<span class="orangeheading">[@spring.message "systemUsers.defineNewSystemUser.enterUserInformation"/]</span></p>
        <p>[@spring.message "systemUsers.defineNewSystemUser.infomessage"/]</p>
        <span>*</span><span>[@spring.message "manageProducts.defineProductmix.fieldsmarkedwithanasteriskarerequired"/]</span>
        
        <form name="enterUserDetails" method="post" action="user.ftl?execution=${flowExecutionKey}">
        	<div id="allErrorsDiv" class="allErrorsDiv">
            	[@mifosmacros.showAllErrors "userFormBean.*"/]
        	</div>
        	<div class="fontBold">[@spring.message "systemUsers.preview.office" /]</div>
	        [@spring.formHiddenInput "userFormBean.officeId" /]
	        <div><span>[@spring.message "systemUsers.defineNewSystemUser.form.office" /]:</span>
	        <span>${userFormBean.officeName}</span></div>
	        
	        <div class="fontBold">[@spring.message "systemUsers.preview.userdetails" /]</div>
	        <label for="firstName"><span class="red">*</span>[@spring.message "systemUsers.preview.firstName" /]:</label>
	        [@spring.formInput "userFormBean.firstName" /]<br />
	        
	        <label for="middleName">[@spring.message "systemUsers.preview.middleName" /]:</label>
	        [@spring.formInput "userFormBean.middleName" /] <br />
	        
	        <label for="secondLastName">[@spring.message "systemUsers.preview.secondLastName" /]:</label>
	        [@spring.formInput "userFormBean.secondLastName" /]<br />
	        
	        <label for="lastName"><span class="red">*</span>[@spring.message "systemUsers.preview.lastName" /]:</label>
	        [@spring.formInput "userFormBean.lastName" /]<br />
	        
	        <label for="governmentId">[@spring.message "systemUsers.preview.governmentID" /]:</label>
	        <span>${userFormBean.governmentId}</span><br />
	        
	        <label for="email">[@spring.message "systemUsers.preview.email" /]:</label>
	        [@spring.formInput "userFormBean.email" /]<br />
	        
	        <div>
	        	<span><span class="red">*</span>[@spring.message "systemUsers.preview.dateofBirth" /]:</span>
	        	<span>${userFormBean.dateOfBirth}</span>
	        </div>
	        
	        <label for="selectedMaritalStatus">[@spring.message "systemUsers.preview.maritalStatus" /]:</label>
	        [@mifos.formSingleSelectWithPrompt "userFormBean.selectedMaritalStatus", userFormBean.maritalStatusOptions, "--select one--" /]<br />
	        
	        <label for="selectedGender"><span class="red">*</span>[@spring.message "systemUsers.preview.gender" /]:</label>
	        [@mifos.formSingleSelectWithPrompt "userFormBean.selectedGender", userFormBean.genderOptions, "--select one--" /]<br />
	        
	        <label for="selectedPreferredLanguage">[@spring.message "systemUsers.preview.languagePreferred" /]:</label>
	        [@mifos.formSingleSelectWithPrompt "userFormBean.selectedPreferredLanguage", userFormBean.preferredLanguageOptions, "--select one--" /]<br />
	        
	         <div>
	        	<span>[@spring.message "systemUsers.preview.mFIJoiningDate" /]:</span>
	        	<span>${userFormBean.mfiJoiningDate}</span>
	        </div>
	        
	        <div class="fontBold">[@spring.message "systemUsers.preview.address" /]</div>
	        
	        <label for="address.address1">[#if userFormBean.address.address1Mandatory]<span class="red">*</span>[/#if][@spring.message "systemUsers.preview.address1" /]:</label>
	        [@spring.formInput "userFormBean.address.address1" /]<br />
	        
	        [#if userFormBean.address.address2Hidden]
	        	[@spring.formHiddenInput "userFormBean.address.address2" /]
	        [#else]
	        	<label for="address.address2">[@spring.message "systemUsers.preview.address2" /]:</label>
	        	[@spring.formInput "userFormBean.address.address2" /]<br />
	        [/#if]

	         [#if userFormBean.address.address3Hidden]
	        	[@spring.formHiddenInput "userFormBean.address.address3" /]
	        [#else]	        
	        	<label for="address.address3">[@spring.message "systemUsers.preview.address3" /]:</label>
	        	[@spring.formInput "userFormBean.address.address3" /]<br />
	        [/#if]
	        
	        [#if userFormBean.address.cityDistrictHidden]
	        	[@spring.formHiddenInput "userFormBean.address.cityDistrict" /]
	        [#else]	        
	        <label for="address.city">[@spring.message "systemUsers.preview.city" /]:</label>
	        [@spring.formInput "userFormBean.address.cityDistrict" /]<br />
	        [/#if]
	        
	        [#if userFormBean.address.stateHidden]
	        	[@spring.formHiddenInput "userFormBean.address.state" /]
	        [#else]
	        <label for="address.state">[@spring.message "systemUsers.preview.state" /]:</label>
	        [@spring.formInput "userFormBean.address.state" /]<br />
	        [/#if]
	        
	        [#if userFormBean.address.countryHidden]
	        	[@spring.formHiddenInput "userFormBean.address.country" /]
	        [#else]
	        <label for="address.country">[@spring.message "systemUsers.preview.country" /]:</label>
	        [@spring.formInput "userFormBean.address.country" /]<br />
	        [/#if]
	        
	        [#if userFormBean.address.postalCodeHidden]
	        	[@spring.formHiddenInput "userFormBean.address.postalCode" /]
	        [#else]
	        <label for="address.postalcode">[@spring.message "systemUsers.preview.postalcode" /]:</label>
	        [@spring.formInput "userFormBean.address.postalCode" /]<br />
	        [/#if]
	        
	        <label for="address.telephone">[@spring.message "systemUsers.preview.telephone" /]:</label>
	        [@spring.formInput "userFormBean.address.telephoneNumber" /]<br />
	        
	        <div class="fontBold">[@spring.message "systemUsers.preview.permissions" /]</div>
	        <label for="userTitle">[@spring.message "systemUsers.preview.userTitle" /]:</label>
	        [@mifos.formSingleSelectWithPrompt "userFormBean.selectedUserTitle", userFormBean.userTitleOptions, "--select one--" /]<br />
	        
	        <label for="userTitle"><span class="red">*</span>[@spring.message "systemUsers.preview.userHierarchy" /]:</label>
	        [@mifos.formSingleSelectWithPrompt "userFormBean.selectedUserHierarchy", userFormBean.userHierarchyOptions, "--select one--" /]<br />
	        
          	<div class="span-23">
            	<span class="span-12 ">
                	<span class="span-8">[@spring.message "systemUsers.preview.roles" /]</span>
                    <span class="span-4">
            			[@spring.formMultiSelect "userFormBean.availableRoles", userFormBean.availableRolesOptions, "class=listSize" /]
					</span>
                    <span class="span-3">
                    	<br />
                    	<input class="buttn2" name="add" type="button" id="roles.button.add"  value="Add >>" onclick="moveOptions(this.form.availableRoles, this.form.selectedRoles);" />
                    	<br /><br />
						<input class="buttn2" name="remove" type="button" value="<< Remove" onclick="moveOptions(this.form.selectedRoles, this.form.availableRoles);" />
					</span>
					<span class="span-4">
						[@spring.formMultiSelect "userFormBean.selectedRoles", userFormBean.selectedRolesOptions, "class=listSize" /]
					</span>
               	</span>
            </div>
	        
	        <div class="fontBold">[@spring.message "systemUsers.preview.logininfo" /]</div>
	        <label for="username"><span class="red">*</span>[@spring.message "systemUsers.preview.userName" /]:</label>
	        [@spring.formInput "userFormBean.username" /]<br />
	        
	        <label for="password"><span class="red">*</span>[@spring.message "systemUsers.preview.password" /]:</label>
	        [@spring.formPasswordInput "userFormBean.password" /]<br />
	        
	        <label for="confirmedPassword"><span class="red">*</span>[@spring.message "systemUsers.preview.confirmedPassword" /]:</label>
	        [@spring.formPasswordInput "userFormBean.confirmedPassword" /]<br />
	        
	        <div class="fontBold">[@spring.message "systemUsers.preview.additionalInformation" /]</div>
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
	        <div class="prepend-8">
	        	<input class="buttn" type="submit" id="preview" name="_eventId_preview" value="Preview" onclick="selectAllOptions(this.form.selectedRoles);selectAllOptions(this.form.availableRoles);" />
	        	<input class="buttn2" type="submit" id="cancel" name="_eventId_cancel" value="Cancel" />
	        </div>
        </form>
      </div>
      
    </div>
  </div>
  <!--Main Content Ends-->
  [@mifos.footer/]