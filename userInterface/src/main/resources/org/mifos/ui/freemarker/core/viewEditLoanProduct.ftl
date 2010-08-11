[#ftl]
[#import "spring.ftl" as spring]
[#import "blueprintmacros.ftl" as mifos]

[@mifos.header "title" /]
[@mifos.topNavigationNoSecurity currentTab="Admin" /]
<div class="sidebar htTotal">
[#include "adminLeftPane.ftl" ]
</div> 

  <!--  Main Content Begins-->  
  <div class=" content leftMargin180">
    	<div class="bluedivs paddingLeft"><a href="admin.ftl">[@spring.message "admin"/]</a>&nbsp;/&nbsp;<a href="manageLoanProducts.editloanproduct.viewLoanProducts.ftl">[@spring.message "viewLoanProducts"/]</a>&nbsp;/&nbsp;<span class="fontBold">[@spring.message "manageLoanProducts.editloanproduct.loanProductName"/]</span></div>
     <p>&nbsp;&nbsp;</p>
  	<form method="" action="" name="formname">
    <div class="span-22">
  		    	<div class="span-22 ">
        	<div class="span-18 ">
            	<span class="orangeheading">[@spring.message "manageLoanProducts.editloanproduct.loanProductName" /]</span><br /><br />
                <span><span><img src="pages/framework/images/status_activegreen.gif" /></span>&nbsp;<span>[@spring.message "active" /]</span>
        	</div>
            <span class="span-5 rightAlign"><a href="#">[@spring.message "manageLoanProducts.editloanproduct.editLoanproductinformation" /]</a></span>
        </div>
        <div class="clear">&nbsp;</div>
        <p class="span-22 ">
        	<div class="fontBold">[@spring.message "manageLoanProducts.editloanproduct.loanproductdetails" /]</div>
            <div class="span-22 "><span>[@spring.message "manageLoanProducts.editloanproduct.productinstancename" /]</span><span>&nbsp;</span></div>
            <div class="span-22 "><span>[@spring.message "manageLoanProducts.editloanproduct.shortname" /]</span><span>&nbsp;</span></div>
            <div class="clear">&nbsp;</div>
            <div class="span-22 "><span>[@spring.message "manageLoanProducts.editloanproduct.description" /]</span><br /><span>&nbsp;</span></div>
            <div class="clear">&nbsp;</div>
            <div class="span-22 "><span>[@spring.message "manageLoanProducts.editloanproduct.productcategory" /]</span><span>&nbsp;</span></div>
            <div class="span-22 "><span>[@spring.message "manageLoanProducts.editloanproduct.startdate" /]</span><span>&nbsp;</span></div>
            <div class="span-22 "><span>[@spring.message "manageLoanProducts.editloanproduct.enddate" /]</span><span>&nbsp;</span></div>
            <div class="span-22 "><span>[@spring.message "manageLoanProducts.editloanproduct.applicablefor" /]</span><span>&nbsp;</span></div>
            <div class="span-22 "><span>[@spring.message "manageLoanProducts.editloanproduct.includeinLoancyclecounter" /]</span><span>&nbsp;</span></div>
            <div class="span-22 "><span>[@spring.message "manageLoanProducts.editloanproduct.calculateLoanAmountas" /]</span><span>&nbsp;</span></div>
            <div class="span-20 ">
          		<div class="span-17 bluedivs fontBold paddingLeft">
            		<span class="span-4">[@spring.message "manageLoanProducts.editloanproduct.minloanamount" /]</span>
                    <span class="span-4">[@spring.message "manageLoanProducts.editloanproduct.maxloanamount" /]</span>
                	<span class="span-5 last">[@spring.message "manageLoanProducts.editloanproduct.defaultamount" /]</span>
            	</div>
            	<div class="span-17 paddingLeft">
                	<span class="span-4 ">&nbsp;</span>
                	<span class="span-4 ">&nbsp;</span>
                	<span class="span-5 last">&nbsp;</span>
            	</div>
          </div>
        </p>
        <p class="span-22 ">
			<div class="fontBold span-22 ">[@spring.message "manageLoanProducts.editloanproduct.interestrate" /]</div>
            <div class="span-22 "><span>[@spring.message "manageLoanProducts.editloanproduct.interestratetype" /]</span><span>&nbsp;</span></div>
            <div class="span-22 "><span>[@spring.message "manageLoanProducts.editloanproduct.maxInterestrate" /]</span><span>&nbsp;</span></div>
            <div class="span-22 "><span>[@spring.message "manageLoanProducts.editloanproduct.minInterestrate" /]</span><span>&nbsp;</span></div>
            <div class="span-22 "><span>[@spring.message "manageLoanProducts.editloanproduct.defaultInterestrate" /]</span><span>&nbsp;</span></div>
        </p>
        <div class="clear">&nbsp;</div>
        <p class="span-22 ">
			<div class="fontBold span-22 ">[@spring.message "manageLoanProducts.editloanproduct.repaymentSchedule" /]</div>
            <div class="clear">&nbsp;</div>
            <div class="span-22 "><span>[@spring.message "manageLoanProducts.editloanproduct.frequencyofinstallments" /]</span><span>&nbsp;</span></div>
            <div class="span-22 "><span>[@spring.message "manageLoanProducts.editloanproduct.calculateofinstallmentsas" /]</span><span>&nbsp;</span></div>
			<div class="span-20 ">
          		<div class="span-17 bluedivs fontBold paddingLeft">
            		<span class="span-4">[@spring.message "manageLoanProducts.editloanproduct.minofinstallments" /]</span>
                	<span class="span-4">[@spring.message "manageLoanProducts.editloanproduct.maxofinstallments" /]</span>
                	<span class="span-5 last">[@spring.message "manageLoanProducts.editloanproduct.defaultofinstallments" /]</span>
            	</div>
            	<div class="span-17 paddingLeft">
                	<span class="span-4 ">&nbsp;</span>
                	<span class="span-4 ">&nbsp;</span>
                	<span class="span-5 last">&nbsp;</span>
            	</div>
          </div>
          <div class="span-22 "><span>[@spring.message "manageLoanProducts.editloanproduct.graceperiodtype" /]</span><span>&nbsp;</span></div>
          <div class="span-22 "><span>[@spring.message "manageLoanProducts.editloanproduct.graceperiodduration" /]</span><span>&nbsp;</span></div>
        </p> 
        <p class="fontBold span-22 ">[@spring.message "manageLoanProducts.editloanproduct.fees" /]</p>
        <div class="clear">&nbsp;</div>       
        <p class="span-22 ">
			<div class="fontBold span-22 ">[@spring.message "manageLoanProducts.editloanproduct.accounting" /]</div>
            <div class="span-20 "><span>[@spring.message "manageLoanProducts.editloanproduct.sourcesoffunds" /]&nbsp;&nbsp;</span><br /><span>&nbsp;</span>
			</div>
            <div class="span-20 "><span>[@spring.message "manageLoanProducts.editloanproduct.productGLcode" /]&nbsp;&nbsp;</span></div>
            <div class="span-20 "><span>[@spring.message "manageLoanProducts.editloanproduct.interest" /]&nbsp;&nbsp;</span>
                    <span>&nbsp;</span>
			</div>
            <div class="span-20 "><span >[@spring.message "manageLoanProducts.editloanproduct.principal" /]&nbsp;&nbsp;</span>
                    <span>&nbsp;</span>
			</div>
        </p>
        <div class="clear">&nbsp;</div> 
        <p class="span-22 ">
            <div class="span-22 "><a href="#">[@spring.message "manageLoanProducts.editloanproduct.viewChangeLog" /]</a></div>
        </p>
	</div>
   	</form> 
  </div><!--Main Content Ends-->
  [@mifos.footer/]