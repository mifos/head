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

  <div class="container">&nbsp;

  <!--  Main Content Begins-->
  <span id="page.id" title="editProductsMix"></span>
<div class="content definePageMargin">
    <div class="borders margin20lefttop width90prc">
        <div class="borderbtm width100prc height25px">
            <p class="span-17 timelineboldorange arrowIMG  padding20left"
               style="width:50%">[@spring.message "manageProducts.defineProductmix.productmixinformation"/]</p>

            <p class="span-3 timelineboldorange arrowIMG1 last padding20left10right width130px"
               style="float:right">[@spring.message "reviewAndSubmit" /]</p>
        </div>
        <div class="margin20lefttop">
          <p class="font15 margin10topbottom"><span class="fontBold">[@spring.message "manageProducts.defineProductmix.addanewproductmix"/]</span>&nbsp;-<span class="orangeheading"> [@spring.message "manageProducts.defineProductmix.enterproductmixinformation"/]</span></p>
          <div>[@spring.message "manageProducts.defineProductmix.completethefieldsbelow.ThenclickPreview.ClickCanceltoreturntoAdminwithoutsubmittinginformation"/] </div>
          <div><span class="red">* </span>[@spring.message "manageProducts.defineProductmix.fieldsmarkedwithanasteriskarerequired"/] </div>
          [@form.showAllErrors "formBean.*"/]
          <p class="fontBold margin10topbottom">[@spring.message "manageProducts.defineProductmix.productmixdetails"/] </p>
        <form name="productsmixform" id="productsmixform" method="post" action="editProductMix.ftl">
            <div class="prepend-3 last width90prc">
                <div class="span-20 ">
                    <span class="span-5 rightAlign">
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
                    <span class="span-5 rightAlign">
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

                <div class="span-20 last margin20topbottom width100prc">
                      <div class="span-20 width100prc">
                          <span class="pull-3 span-8 rightAlign">[@spring.message "manageProducts.defineProductmix.removenotallowedproducts"/]&nbsp;:&nbsp;</span>
                        <span class="span-12" style="width:auto;">
                            <span class="span-9">[@spring.message "manageProducts.defineProductmix.clickonaproductintherightboxtoselect.ThenclickRemove"/]</span>
                            <br/>
                            <span class="span-4" style="width:auto;">
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
                            <span class="span-3" >
                                <br />
                                <input class="buttn2 width70px" name="add" type="button" value="[@spring.message "add"/] >>"  onclick="moveOptions(this.form.notAllowed, this.form.allowed);"/>
                                <br /><br />
                                <input class="buttn2 width70px" name="remove" type="button" value="<< [@spring.message "remove"/]" onclick="moveOptions(this.form.allowed, this.form.notAllowed);"/>
                            </span>
                            <span class="span-4" style="width:auto;">
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
            </div>
            <div class="buttonsSubmitCancel margin20right margin10topbottom">
                <input class="buttn" type="submit" id="holiday.button.preview" name="preview"  value="[@spring.message "preview"/]" onclick="selectAllOptions(this.form.notAllowed);selectAllOptions(this.form.allowed);" />
                <input class="buttn2" type="submit" id="CANCEL" name="CANCEL" value="[@spring.message "cancel"/]"/>
            </div>
        </form>
      <!--Subcontent Ends-->
    </div>
  </div>
  <!--Main Content Ends-->
  <div class="footer">&nbsp;</div>
</div>
<!--Container Ends-->
[@layout.footer/]