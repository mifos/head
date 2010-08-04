[#ftl]
[#import "spring.ftl" as spring]
[#import "blueprintmacros.ftl" as mifos]
[@mifos.header "title" /]
  [@mifos.topNavigationNoSecurity currentTab="Admin" /]
  <!--  Left Sidebar Begins-->
  <div class="sidebar ht1350">
  [#include "adminLeftPane.ftl" ]
  </div> 
   <!--  Left Sidebar Ends-->
 
   <!--  Main Content Begins-->  
  <div class=" content leftMargin180">
  <span id="page.id" title="definelabels" />
  
  [@mifos.crumbs breadcrumbs/]
  
  	<form method="post" action="defineLabels.ftl" name="formname">  	
    <div class="span-16">  		
        <div class="clear">&nbsp;</div>
        <div class="fontBold"><span class="orangeheading"> Define Labels</span></div>
        [@spring.bind "formBean" /]
  		[@spring.showErrors "<br>" /]
  		
		<p class="fontBold">Office Hierarchy </p>
        <div class="span-16 last">
        	<div class="span-16">
        			<span class="span-4 rightAlign"><label for="headoffice">Head office:</label></span>
    				<span class="span-3">
    				    [@spring.bind "formBean.headOffice" /]
						<input type="text" id="headoffice" name="${spring.status.expression}" value="${spring.status.value?default("")}">
    				</span>
  			</div>
  			
			<div class="span-16">
        			<span class="span-4 rightAlign"><label for="regionaloffice">Regional office:</label></span>
    				<span class="span-3">
    				    [@spring.bind "formBean.regionalOffice" /]
						<input type="text" id="regionaloffice" name="${spring.status.expression}" value="${spring.status.value?default("")}">
    				</span>
  			</div>
  			
			<div class="span-16">
        			<span class="span-4 rightAlign"><label for="subregionaloffice">Sub regional office:</label></span>
    				<span class="span-3">
    				    [@spring.bind "formBean.subRegionalOffice" /]
						<input type="text" id="subregionaloffice" name="${spring.status.expression}" value="${spring.status.value?default("")}">
    				</span>
  			</div>

			<div class="span-16">
        			<span class="span-4 rightAlign"><label for="areaoffice">Area office:</label></span>
    				<span class="span-3">
    				    [@spring.bind "formBean.areaOffice" /]
						<input type="text" id="areaoffice" name="${spring.status.expression}" value="${spring.status.value?default("")}">
    				</span>
  			</div>  			

			<div class="span-16">
        			<span class="span-4 rightAlign"><label for="branchoffice">Branch office:</label></span>
    				<span class="span-3">
    				    [@spring.bind "formBean.branchOffice" /]
						<input type="text" id="branchoffice" name="${spring.status.expression}" value="${spring.status.value?default("")}">
    				</span>
  			</div>  
        </div>
        
        <p class="fontBold">Clients </p>
        <div class="span-16 last">
        	<div class="span-16">
        			<span class="span-4 rightAlign"><label for="client">Client:</label></span>
    				<span class="span-3">
    				    [@spring.bind "formBean.client" /]
						<input type="text" id="client" name="${spring.status.expression}" value="${spring.status.value?default("")}">
    				</span>
  			</div>
  			
			<div class="span-16">
        			<span class="span-4 rightAlign"><label for="group">Group:</label></span>
    				<span class="span-3">
    				    [@spring.bind "formBean.group" /]
						<input type="text" id="group" name="${spring.status.expression}" value="${spring.status.value?default("")}">
    				</span>
  			</div>
        
        	<div class="span-16">
        			<span class="span-4 rightAlign"><label for="center">Center:</label></span>
    				<span class="span-3">
    				    [@spring.bind "formBean.center" /]
						<input type="text" id="center" name="${spring.status.expression}" value="${spring.status.value?default("")}">
    				</span>
  			</div>
        </div>
        
        <p class="fontBold">Product Types </p>
        
        <div class="span-16 last">
        
        	<div class="span-16">
        			<span class="span-4 rightAlign"><label for="loans">Loans:</label></span>
    				<span class="span-3">
    				    [@spring.bind "formBean.loans" /]
						<input type="text" id="loans" name="${spring.status.expression}" value="${spring.status.value?default("")}">
    				</span>
  			</div>
        
            <div class="span-16">
        			<span class="span-4 rightAlign"><label for="savings">Savings:</label></span>
    				<span class="span-3">
    				    [@spring.bind "formBean.savings" /]
						<input type="text" id="savings" name="${spring.status.expression}" value="${spring.status.value?default("")}">
    				</span>
  			</div>
        </div>
        
        <p class="fontBold">Personal Information </p>
        
        <div class="span-16 last">
            <div class="span-16">
        			<span class="span-4 rightAlign"><label for="state">State:</label></span>
    				<span class="span-3">
    				    [@spring.bind "formBean.state" /]
						<input type="text" id="state" name="${spring.status.expression}" value="${spring.status.value?default("")}">
    				</span>
  			</div>
  			
			<div class="span-16">
        			<span class="span-4 rightAlign"><label for="postalcode">Postal code:</label></span>
    				<span class="span-3">
    				    [@spring.bind "formBean.postalCode" /]
						<input type="text" id="postalcode" name="${spring.status.expression}" value="${spring.status.value?default("")}">
    				</span>
  			</div>
  			
  			<div class="span-16">
        			<span class="span-4 rightAlign"><label for="ethnicity">Ethnicity:</label></span>
    				<span class="span-3">
    				    [@spring.bind "formBean.ethnicity" /]
						<input type="text" id="ethnicity" name="${spring.status.expression}" value="${spring.status.value?default("")}">
    				</span>
  			</div>
  			
  			<div class="span-16">
        			<span class="span-4 rightAlign"><label for="definelabels.input.citizenship">Citizenship:</label></span>
    				<span class="span-3">
    				    [@spring.bind "formBean.citizenship" /]
						<input type="text" id="definelabels.input.citizenship" name="${spring.status.expression}" value="${spring.status.value?default("")}">
    				</span>
  			</div>
  			
  			<div class="span-16">
        			<span class="span-4 rightAlign"><label for="handicapped">Handicapped:</label></span>
    				<span class="span-3">
    				    [@spring.bind "formBean.handicapped" /]
						<input type="text" id="handicapped" name="${spring.status.expression}" value="${spring.status.value?default("")}">
    				</span>
  			</div>
  			
			<div class="span-16">
        			<span class="span-4 rightAlign"><label for="definelabels.input.govtId">Government ID:</label></span>
    				<span class="span-3">
    				    [@spring.bind "formBean.govtId" /]
						<input type="text" id="definelabels.input.govtId" name="${spring.status.expression}" value="${spring.status.value?default("")}">
    				</span>
  			</div>
        </div>
        
        <p class="fontBold">Address  </p>
        <div class="span-16 last">
        	<div class="span-16">
        			<span class="span-4 rightAlign"><label for="address1">Address 1:</label></span>
    				<span class="span-3">
    				    [@spring.bind "formBean.address1" /]
						<input type="text" id="address1" name="${spring.status.expression}" value="${spring.status.value?default("")}">
    				</span>
  			</div>

        	<div class="span-16">
        			<span class="span-4 rightAlign"><label for="address2">Address 2:</label></span>
    				<span class="span-3">
    				    [@spring.bind "formBean.address2" /]
						<input type="text" id="address2" name="${spring.status.expression}" value="${spring.status.value?default("")}">
    				</span>
  			</div>
  			
  			<div class="span-16">
        			<span class="span-4 rightAlign"><label for="address3">Address 3:</label></span>
    				<span class="span-3">
    				    [@spring.bind "formBean.address3" /]
						<input type="text" id="address3" name="${spring.status.expression}" value="${spring.status.value?default("")}">
    				</span>
  			</div>        
        
        </div>
        
        <p class="fontBold">Statuses </p>
        <div class="span-16 last">
        
        	<div class="span-16">
        			<span class="span-4 rightAlign"><label for="partialapplication">Partial Application:</label></span>
    				<span class="span-3">
    				    [@spring.bind "formBean.partialApplication" /]
						<input type="text" id="partialapplication" name="${spring.status.expression}" value="${spring.status.value?default("")}">
    				</span>
  			</div>
  			
			<div class="span-16">
        			<span class="span-4 rightAlign"><label for="pendingapproval">Pending Approval:</label></span>
    				<span class="span-3">
    				    [@spring.bind "formBean.pendingApproval" /]
						<input type="text" id="pendingapproval" name="${spring.status.expression}" value="${spring.status.value?default("")}">
    				</span>
  			</div>
  			
			<div class="span-16">
        			<span class="span-4 rightAlign"><label for="approved">Approved:</label></span>
    				<span class="span-3">
    				    [@spring.bind "formBean.approved" /]
						<input type="text" id="approved" name="${spring.status.expression}" value="${spring.status.value?default("")}">
    				</span>
  			</div>

			<div class="span-16">
        			<span class="span-4 rightAlign"><label for="cancel">Cancel:</label></span>
    				<span class="span-3">
    				    [@spring.bind "formBean.cancel" /]
						<input type="text" id="cancel" name="${spring.status.expression}" value="${spring.status.value?default("")}">
    				</span>
  			</div>
 
 			<div class="span-16">
        			<span class="span-4 rightAlign"><label for="closed">Closed:</label></span>
    				<span class="span-3">
    				    [@spring.bind "formBean.closed" /]
						<input type="text" id="closed" name="${spring.status.expression}" value="${spring.status.value?default("")}">
    				</span>
  			</div>

 			<div class="span-16">
        			<span class="span-4 rightAlign"><label for="onhold">On hold:</label></span>
    				<span class="span-3">
    				    [@spring.bind "formBean.onhold" /]
						<input type="text" id="onhold" name="${spring.status.expression}" value="${spring.status.value?default("")}">
    				</span>
  			</div>
  			
 			<div class="span-16">
        			<span class="span-4 rightAlign"><label for="active">Active:</label></span>
    				<span class="span-3">
    				    [@spring.bind "formBean.active" /]
						<input type="text" id="active" name="${spring.status.expression}" value="${spring.status.value?default("")}">
    				</span>
  			</div>

 			<div class="span-16">
        			<span class="span-4 rightAlign"><label for="inactive">Inactive:</label></span>
    				<span class="span-3">
    				    [@spring.bind "formBean.inActive" /]
						<input type="text" id="inactive" name="${spring.status.expression}" value="${spring.status.value?default("")}">
    				</span>
  			</div>
  			
 			<div class="span-16">
        			<span class="span-4 rightAlign"><label for="activeInGoodStanding">Active in good standing:</label></span>
    				<span class="span-3">
    				    [@spring.bind "formBean.activeInGoodStanding" /]
						<input type="text" id="activeInGoodStanding" name="${spring.status.expression}" value="${spring.status.value?default("")}">
    				</span>
  			</div>

 			<div class="span-16">
        			<span class="span-4 rightAlign"><label for="activeInBadStanding">Active in bad standing:</label></span>
    				<span class="span-3">
    				    [@spring.bind "formBean.activeInBadStanding" /]
						<input type="text" id="activeInBadStanding" name="${spring.status.expression}" value="${spring.status.value?default("")}">
    				</span>
  			</div>

 			<div class="span-16">
        			<span class="span-4 rightAlign"><label for="closedObligationMet">Closed-obligation met:</label></span>
    				<span class="span-3">
    				    [@spring.bind "formBean.closedObligationMet" /]
						<input type="text" id="closedObligationMet" name="${spring.status.expression}" value="${spring.status.value?default("")}">
    				</span>
  			</div>
  			
 			<div class="span-16">
        			<span class="span-4 rightAlign"><label for="closedRescheduled">Closed-rescheduled:</label></span>
    				<span class="span-3">
    				    [@spring.bind "formBean.closedRescheduled" /]
						<input type="text" id="closedRescheduled" name="${spring.status.expression}" value="${spring.status.value?default("")}">
    				</span>
  			</div>
  			
 			<div class="span-16">
        			<span class="span-4 rightAlign"><label for="closedWrittenOff">Closed-written off:</label></span>
    				<span class="span-3">
    				    [@spring.bind "formBean.closedWrittenOff" /]
						<input type="text" id="closedWrittenOff" name="${spring.status.expression}" value="${spring.status.value?default("")}">
    				</span>
  			</div>
        </div>
        <p class="fontBold">Grace types </p>
        <div class="span-16 last">
        	<div class="span-16">
        			<span class="span-4 rightAlign"><label for="gracenone">None:</label></span>
    				<span class="span-3">
    				    [@spring.bind "formBean.none" /]
						<input type="text" id="gracenone" name="${spring.status.expression}" value="${spring.status.value?default("")}">
    				</span>
  			</div>
        	
        	<div class="span-16">
        			<span class="span-4 rightAlign"><label for="graceallrepayments">Grace on all repayments:</label></span>
    				<span class="span-3">
    				    [@spring.bind "formBean.graceOnAllRepayments" /]
						<input type="text" id="graceallrepayments" name="${spring.status.expression}" value="${spring.status.value?default("")}">
    				</span>
  			</div>
        	
        	<div class="span-16">
        			<span class="span-4 rightAlign"><label for="graceprincipalonly">Principal only grace:</label></span>
    				<span class="span-3">
    				    [@spring.bind "formBean.principalOnlyGrace" /]
						<input type="text" id="graceprincipalonly" name="${spring.status.expression}" value="${spring.status.value?default("")}">
    				</span>
  			</div>
        </div>
        
        <p class="fontBold">Miscellaneous </p>
        
        <div class="span-16 last">
        	<div class="span-16">
        			<span class="span-4 rightAlign"><label for="interest">Interest:</label></span>
    				<span class="span-3">
    				    [@spring.bind "formBean.interest" /]
						<input type="text" id="interest" name="${spring.status.expression}" value="${spring.status.value?default("")}">
    				</span>
  			</div>
        
            <div class="span-16">
        			<span class="span-4 rightAlign"><label for="externalId">External ID:</label></span>
    				<span class="span-3">
    				    [@spring.bind "formBean.externalId" /]
						<input type="text" id="externalId" name="${spring.status.expression}" value="${spring.status.value?default("")}">
    				</span>
  			</div>

            <div class="span-16">
        			<span class="span-4 rightAlign"><label for="bulkEntry">Bulk entry:</label></span>
    				<span class="span-3">
    				    [@spring.bind "formBean.bulkEntry" /]
						<input type="text" id="bulkEntry" name="${spring.status.expression}" value="${spring.status.value?default("")}">
    				</span>
  			</div>
        </div>
        
        <div class="clear">&nbsp;</div>
        <hr />
        <div class="span-20 last">
        
        
        <div class="prepend-7">
             <input class="buttn"  type="submit" id="definelabels.button.submit" name="submit" value="[@spring.message "submit"/]"/>
            <input class="buttn2" type="submit" id="CANCEL" name="CANCEL" value="[@spring.message "cancel"/]"/>
        </div>
        
        </div>
	</div>
   	</form> 
  </div>
  <!--Main content ends-->
  [@mifos.footer /]