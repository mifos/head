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
  <div class=" content">
      <form method="" action="" name="formname">
      <p class="bluedivs paddingLeft"><a href="admin.ftl">[@spring.message "admin"/]</a>&nbsp;/&nbsp;<span class="fontBold">[@spring.message "admin.viewAdditionalFields"/]</span></p>
      <p>&nbsp;&nbsp;</p>
    <div class="orangeheading">[@spring.message "admin.viewAdditionalFields"/]</div>
    <p>[@spring.message "datadisplayandrules.viewadditionalfields.clickonacategorybelowtoviewandedittheadditionalfieldsdefinedforthatcategoryor"/]&nbsp;<a href="#">[@spring.message "datadisplayandrules.viewadditionalfields.addanewadditionalfield"/] </a></p>
    <div class="span-22">
    <p>&nbsp;&nbsp;</p>
        <ul>
            <li type="circle"><a href="#">[@spring.message "datadisplayandrules.viewadditionalfields.personnel"/]</a></li>
            <li type="circle"><a href="#">[@spring.message "datadisplayandrules.viewadditionalfields.office"/]</a></li>
            <li type="circle"><a href="#">[@spring.message "datadisplayandrules.viewadditionalfields.client"/]</a></li>
            <li type="circle"><a href="#">[@spring.message "datadisplayandrules.viewadditionalfields.group"/]</a></li>
            <li type="circle"><a href="#">[@spring.message "datadisplayandrules.viewadditionalfields.center"/]</a></li>
            <li type="circle"><a href="#">[@spring.message "datadisplayandrules.viewadditionalfields.loan"/]</a></li>
            <li type="circle"><a href="#">[@spring.message "datadisplayandrules.viewadditionalfields.savings"/]</a></li>
        </ul>
    </div>
       </form>
  </div><!--Main Content Ends-->
[/@adminLeftPaneLayout]