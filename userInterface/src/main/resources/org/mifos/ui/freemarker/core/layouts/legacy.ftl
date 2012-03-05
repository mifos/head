[#ftl]
[#--
* Copyright (c) 2005-2011 Grameen Foundation USA
*  All rights reserved.
*
*  Licensed under the Apache License, Version 2.0 (the "License");
*  you may not use this file except in compliance with the License.
*  You may obtain a copy of the License at
*
*      http://www.apache.org/licenses/LICENSE-2.0
*
*  Unless required by applicable law or agreed to in writing, software
*  distributed under the License is distributed on an "AS IS" BASIS,
*  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
*  implied. See the License for the specific language governing
*  permissions and limitations under the License.
*
*  See also http://www.apache.org/licenses/LICENSE-2.0.html for an
*  explanation of the license and how it is applied.
--]

[#-- 
* These layout macros used to be in layout.ftl. To prevent potential 
* confusion with layouts.ftl where layouts are managed and accessed via
* namespace, they've been moved here. 
--]

[#assign security=JspTaglibs["http://www.springframework.org/security/tags"]]
[#assign mifostag=JspTaglibs["/tags/mifos-html"]]

[#macro adminLeftPaneLayout]
    [@layout.header "title" /]
    [@widget.topNavigationNoSecurity currentTab="Admin" /]
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
                    [#include "/adminLeftPane.ftl" /]
                </div>
            </div>
        </div>
    </div>
    [@layout.footer/]
[/#macro]

[#macro clientLeftPane currentTab="Admin"]
    [@layout.header "title" /]
    [@widget.topNavigationNoSecurity currentTab="${currentTab}" /]
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
                    [#include "/newClientLeftPane.ftl" /]
                </div>
            </div>
        </div>
    </div>
    [@layout.footer/]
[/#macro]
[#macro adminClientLeftPane]
    [@layout.header "title" /]
    [@widget.topNavigationNoSecurity currentTab="Admin" /]
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
                    [#include "/adminClientLeftPane.ftl" /]
                </div>
            </div>
        </div>
    </div>
    [@layout.footer/]
[/#macro]
[#macro headerOnlyLayout]
    [@layout.header "title" /]
    [@widget.topNavigationNoSecurity currentTab="ClientsAndAccounts" /]
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
    [@layout.footer/]
[/#macro]
