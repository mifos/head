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
<div class="span-20 bluedivs paddingLeft">
	    <a href="admin.ftl">[@spring.message "admin" /]</a>&nbsp;/&nbsp;<span class="fontBold">[@spring.message "viewLoanProducts" /]</span>  		
 </div>
  <p>&nbsp;&nbsp;</p>
  	<form method="" action="" name="formname">
  <p class="font15 orangeheading">[@spring.message "viewLoanProducts"/]</p>
    <p>[@spring.message "clickonaLoanproductbelowtoviewdetailsandmakechangesor"/] <a href="loanproductaction.do?method=load&recordOfficeId=${model.request.getSession().getAttribute("UserContext").branchId}&recordLoanOfficerId=${model.request.getSession().getAttribute("UserContext").id}">[@spring.message "defineanewLoanproduct"/] </a></p>
       	</form> 
  </div><!--Main Content Ends-->
[@mifos.footer/]