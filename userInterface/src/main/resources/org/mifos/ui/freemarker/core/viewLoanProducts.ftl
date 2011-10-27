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
  [@widget.crumbs breadcrumbs/]
  <p>&nbsp;&nbsp;</p>
        <p class="marginLeft30 font15 orangeheading">[@spring.message "admin.viewLoanProducts"/]</p>
        <p class="marginLeft30">[@spring.message "manageLoanProducts.viewLoanProducts.clickonaLoanproductbelowtoviewdetailsandmakechangesor"/] <a href="defineLoanProducts.ftl">[@spring.message "admin.definenewLoanproduct"/]</a></p>
        <div class="marginTop15">
        <div class="span-22 marginLeft30">
           [#list formBean as loanProduct]
               <div>
            <img src="pages/framework/images/bullet_circle.gif" width="9" height="11"/><a href="viewEditLoanProduct.ftl?productId=${loanProduct.prdOfferingId}">${loanProduct.prdOfferingName}</a>
            [#switch loanProduct.prdOfferingStatusId]
                  [#case 1]
                [#break]
                [#case 4]
                <span><img src="pages/framework/images/status_closedblack.gif" />[@spring.message "inactive"/]</span>
                [#break]
             [/#switch]
            </div>
        [/#list]
        </div>
    </div>
  </div>
  <!--Main Content Ends-->
[/@adminLeftPaneLayout]