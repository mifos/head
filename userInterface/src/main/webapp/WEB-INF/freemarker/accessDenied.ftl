[#ftl]
[#import "spring.ftl" as spring]
[#import "blueprintmacros.ftl" as mifos]
[#include "layout.ftl"]

[@adminLeftPaneLayout]
<div class="content_panel">
    <div class="marginLeft30 marginTop20">
        <h3 id="accessDeniedHeading">[@spring.message "accessDeniedHeading" /]</h3>
        <p id="accessDeniedMessage">[@spring.message "accessDeniedMessage" /]</p>
    </div>
</div>

[@mifos.footer /]
[/@adminLeftPaneLayout]
