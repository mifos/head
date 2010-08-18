[#ftl]
[#import "spring.ftl" as spring]
[#import "blueprintmacros.ftl" as mifos]

[@mifos.header "title" /]
<div class="container">&nbsp;
  [@mifos.topNavigationNoSecurity currentTab="Admin" /]
  <div class="sidebar htTotal">
  [#include "adminLeftPane.ftl" ]
  </div>
  <div class=" content leftMargin180">
    <div class="span-21">
  		[@mifos.crumbs breadcrumbs/]
        <div class="clear">&nbsp;</div>
    	<div class="span-20">
        	<div class="span-19">
            	<span class="orangeheading">${detailsDto.productCategoryName}</span><br />
                <span>
                [#switch detailsDto.productCategoryStatusId]
                	[#case 1]
                		<span><img src="pages/framework/images/status_activegreen.gif" /></span>&nbsp;<span>Active</span>
                	[#break]
                	[#case 2]
                		<span><img src="pages/framework/images/status_closedblack.gif" /></span>&nbsp;<span>Inactive</span>
                	[#break]
                [/#switch]
                </span><br />
                <span><span>[@spring.message "manageProducts.defineNewCategory.productType"/]</span>&nbsp;:
                	  <span>
                	  		[#list typeDto as type]
                	  			[#if type.productTypeID == detailsDto.productTypeId]
                	  				${type.productName}
                	  			[/#if]
                	  		[/#list]
                	  </span>
                </span>
                <span class="span-19 rightAlign"><a href="editCategoryInformation.ftl?globalPrdCategoryNum=${globalPrdCategoryNum}">[@spring.message "manageProducts.editCategory.editcategoryinformation"/]</a></span>                
        	</div>        	
        </div>        
        <div class="clear">&nbsp;</div>
        <p class="span-20 ">
        [#if detailsDto.productCategoryDesc?exists && detailsDto.productCategoryDesc != "null" && detailsDto.productCategoryDesc!='']
		${detailsDto.productCategoryDesc}
		[/#if]
		</span>
		<span class="fontnormal"><br>		
		<br />
        </p>
	</div>
  </div><!--Main Content Ends-->
  <div class="footer">&nbsp;</div>
</div><!--Container Ends--> 
[@mifos.footer/]