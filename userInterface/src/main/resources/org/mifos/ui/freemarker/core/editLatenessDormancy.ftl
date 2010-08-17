[#ftl]
[#import "spring.ftl" as spring]
[#import "blueprintmacros.ftl" as mifos]

[@mifos.header "title" /]
  [@mifos.topNavigationNoSecurity currentTab="Admin" /]
  <div class="sidebar ht550">
  [#include "adminLeftPane.ftl" /]
  </div> 
 <!--  Main Content Begins-->  
  <div class=" content leftMargin180">
  	<form method="post" name="latenessanddormancy" action="editLatenessDormancy.ftl">
[@mifos.crumbs breadcrumbs /]	
  		[@spring.bind "formBean" /]
  		  [@mifos.showAllErrors "formBean.*"/]   
  		<div class="marginLeft30">
		<div class="span-24">
  		    <div class="clear">&nbsp;</div>
    		<div class="span-23 borderbtm">
        		<p class="font15"><span class="orangeheading">[@spring.message "manageProducts.editLatenessDormancy.setLatenessDefinition" /]</span></p>
            	<div class="fontBold">Loans</div>
            	<div class="span-23">
	            	<span class="span-11">[@spring.message "manageProducts.editLatenessDormancy.specifyTheNumberOfDaysOfNonPayment" /]</span>
    	            <span class="span-8">
    	            	[@spring.bind "formBean.latenessDays" /]
						<input type="text" id="lateness" name="${spring.status.expression}" value="${spring.status.value?default("")}">&nbsp;[@spring.message "manageProducts.editLatenessDormancy.days" /]
					</span>
					[@spring.showErrors "<br>" /]
        	    </div>
			<div class="clear">&nbsp;</div>
        </div>
        <div class="clear">&nbsp;</div>
        <div class="span-23 borderbtm">
			        	<p class="font15 orangeheading">[@spring.message "manageProducts.editLatenessDormancy.setDormancyDefinition" /] </p>
            <div class="fontBold">Savings</div>
            <div class="span-23">
            	<span class="span-11">[@spring.message "manageProducts.editLatenessDormancy.specifyTheNumberOfDaysToDefineDormancy"/]</span>
                <span class="span-8">
                	[@spring.bind "formBean.dormancyDays" /]
                	<input type="text" id="dormancy" name="${spring.status.expression}" value="${spring.status.value?default("")}">&nbsp;[@spring.message "manageProducts.editLatenessDormancy.days"/]
                </span>
				[@spring.showErrors "<br>" /]
            </div>
            <div class="clear">&nbsp;</div>
        </div>
        <div class="clear">&nbsp;</div>
        <div class="prepend-10">
            <input class="buttn"  type="submit" name="submit" value="[@spring.message "submit"/]"/>
            <input class="buttn2" type="submit" id="CANCEL" name="CANCEL" value="[@spring.message "cancel"/]"/>
        </div>
	</div>
	</div>
   	</form>	 
  </div><!--Main Content Ends-->
  [@mifos.footer/]