[#ftl]
[#--
* Copyright (c) 2005-2010 Grameen Foundation USA
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
[#import "blueprintmacros.ftl" as mifos]
[#include "layout.ftl"]
[@adminLeftPaneLayout]
<!--  Main Content Begins-->  
  <div class="content">
      <form method="post" action="viewSystemUsers.ftl" name="searchSystemUsers">
      [#assign breadcrumb = {"admin":"AdminAction.do?method=load", "systemUsers.viewSystemUsers":""}/]
    [@mifos.crumbpairs breadcrumb/]
      <div class="marginLeft30 marginTop15">
    <div class="orangeheading marginTop20">[@spring.message "admin.viewsystemusers"/]</div>
    <p class="paddingLeft marginTop20">[@spring.message "systemUsers.viewSystemUsers.searchUsersByName"/]<br />
        [@spring.formInput "searchResults.search" /]
        <input class="buttn" type="submit" name="searchbutton    " value="Search"/>
    </p>
    </div>
    <div class="marginTop20 marginLeft30">    
        [#list pagedResults.pagedUserDetails as userDetail]
            <hr />${userDetail_index + 1}.&nbsp;&nbsp;${userDetail.officeName} /<a href="viewSystemUserDetails.ftl?id=${userDetail.id}" id="userId_${userDetail.id}">${userDetail.firstName} ${userDetail.lastName}</a>
            [#if !userDetail_has_next]
            <hr />
            [/#if]
        [/#list]
    </div>
    [#if pagedResults.page > 0]
    <div>
        <input type="hidden" name="lastPage" value="${pagedResults.page}" />
        <input type="hidden" name="lastSearch" value="${searchResults.search}" />
        <span class="span-6 rightAlign">
            [#if pagedResults.page > 1]
            <input class="buttn" type="submit" name="previous" value="[@spring.message "viewSystemUsers.previous"/]" />
            [#else]
            <span class="inactiveButton">[@spring.message "viewSystemUsers.previous"/]</span>
            [/#if]
        </span>
        <span class="span-4 fontBold" align="center">[@spring.message "results"/] ${pagedResults.currentRange} of ${pagedResults.totalCount}</span>
        <span class="span-8">
            [#if pagedResults.notLastPage]
            <input class="buttn" type="submit" name="next" value="[@spring.message "viewSystemUsers.next"/]" />
            [#else]
            <span class="inactiveButton">[@spring.message "viewSystemUsers.next"/]</span>
            [/#if]
        </span>
    </div>
    [#else]
        <input type="hidden" name="lastPage" value="1" />
    [/#if]
    </form>
  </div>
<!--Main Content Ends-->
[/@adminLeftPaneLayout]