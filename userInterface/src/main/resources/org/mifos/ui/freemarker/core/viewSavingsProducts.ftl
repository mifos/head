[#ftl]
[#import "spring.ftl" as spring]
[#import "blueprintmacros.ftl" as mifos]

[@mifos.header "title" /]
  [@mifos.topNavigationNoSecurity currentTab="Admin" /]
  <div class="sidebar htTotal">
  [#include "adminLeftPane.ftl" ]
  </div> 
   <!--  Main Content Begins-->  
  <div class=" content leftMargin180">
  [@mifos.crumb url="admin.viewSavingsproducts" /]
  <p>&nbsp;&nbsp;</p><br/>
  	<form method="" action="" name="formname">
	    <p class="font15 orangeheading">[@spring.message "admin.viewSavingsproducts"/]</p>
<p>[@spring.message "manageLoanProducts.viewSavingsProducts.clickonaSavingsproductbelowtoviewdetailsandmakechangesor"/] <a href="savingsproductaction.do?method=load">[@spring.message "admin.definenewSavingsproduct"/]</a></p>	    
            [#list products as product]
                <img src="pages/framework/images/bullet_circle.gif" width="9" height="11"/>
                ${product.prdOfferingId}  ${product.prdOfferingName } 
                                [@spring.message "ProductStatus-Active" /]
                ${product.prdOfferingStatusId} ${product.prdOfferingStatusName} 
                <br/>
            [/#list]   
  	</form> 
  </div><!--Main Content Ends-->
  [@mifos.footer/]