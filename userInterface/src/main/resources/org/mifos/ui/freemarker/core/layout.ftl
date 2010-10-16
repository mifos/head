[#ftl]
[#import "spring.ftl" as spring]
[#import "newblueprintmacros.ftl" as mifos]
[#import "macros.ftl" as mifosmacros]
[#assign security=JspTaglibs["http://www.springframework.org/security/tags"]]

[#macro adminLeftPaneLayout]
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
                <div>
                    [#include "adminLeftPane.ftl" /]
                </div>
            </div>
        </div>
    </div>
    [@mifos.footer/]
[/#macro]

[#macro clientLeftPane]
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
                    [#include "newClientLeftPane.ftl" /]
                </div>
            </div>
        </div>
    </div>
    [@mifos.footer/]
[/#macro]
[#macro adminClientLeftPane]
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
                    [#include "adminClientLeftPane.ftl" /]
                </div>
            </div>
        </div>
    </div>
    [@mifos.footer/]
[/#macro]
[#macro headerOnlyLayout]
    [@mifos.header "title" /]
    [@mifos.topNavigationNoSecurity currentTab="ClientsAndAccounts" /]
    <div class="colmask leftmenu">
        <div class="leftmenu noleftcol">
            <div class="col1wrap">
                <div class="col1nomargin">
                <div class="main_content">
                    [#nested]
                </div>
             </div>
            </div>
        </div>
    </div>
    [@mifos.footer/]
[/#macro]