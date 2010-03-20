[#ftl]
[#import "spring.ftl" as spring]
[#import "macros.ftl" as mifos]

[@mifos.header "pageNotFoundTitle" /]
    <h1 id="pageNotFoundHeading">[@spring.message "pageNotFoundHeading" /]</h1>
    <p id="pageNotFoundMessage">[@spring.message "pageNotFoundMessage" /]</p>
[@mifos.footer /]

