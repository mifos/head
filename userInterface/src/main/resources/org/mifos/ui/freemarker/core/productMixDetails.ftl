[#ftl]
[#import "spring.ftl" as spring]
[#import "blueprintmacros.ftl" as mifos]
[@mifos.header "title" /]
  [@mifos.topNavigationNoSecurity currentTab="Admin" /]
  <!--  Left Sidebar Begins-->
  <div class="sidebar ht600">
  [#include "adminLeftPane.ftl" ]
  </div> 
  <!--  Left Sidebar Ends-->
  <!--  Main Content Begins-->  
  <div class=" content leftMargin180">
  	<form method="" action="" name="formname">
  	[#assign breadcrumb = {"admin":"AdminAction.do?method=load", "viewProductMix":"viewProductMix.ftl","${mixDetails.prdOfferingName}":""}/]
    [@mifos.crumbpairs breadcrumb/]
    <div class="span-22 ">
    	<div class="span-16 ">
        	<span class="orangeheading">${mixDetails.prdOfferingName}</span><br /><br />
        </div>
        <span class="span-5 rightAlign"><a href="#">[@spring.message "editproductmixinformation"/]</a></span>
    </div>
    <div class="span-22">
    	<div class="span-22"><span class="fontBold">[@spring.message "productMixDetails.allowedProducts"/] :</span><span>&nbsp;</span><br />
    	[#list mixDetails.allowedPrdOfferingNames as allowedProducts]
    	[#if allowedProducts_has_next]
    		<div><span>${allowedProducts}</span></div>
    		[#else]
    		<div><span>${allowedProducts}</span></div>
    	[/#if]    		
    	[/#list]	
    	</div>
    	<div class="clear">&nbsp; </div>
    	<div class="span-22"><span class="fontBold">[@spring.message "productMixDetails.notAllowedProducts"/] :</span><span>&nbsp;</span><br />
    		<div class="clear">&nbsp; </div>
    		[#list mixDetails.notAllowedPrdOfferingNames as notAllowedProducts]
    		[#if notAllowedProducts_has_next]
    		<div>
    			<span>${notAllowedProducts}</span>
    		</div>
    		[#else]
    		<div>
    			<span>${notAllowedProducts}</span>
    		</div>
    		[/#if]
    	[/#list]
    	</div>
        <div class="span-22"><a href="#">[@spring.message "viewChangeLog"/]</a></div>
    </div>
   	</form> 
  </div><!--Main Content Ends-->
  [@mifos.footer/]