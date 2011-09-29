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
<div id="header">
    <div class="topAlign append-1">
        <span class="logo"></span>
        <a href="yourSettings.do?method=get" title="[@spring.message "yourSettings"/]">[@spring.message "yourSettings"/]</a>&nbsp;|&nbsp;
        <a id="logout_link" href="j_spring_security_logout" title="[@spring.message "logout"/]">[@spring.message "logout"/]</a>
    </div>
    <div>
        <span class="menu">
          <a id="header.link.home" href="custSearchAction.do?method=getHomePage" class="[#if currentTab == "Home"]taborange[#else]tablightorange[/#if]" title="[@spring.message "tab.Home" /]">[@spring.message "tab.Home" /]</a>
          <a id="header.link.clientsAndAccounts" href="custSearchAction.do?method=loadMainSearch" class="[#if currentTab == "ClientsAndAccounts"]taborange[#else]tablightorange[/#if]" title="[@spring.message "tab.ClientsAndAccounts" /]">[@spring.message "tab.ClientsAndAccounts" /]</a>
          <a id="header.link.reports" href="reportsAction.do?method=load" class="[#if currentTab == "Reports"]taborange[#else]tablightorange[/#if]" title="[@spring.message "tab.Reports" /]">[@spring.message "tab.Reports" /]</a>
          [@security.authorize ifAllGranted="ROLE_ADMIN"]
          <a id="header.link.admin" href="AdminAction.do?method=load" class="[#if currentTab == "Admin"]taborange[#else]tablightorange[/#if]" title="[@spring.message "tab.Admin" /]">[@spring.message "tab.Admin" /]</a></span>
          [/@security.authorize]
        </span>
    </div>
</div>
[/#macro]

[#macro topNavigationNoSecurity currentTab]
<div id="header">
    <div class="site_logo">
        <span class="logo"></span>
        <a href="yourSettings.do?method=get" title="[@spring.message "yourSettings"/]">[@spring.message "yourSettings"/]</a>&nbsp;|&nbsp;
        <a id="logout_link" href="j_spring_security_logout" title="[@spring.message "logout"/]">[@spring.message "logout"/]</a>
    </div>
    <div class="top_menu">
        <ul>
         <li><a id="header.link.home" href="custSearchAction.do?method=getHomePage" class="[#if currentTab == "Home"]taborange[#else]tablightorange[/#if]" title="[@spring.message "tab.Home" /]">[@spring.message "tab.Home" /]</a>
         </li> <li> <a id="header.link.clientsAndAccounts" href="custSearchAction.do?method=loadMainSearch" class="[#if currentTab == "ClientsAndAccounts"]taborange[#else]tablightorange[/#if]" title="[@spring.message "tab.ClientsAndAccounts" /]">[@spring.message "tab.ClientsAndAccounts" /]</a>
         </li> <li> <a id="header.link.reports" href="reportsAction.do?method=load" class="[#if currentTab == "Reports"]taborange[#else]tablightorange[/#if]" title="[@spring.message "tab.Reports" /]">[@spring.message "tab.Reports" /]</a>
          </li> <li><a id="header.link.admin" href="AdminAction.do?method=load" class="[#if currentTab == "Admin"]taborange[#else]tablightorange[/#if]" title="[@spring.message "tab.Admin" /]">[@spring.message "tab.Admin" /]</a></li>
     </ul>
    </div>

</div>
[/#macro]

[#macro crumbs breadcrumbs]
<div class="breadcrumb">
        [#list breadcrumbs as messages]
              [#if messages_has_next]
                <a href="${messages.link}">[@spring.message "${messages.message}" /]</a>&nbsp;/&nbsp;[#else]<span class="fontBold">[@spring.messageText "${messages.message}","${messages.message}" /]</span>
              [/#if]
          [/#list]
 </div>
[/#macro]

[#macro flowCrumbs breadcrumbs]
<div class="breadcrumb">
        [#list breadcrumbs as messages]
            [#if messages_has_next]
                <a href="${flowExecutionUrl}&_eventId=${messages.link}">[@spring.message "${messages.message}" /]</a>&nbsp;/&nbsp;[#else]<span class="fontBold">[@spring.messageText "${messages.message}","${messages.message}" /]</span>
            [/#if]
        [/#list]
 </div>
[/#macro]

[#macro crumbpairs breadcrumbs lastEntryIsText="true"]
<div class="breadcrumb">
        [#list breadcrumbs?keys as text]
              [#if text_has_next || lastEntryIsText=="false"]
                <a href="${breadcrumbs[text]}">[@spring.messageText text, text/]</a>&nbsp;/
            [#else]
                <span class="fontBold">[@spring.messageText text, text/]</span>
               [/#if]
          [/#list]
 </div>
[/#macro]

[#macro crumb url]
    <div class="breadcrumb"><a href="AdminAction.do?method=load">[@spring.message "tab.Admin"/]</a>&nbsp;/&nbsp;<span class="fontBold">[@spring.message "${url}"/]</span></div>
[/#macro]

[#macro header pageTitle]
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
    "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html dir="${Application.LocaleSetting.direction}">
    <head>
        <title id="${pageTitle}">[@spring.message "${pageTitle}" /]</title>
        <link href="pages/css/maincss.css" rel="stylesheet" type="text/css" />
        <link href="pages/css/gazelle.css" rel="stylesheet" type="text/css" />
        <link href="pages/css/screen.css" rel="stylesheet" type="text/css" />
        <link href="pages/css/main.css" rel="stylesheet" type="text/css" />
        <link href="pages/css/app.css" rel="stylesheet" type="text/css" />
        <link rel="shortcut icon" href="pages/framework/images/favicon.ico"/>
        <script type="text/javascript" src="pages/js/jquery/jquery-1.4.2.min.js"></script>
    </head>
    <body>
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

[#macro formSingleSelectWithPrompt path options selectPrompt attributes=""]
    [@spring.bind path/]
    <select id="${spring.status.expression}" name="${spring.status.expression}" ${attributes}>
        <option value="" [@spring.checkSelected ""/]>[@spring.message "${selectPrompt}"/]</option>
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

[#macro footer]
    </body>
    </html>
[/#macro]

[#macro editPageBreadcrumbs breadcrumbs]
<div class="breadcrumb">
        [#list breadcrumbs?keys as text]
                <a href="${breadcrumbs[text]}">[@spring.messageText text, text/]</a>&nbsp;[#if text_has_next]/[/#if]  &nbsp;
          [/#list]
 </div>
[/#macro]

[#-- Directive for filtering text with String.format() and MarkdownJ. --]
[#-- Useful (and very specific) for parsing i18n messages containing links. --]
[#-- usage [@mifos.mlink dest="viewQuestions.ftl"][@spring.message "view.questions.mlink"][/@mifos.mlink] --]
[#assign mlink="org.mifos.ui.ftl.MarkdownLinker"?new()]

[#-- Template method for localized formatting of Java or Joda dates --]
[#-- usage: ${mifos.date_formatter(monthlyCashFlow.dateTime, "MMMM", Application.LocaleSetting.locale)} --]
[#assign date_formatter="org.mifos.ui.ftl.DateFormatter"?new()]
