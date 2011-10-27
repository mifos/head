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
[@adminClientLeftPane]
    <!--  Main Content Begins-->
    <span id="page.id" title="viewLoanPrdChangeLog"></span>
      <div class=" content">
      [#assign breadcrumb = {"admin":"AdminAction.do?method=load", "admin.viewLoanProducts":"viewLoanProducts.ftl",auditLog.name:"viewEditLoanProduct.ftl?productId=${auditLog.id}"}/]
        [@widget.editPageBreadcrumbs breadcrumb/]
          <p class="font15 fontBold marginTop15"><span class="">${auditLog.name}</span>&nbsp;-&nbsp;<span class="orangeheading">[@spring.message "changeLog"/]</span></p>
          <div class="marginTop15">
              <span>[@spring.message "recordCreationDate"/]&nbsp;:</span><span>${auditLog.createdDate}</span>
          </div>
          <div>
              <div class="fontBold span-16 bluedivs marginTop15">
                  <span class="span-3">[@spring.message "date"/]</span>
                <span class="span-3">[@spring.message "field"/]</span>
                <span class="span-3">[@spring.message "oldValue"/]</span>
                <span class="span-3">[@spring.message "newValue"/]</span>
                <span class="span-3">[@spring.message "user"/]</span>
              </div>
            [#list auditLog.auditLogRecords as changeLog]
            <div class="fontBold span-18">
                <span class="span-3">${changeLog.date}</span>
                <span class="span-3">${changeLog.field}</span>
                <span class="span-3">${changeLog.oldValue}</span>
                <span class="span-3">${changeLog.newValue}</span>
                <span class="span-3">${changeLog.user}</span>
            </div>
            <hr />
            [/#list]
        </div>
        <br />
        <div class="clear"></div>
        <div align="center" class="span-16 marginTop15">
            <form name="backform" method="get" action="viewEditLoanProduct.ftl">
                <input type="hidden" name="productId" value="${auditLog.id}" />
                <input class="buttn" type="submit" name="submit" value="[@spring.message "backToDetailsPage"/]" />
            </form>
        </div>
      </div>
      <!--Main Content Ends-->
[/@adminClientLeftPane]