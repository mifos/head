[#ftl]
[#import "spring.ftl" as spring]
[#import "blueprintmacros.ftl" as mifos]
[#import "macros.ftl" as mifosmacros]
[@mifos.header "title" /]
[@mifos.topNavigationNoSecurity currentTab="Admin" /]
  <div class="container">&nbsp;
  <!--  Main Content Begins-->
  <span id="page.id" title="previewProductsMix" />
<div class="content definePageMargin">
    <div class="borders margin20lefttop width90prc">
        <div class="borderbtm width100prc height25px">
            <p class="span-17 completeIMG silverheading  padding20left"
               style="width:50%">[@spring.message "manageProducts.defineProductmix.productmixinformation"/]</p>

            <p class="span-3 timelineboldorange arrowIMG1 last padding20left10right"
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
[@mifos.footer/]