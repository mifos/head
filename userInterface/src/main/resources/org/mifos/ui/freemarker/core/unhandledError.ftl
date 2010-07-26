[#ftl]
[#import "spring.ftl" as spring]
[#import "blueprintmacros.ftl" as mifos]

[@mifos.header "system.unhandledError" /]
	  [@mifos.topNavigation currentTab="Home" /]
  <h1 id="accessDeniedHeading">[@spring.message "system.unhandledErrorHeading" /]</h1>
  <div class="error">
    ${exception}
 </div>   
[@mifos.footer /]

