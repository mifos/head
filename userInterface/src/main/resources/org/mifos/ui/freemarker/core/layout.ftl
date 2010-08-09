[#ftl]
[#import "spring.ftl" as spring]
[#import "newblueprintmacros.ftl" as mifos]

[#macro adminLeftPanelLayout]
    [@mifos.header "title" /]
    [@mifos.topNavigationNoSecurity currentTab="Admin" /]
    <div class="colmask leftmenu">
        <div class="colleft">
            <div class="col1wrap">
                <div class="col1">
                <div class="main_content">
                    [#nested]
                </div>
             </div>
            </div>
            <div class="col2">
                <div class="side_bar">
                    [#include "newadminLeftPane.ftl" /]
                </div>
            </div>
        </div>
    </div>
    [@mifos.footer/]
[/#macro]