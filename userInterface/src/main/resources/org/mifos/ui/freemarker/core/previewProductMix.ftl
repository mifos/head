[#ftl]
[#import "spring.ftl" as spring]
[#import "blueprintmacros.ftl" as mifos]
[#import "macros.ftl" as mifosmacros]
[@mifos.header "title" /]
[@mifos.topNavigationNoSecurity currentTab="Admin" /]
  <div class="container">&nbsp;
  <!--  Main Content Begins-->
  <span id="page.id" title="previewProductsMix" />
  <div class="content marginAuto">
    <div class="borders span-22">
      <div class="borderbtm span-22">
        <p class="span-15 arrowIMG1 orangeheading">[@spring.message "manageProducts.defineProductmix.productmixinformation"/]</p>
        <p class="span-3 arrowIMG orangeheading last">[@spring.message "review&Submit"/]</p>
      </div>
      <div class="subcontent ">
          <p class="fontBold">[@spring.message "manageProducts.defineProductmix.productmixdetails"/] </p>
          
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
      <!--Subcontent Ends-->
    </div>
  </div>
  <!--Main Content Ends-->
  <div class="footer">&nbsp;</div>
</div>
<!--Container Ends-->
[@mifos.footer/]