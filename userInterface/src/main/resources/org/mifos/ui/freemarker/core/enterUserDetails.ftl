[#ftl]
[#import "spring.ftl" as spring]
[#import "blueprintmacros.ftl" as mifos]

[@mifos.header "title" /]
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
        	<fieldset>
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
	        [@spring.formInput "userFormBean.governmentId" /]<br />
	        
	        <label for="email">[@spring.message "systemUsers.preview.email" /]:</label>
	        [@spring.formInput "userFormBean.email" /]<br />
	        
	        <div>
	        	<span><span class="red">*</span>[@spring.message "systemUsers.preview.dateofBirth" /]:</span>
	        	<span>[@spring.formInput "userFormBean.dateOfBirthDay" /]</span>
	        	<span>DD</span>
	        	<span>[@spring.formInput "userFormBean.dateOfBirthMonth" /]</span>
	        	<span>MM</span>
	        	<span>[@spring.formInput "userFormBean.dateOfBirthYear" /]</span>
	        	<span>YYYY</span>
	        </div>
	        
	        <label for="selectedMaritalStatus">[@spring.message "systemUsers.preview.maritalStatus" /]:</label>
	        [@mifos.formSingleSelectWithPrompt "userFormBean.selectedMaritalStatus", userFormBean.maritalStatusOptions, "--select one--" /]<br />
	        
	        <label for="selectedPreferredLanguage">[@spring.message "systemUsers.preview.languagePreferred" /]:</label>
	        [@mifos.formSingleSelectWithPrompt "userFormBean.selectedPreferredLanguage", userFormBean.preferredLanguageOptions, "--select one--" /]<br />
	        
	         <div>
	        	<span>[@spring.message "systemUsers.preview.mFIJoiningDate" /]:</span>
	        	<span>[@spring.formInput "userFormBean.mfiJoiningDateDay" /]</span>
	        	<span>DD</span>
	        	<span>[@spring.formInput "userFormBean.mfiJoiningDateMonth" /]</span>
	        	<span>MM</span>
	        	<span>[@spring.formInput "userFormBean.mfiJoiningDateYear" /]</span>
	        	<span>YYYY</span>
	        </div>
	        
	        
	        <div class="fontBold">[@spring.message "systemUsers.preview.address" /]</div>
	        
	        <div class="fontBold">[@spring.message "systemUsers.preview.permissions" /]</div>
	        <label for="userTitle">[@spring.message "systemUsers.preview.userTitle" /]:</label>
	        [@mifos.formSingleSelectWithPrompt "userFormBean.selectedUserTitle", userFormBean.userTitleOptions, "--select one--" /]<br />
	        
	        <label for="userTitle">[@spring.message "systemUsers.preview.userHierarchy" /]:</label>
	        [@mifos.formSingleSelectWithPrompt "userFormBean.selectedUserHierarchy", userFormBean.userHierarchyOptions, "--select one--" /]<br />
	        
          	<div class="span-23">
          		<span class="pull-3 span-8 rightAlign">[@spring.message "systemUsers.preview.roles" /]</span>
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
	        
        	</fieldset>
        
	        <div class="prepend-8">
	        	<input class="buttn" type="submit" id="preview" name="_eventId_preview" value="Preview" />
	        	<input class="buttn2" type="submit" id="cancel" name="_eventId_cancel" value="Cancel" />
	        </div>
        </form>
      </div>
      
    </div>
  </div>
  <!--Main Content Ends-->
  [@mifos.footer/]