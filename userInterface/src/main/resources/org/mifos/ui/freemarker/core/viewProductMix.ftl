[#ftl]
[#import "spring.ftl" as spring]
[#import "blueprintmacros.ftl" as mifos]
[@mifos.header "title" /]
  [@mifos.topNavigationNoSecurity currentTab="Admin" /]
  [#include "adminLeftPane.ftl" /]
  <!--  Main Content Begins-->  
  
  <div class=" content leftMargin180">
	<p class="span-20 bluedivs paddingLeft"><a href="admin.ftl">Admin</a>&nbsp;/&nbsp;<span class="fontBold"> View Product Mix</span></p>
  	<form method="" action="" name="formname">
  	    <div class="marginTop10">&nbsp;</div> 
    <p class="font15 orangeheading">[@spring.message "viewProductMix"/]</p>
    <p>[@spring.message "clickonaproductinstancebelowtoviewmixdetailsandmakechangesor"/] <a href="productMixAction.do?method=load&recordOfficeId=${model.request.getSession().getAttribute("UserContext").branchId}&recordLoanOfficerId=${model.request.getSession().getAttribute("UserContext").id}">[@spring.message "definemixforanewproduct"/]</a></p>
    
   	</form> 
  </div><!--Main Content Ends-->
  [@mifos.footer/] 