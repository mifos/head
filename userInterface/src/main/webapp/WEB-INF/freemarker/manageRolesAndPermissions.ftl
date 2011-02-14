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
<!--  Main Content Begins-->
  <div class="content">
      <form method="" action="" name="formname">
      <div class="marginLeft10">
      <div class="bluedivs paddingLeft"><a href="admin.ftl">[@spring.message "admin"/]</a>&nbsp;/&nbsp;<span class="fontBold">[@spring.message "systemusers.managerolesandpermissions.rolesandpermissions"/]</span></div>
    <div class="span-18">
        <div class="clear">&nbsp;</div>
        <p class="font15"><span class=" orangeheading">[@spring.message "systemusers.managerolesandpermissions.rolesandpermissions"/] </span></p>
        <div>[@spring.message "systemusers.managerolesandpermissions.aroleisthename"/]<br /> [@spring.message "systemusers.managerolesandpermissions.nameandtaskpermissions"/] <a href="#">[@spring.message "systemusers.managerolesandpermissions.newRole"/]</a></div>
        <div class="clear">&nbsp;</div>
        <div class="span-22">
                [#list roles as roles]
            <div class="borderbtm span-22 last"><span class="span-9 fontBold"><a href="modifyRole.ftl?user=${roles.name}&roleId=${roles.id}" >${roles.name}</a></span><span class="span-4 last"><a href="#">[@spring.message "systemusers.managerolesandpermissions.deleterole"/]</a></span></div>
            [/#list]
        </div>
    </div>
    </div>
       </form>
  </div><!--Main Content Ends-->
  [/@adminLeftPaneLayout]