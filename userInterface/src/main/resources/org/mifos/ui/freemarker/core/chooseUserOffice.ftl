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
        <p class="font15"><span class="fontBold">[@spring.message "systemUsers.defineNewSystemUser.addanewuser"/]</span>&nbsp;-&nbsp;<span class="orangeheading">[@spring.message "systemUsers.defineNewSystemUser.chooseOffice"/]</span></p>
        <p>[@spring.message "systemUsers.defineNewSystemUser.toselect,clickonaofficefromthelistbelow.ClickCanceltoreturntoAdminpage"/]</p>
        
        <p class="fontBold"><a href="${flowExecutionUrl}&_eventId=officeSelected&officeId=1">[@spring.message "systemUsers.defineNewSystemUser.mifosHo"/]</a></p>

		<ul>        
        [#list officeDetails.nonBranches as item]
			<li><a href="${flowExecutionUrl}&_eventId=officeSelected&officeId=${item.id}">${item.name}</a></li>
        [/#list]
        </ul>
        
        [#list officeDetails.branchOnlyOfficeHierarchy as office]
	        <div>${office.name}</div>
	        <ul>    
	       	[#list office.children as branch]
			       	<li><a href="${flowExecutionUrl}&_eventId=officeSelected&officeId=${branch.id}">${branch.name}</a></li>
	       	[/#list]
	       	</ul>
       	[/#list]
        
        <hr />
        <div class="prepend-8">
        	<form name="enterUserDetails" method="post" action="user.ftl?execution=${flowExecutionKey}">
	        	<input class="buttn2" id="choose_office.button.cancel" type="submit" name="_eventId_cancel" value="Cancel" />
	        </form>
        </div>
      </div>
      
    </div>
  </div>
  <!--Main Content Ends-->
  [@mifos.footer/]