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

[@adminLeftPaneLayout]
  <div class=" content">
    <div class="width100prc">
          [@widget.crumbs breadcrumbs/]
        <div class="width95prc margin20lefttop">
            <div class="">
                <span class="orangeheading">${detailsDto.productCategoryName}</span><br />
                  <div style="position:relative;top:-20px; text-align:right; height:10px;"><a href="editCategoryInformation.ftl?globalPrdCategoryNum=${globalPrdCategoryNum}">[@spring.message "manageProducts.editCategory.editcategoryinformation"/]</a></div>
                <span>
                [#switch detailsDto.productCategoryStatusId]
                    [#case 1]
                        <span><img src="pages/framework/images/status_activegreen.gif" /></span>&nbsp;
                        <span>
                            [@spring.message "ftlDefinedLabels.active" /]
                        </span>
                    [#break]
                    [#case 2]
                        <span><img src="pages/framework/images/status_closedblack.gif" /></span>&nbsp;
                        <span>
                            [@spring.message "ftlDefinedLabels.inactive" /]
                        </span>
                    [#break]
                [/#switch]
                </span><br />
                <span><span>[@spring.message "manageProducts.defineNewCategory.productType"/]:</span>&nbsp;
                      <span>
                              [#list typeDto as type]
                                  [#if type.productTypeID == detailsDto.productTypeId]
                                      [@mifostag.mifoslabel name="${type.productName}" /]
                                  [/#if]
                              [/#list]
                      </span>
                </span>
            </div>
        </div>
        <p class="margin20lefttop">
        [@spring.message "manageProducts.editCategory.description"/]<br />
        [#if detailsDto.productCategoryDesc?exists && detailsDto.productCategoryDesc != "null" && detailsDto.productCategoryDesc!='']
        ${detailsDto.productCategoryDesc}
        [/#if]
        </span>
        <span class="fontnormal"><br>
        <br />
        </p>
    </div>
  </div><!--Main Content Ends-->
[/@adminLeftPaneLayout]