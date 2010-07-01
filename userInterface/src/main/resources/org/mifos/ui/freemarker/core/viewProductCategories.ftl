[#ftl]
[#import "spring.ftl" as spring]
[#import "blueprintmacros.ftl" as mifos]

[@mifos.header "title" /]
  [@mifos.topNavigationNoSecurity currentTab="Admin" /]
  [#include "adminLeftPane.ftl" ] 
   <!--  Main Content Begins--> 
<div class=" content leftMargin180">
  	<form method="" action="" name="formname">
 <p class="bluedivs paddingLeft"><a href="admin.ftl">Admin</a>&nbsp;/&nbsp;<span class="fontBold">View Product Categories</span></p><br/>
    <br/><p class="font15 orangeheading">[@spring.message "viewproductcategories" /]</p>
    <p>[@spring.message "clickonacategorybelowtoviewdetailsandmakechangesor"/] <a href="productCategoryAction.do?method=load">[@spring.message "definenewreportcategory"/]</a></p>
    <div class="fontBold">[@spring.message "loans"/]</div>
    <div class="span-22">
    	<ul>
        	<li type="circle"><a href="productCategoryAction.do?method=get&globalPrdCategoryNum=1-1">[@spring.message "others"/]</a></li>
        </ul>	
    </div>
    <div class="fontBold">[@spring.message "savings"/]</div>
    <div class="span-22">
    	<ul>
        	<li type="circle"><a href="productCategoryAction.do?method=get&globalPrdCategoryNum=1-2">[@spring.message "others"/]</a></li>
        </ul>	
    </div>
   	</form> 
  </div><!--Main Content Ends "-->
[@mifos.footer/]