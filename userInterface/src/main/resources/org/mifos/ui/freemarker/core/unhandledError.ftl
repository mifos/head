[#ftl]
[#import "spring.ftl" as spring]
[#import "macros.ftl" as mifos]

[@mifos.header "system.unhandledError" /]
	  [@mifos.topNavigation currentTab="Home" /]
  <h1 id="accessDeniedHeading">[@spring.message "system.unhandledErrorHeading" /]</h1>
  <div class="error-messages">
    ${exception}
 </div>   
[@mifos.footer /]

