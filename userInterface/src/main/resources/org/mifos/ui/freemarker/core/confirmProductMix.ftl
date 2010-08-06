[#ftl]
[#import "spring.ftl" as spring]
[#import "blueprintmacros.ftl" as mifos]
[#import "macros.ftl" as mifosmacros]
[@mifos.header "title" /]
[@mifos.topNavigationNoSecurity currentTab="Admin" /]
  <div class="container">&nbsp;
  <!--  Main Content Begins-->
  <span id="page.id" title="createProductsMixConfirmation" />
  
  <div class="content marginAuto">
  	<p>You have successfully defined a new product mix</p>
  	
  	<a href="productMixDetails.ftl?prdOfferingId=${productId}&productType=1">View product mix details now</a>

	<a href="defineProductMix.ftl">Define mix for a new product</a>
   
  </div>
    
  <!--Main Content Ends-->
  <div class="footer">&nbsp;</div>
</div>
<!--Container Ends-->
[@mifos.footer/]