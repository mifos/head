[#ftl]
[#import "spring.ftl" as spring]
[#import "blueprintmacros.ftl" as mifos]

[@mifos.header "title" /]
  [@mifos.topNavigationNoSecurity currentTab="Admin" /]
  <div class="sidebar ht600">
  [#include "adminLeftPane.ftl" ]
  </div> 
    <!--  Main Content Begins-->  
  <div class="content leftMargin180">
  [@mifos.crumbs breadcrumbs/]
  <p>&nbsp;&nbsp;</p>
  	<form method="POST" action="viewLoanproducts.ftl" name="viewLoanproducts">
  <p class="marginLeft30 font15 orangeheading">[@spring.message "admin.viewLoanProducts"/]</p>
    <p class="marginLeft30">[@spring.message "manageLoanProducts.viewLoanProducts.clickonaLoanproductbelowtoviewdetailsandmakechangesor"/] <a href="#">[@spring.message "admin.definenewLoanproduct"/] </a></p>
    <div class="marginTop15">
    <div class="span-22 marginLeft30"> 
    	<ul>
    	[#list formBean as loans]
    	<li><a href="loanproductaction.do?method=get&prdOfferingId=${loans.prdOfferingId}">${loans.prdOfferingName}</a></li>
    	[/#list]        	 
        </ul>	
    </div> 
    </div>
       	</form> 
  </div><!--Main Content Ends-->
[@mifos.footer/]