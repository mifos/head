[#ftl]
[#import "spring.ftl" as spring]
[#import "blueprintmacros.ftl" as mifos]
[#import "macros.ftl" as mifosmacros]
[@mifos.header "title" /]
[@mifos.topNavigationNoSecurity currentTab="Admin" /]
 <!--  Left Sidebar Begins-->
  <div class="sidebar">
  [#include "adminLeftPane.ftl" ]
  </div> 
  <!--  Left Sidebar Ends-->
  <div class="container">&nbsp;
  <!--  Main Content Begins-->
  <span id="page.id" title="createProductsMixConfirmation" />
  
  <div class="content marginAuto leftMargin180">
  	<p>[@spring.message "manageProductMix.youhavesuccessfullydefinedanewproductmix"/]</p>
  	
  	<a href="productMixDetails.ftl?prdOfferingId=${productId}&productType=1">[@spring.message "manageProductMix.viewproductmixdetailsnow"/]</a>

	<a href="defineProductMix.ftl">[@spring.message "manageProductMix.definemixforanewproduct"/]</a>
   
  </div>
    
  <!--Main Content Ends-->
  <div class="footer">&nbsp;</div>
</div>
<!--Container Ends-->
[@mifos.footer/]