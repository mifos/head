[#ftl]
[#import "spring.ftl" as spring]
[#import "blueprintmacros.ftl" as mifos]

[@mifos.header "title" /]
[@mifos.topNavigationNoSecurity currentTab="Admin" /]
<div class="sidebar ht950">
[#include "adminLeftPane.ftl" ]
</div> 

  <!--  Main Content Begins-->  
  <div class=" content leftMargin180">
    	<div class="bluedivs paddingLeft"><a href="admin.ftl">[@spring.message "admin"/]</a>&nbsp;/&nbsp;<a href="viewLoanProducts.ftl">[@spring.message "viewLoanProducts"/]</a>&nbsp;/&nbsp;<span class="fontBold">[@spring.message "loanProductName"/]</span></div>
     <p>&nbsp;&nbsp;</p>
  	<form method="" action="" name="formname">
    <div class="span-22">
  		    	<div class="span-22 ">
        	<div class="span-18 ">
            	<span class="orangeheading">[@spring.message "loanProductName" /]</span><br /><br />
                <span><span><img src="pages/framework/images/status_activegreen.gif" /></span>&nbsp;<span>[@spring.message "active" /]</span>
        	</div>
            <span class="span-5 rightAlign"><a href="#">[@spring.message "editLoanproductinformation" /]</a></span>
        </div>
        <div class="clear">&nbsp;</div>
        <p class="span-22 ">
        	<div class="fontBold">[@spring.message "loanproductdetails" /]</div>
            <div class="span-22 "><span>[@spring.message "productinstancename" /]</span><span>&nbsp;</span></div>
            <div class="span-22 "><span>[@spring.message "shortname" /]</span><span>&nbsp;</span></div>
            <div class="clear">&nbsp;</div>
            <div class="span-22 "><span>[@spring.message "description" /]</span><br /><span>&nbsp;</span></div>
            <div class="clear">&nbsp;</div>
            <div class="span-22 "><span>[@spring.message "productcategory" /]</span><span>&nbsp;</span></div>
            <div class="span-22 "><span>[@spring.message "startdate" /]</span><span>&nbsp;</span></div>
            <div class="span-22 "><span>[@spring.message "enddate" /]</span><span>&nbsp;</span></div>
            <div class="span-22 "><span>[@spring.message "applicablefor" /]</span><span>&nbsp;</span></div>
            <div class="span-22 "><span>[@spring.message "includeinLoancyclecounter" /]</span><span>&nbsp;</span></div>
            <div class="span-22 "><span>[@spring.message "calculateLoanAmountas" /]</span><span>&nbsp;</span></div>
            <div class="span-20 ">
          		<div class="span-17 bluedivs fontBold paddingLeft">
            		<span class="span-4">[@spring.message "minloanamount" /]</span>
                    <span class="span-4">[@spring.message "maxloanamount" /]</span>
                	<span class="span-5 last">[@spring.message "defaultamount" /]</span>
            	</div>
            	<div class="span-17 paddingLeft">
                	<span class="span-4 ">&nbsp;</span>
                	<span class="span-4 ">&nbsp;</span>
                	<span class="span-5 last">&nbsp;</span>
            	</div>
          </div>
        </p>
        <p class="span-22 ">
			<div class="fontBold span-22 ">[@spring.message "interestrate" /]</div>
            <div class="span-22 "><span>[@spring.message "interestratetype" /]</span><span>&nbsp;</span></div>
            <div class="span-22 "><span>[@spring.message "maxInterestrate" /]</span><span>&nbsp;</span></div>
            <div class="span-22 "><span>[@spring.message "minInterestrate" /]</span><span>&nbsp;</span></div>
            <div class="span-22 "><span>[@spring.message "defaultInterestrate" /]</span><span>&nbsp;</span></div>
        </p>
        <div class="clear">&nbsp;</div>
        <p class="span-22 ">
			<div class="fontBold span-22 ">[@spring.message "repaymentSchedule" /]</div>
            <div class="clear">&nbsp;</div>
            <div class="span-22 "><span>[@spring.message "frequencyofinstallments" /]</span><span>&nbsp;</span></div>
            <div class="span-22 "><span>[@spring.message "calculateofinstallmentsas" /]</span><span>&nbsp;</span></div>
			<div class="span-20 ">
          		<div class="span-17 bluedivs fontBold paddingLeft">
            		<span class="span-4">[@spring.message "minofinstallments" /]</span>
                	<span class="span-4">[@spring.message "maxofinstallments" /]</span>
                	<span class="span-5 last">[@spring.message "defaultofinstallments" /]</span>
            	</div>
            	<div class="span-17 paddingLeft">
                	<span class="span-4 ">&nbsp;</span>
                	<span class="span-4 ">&nbsp;</span>
                	<span class="span-5 last">&nbsp;</span>
            	</div>
          </div>
          <div class="span-22 "><span>[@spring.message "graceperiodtype" /]</span><span>&nbsp;</span></div>
          <div class="span-22 "><span>[@spring.message "graceperiodduration" /]</span><span>&nbsp;</span></div>
        </p> 
        <p class="fontBold span-22 ">[@spring.message "fees" /]</p>
        <div class="clear">&nbsp;</div>       
        <p class="span-22 ">
			<div class="fontBold span-22 ">[@spring.message "accounting" /]</div>
            <div class="span-20 "><span>[@spring.message "sourcesoffunds" /]&nbsp;&nbsp;</span><br /><span>&nbsp;</span>
			</div>
            <div class="span-20 "><span>[@spring.message "productGLcode" /]&nbsp;&nbsp;</span></div>
            <div class="span-20 "><span>[@spring.message "interest" /]&nbsp;&nbsp;</span>
                    <span>&nbsp;</span>
			</div>
            <div class="span-20 "><span >[@spring.message "principal" /]&nbsp;&nbsp;</span>
                    <span>&nbsp;</span>
			</div>
        </p>
        <div class="clear">&nbsp;</div> 
        <p class="span-22 ">
            <div class="span-22 "><a href="#">[@spring.message "viewChangeLog" /]</a></div>
        </p>
	</div>
   	</form> 
  </div><!--Main Content Ends-->
  [@mifos.footer/]