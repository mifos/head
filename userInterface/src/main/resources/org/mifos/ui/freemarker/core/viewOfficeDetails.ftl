[#ftl]
[#import "spring.ftl" as spring]
[#import "blueprintmacros.ftl" as mifos]
[@mifos.header "title" /]
  [@mifos.topNavigationNoSecurity currentTab="Admin" /]
  <!--  Left Sidebar Begins-->
  <div class="sidebar ht600">
  [#include "adminLeftPane.ftl" ]
  </div> 
  <!--  Left Sidebar Ends-->
    <!--  Main Content Begins-->  
  <div class=" content leftMargin180">
  	<form method="" action="" name="formname">
    <div class="span-24">
  		<div class="bluedivs paddingLeft"><a href="admin.ftl">[@spring.message "admin" /]</a>&nbsp;/&nbsp;<a href="viewOffices.ftl">[@spring.message "viewOffices" /]</a>&nbsp;/&nbsp;<span class="fontBold">[@spring.message "OfficeName" /]</span></div>
        <div class="clear">&nbsp;</div>
    	<div class="span-24 ">
        	<div class="span-19 ">
            	<span class="orangeheading">[@spring.message "OfficeName" /]</span><br /><br />
                <span><span><img src="pages/framework/images/status_activegreen.gif" /></span>&nbsp;<span>[@spring.message "active" /]</span><br />
                <span><span >[@spring.message "officeshortname" /]</span>&nbsp;<span id="viewOfficeDetails.text.shortName">&nbsp;</span><br />
                <span><span >[@spring.message "officetype" /]</span>&nbsp;<span id="viewOfficeDetails.text.officeLevel">&nbsp;</span><br />
                <span><span>[@spring.message "parentoffice" /]</span>&nbsp;<br />
        	</div>
            <span class="span-4 rightAlign"><a href="editOfficeInformation.ftl" id="viewOfficeDetails.link.editOfficeInformation">[@spring.message "editofficeinformation" /]</a></span>
        </div>
        <div class="clear">&nbsp;</div>
        <div class="span-24 ">
			<span class="fontBold">[@spring.message "additionalinformation" /] </span><br />
            <span>[@spring.message "externalId" /]</span><br />
            <span>[@spring.message "numberofClientsperGroup" /] </span><br />
            <span>[@spring.message "numberofClientsperCenter" /]</span><br />
            <span>[@spring.message "distancefromHOtoBOforoffice" /] </span><br />
                                    
        </div>
	</div>
   	</form> 
  </div>
  <!--Main Content Ends-->
  [@mifos.footer/]