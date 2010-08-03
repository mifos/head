[#ftl]
[#import "spring.ftl" as spring]
[#import "blueprintmacros.ftl" as mifos]

[@mifos.header "title" /]
  [@mifos.topNavigationNoSecurity currentTab="Admin" /]
  [#include "adminLeftPane.ftl" ] 
   <!--  Main Content Begins--> 
<div class=" content leftMargin180">
  	<form method="" action="" name="formname">
 		<p class="bluedivs paddingLeft"><a href="admin.ftl">[@spring.message "admin"/]</a>&nbsp;/&nbsp;<span class="fontBold">[@spring.message "admin.viewproductcategories"/]</span></p>
 		<br/>
 		<p class="font15 orangeheading">[@spring.message "manageProducts.viewProductCategories.viewproductcategories" /]</p>
 		<p>[@spring.message "manageProducts.viewProductCategories.clickonacategorybelowtoviewdetailsandmakechangesor"/] <a href="productCategoryAction.do?method=load">[@spring.message "admin.definenewreportcategory"/]</a></p>
      	<div class="fontBold">[@spring.message "manageProducts.viewProductCategories.loans"/]</div>
      	<div class="span-22">
    		<ul>
        		<li type="circle"><a href="productCategoryAction.do?method=get&globalPrdCategoryNum=1-1">[@spring.message "manageProducts.viewProductCategories.others"/]</a></li>
        	</ul>	
    	</div>
    	<br/><p>&nbsp;&nbsp;</p>
        <div class="fontBold">[@spring.message "manageProducts.viewProductCategories.savings"/] </div>
    	<div class="span-22">
    	<ul>
        	<li type="circle"><a href="productCategoryAction.do?method=get&globalPrdCategoryNum=1-2">[@spring.message "manageProducts.viewProductCategories.others"/]</a></li>
        </ul>	
    	</div>
    	
    	
   	</form> 
  </div><!--Main Content Ends -->
[@mifos.footer/]