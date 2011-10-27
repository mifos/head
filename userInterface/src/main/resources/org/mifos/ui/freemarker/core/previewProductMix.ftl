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
  <div class="container">&nbsp;
  <!--  Main Content Begins-->
  <span id="page.id" title="previewProductsMix"></span>
<div class="content definePageMargin">
    <div class="borders margin20lefttop width90prc">
        <div class="borderbtm width100prc height25px">
            <p class="span-17 completeIMG silverheading  padding20left"
               style="width:50%">[@spring.message "manageProducts.defineProductmix.productmixinformation"/]</p>

            <p class="span-3 timelineboldorange arrowIMG last padding20left10right width130px"
               style="float:right">[@spring.message "reviewAndSubmit" /]</p>
        </div>
        <div class="margin20lefttop">
          <p class="fontBold">[@spring.message "manageProducts.defineProductmix.productmixdetails"/] </p>
          <form name="productsmixform" id="productsmixform" method="post" action="previewProductMix.ftl">
            <div class="span-21">
                <div class="span-20 ">
                    <span class="span-5">
                        [@spring.message "manageProducts.defineProductmix.producttype"/]
                    </span>
                    <span class="span-5">${springMacroRequestContext.getMessage(ref.productTypeNameKey)}</span>
                </div>

                <div class="span-20">
                    <span class="span-5">
                        [@spring.message "manageProducts.defineProductmix.productinstancename"/]
                    </span>
                    <span class="span-7">${ref.productName}</span>
                </div>

                <div class="span-20">
                    <div class="fontBold margin10top">[@spring.message "manageProduct.productMixDetails.allowedProducts"/]&nbsp;:</div>
                    [#list ref.allowedProductMix as allowed]
                    <div>${allowed}</div>
                    [/#list]

                    <div class="fontBold margin10top">[@spring.message "manageProduct.productMixDetails.notAllowedProducts"/]&nbsp;:</div>
                    [#list ref.notAllowedProductMix as notAllowed]
                    <div>${notAllowed}</div>
                    [/#list]
                  </div>

                <input type="hidden" name="FORMVIEW" id="formview" value="${formView}" />

                <div>
                    <input class="insidebuttn margin30top" type="submit" class="buttn2" name="EDIT" value="[@spring.message "manageProduct.productMixDetails.editproductmixinformation"/]"/>
                </div>
            </div>
            <div class="clear">&nbsp;</div>
            <div class="buttonsSubmitCancel margin20right margin15bottom">
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
[@layout.footer/]