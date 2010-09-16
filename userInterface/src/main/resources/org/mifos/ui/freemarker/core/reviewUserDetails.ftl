[#ftl]
[#import "spring.ftl" as spring]
[#import "blueprintmacros.ftl" as mifos]
[@mifos.header "title" /]
  [@mifos.topNavigationNoSecurity currentTab="Admin" /]
  <!--  Main Content Begins-->
  <div class="content marginAuto">
    <div class="borders span-22">
      <div class="borderbtm span-22">
        <p class="span-8 completeIMG silverheading ">[@spring.message "systemUsers.preview.chooseOffice"/]</p>
        <p class="span-8 completeIMG silverheading ">[@spring.message "systemUsers.preview.userInformation"/]</p>
        <p class="span-3 arrowIMG orangeheading last">[@spring.message "review&Submit"/]</p>
      </div>
      <div class="subcontent ">
      <form method="post" action="user.ftl?execution=${flowExecutionKey}">
        <p class="font15"><span class="fontBold">[@spring.message "systemUsers.preview.addanewuser"/]</span>&nbsp;-&nbsp;<span class="orangeheading">[@spring.message "review&Submit"/]</span></p>
        <div>[@spring.message "systemUsers.preview.reviewtheinformationbelow"/]</div>
        <p>&nbsp;</p>
        <p><span class="fontBold">[@spring.message "systemUsers.preview.office"/] </span><span>${userFormBean.officeName}</span></p>
        
        <div><span class="orangeheading">[@spring.message "systemUsers.preview.userInformation"/] </span><span>&nbsp;</span></div>
        <div class="clear">&nbsp;</div>
        
        <div class="prepend-1  span-21 last">
        	<div class="span-20 "><span class="fontBold">[@spring.message "systemUsers.preview.firstName"/]&nbsp;</span><span>${userFormBean.firstName}</span>
  			</div>
            <div class="span-20 "><span class="fontBold">[@spring.message "systemUsers.preview.middleName"/]&nbsp;</span><span>${userFormBean.middleName}</span>
  			</div>
            <div class="span-20 "><span class="fontBold">[@spring.message "systemUsers.preview.secondLastName"/]&nbsp;</span><span>${userFormBean.secondLastName}</span>
			</div>
            <div class="span-20 "><span class="fontBold">[@spring.message "systemUsers.preview.lastName"/]&nbsp;</span><span>${userFormBean.lastName}</span>
  			</div>
            <div class="span-20 "><span class="fontBold">[@spring.message "systemUsers.preview.governmentID"/]&nbsp;</span><span>${userFormBean.governmentId}</span> 
  			</div>
            <div class="span-20 "><span class="fontBold">[@spring.message "systemUsers.preview.email"/]&nbsp;</span><span>${userFormBean.email}</span>
            </div>
            <div class="span-20 "><span class="fontBold">[@spring.message "systemUsers.preview.dateofBirth"/]&nbsp;</span><span>${userFormBean.dateOfBirth}</span>
            </div>
            <div class="span-20 "><span class="fontBold">[@spring.message "systemUsers.preview.age"/]&nbsp;</span><span>${userFormBean.age}</span>
            </div>
            <div class="span-20 "><span class="fontBold">[@spring.message "systemUsers.preview.maritalStatus"/]&nbsp;</span><span>${userFormBean.maritalStatusName}</span>
  			</div>
            <div class="span-20 "><span class="fontBold">[@spring.message "systemUsers.preview.gender"/]&nbsp;</span><span>${userFormBean.genderName}</span>
  			</div>
            <div class="clear">&nbsp;</div>
            <div class="span-20 "><span class="fontBold">[@spring.message "systemUsers.preview.languagePreferred"/]&nbsp;</span><span>${userFormBean.preferredLanguageName}</span>
  			</div>
            <div class="span-20 "><span class="fontBold">[@spring.message "systemUsers.preview.mFIJoiningDate"/]:&nbsp;</span><span>${userFormBean.mfiJoiningDate}</span>
  			</div>
            <div class="clear">&nbsp;</div>
        	<div class="clear">&nbsp;</div>
            <div class="span-20 "><span class="fontBold">[@spring.message "systemUsers.preview.address"/]&nbsp;</span><span>${userFormBean.address.address1}, ${userFormBean.address.address2}, ${userFormBean.address.address3}</span>
            </div>
            <div class="span-20 "><span class="fontBold">[@spring.message "systemUsers.preview.city"/]&nbsp;</span><span>${userFormBean.address.cityDistrict}</span>
            </div>
            <div class="span-20 "><span class="fontBold">[@spring.message "systemUsers.preview.state"/]&nbsp;</span><span>${userFormBean.address.state}</span>
            </div>
            <div class="span-20 "><span class="fontBold">[@spring.message "systemUsers.preview.country"/]&nbsp;</span><span>${userFormBean.address.country}</span>
            </div>
            <div class="span-20 "><span class="fontBold">[@spring.message "systemUsers.preview.postalcode"/]&nbsp;</span><span>${userFormBean.address.postalCode}</span>
            </div>
            <div class="clear">&nbsp;</div>
            <div class="span-20 "><span class="fontBold">[@spring.message "systemUsers.preview.telephone"/]&nbsp;</span><span>${userFormBean.address.telephoneNumber}</span>
            </div>
            <div class="clear">&nbsp;</div>
            <div class="span-20 "><span class="fontBold">[@spring.message "systemUsers.preview.office&Permissions"/]</span></div>
          
            <div class="span-20 "><span class="fontBold">[@spring.message "systemUsers.preview.userTitle"/]&nbsp;</span><span>${userFormBean.userTitleName}</span>
            </div>
            <div class="span-20 "><span class="fontBold">[@spring.message "systemUsers.preview.userHierarchy"/]&nbsp;</span><span>${userFormBean.userHierarchyName}</span>
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
            
            <div class="clear">&nbsp;</div>
            	<input class="buttn2" type="submit" name="_eventId_reedit" value="Edit user information" />
        	</div>
            
            <div class="clear">&nbsp;</div>
            <hr />
            <div class="clear">&nbsp;</div>
            <div class="prepend-9">
          		<input class="buttn" type="submit" name="_eventId_submit" value="Submit" />
          		<input class="buttn2" type="submit" name="_eventId_cancel" value="Cancel" />
        	</div>
        </form>
      </div>
    </div>
  </div>
  <!--Main Content Ends-->
  [@mifos.footer/]