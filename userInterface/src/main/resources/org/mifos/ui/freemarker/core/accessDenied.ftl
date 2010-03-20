[#ftl]
[#import "spring.ftl" as spring]
[#import "macros.ftl" as mifos]

[@mifos.header "accessDeniedTitle" /]
	  [@mifos.topNavigation currentTab="Home" /]

    <h1 id="accessDeniedHeading">[@spring.message "accessDeniedHeading" /]</h1>
    <p id="accessDeniedMessage">[@spring.message "accessDeniedMessage" /]</p>
[@mifos.footer /]

