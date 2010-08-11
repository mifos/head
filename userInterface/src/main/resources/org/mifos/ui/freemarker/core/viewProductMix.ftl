[#ftl]
[#import "spring.ftl" as spring]
[#import "blueprintmacros.ftl" as mifos]
[@mifos.header "title" /]
  [@mifos.topNavigationNoSecurity currentTab="Admin" /]
  <div class="sidebar htTotal">
  [#include "adminLeftPane.ftl" /]
  </div>
  <!--  Main Content Begins-->  
  <div class=" content leftMargin180">
  	<span id="page.id" title="viewProductsMix" />
  		[@mifos.crumbs breadcrumbs/]
		<div class="marginLeft30">
			<form method="" action="" name="formname">
				<div class="marginTop10">&nbsp;</div>
				<p class="font15 orangeheading">[@spring.message "admin.viewproductsmix"/]</p>
				<p>[@spring.message "manageProduct.viewProductMix.clickonaproductinstancebelowtoviewmixdetailsandmakechangesor" /] <a href="defineProductMix.ftl" >[@spring.message "admin.defineproductsmix"/] </a></p>
				<br/>
				<div>
	   	 			<span class="fontBold">[@spring.message "manageProduct.viewProductMix.loan" /]</span>
	   	 			<ul>
	   	 			[#list mixList.mix as text]
	   	 				[#if text_has_next]
	   	 					<li><a href="productMixDetails.ftl?prdOfferingId=${text.prdOfferingId}&productType=${text.productTypeID}">${text.prdOfferingName} </a></li>
	   	 				[#else]
	   	 				    <li><a href="productMixDetails.ftl?prdOfferingId=${text.prdOfferingId}&productType=${text.productTypeID}">${text.prdOfferingName} </a></li>
	   	 				[/#if]
	   	 			[/#list]
	   	 			</ul>
   	 			</div>
   	 			<div>
   	 			<span class="fontBold">[@spring.message "manageProduct.viewProductMix.savings" /]</span>
   	 		</form>
   	 	</div>
  	 </div>  	 
  </div><!--Main Content Ends-->
  [@mifos.footer/] 