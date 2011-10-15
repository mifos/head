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
[#import "spring.ftl" as spring]
[#assign security=JspTaglibs["http://www.springframework.org/security/tags"]]

[#-- To use macros defined here add the following directive at the top of an ftl file --]
[#-- [#import "macros.ftl" as mifos] --]

[#-- header macro --]
[#-- Generate the Mifos header with tabbed navigation --]
[#-- currentTab values: "Home","ClientsAndAccounts","Reports","Admin" --]
[#-- usage   [@mifos.topNavigation currentTab="Home" /] --]
[#macro topNavigation currentTab]
<div id = "homePageHeader">
    <IMG id="logo" height=70 alt=""
                    src="pages/framework/images/logo.jpg" width=200></td>

    <div id="top-right-links">
      <a id="settings" href="#">[@spring.message "yourSettings"/]</a> |
       <a id="logout_link" href="j_spring_security_logout">[@spring.message "logout"/]</a>
    </div>
    <div id="top-menu-bar">
        <ul id="simple-menu">
        <li><a href="home.ftl" id="header.tab.home" title="[@spring.message "tab.Home" /]" [#if currentTab == "Home"]class="current"[/#if]>[@spring.message "tab.Home" /]</a></li>
        <li><a href="clientsAndAccounts.ftl" id="header.tab.clientsAndAccounts" title="[@spring.message "tab.ClientsAndAccounts" /]" [#if currentTab == "ClientsAndAccounts"]class="current"[/#if]>[@spring.message "tab.ClientsAndAccounts" /]</a></li>
        <li><a href="" id="header.tab.reports" title="[@spring.message "tab.Reports" /]" [#if currentTab == "Reports"]class="current"[/#if]>[@spring.message "tab.Reports" /]</a></li>
        [@security.authorize ifAllGranted="ROLE_ADMIN"]
        <li><a href="AdminAction.do?method=load"  id="header.tab.admin" title="[@spring.message "tab.Admin" /]" [#if currentTab == "Admin"]class="current"[/#if]>[@spring.message "tab.Admin" /]</a></li>
        [/@security.authorize]
        </ul>
    </div>
</div>
[/#macro]

[#macro topNavigationNoSecurity currentTab]
<div id = "homePageHeader">
    <IMG id="logo" height=70 alt=""
                    src="images/logo.jpg" width=200></td>

    <div id="top-right-links">
      <a id="settings" href="#">[@spring.message "yourSettings"/]</a> |
       <a id="logout" href="j_spring_security_logout">[@spring.message "logout"/]</a>
    </div>
    <div id="top-menu-bar">
        <ul id="simple-menu">
        <li><a href="home.ftl" id="header.tab.home" title="[@spring.message "tab.Home" /]" [#if currentTab == "Home"]class="current"[/#if]>[@spring.message "tab.Home" /]</a></li>
        <li><a href="clientsAndAccounts.ftl" id="header.tab.clientsAndAccounts" title="[@spring.message "tab.ClientsAndAccounts" /]" [#if currentTab == "ClientsAndAccounts"]class="current"[/#if]>[@spring.message "tab.ClientsAndAccounts" /]</a></li>
        <li><a href="" id="header.tab.reports" title="[@spring.message "tab.Reports" /]" [#if currentTab == "Reports"]class="current"[/#if]>[@spring.message "tab.Reports" /]</a></li>
        <li><a href="AdminAction.do?method=load"  id="header.tab.admin" title="[@spring.message "tab.Admin" /]" [#if currentTab == "Admin"]class="current"[/#if]>[@spring.message "tab.Admin" /]</a></li>
        </ul>
    </div>
</div>
[/#macro]

[#macro header pageTitle ]
<html>
  <head>
    <title id="${pageTitle}">[@spring.message "${pageTitle}" /]</title>
    <link href="pages/css/maincss.css" rel="stylesheet" type="text/css" />
    <link href="pages/css/gazelle.css" rel="stylesheet" type="text/css" />
    <link href="pages/css/screen.css" rel="stylesheet" type="text/css" />
    <link href="pages/css/main.css" rel="stylesheet" type="text/css" />
    <STYLE TYPE="text/css"><!-- @import url(pages/css/jquery/jquery-ui.css); --></STYLE>
    <STYLE TYPE="text/css"><!-- @import url(pages/css/datepicker/datepicker.css); --></STYLE>
    <link rel="shortcut icon" href="pages/framework/images/favicon.ico"/>
    <script type="text/javascript" src="pages/js/jquery/jquery-1.4.2.min.js"></script>
    <script type="text/javascript" src="pages/js/jquery/jquery-ui.min.js"></script>
    <script type="text/javascript" src="pages/js/jquery/jquery.datePicker.configuration.js"></script>
    <script type="text/javascript" src="pages/js/jquery/jquery.ui.datepicker.min.js"></script>
    <script type="text/javascript" src="pages/js/jquery/jquery-ui-i18n.js"></script>
    <script type="text/javascript" src="pages/framework/js/CommonUtilities.js"></script>
  </head>
  <body>
[/#macro]

[#macro footer]
</body>
</html>
[/#macro]


[#macro formSingleSelectWithPrompt path options selectPrompt attributes=""]
    [@spring.bind path/]
    <select id="${spring.status.expression}" name="${spring.status.expression}" ${attributes}>
        <option value="" [@spring.checkSelected ""/]>${selectPrompt}</option>
        [#if options?is_hash]
            [#list options?keys as value]
            <option value="${value?html}"[@spring.checkSelected value/]>${options[value]?html}</option>
            [/#list]
        [#else]
            [#list options as value]
            <option value="${value?html}"[@spring.checkSelected value/]>${value?html}</option>
            [/#list]
        [/#if]
    </select>
[/#macro]


[#macro formMultiSelect path options attributes=""]
    [@spring.bind path/]
    <input type="hidden" name="_${spring.status.expression}" value="true"/>
    <select multiple="multiple" id="${spring.status.expression}" name="${spring.status.expression}" ${attributes}>
        [#list options?keys as value]
        [#assign isSelected = spring.contains(spring.status.value?default([""]), value)]
        <option value="${value?html}"[#if isSelected] selected="selected"[/#if]>${options[value]?html}</option>
        [/#list]
    </select>
[/#macro]


[#macro showAllErrors path]
    [@spring.bind path/]
    [#if spring.status.errorMessages?size > 0]
    <div class="marginLeft30">
        <ul class="error">
         [#list spring.status.errorMessages as error]
          <li><b>${error}</b></li>
         [/#list]
        </ul>
    </div>
    [/#if]
[/#macro]

[#macro formCheckbox path attributes=""]
    [@spring.bind path /]
    [#assign id="${spring.status.expression}"]
    [#assign isSelected = spring.status.value?? && spring.status.value?string=="true"]
    <input type="hidden" name="_${id}" value="true"/>
    <input type="checkbox" id="${id}" name="${id}"[#if isSelected] checked="checked"[/#if] ${attributes}/>
[/#macro]

[#macro formCheckboxes path options separator attributes=""]
    [@spring.bind path /]
    [#if options?is_hash]
        [#list options?keys as value]
        [#assign id="${spring.status.expression}${value_index}"]
        [#assign isSelected = spring.contains(spring.status.value?default([""]), value)]
        <input type="checkbox" id="${id}" name="${spring.status.expression}" value="${value?html}"[#if isSelected] checked="checked"[/#if] ${attributes}[@spring.closeTag/]
        <label for="${id}" style="float:none;">${options[value]?html}</label>${separator}
        [/#list]
    [#else]
        [#list options as value]
        [#assign id="${spring.status.expression}${value_index}"]
        [#assign isSelected = spring.contains(spring.status.value?default([""]), value)]
        <input type="checkbox" id="${id}" name="${spring.status.expression}" value="${value?html}"[#if isSelected] checked="checked"[/#if] ${attributes}[@spring.closeTag/]
        <label for="${id}" style="float:none;">${value?html}</label>${separator}
        [/#list]
    [/#if]
    <input type="hidden" name="_${spring.status.expression}" value="on"/>
[/#macro]

[#macro formCheckboxesWithTags path options separator attributes=""]
    [@spring.bind path /]
    [#list options as option]
        [#if option.tags?exists && option.tags?size > 0]
            [#list option.tags as tag]
                [#assign id="${spring.status.expression}${option_index}${tag_index}"]
                [#assign isSelected = spring.contains(spring.status.value?default([""]), option.value + ":" + tag)]
                <input type="checkbox" id="${id}" name="${spring.status.expression}" value="${option.value?html}:${tag?html}"[#if isSelected] checked="checked"[/#if] ${attributes}[@spring.closeTag/]
                <label for="${id}" choice="${option.value}" tag="${tag}" style="float:none;">${option.value?html}&nbsp;:&nbsp;${tag?html}</label>${separator}
            [/#list]
        [#else]
            [#assign id="${spring.status.expression}${option_index}"]
            [#assign isSelected = spring.contains(spring.status.value?default([""]), option.value)]
            <input type="checkbox" id="${id}" name="${spring.status.expression}" value="${option.value?html}"[#if isSelected] checked="checked"[/#if] ${attributes}[@spring.closeTag/]
            <label for="${id}" choice="${option.value}" tag="" style="float:none;">${option.value?html}</label>${separator}
        [/#if]
    [/#list]
    <input type="hidden" name="_${spring.status.expression}" value="on"/>
[/#macro]

[#macro formRadioButtons path options separator attributes=""]
    [@spring.bind path /]
    [#list options as value]
    [#assign id="${spring.status.expression}${value_index}"]
    <input type="radio" id="${id}" name="${spring.status.expression}" value="${value?html}"[#if spring.stringStatusValue == value] checked="checked"[/#if] ${attributes}[@spring.closeTag/]
    <label for="${id}" style="float:none;">${value?html}</label>${separator}
    [/#list]
[/#macro]

[#macro boolRadioButtons path options separator attributes=""]
    [@spring.bind path/]
    [#list options?keys as value]
    [#assign id="${spring.status.expression}${value_index}"]
    <input type="radio" id="${id}" name="${spring.status.expression}" value="${value?html}"[#if spring.stringStatusValue == value] checked="checked"[/#if] ${attributes}[@spring.closeTag/]
    <label for="${id}" style="float:none;">${options[value]?html}</label>${separator}
    [/#list]
[/#macro]