[#ftl]
[#import "spring.ftl" as spring]
[#import "blueprintmacros.ftl" as mifos]


[@mifos.header "accessDeniedTitle" /]
[@mifos.topNavigation currentTab="Home" /]
<div class="marginLeft30 marginTop20">
    <h1 id="accessDeniedHeading">[@spring.message "accessDeniedHeading" /]</h1>
    <p id="accessDeniedMessage">[@spring.message "accessDeniedMessage" /]</p>
</div>
[@mifos.footer /]

