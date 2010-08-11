[#ftl]
[#import "spring.ftl" as spring]
[#import "blueprintmacros.ftl" as mifos]

[@mifos.header "title" /]
<div class="container">&nbsp;
  [@mifos.topNavigationNoSecurity currentTab="Admin" /]
  <div class="sidebar htTotal">
  [#include "adminLeftPane.ftl" ]
  </div> 
   <!--  Main Content Begins--> 
<div class="content leftMargin180">
  	<form method="" action="viewProductCategories.ftl" name="viewProductCategories">
  		[@mifos.crumbs breadcrumbs/]
  		<p class="font15 orangeheading">[@spring.message "manageProducts.viewProductCategories.viewproductcategories" /]</p>
 		<p>[@spring.message "manageProducts.viewProductCategories.clickonacategorybelowtoviewdetailsandmakechangesor"/] <a href="productCategoryAction.do?method=load">[@spring.message "admin.definenewreportcategory"/]</a></p>
 		<div class="span-22 marginLeft30"> 		
 			<span class="fontBold">[@spring.message "manageProducts.viewProductCategories.loans"/]</span>
    			<ul>
        			[#list dto.productCategoryTypeList as typeList]      		      	
    					[#list dto.productCategoryDtoList as dtoList]    			
    						[#if typeList.productName == "Loan-Loan" && typeList_index == dtoList_index]
    							<li type="circle"><a href="viewProductCategoryDetails.ftl?globalPrdCategoryNum=${dtoList.globalProductCategoryNumber}">${dtoList.productCategoryName}</a></li>
    						[/#if]
    					[/#list]      			      		
      				[/#list]
      			</ul>
   		</div>
    	<div class="span-22 marginLeft30">
      		<span class="fontBold">[@spring.message "manageProducts.viewProductCategories.savings"/]</span>
    			<ul>
        			[#list dto.productCategoryTypeList as typeList]      		      	
    					[#list dto.productCategoryDtoList as dtoList]    			
    						[#if typeList.productName=="Savings-Savings" && typeList_index==dtoList_index]
    							<li type="circle"><a href="viewProductCategoryDetails.ftl?globalPrdCategoryNum=${dtoList.globalProductCategoryNumber}">${dtoList.productCategoryName}</a></li>
    						[/#if]
    					[/#list]      			      		
      				[/#list]    
        		</ul>	
    		</div>    	
    </form> 
  </div><!--Main Content Ends -->
  </div>
[@mifos.footer/]