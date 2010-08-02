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
 		
			<table width="95%" border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td width="70%" align="left" valign="top" class="paddingL15T15">
					<table width="96%" border="0" cellpadding="3" cellspacing="0">
						<tr>
							<td width="50%" height="23" class="headingorange"><c:out
								value="${BusinessKey.productCategoryName}" /></td>
							<td width="50%" align="right"> 		
 								<p>[@spring.message "clickonacategorybelowtoviewdetailsandmakechangesor"/] <a href="productCategoryAction.do?method=manage">[@spring.message "definenewreportcategory"/]</a></p>
							</td>
						</tr>
						<tr>
							<td height="23" colspan="2" class="fontnormalbold"><span
								class="fontnormal"> </span><span class="fontnormal"> 
								[#switch detailsDto.productCategoryStatusId] 
									[#case productCategoryStatusId = 1]
									<span class="fontnormal"> <img
										src="pages/framework/images/status_activegreen.gif" width="8"
										height="9">&nbsp; Active </span>
									[#break]
									[#default]
									<span class="fontnormal"> <img
										src="pages/framework/images/status_closedblack.gif" width="8"
										height="9">&nbsp;Active </span>
							[/#switch] <br>
							<span class="fontnormal"> Product Type <br>
								[#list typeDto as type]
									[#if type.productTypeID == detailsDto.productTypeId]
										${type.productName} /></span><br>
									[/#if]
								[/#list] <span class="fontnormal"> </span><span
								class="fontnormal"></span><span class="fontnormal"> </span><span
								class="fontnormal"></span><br>

								[#if detailsDto.productCategoryDesc != null && detailsDto.productCategoryDesc != ''] 
								Product Description
								<span class="fontnormal"><br>
								</span>
								<span class="fontnormal"> <c:out
									value="${detailsDto.productCategoryDesc}" /> </span>
								<span class="fontnormal"><br>
								[/#if] </span><span class="fontnormal"> </span></td>
						</tr>
					</table>
					<br>
					</td>
				</tr>
			</table>    	

  </div><!--Main Content Ends -->
[@mifos.footer/]