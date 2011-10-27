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
      <div class="borderbtm span-22">
        <p class="span-15 arrowIMG orangeheading">[@spring.message "editReportCategory.categoryinformation" /]</p>
        <p class="span-3 arrowIMG1 orangeheading last">[@spring.message "reviewAndSubmit" /]</p>
      </div>
      <div class="subcontent ">
        <form method="post" action="editReportCategory.ftl" name="editReportCategory">
        <p>&nbsp;&nbsp;</p>
        <p class="font15">
            <span class="fontBold">[@spring.message "admin.definenewreportcategory" /]</span>&nbsp;--&nbsp;<span class="orangeheading">[@spring.message "editReportCategory.entercategoryinformation" /]</span>
        </p>
        <p>&nbsp;&nbsp;</p>
        <div>[@spring.message "editReportCategory.informationmessage" /]</div>
        <div><span class="red">* </span>[@spring.message "fieldsmarkedwithanasteriskarerequired" /] </div>
        <p>&nbsp;&nbsp;</p>
        [@form.showAllErrors "reportCategory.*"/]
        <p class="fontBold">[@spring.message "editReportCategory.categoryDetails" /]</p>
        <div class="prepend-3  span-21 last">
            <div class="span-20 ">
                <span class="span-4 rightAlign"><span class="red">* </span>[@spring.message "editReportCategory.categoryName" /]</span>
                <span class="span-4">
                    [@spring.bind "reportCategory.name"/]
                    <input type="text" name="${spring.status.expression}" id="${spring.status.expression}" value="${spring.status.value?default("")}" />
                </span>
              </div>
          </div>
          <div class="clear">&nbsp;</div>
          <hr />
          <div class="prepend-9">
            <input type="hidden" name="editFormView" value="editReportCategory" />
              <input class="buttn" type="submit" name="PREVIEW" value="[@spring.message "preview"/]"/>
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