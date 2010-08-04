[#ftl]
[#import "spring.ftl" as spring]
[#import "blueprintmacros.ftl" as mifos]
[#import "macros.ftl" as mifosmacros]
[@mifos.header "title" /]
[@mifos.topNavigationNoSecurity currentTab="Admin" /]
  
  <div class="container">&nbsp;
  <!--  Main Content Begins-->
  <span id="page.id" title="createProductsMix" />
  <div class="content marginAuto">
    <div class="borders span-22">
      <div class="borderbtm span-22">
        <p class="span-15 arrowIMG orangeheading">[@spring.message "manageProducts.defineProductmix.productmixinformation"/]</p>
        <p class="span-3 arrowIMG1 orangeheading last">[@spring.message "review&Submit"/]</p>
      </div>
      <div class="subcontent ">
          <p class="font15"><span class="fontBold">[@spring.message "manageProducts.defineProductmix.addanewproductmix"/]</span>&nbsp;--&nbsp;<span class="orangeheading"> [@spring.message "manageProducts.defineProductmix.enterproductmixinformation"/]</span></p>
          <div>[@spring.message "manageProducts.defineProductmix.completethefieldsbelow.ThenclickPreview.ClickCanceltoreturntoAdminwithoutsubmittinginformation"/] </div>
          <div><span class="red">* </span>[@spring.message "manageProducts.defineProductmix.fieldsmarkedwithanasteriskarerequired"/] </div>
          <p class="error" id="error1"></p>
          <p class="fontBold">[@spring.message "manageProducts.defineProductmix.productmixdetails"/] </p>
        <form name="productsmixform" id="productsmixform" method="post" action="defineProductMix.ftl">
        	<div class="prepend-3  span-21 last">
        		<div class="span-20 ">
	        		<span class="span-5 rightAlign">
	        			<span class="red">* </span>[@spring.message "manageProducts.defineProductmix.producttype"/]&nbsp;:&nbsp;
	        		</span>
        			<span class="span-5">
	   					[@spring.bind "formBean.productTypeId" /]
					    <select id="${spring.status.expression}" name="${spring.status.expression}" onchange="return productsmixform.submit();">
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
					    <select id="${spring.status.expression}" name="${spring.status.expression}" onchange="return productsmixform.submit();">
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
				
				<div class="span-20 last">
	          		<div class="span-20">
	          			<span class="pull-3 span-8 rightAlign">[@spring.message "manageProducts.defineProductmix.removenotallowedproducts"/]&nbsp;:&nbsp;</span>
	            		<span class="span-12 ">
	                		<span class="span-9">[@spring.message "manageProducts.defineProductmix.clickonaproductintherightboxtoselect.ThenclickRemove"/]</span>
	                    	<span class="span-4">
								[@spring.formMultiSelect "formBean.notAllowed", formBean.notAllowedProductOptions, "class=listSize" /]	                    		
	            			</span>
	                    	<span class="span-3">
	                    		<br /><input class="buttn2" name="add" type="button" value="[@spring.message "add"/]" /><br /><br />
								<input class="buttn2" name="remove" type="button" value="[@spring.message "remove"/]" />
							</span>
							<span class="span-4">
								[@spring.formMultiSelect "formBean.allowed", formBean.allowedProductOptions, "class=listSize" /]
	            			</span>
	               		</span>
	            	</div>
	          	</div>
				<div class="clear">&nbsp;</div>
		
				<hr />
		        <div class="prepend-10">
		            <input class="buttn" type="submit" id="holiday.button.preview" name="preview"  value="Preview" />
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
[@mifos.footer/]