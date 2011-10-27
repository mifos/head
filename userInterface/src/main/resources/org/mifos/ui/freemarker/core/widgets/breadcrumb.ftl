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
Creates a simple breadcrumb from a List of BreadCrumbLinks object. The list can be 
created using the output of AdminBreadcrumbBuilder. 
--]
[#macro crumbs breadcrumbs]
<div class="breadcrumb">
        [#list breadcrumbs as messages]
            [#if messages_has_next]
                <a href="${messages.link}">[@spring.message "${messages.message}" /]</a>&nbsp;/&nbsp;[#else]<span class="fontBold">[@spring.messageText "${messages.message}","${messages.message}" /]</span>
            [/#if]
        [/#list]
 </div>
[/#macro]

[#-- 
Creates a breadcrumb from a List of BreadCrumbLinks object. The list can be 
created using the output of AdminBreadcrumbBuilder.  The links in this list
are interpreted as event names in a Spring Webflow definition.
--]
[#macro flowCrumbs breadcrumbs]
<div class="breadcrumb">
        [#list breadcrumbs as messages]
            [#if messages_has_next]
                <a href="${flowExecutionUrl}&_eventId=${messages.link}">[@spring.message "${messages.message}" /]</a>&nbsp;/&nbsp;[#else]<span class="fontBold">[@spring.messageText "${messages.message}","${messages.message}" /]</span>
            [/#if]
        [/#list]
 </div>
[/#macro]

[#-- 
Creates a simple breadcrumb from a Hash (label -> URL). FTL calling this macro is 
responsible for populating the hash.
--]
[#macro crumbpairs breadcrumbs lastEntryIsText="true"]
<div class="breadcrumb">
        [#list breadcrumbs?keys as text]
            [#if text_has_next || lastEntryIsText=="false"] [#-- <item>_has_next is a special loop variable --]
                <a href="${breadcrumbs[text]}">[@spring.messageText text, text/]</a>&nbsp;/
            [#else]
                <span class="fontBold">[@spring.messageText text, text/]</span>
            [/#if]
        [/#list]
 </div>
[/#macro]

[#-- Create a simple (1 item) admin bread crumb trail. --]
[#macro crumb url]
    <div class="breadcrumb"><a href="AdminAction.do?method=load">[@spring.message "tab.Admin"/]</a>&nbsp;/&nbsp;<span class="fontBold">[@spring.message "${url}"/]</span></div>
[/#macro]

[#-- See crumbpairs macro --]
[#macro editPageBreadcrumbs breadcrumbs]
<div class="breadcrumb">
        [#list breadcrumbs?keys as text]            
                <a href="${breadcrumbs[text]}">[@spring.messageText text, text/]</a>&nbsp;[#if text_has_next]/[/#if]  &nbsp;            
        [/#list]
 </div>
[/#macro]
