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
      <span id="page.id" title="viewProductsMix"></span>
          [@widget.crumbs breadcrumbs/]
        <div class="margin20lefttop">
            <form method="" action="" name="formname">
                <p class="font15 orangeheading">[@spring.message "admin.viewproductsmix"/]</p>
                <p class="margin5top10bottom">[@spring.message "manageProduct.viewProductMix.clickonaproductinstancebelowtoviewmixdetailsandmakechangesor" /] <a href="defineProductMix.ftl" >[@spring.message "admin.defineproductsmix"/] </a></p>
                <div>
                        <span class="fontBold">
                        [@spring.message "ftlDefinedLabels.manageProduct.viewProductMix.loan" /]
                        </span>
                        <ul>
                        [#list mixList.mix as text]
                            [#if text_has_next]
                                <li><a href="productMixDetails.ftl?prdOfferingId=${text.prdOfferingId}&productType=${text.productTypeID}">${text.prdOfferingName} </a></li>
                            [#else]
                                <li><a href="productMixDetails.ftl?prdOfferingId=${text.prdOfferingId}&productType=${text.productTypeID}">${text.prdOfferingName} </a></li>
                            [/#if]
                        [/#list]
                        </ul>
                    </div>
                    <div>
                    <span class="fontBold">
                    [@spring.message "ftlDefinedLabels.manageProduct.viewProductMix.savings"  /]
                </span>
                </form>
            </div>
       </div>
  	<div class="clear">&nbsp;</div>
   	<div class ="marginLeft20px">
    [@form.returnToPage  "AdminAction.do?method=load" "button.back" "viewproductsmix.button.back"/]
    </div>
  </div><!--Main Content Ends-->
[/@adminLeftPaneLayout]