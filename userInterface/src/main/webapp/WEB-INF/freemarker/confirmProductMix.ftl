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
  <span id="page.id" title="createProductsMixConfirmation"></span>
  <div class="content">
      <div class="marginLeft30 marginTop15">
          <p class="orangeheading marginTop15">[@spring.message "manageProductMix.youhavesuccessfullydefinedanewproductmix"/]</p>
          <span class="fontBold marginTop15"><a href="productMixDetails.ftl?prdOfferingId=${productId}&productType=1">[@spring.message "manageProductMix.viewproductmixdetailsnow"/]</a></span><br />
          <span class="marginTop15"><a href="defineProductMix.ftl">[@spring.message "manageProductMix.definemixforanewproduct"/]</a></span>
      </div>
  </div>
  <!--Main Content Ends-->
 [/@adminLeftPaneLayout]