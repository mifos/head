[#ftl]
[#--[#ftl]

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

[#import "spring.ftl" as spring]
[#import "blueprintmacros.ftl" as mifos]
[@mifos.header "title" /]
  [@mifos.topNavigationNoSecurity currentTab="Admin" /]
  <!--  Left Sidebar Begins-->
  <div>
  <div class="sidebar ht600">
  [#include "adminLeftPane.ftl" ]
  </div>
   <!--  Main Content Begins-->
  <div class="content marginAuto">
    <div class="borders span-22">
      <div class="borderbtm span-22">
        <p class="span-17 completeIMG silverheading">[@spring.message "manageProducts.previewProductMix.productmixinformation" /]</p>
        <p class="span-3 arrowIMG orangeheading last">[@spring.message "reviewAndSubmit" /]</p>
      </div>
      <div class="subcontent ">
        <form name="productsmixform" id="productsmixform" method="post" action="previewProductMix.ftl">
            <div class="prepend-3  span-21 last">
                <div class="span-20 ">
                    <span class="span-5 rightAlign">
                        [@spring.message "manageProducts.defineProductmix.producttype"/]
                    </span>
                    <span class="span-5">${springMacroRequestContext.getMessage(ref.productTypeNameKey)}</span>
                </div>

                <div class="span-20 ">
                    <span class="span-5 rightAlign">
                        [@spring.message "manageProducts.defineProductmix.productinstancename"/]
                    </span>
                    <span class="span-7">${ref.productName}</span>
                </div>

                <div class="span-20 last">
                    [@spring.message "manageProduct.productMixDetails.allowedProducts"/]:
                    [#list ref.allowedProductMix as allowed]
                    <div>${allowed}</div>
                    [/#list]
                    <br />
                    [@spring.message "manageProduct.productMixDetails.notAllowedProducts"/]:
                    [#list ref.notAllowedProductMix as notAllowed]
                    <div>${notAllowed}</div>
                    [/#list]
                  </div>
                <div class="clear">&nbsp;</div>

                <input type="hidden" name="FORMVIEW" id="formview" value="${formView}" />

                <div class="prepend-1">
                    <input type="submit" class="buttn2" name="EDIT" value="[@spring.message "manageProduct.productMixDetails.editproductmixinformation"/]"/>
                </div>
                <hr />
                <div class="prepend-10">
                    <input class="buttn" type="submit" id="holiday.button.submit" name="submit" value="[@spring.message "submit"/]" />
                    <input class="buttn2" type="submit" id="CANCEL" name="CANCEL" value="[@spring.message "cancel"/]"/>
                </div>
            </div>

        </form>
      </div>
      <!--Subcontent Ends-->
    </div>
  </div>
  <!--Main Content Ends-->
[@mifos.footer/]--]



[#include "layout.ftl"]
[@adminLeftPaneLayout]

 <script type="text/javascript">
 function addOption(root, text, value)
{
  var newOpt = new Option(text, value);
  var rootLength = root.length;
  root.options[rootLength] = newOpt;
}

function deleteOption(root, index)
{
  var rootLength= root.length;
  if(rootLength>0)
  {
    root.options[index] = null;
  }
}

function moveOptions(root, destination)
{

  var rootLength= root.length;
  var rootText = new Array();
  var rootValues = new Array();
  var rootCount = 0;

  var i;
  for(i=rootLength-1; i>=0; i--)
  {
    if(root.options[i].selected)
    {
      rootText[rootCount] = root.options[i].text;
      rootValues[rootCount] = root.options[i].value;
      deleteOption(root, i);
      rootCount++;
    }
  }
  for(i=rootCount-1; i>=0; i--)
  {
    addOption(destination, rootText[i], rootValues[i]);
  }
}

function selectAllOptions(outSel)
{
    if(null != outSel) {
         var selLength =outSel.length;
        outSel.multiple=true;
        for(i=selLength-1; i>=0; i--)
        {
            outSel.options[i].selected=true;
        }
    }
}
</script>

  <!--  Main Content Begins-->
       <div class="content">
          <p class="font15"><span class="fontBold">[@spring.message "manageProducts.defineProductmix.addanewproductmix"/]</span>&nbsp;--&nbsp;<span class="orangeheading"> [@spring.message "manageProducts.defineProductmix.enterproductmixinformation"/]</span></p>
          <div>[@spring.message "manageProducts.defineProductmix.completethefieldsbelow.ThenclickPreview.ClickCanceltoreturntoAdminwithoutsubmittinginformation"/] </div>
          <div><span class="red">* </span>[@spring.message "manageProducts.defineProductmix.fieldsmarkedwithanasteriskarerequired"/] </div>
          [@mifos.showAllErrors "formBean.*"/]
          <p class="fontBold">[@spring.message "manageProducts.defineProductmix.productmixdetails"/] </p>
        <form name="productsmixform" id="productsmixform" method="post" action="editProductMix.ftl">
            <div class="prepend-3  span-21">
                <div class="span-20 ">
                    <span class="span-5">
                        <span class="red">* </span>[@spring.message "manageProducts.defineProductmix.producttype"/]&nbsp;:&nbsp;
                    </span>
                    <span class="span-5">
                           [@spring.bind "formBean.productTypeId" /]
                        <select id="${spring.status.expression}" name="${spring.status.expression}" onchange="return productsmixform.submit();" disabled>
                            <option value="" [@spring.checkSelected ""/]>${springMacroRequestContext.getMessage("--Select--")}</option>
                            [#if formBean.productTypeOptions?is_hash]
                                [#list formBean.productTypeOptions?keys as value]
                                <option value="${value?html}"[@spring.checkSelected value/]>${springMacroRequestContext.getMessage(formBean.productTypeOptions[value]?html)}</option>
                                [/#list]
                            [#else]
                                [#list formBean.productTypeOptions as value]
                                <option value="${value?html}"[@spring.checkSelected value/]>${springMacroRequestContext.getMessage(value?html)}</option>
                                [/#list]
                            [/#if]
                        </select>
                    </span>
                </div>

                <div class="span-20 ">
                    <span class="span-5">
                        <span class="red">* </span>[@spring.message "manageProducts.defineProductmix.productinstancename"/]&nbsp;:&nbsp;
                    </span>
                    <span class="span-7">
                        [@spring.bind "formBean.productId" /]
                        <select id="${spring.status.expression}" name="${spring.status.expression}" onchange="return productsmixform.submit();" disabled>
                            <option value="" [@spring.checkSelected ""/]>${springMacroRequestContext.getMessage("--Select--")}</option>
                            [#if formBean.productNameOptions?is_hash]
                                [#list formBean.productNameOptions?keys as value]
                                <option value="${value?html}"[@spring.checkSelected value/]>${formBean.productNameOptions[value]?html}</option>
                                [/#list]
                            [#else]
                                [#list formBean.productNameOptions as value]
                                <option value="${value?html}"[@spring.checkSelected value/]>${value?html}</option>
                                [/#list]
                            [/#if]
                        </select>
                    </span>
                </div>

                <div class="span-20">
                      <div class="span-20">
                          <span class="pull-3 span-5">[@spring.message "manageProducts.defineProductmix.removenotallowedproducts"/]&nbsp;:&nbsp;</span>
                        <span class="span-12 ">
                            <span class="span-9">[@spring.message "manageProducts.defineProductmix.clickonaproductintherightboxtoselect.ThenclickRemove"/]</span>
                            <span class="span-4">
									[@spring.bind "formBean.notAllowed"/]
					                <select name="${spring.status.expression}" class="listSize" multiple="multiple">
										[#list formBean.notAllowedProductOptions?keys as optionKey]
											[#if spring.status.value?default("")?string == optionKey?string]
											<option selected="true" value="${optionKey}">${options[optionKey]}
											[#else]
											<option value="${optionKey}">${formBean.notAllowedProductOptions[optionKey]}
											[/#if]
											</option>
										[/#list]
									</select>
                            </span>
                            <span class="span-3">
                                <br />
                                <input class="buttn2" name="add" type="button" value="[@spring.message "add"/]" onclick="moveOptions(this.form.notAllowed, this.form.allowed);"/>
                                <br /><br />
                                <input class="buttn2" name="remove" type="button" value="[@spring.message "remove"/]" onclick="moveOptions(this.form.allowed, this.form.notAllowed);"/>
                            </span>
                            <span class="span-4">
                            	[@spring.bind "formBean.allowed"/]
				                <select name="${spring.status.expression}" class="listSize" multiple="multiple">
									[#list formBean.allowedProductOptions?keys as optionKey]
										[#if spring.status.value?default("")?string == optionKey?string]
										<option selected="true" value="${optionKey}">${options[optionKey]}
										[#else]
										<option value="${optionKey}">${formBean.allowedProductOptions[optionKey]}
										[/#if]
										</option>
									[/#list]
								</select>
                            </span>
                           </span>
                    </div>
                  </div>
                <div class="clear">&nbsp;</div>
                <hr />
                <div class="prepend-10">
                    <input class="buttn" type="submit" id="holiday.button.preview" name="preview"  value="[@spring.message "preview"/]" onclick="selectAllOptions(this.form.notAllowed);selectAllOptions(this.form.allowed);" />
                    <input class="buttn2" type="submit" id="CANCEL" name="CANCEL" value="[@spring.message "cancel"/]"/>
                </div>
            </div>
        </form>
      <!--Subcontent Ends-->
    </div>
  </div>
  <!--Main Content Ends-->
  <div class="footer">&nbsp;</div>
</div>
<!--Container Ends-->
[/@adminLeftPaneLayout]