[#ftl]
[#include "layout.ftl"]
[@adminLeftPaneLayout]
 <span id="page.id" title="AppliedUpgrades" />
 
  	<div id="content_panel" style="padding-left: 0px; margin-left: 20px; margin-top: 10px; font-size:12px">
  		<h3>[@spring.message "systemAdministration.viewsysteminformation.mifosDatabaseVersion.listOfAppliedUpgrades"/] </h3>
  		<div>&nbsp;</div>
  	
  		<ul style="list-style-type: decimal">
  			[#list upgrades as upgrade]
  				<li>${upgrade?c}</li>
  			[/#list]
  		</ul> 
  	
  	</div>
[/@adminLeftPaneLayout]