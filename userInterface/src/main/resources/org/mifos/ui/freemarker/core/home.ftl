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
[#assign form=JspTaglibs["/WEB-INF/tld/spring-form.tld"]]

[@layout.header "title" /]
  [@mifos.topNavigation currentTab="Home" /]
  [#include "homeLeftPane.ftl" ]


  <div id="page-content">
      <div id="homePageContent">
      [@form.form action="clientSearchResults.ftl" commandName="searchString"]
      [@form.errors path="*" cssClass="error-messages"/]

      <h2 id="hello.heading">[@spring.message "welcomeToMifos" /]</h2>
      <h4>[@spring.message "toQuicklyFindAClientYouCan"/]</h4>
      <label id="clientSearch">[@spring.message "searchByName" /]</label>
      [@form.input path="searchString"/]
      <input type="submit" value="[@spring.message "search" /]" class="buttn" id="clientSearch.form.submit" tabindex="6" />
      <h4>[@spring.message "toViewAListOfGroups"/]</h4>
      <a href="viewGroups.ftl" id="home.list.groups">[@spring.message "seealistofgroups"/]</a>
      [/@form.form]
    </div>
  </div>

[@layout.footer /]