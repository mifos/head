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
<!--  Left Sidebar Ends-->
<!--  Main Content Begins-->
<div class="content">
    <form method="" action="" name="formname">
        [#assign breadcrumb = {"admin":"AdminAction.do?method=load", "admin.viewproductsmix":"viewProductMix.ftl","${mixDetails.prdOfferingName}":""}/]
    [@widget.crumbpairs breadcrumb/]
        <div class="width95prc margin20lefttop">
            <div class="">
                <span class="orangeheading">${mixDetails.prdOfferingName}</span>
            </div>
            <div class="rightAlign" style="position:relative; top:-20px;"><a
                    href="editProductMix.ftl?productId=${mixDetails.prdOfferingId}">[@spring.message "manageProduct.productMixDetails.editproductmixinformation"/]</a></div>

            <div class="span-20">
                <div class="span-20"><span
                        class="fontBold">[@spring.message "manageProduct.productMixDetails.allowedProducts"/]
                    :</span><span>&nbsp;</span><br/>
                    [#list mixDetails.allowedPrdOfferingNames as allowedProducts]
                        [#if allowedProducts_has_next]
                            <div><span>${allowedProducts.prdOfferingName}</span></div>
                            [#else]
                                <div><span>${allowedProducts.prdOfferingName}</span></div>
                        [/#if]
                    [/#list]
                </div>
                <div class="clear">&nbsp; </div>
                <div class="span-20"><span
                        class="fontBold">[@spring.message "manageProduct.productMixDetails.notAllowedProducts"/]
                    :</span><span>&nbsp;</span><br/>

                    [#list mixDetails.notAllowedPrdOfferingNames as notAllowedProducts]
                        [#if notAllowedProducts_has_next]
                            <div>
                                <span>${notAllowedProducts.prdOfferingName}</span>
                            </div>
                            [#else]
                                <div>
                                    <span>${notAllowedProducts.prdOfferingName}</span>
                                </div>
                        [/#if]
                    [/#list]
                </div>
                <!-- productMix change log not working in earlier versions of mifos - keithw. -->
                <!--
        <div class="span-22"><a href="productMixAction.do?method=loadChangeLog&entityType=ProductMix&entityId=${mixDetails.prdOfferingId}">[@spring.message "viewChangeLog"/]</a></div>
        -->
            </div>
        </div>
    </form>
</div><!--Main Content Ends-->
[/@adminLeftPaneLayout]