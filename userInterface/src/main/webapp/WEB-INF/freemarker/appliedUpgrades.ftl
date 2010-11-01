[#ftl]
[#include "layout.ftl"]
[@adminLeftPaneLayout]
 <span id="page.id" title="AppliedUpgrades" />
 [@mifos.crumb "admin.viewSystemInformation.viewAppliedUpgrades"/]
  	<div id="content_panel" style="padding-left: 0px; margin-left: 20px; margin-top: 10px; font-size:12px">
  		<div class="fontBold">[@spring.message "systemAdministration.viewsysteminformation.mifosDatabaseVersion.listOfAppliedDatabaseUpgrades"/]</div>
  		<div>&nbsp;</div>
  	
  		<ul style="list-style-type: decimal">
  			[#list upgrades as upgrade]
  				<li>${upgrade?c}</li>
  			[/#list]
  		</ul> 
  	
  	</div>
[/@adminLeftPaneLayout]