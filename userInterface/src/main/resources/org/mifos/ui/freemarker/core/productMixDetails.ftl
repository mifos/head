[#ftl]
[#import "spring.ftl" as spring]
[#import "blueprintmacros.ftl" as mifos]
[@mifos.header "title" /]
  [@mifos.topNavigationNoSecurity currentTab="Admin" /]
  <!--  Left Sidebar Begins-->
  <div class="sidebar htTotal">
  [#include "adminLeftPane.ftl" ]
  </div> 
  <!--  Left Sidebar Ends-->
  <!--  Main Content Begins-->  
  <div class=" content leftMargin180">
  	<form method="" action="" name="formname">
  	[#assign breadcrumb = {"admin":"AdminAction.do?method=load", "admin.viewproductsmix":"viewProductMix.ftl","${mixDetails.prdOfferingName}":""}/]
    [@mifos.crumbpairs breadcrumb/]
    <div class="span-22 ">
    	<div class="span-16 ">
        	<span class="orangeheading">${mixDetails.prdOfferingName}</span><br /><br />
        </div>
        <span class="span-5 rightAlign"><a href="editProductMix.ftl?productId=${mixDetails.prdOfferingId}">[@spring.message "manageProduct.productMixDetails.editproductmixinformation"/]</a></span>
    </div>
    <div class="span-22">
    	<div class="span-22"><span class="fontBold">[@spring.message "manageProduct.productMixDetails.allowedProducts"/] :</span><span>&nbsp;</span><br />
    	[#list mixDetails.allowedPrdOfferingNames as allowedProducts]
    	[#if allowedProducts_has_next]
    		<div><span>${allowedProducts.prdOfferingName}</span></div>
    		[#else]
    		<div><span>${allowedProducts.prdOfferingName}</span></div>
    	[/#if]    		
    	[/#list]	
    	</div>
    	<div class="clear">&nbsp; </div>
    	<div class="span-22"><span class="fontBold">[@spring.message "manageProduct.productMixDetails.notAllowedProducts"/] :</span><span>&nbsp;</span><br />
    		<div class="clear">&nbsp; </div>
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
   	</form> 
  </div><!--Main Content Ends-->
  [@mifos.footer/]