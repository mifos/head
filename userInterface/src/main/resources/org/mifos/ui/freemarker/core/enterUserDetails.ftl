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
        
        <div>
        	<span>[@spring.message "systemUsers.defineNewSystemUser.form.office"/]</span>
        	<span></span>
        </div>
        
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