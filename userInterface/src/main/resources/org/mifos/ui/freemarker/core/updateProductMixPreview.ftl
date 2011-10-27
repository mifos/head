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
   <div>
  <div class=" content">
  [#assign breadcrumb = {"admin":"AdminAction.do?method=load", "organizationPreferences.viewfunds":"viewFunds.ftl"}/]
    [@widget.crumbpairs breadcrumb/]
          <p class="fontBold">[@spring.message "manageProducts.defineProductmix.productmixdetails"/] </p>
          <form name="productsmixform" id="productsmixform" method="post" action="previewProductMix.ftl">
            <div class="span-21">
                <div class="span-20 ">
                    <span class="span-5">
                        [@spring.message "manageProducts.defineProductmix.producttype"/]
                    </span>
                    <span class="span-5">[#--${springMacroRequestContext.getMessage(ref.productTypeNameKey)}--]Product Type Name</span>
                </div>

                <div class="span-20">
                    <span class="span-5">
                        [@spring.message "manageProducts.defineProductmix.productinstancename"/]
                    </span>
                    <span class="span-7">[#--${ref.productName}--]ProductName</span>
                </div>

                <div class="span-20">
                    <span class="fontBold">[@spring.message "manageProduct.productMixDetails.allowedProducts"/]</span>&nbsp;:
                    [#list ref.allowedProductMix as allowed]
                    <div>${allowed}</div>
                    [/#list]
                    <br />
                    <span class="fontBold">[@spring.message "manageProduct.productMixDetails.notAllowedProducts"/]</span>&nbsp;:
                    [#list ref.notAllowedProductMix as notAllowed]
                    <div>${notAllowed}</div>
                    [/#list]
                  </div>
                <div class="clear">&nbsp;</div>

                <input type="hidden" name="FORMVIEW" id="formview" value="${formView}" />

                <div>
                    <input type="submit" class="buttn2" name="EDIT" value="[@spring.message "manageProduct.productMixDetails.editproductmixinformation"/]"/>
                </div>
            </div>
            <hr />
            <div class="prepend-10">
                <input class="buttn" type="submit" id="holiday.button.submit" name="submit" value="[@spring.message "submit"/]" />
                <input class="buttn2" type="submit" id="CANCEL" name="CANCEL" value="[@spring.message "cancel"/]"/>
            </div>
        </form>
      <!--Subcontent Ends-->
    </div>
  </div>
  <!--Main Content Ends-->
</div>
<!--Container Ends-->
[/@adminLeftPaneLayout]