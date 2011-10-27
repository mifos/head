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
[#include "layout.ftl"]
[@adminLeftPaneLayout]
<span id="page.id" title="AppliedUpgrades"></span>
<div class=" content">
    [#assign breadcrumb = {"admin":"AdminAction.do?method=load", "systemAdministration.viewsysteminformation.systeminformation":"systemInformation.ftl", "admin.viewSystemInformation.viewAppliedUpgrades":""}/]
    [@widget.crumbpairs breadcrumb/]
      <div id="content_panel" style="padding-left: 0px; margin-left: 20px; margin-top: 10px; font-size:12px">
          <div class="fontBold">[@spring.message "systemAdministration.viewsysteminformation.mifosDatabaseVersion.listOfAppliedDatabaseUpgrades"/]</div>
          <div>&nbsp;</div>

          <ul style="list-style-type: decimal">
              <table>
                  <thead>
                      <th width="25%">[@spring.message "admin.viewSystemInformation.id"/]</th>
                      <th width="25%">[@spring.message "admin.viewSystemInformation.author"/]</th>
                      <th width="25%">[@spring.message "admin.viewSystemInformation.dateExecuted"/]</th>
                      <th width="25%">[@spring.message "admin.viewSystemInformation.executionType"/]</th>
                  </thead>
                  [#if appliedChangeSets?size > 0]
                      [#list appliedChangeSets as appliedChangeSet]
                      <tr>
                          <td width="25%">${appliedChangeSet.id}</td>
                          <td width="25%">${appliedChangeSet.author}</td>
                          <td width="25%">${appliedChangeSet.dateExecuted?datetime}</td>
                          <td width="25%">${appliedChangeSet.execType}</td>
                      </tr>
                      [/#list]
                  [#else]
                      <tr>
                          <td colspan="4"><center><i>[@spring.message "admin.viewSystemInformation.noRecords"/]</i></center></td>
                      </tr>
                  [/#if]
              </table>
          </ul>

          <div class="fontBold">[@spring.message "systemAdministration.viewsysteminformation.mifosDatabaseVersion.listOfUnAppliedDatabaseUpgrades"/]</div>
          <div>&nbsp;</div>

          <ul style="list-style-type: decimal">
              <table>
                  <thead>
                      <th width="25%">[@spring.message "admin.viewSystemInformation.id"/]</th>
                      <th width="25%">[@spring.message "admin.viewSystemInformation.author"/]</th>
                      <th width="25%">[@spring.message "admin.viewSystemInformation.contexts"/]</th>
                  </thead>
                  [#if unRunChangeSets?size > 0]
                      [#list unRunChangeSets as unRunChangeSet]
                      <tr>
                          <td width="25%">${unRunChangeSet.id}</td>
                          <td width="25%">${unRunChangeSet.author}</td>
                          <td width="25%">${unRunChangeSet.contexts}</td>
                      </tr>
                      [/#list]
                  [#else]
                      <tr>
                          <td colspan="3"><center><i>[@spring.message "admin.viewSystemInformation.noRecords"/]</i></center></td>
                      </tr>
                  [/#if]
              </table>
          </ul>

      </div>
</div>
[/@adminLeftPaneLayout]