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
[#-- <div class="left-pane">
      <div class="left-pane-header">[@spring.message "administrativeTasks" /]</div>
      <div class="left-pane-content">
         </div>
</div>--]
 <div  class="sidebar ht550">
  <div>
  <form method="post" action="searchResult.ftl">
    <p class="orangetab">[@spring.message "admin.administrativeTasks" /]</p>
    <p class="leftpanelform fontnormal8ptbold">[@spring.message "admin.searchbynamesystemIDoraccountnumber"/]<br />
    <input type="text" class="t_box" name="searchString" maxlength="200" size="20" value="" style="margin-left:0px;margin-top:4px;">
      <br />
      <input type="submit" name="searchButton" value="[@spring.message "admin.search" /]" class="buttn floatRight" style="margin-right:16px;
margin-top:5px;">
    </p>
    </form>
  </div>
</div>