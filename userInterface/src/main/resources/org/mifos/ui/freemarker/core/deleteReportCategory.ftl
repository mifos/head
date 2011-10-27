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
[@layout.header "title" /]
  [@widget.topNavigationNoSecurity currentTab="Admin" /]
   <!--  Main Content Begins-->
  <div class="content marginAuto">
    <div class="borders span-22">
      <div class="subcontent ">
        <form method="post" action="deleteReportCategory.ftl" name="deleteReportCategory">
        <p>&nbsp;&nbsp;</p>
        <p class="font15">
            <span class="fontBold">[@spring.message "admin.definenewreportcategory" /]</span>&nbsp;--&nbsp;<span class="orangeheading">[@spring.message "reviewAndSubmit" /]</span>
        </p>
        <p>&nbsp;&nbsp;</p>
        <div>[@spring.message "editReportCategory.informationmessage" /]</div>
        [@form.showAllErrors "reportCategory.*"/]
        <div class="prepend-3  span-21 last">
            <div class="span-20 ">
                <span class="span-4 rightAlign">[@spring.message "editReportCategory.categoryName" /]</span>
                <span class="span-4">${reportCategory.name}</span>
              </div>
          </div>
          <div class="clear">&nbsp;</div>
          <hr />
          <div class="prepend-9">
              <input class="buttn" type="submit" name="submit" value="[@spring.message "submit"/]"/>
              <input class="buttn2" type="submit" name="CANCEL" value="[@spring.message "cancel"/]"/>
          </div>
          <div class="clear">&nbsp;</div>
        </form>
      </div>
      <!--Subcontent Ends-->
    </div>
  </div>
  <!--Main Content Ends-->
  [@layout.footer/]