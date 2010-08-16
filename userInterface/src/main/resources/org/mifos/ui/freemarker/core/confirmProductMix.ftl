[#ftl]
[#import "spring.ftl" as spring]
[#import "blueprintmacros.ftl" as mifos]
[@mifos.header "title" /]
[@mifos.topNavigationNoSecurity currentTab="Admin" /]
 <!--  Left Sidebar Begins-->
 <div class="container">&nbsp;
  <div class="sidebar">
  [#include "adminLeftPane.ftl" ]
  </div> 
  <!--  Left Sidebar Ends-->  
  <!--  Main Content Begins-->
  <span id="page.id" title="createProductsMixConfirmation" />  
  <div class="content leftMargin180">
  	<div class="marginLeft30 marginTop15">
  		<p class="orangeheading marginTop15">[@spring.message "manageProductMix.youhavesuccessfullydefinedanewproductmix"/]</p>
  		<span class="fontBold marginTop15"><a href="productMixDetails.ftl?prdOfferingId=${productId}&productType=1">[@spring.message "manageProductMix.viewproductmixdetailsnow"/]</a></span><br />
  		<span class="marginTop15"><a href="defineProductMix.ftl">[@spring.message "manageProductMix.definemixforanewproduct"/]</a></span>
  	</div>
  </div>  
  <!--Main Content Ends-->
  <div class="footer">&nbsp;</div>
</div>
<!--Container Ends-->
[@mifos.footer/]