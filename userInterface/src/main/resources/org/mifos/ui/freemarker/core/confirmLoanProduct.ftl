[#ftl]
[#--
* Copyright (c) 2005-2010 Grameen Foundation USA
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
    <p class="font15 orangeheading">[@spring.message "manageLoanProduct.youhavesuccessfullyaddedanewLoanproduct"/] </p>
    <p><span class="fontBold">[@spring.message "manageLoanProduct.pleaseNoteLoanProducthasbeenassignedthesystemIDnumber"/]</span><span class="fontBold">1-002 </span></p>
    <p class="fontBold"><a href="loanProductDetails.html">[@spring.message "manageLoanProduct.viewLoanproductdetailsnow"/]</a></p>
    <p><a href="defineLoanProducts.ftl">[@spring.message "manageLoanProduct.defineanewLoanproduct"/]</a></p>
  </div>
  <!--Main Content Ends-->
  [/@adminLeftPaneLayout]
  