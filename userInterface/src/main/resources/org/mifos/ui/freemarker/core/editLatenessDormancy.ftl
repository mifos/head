[#ftl]
[#import "spring.ftl" as spring]
[#import "blueprintmacros.ftl" as mifos]

[@mifos.header "title" /]
  [@mifos.topNavigationNoSecurity currentTab="Admin" /]
  [#include "adminLeftPane.ftl" /] 

 
 <!--  Main Content Begins-->  
  <div class=" content leftMargin180">
  	<form method="post" name="latenessanddormancy" action="editLatenessDormancy.ftl">
  	[@spring.bind "formBean" /]
  	[@spring.showErrors "<br>" /]
		<div id="latenessdiv">
			<h3>Set Lateness Definition</h3>

    	    <h4>Loan</h4>
        	<label for="lateness">Specify the number of days of non-payment after which status of Loan account is changed to "In Arrears" by the system:</label>
        	[@spring.bind "formBean.latenessDays" /]
			<input type="text" id="lateness" name="${spring.status.expression}" value="${spring.status.value?default("")}">
			[@spring.showErrors "<br>" /]
		</div>
	    <div id="dormancydiv">
			<h3>Set Dormancy Definition</h3>

    	    <h4>Savings</h4>
        	<label for="dormancy">Specify the number of days to define "Dormancy" in Savings Accounts. The account status will be changed to "On Hold" by the system:</label>
        	[@spring.bind "formBean.dormancyDays" /]
			<input type="text" id="dormancy" name="${spring.status.expression}" value="${spring.status.value?default("")}">
			[@spring.showErrors "<br>" /]
		</div>
		<input type="submit" name="submit" value="Submit" />
		<input type="submit" id="CANCEL" name="CANCEL" value="Cancel" />
   	</form> 
  </div><!--Main Content Ends-->
  [@mifos.footer/]