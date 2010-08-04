[#ftl]
[#import "spring.ftl" as spring]
[#import "blueprintmacros.ftl" as mifos]

[@mifos.header "title" /]
  [@mifos.topNavigationNoSecurity currentTab="Admin" /]
  [#include "adminLeftPane.ftl" ] 
   <!--  Main Content Begins--> 
	<div class=" content leftMargin180">
 		<p class="bluedivs paddingLeft"><a href="admin.ftl">Admin</a>&nbsp;/&nbsp;<span class="fontBold">View Product Categories</span></p>
    	[@mifos.crumb "viewProductCategories"/] 		
 		<br/> 		
 		<p class="font15 orangeheading">[@spring.message "viewproductcategories" /]</p>
 		<p>[@spring.message "clickonacategorybelowtoviewdetailsandmakechangesor"/] <a href="productCategoryAction.do?method=load">[@spring.message "definenewreportcategory"/]</a></p>
 		[#list dto.productCategoryTypeList as typeList]
 		[#list dto.productCategoryDtoList as categoryList]
 		[#if id??]
 			[#if id != typeList.productTypeID]
 				id = typeList.productTypeID;
									<table width="95%" border="0" cellspacing="0" cellpadding="0">
										<tr>
											<td width="61%">
											   <span class="fontnormalbold">
												 ${typeList.productName}
											   </span>
											 </td>
										</tr>
									</table> 			 				
 			[/#if]
 		[#else]
 			id = typeList.productTypeID;
									<table width="95%" border="0" cellspacing="0" cellpadding="0">
										<tr>
											<td width="61%">
											   <span class="fontnormalbold">
												 ${typeList.productName}
											   </span>
											 </td>
										</tr>
									</table> 			
 		[/#if] 		
      	
      	<div class="span-22">
    		<ul>
        		<li type="circle"><a href="productCategoryAction.do?method=get&globalPrdCategoryNum={categoryList.globalProductCategoryNumber}">${categoryList.productCategoryName}</a></li>
        	</ul>
				[#if categoryList.productCategoryStatusId == 0]
											&nbsp;<span class="fontnormal"><img
												src="pages/framework/images/status_closedblack.gif"
												width="8" height="9">&nbsp; <c:out value="Inactive" /></span>
				[/#if]        		
    	</div>
    	<br/><p>&nbsp;&nbsp;</p>
    	[/#list]
    	[/#list]    	

  </div><!--Main Content Ends -->
[@mifos.footer/]