[#ftl]
[#--
* Copyright Grameen Foundation USA
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
      <p class="bluedivs paddingLeft">
          <a href="AdminAction.do?method=load">[@spring.message "admin"/]</a>&nbsp;/&nbsp;<span class="fontBold">[@spring.message "admin.viewreportscategory"/]</span>
      </p>
    <p class="font15 orangeheading">[@spring.message "admin.viewreportscategory"/]</p>
    <p><span>[@spring.message "clickonEdit/Deletetomakechangestoareportcategoryor"/] </span><a href="defineReportCategory.ftl">[@spring.message "addanewreportcategory"/] </a></p>
    <div>&nbsp;</div>
    <div class="span-18">
        <div class="span-22  borderbtm paddingLeft">
            <span class="span-17 fontBold">[@spring.message "reports.categoryName"/]</span>
            <span class="span-3 ">&nbsp;</span>
        </div>

        [#list reportsCategoryList as category]
        <div class="span-22  borderbtm paddingLeft ">
            <span class="span-17">${category.name}</span>
            <span class="span-2 rightAlign"><a href="editReportCategory.ftl?categoryId=${category.id}">[@spring.message "edit"/]</a>&nbsp;|&nbsp;<a href="deleteReportCategory.ftl?categoryId=${category.id}">[@spring.message "delete"/]</a></span>
        </div>
        [/#list]
    </div>
  </div>
 <!--Main Content Ends-->
[/@adminLeftPaneLayout]