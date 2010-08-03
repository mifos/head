[#ftl]
[#import "spring.ftl" as spring]
[#import "blueprintmacros.ftl" as mifos]

[@mifos.header "title" /]
[@mifos.topNavigationNoSecurity currentTab="Admin" /]
 <!--  Main Content Begins-->
  <div class="content marginAuto">
    <div class="borders span-22">
      <div class="borderbtm span-22">
        <p class="span-6 arrowIMG orangeheading ">[@spring.message "systemUsers.defineNewSystemUser.chooseOffice"/]</p>
        <p class="span-6 arrowIMG1 orangeheading ">[@spring.message "systemUsers.defineNewSystemUser.userInformation"/]</p>
        <p class="span-3 arrowIMG1 orangeheading last">[@spring.message "review&Submit"/]</p>
      </div>
      <div class="subcontent">
      <form method="" action="" name="formname">
        <p class="font15"><span class="fontBold">[@spring.message "systemUsers.defineNewSystemUser.addanewuser"/]</span>&nbsp;-&nbsp;<span class="orangeheading">[@spring.message "systemUsers.defineNewSystemUser.chooseOffice"/]</span></p>
        <p>[@spring.message "systemUsers.defineNewSystemUser.toselect,clickonaofficefromthelistbelow.ClickCanceltoreturntoAdminpage"/]</p>
        <p>&nbsp;&nbsp;</p>
		<p class="fontBold"><a href="#">[@spring.message "systemUsers.defineNewSystemUser.mifosHo"/]</a></p>
		 <p>&nbsp;&nbsp;</p>
		  <p><h5 class="fontBold">[@spring.message "systemUsers.defineNewSystemUser.areaOffice"/]</h5></p>
        <ul>
              	<li type="circle"><a href="#">[@spring.message "systemUsers.defineNewSystemUser.testAreaOffice"/]</a></li>
        </ul>
        
         <p><h5 class="fontBold">[@spring.message "systemUsers.defineNewSystemUser.branchOffice"/]</h5></p>     
        <ul>        
        <p>[@spring.message "systemUsers.defineNewSystemUser.testAreaOffice"/]</p>
        	<li type="circle"><a href="#">[@spring.message "systemUsers.defineNewSystemUser.testBranchOffice"/]</a></li>
        </ul>
        <hr />
        <div class="prepend-8">
            <input class="buttn2" id="choose_office.button.cancel" type="button" name="cancel" value="Cancel" onclick="window.location='admin.ftl'"/>
        </div>
        </form>
      </div>
    </div>
  </div>
  <!--Main Content Ends-->
  [@mifos.footer/]