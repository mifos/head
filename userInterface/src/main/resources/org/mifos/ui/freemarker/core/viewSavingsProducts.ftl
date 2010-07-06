[#ftl]
[#import "spring.ftl" as spring]
[#import "blueprintmacros.ftl" as mifos]

[@mifos.header "title" /]
  [@mifos.topNavigationNoSecurity currentTab="Admin" /]
  <div class="sidebar ht600">
  [#include "adminLeftPane.ftl" ]
  </div> 
   <!--  Main Content Begins-->  
  <div class=" content leftMargin180">
  [@mifos.crumb url="viewSavingsproducts" /]
  <p>&nbsp;&nbsp;</p><br/>
  	<form method="" action="" name="formname">
	    <p class="font15 orangeheading">[@spring.message "viewSavingsproducts"/]</p>
	    <p>[@spring.message "clickonaSavingsproductbelowtoviewdetailsandmakechangesor"/] <a href="savingsproductaction.do?method=load&recordOfficeId=${model.request.getSession().getAttribute("UserContext").branchId}&recordLoanOfficerId=${model.request.getSession().getAttribute("UserContext").id}">[@spring.message "defineanewSavingsproduct"/] </a></p>   
  	</form> 
  </div><!--Main Content Ends-->
  [@mifos.footer/]