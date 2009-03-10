[#ftl]
[#import "spring.ftl" as spring]
[#import "macros.ftl" as mifos]

[@mifos.header "user.create.success.title" /]
  <!-- page: userCreateSuccess.ftl -->
  
  [@mifos.topNavigation currentTab="Admin" /]
  [#include "adminLeftPane.ftl" ] 
      
	<div id="page-content">
	
		[#assign userId = ["${model.user.userId}"]]
			
		<h2 id="user.create.success.heading">[@spring.messageArgs "user.create.success.heading" userId /]</h2>
	   
	</div>
[@mifos.footer /]

