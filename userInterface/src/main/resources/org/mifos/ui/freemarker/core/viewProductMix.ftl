[#ftl]
[#import "spring.ftl" as spring]
[#import "blueprintmacros.ftl" as mifos]
[@mifos.header "title" /]
  [@mifos.topNavigationNoSecurity currentTab="Admin" /]
  [#include "adminLeftPane.ftl" /]
  <!--  Main Content Begins-->  
  
  <div class=" content leftMargin180">
    <span id="page.id" title="viewProductsMix" />
	[@mifos.crumbs breadcrumbs/]
	<div class="marginLeft30">
  	<form method="" action="" name="formname">
  	    <div class="marginTop10">&nbsp;</div> 
    <p class="font15 orangeheading">[@spring.message "viewProductMix"/]</p>
    <p>[@spring.message "clickonaproductinstancebelowtoviewmixdetailsandmakechangesor" /] <a href="productMixAction.do?method=load" >[@spring.message "definemixforanewproduct"/] </a></p>
    
   	</form>
   	
   	<br/> 
   	
   	
   	<div>
   	 <span class="fontBold">[@spring.message "loan" /]</span>
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
  	 <span class="fontBold">[@spring.message "savings" /]</span>
  	</div>
  	 
  	 
  	 </div>
  	 
  	 
  </div><!--Main Content Ends-->
  [@mifos.footer/] 