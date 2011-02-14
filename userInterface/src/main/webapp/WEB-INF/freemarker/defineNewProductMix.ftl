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
[#import "spring.ftl" as spring]
[#import "blueprintmacros.ftl" as mifos]
[@mifos.header "title" /]
 [@mifos.topNavigationNoSecurity currentTab="Admin" /]

  <div class="container">&nbsp;
  <!--  Main Content Begins-->
  <div class="content definePageMargin">
    <div class="borders span-22">
      <div class="borderbtm span-22">
        <p class="span-15 arrowIMG orangeheading">[@spring.message "manageProducts.defineProductmix.productmixinformation"/]</p>
        <p class="span-3 arrowIMG1 orangeheading last width130px">[@spring.message "reviewAndSubmit"/]</p>
      </div>
      <div class="subcontent ">
        <form method="" action="" name="formname">
          <p class="font15"><span class="fontBold">[@spring.message "manageProducts.defineProductmix.addanewproductmix"/]</span>&nbsp;--&nbsp;<span class="orangeheading"> [@spring.message "manageProducts.defineProductmix.enterproductmixinformation"/]</span></p>
          <div>[@spring.message "manageProducts.defineProductmix.completethefieldsbelow.ThenclickPreview.ClickCanceltoreturntoAdminwithoutsubmittinginformation"/] </div>
          <div><span class="red">* </span>[@spring.message "manageProducts.defineProductmix.fieldsmarkedwithanasteriskarerequired"/] </div>
          <p class="error" id="error1"></p>
          <p class="fontBold">[@spring.message "manageProducts.defineProductmix.productmixdetails"/] </p>
          <div class="prepend-3  span-21 last">
            <div class="span-20 "><span class="span-5 rightAlign"><span class="red">* </span>[@spring.message "manageProducts.defineProductmix.producttype"/]&nbsp;:&nbsp;</span><span class="span-5">
                       <select name="select">
                          <option >--Select--</option>
                        <option >Loans</option>
                    </select></span>
            </div>
            <div class="span-20 "><span class="span-5 rightAlign"><span class="red">* </span>[@spring.message "manageProducts.defineProductmix.productinstancename"/]&nbsp;:&nbsp;</span><span class="span-7">
                       <select name="select">
                          <option >--Select--</option>
                        <option >a-loan</option>
                        <option >Advance Interest</option>
                        <option >Individual</option>
                    </select></span>
            </div>
            <div class="span-20 last">
              <div class="span-20"><span class="pull-3 span-8 rightAlign">[@spring.message "manageProducts.defineProductmix.removenotallowedproducts"/]&nbsp;:&nbsp;</span>
                <span class="span-12 ">
                    <span class="span-9">[@spring.message "manageProducts.defineProductmix.clickonaproductintherightboxtoselect.ThenclickRemove"/]</span>
                    <span class="span-4">
                        <select name="lstBox" id="lstBox" multiple="multiple" class="listSize"></select></span>
                    <span class="span-3"><br /><input class="buttn2" name="add" type="button" value="[@spring.message "add"/]" /><br /><br />
                    <input class="buttn2" name="remove" type="button" value="[@spring.message "remove"/]" /></span>
                    <span class="span-4">
                    <select name="ListBox1" id="ListBox1" multiple="multiple" class="listSize">
                    </select></span>
                   </span>
            </div>
          </div>
          </div>
          <div class="clear">&nbsp;</div>
          <hr />
          <div class="prepend-9">
                <input class="buttn" type="button" name="PREVIEW" value="Preview"/>
                <input class="buttn2" type="button" name="CANCEL" value="Cancel"/>
          </div>
          <div class="clear">&nbsp;</div>
        </form>
      </div>
      <!--Subcontent Ends-->
    </div>
  </div>
  <!--Main Content Ends-->
  <div class="footer">&nbsp;</div>
</div>
<!--Container Ends-->
[@mifos.footer/]