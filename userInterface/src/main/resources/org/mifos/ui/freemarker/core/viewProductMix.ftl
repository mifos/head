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
   	
   	[#list mixList.mix as text]
   	<div>
   	 <span class="fontBold">[@spring.message "loan" /]</span> 
   		[#if text_has_next]
                <ul><img src="pages/framework/images/bullet_circle.gif" width="9" height="11"><a href="productMixAction.do?method=get&prdOfferingId=${text.prdOfferingId}&productType=${text.productTypeID}">${text.prdOfferingName} </a></ul>
				[#else]
	          <ul><img src="pages/framework/images/bullet_circle.gif" width="9" height="11"><a href="productMixAction.do?method=get&prdOfferingId=${text.prdOfferingId}&productType=${text.productTypeID}">${text.prdOfferingName} </a></ul>
  		[/#if]
  	</div>
  	
  	<div>
  	 <span class="fontBold">[@spring.message "savings" /]</span>
  	</div>
  	 [/#list]
  	 
  	 </div>
  	 
  	 
  </div><!--Main Content Ends-->
  [@mifos.footer/] 