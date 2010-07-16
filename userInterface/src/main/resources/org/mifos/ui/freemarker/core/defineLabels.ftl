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
  
  [@mifos.crumbs breadcrumbs/]
  
  	<form method="" action="" name="formname">  	
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
  			
  			
            <div class="span-16"><span class="span-4 rightAlign">Sub regional office&nbsp;:</span>
    				<span class="span-3"><input type="text" value="Sub regional office" /></span>
  			</div>
            <div class="span-16"><span class="span-4 rightAlign">Area office&nbsp;:</span>
    				<span class="span-3"><input type="text" value="Area office" /></span>
  			</div>
            <div class="span-16"><span class="span-4 rightAlign">Branch office&nbsp;:</span>
    				<span class="span-3"><input type="text" value="Branch office" /></span>
  			</div>
        </div>
        <p class="fontBold">Clients </p>
        <div class="span-16 last">
        	<div class="span-16"><span class="span-4 rightAlign">Client&nbsp;:</span>
    				<span class="span-3"><input type="text" value="Client" /></span>
  			</div>
            <div class="span-16"><span class="span-4 rightAlign">Group&nbsp;:</span>
    				<span class="span-3"><input type="text" value="Group" /></span>
  			</div>
            <div class="span-16"><span class="span-4 rightAlign">Center&nbsp;:</span>
    				<span class="span-3"><input type="text" value="Center" /></span>
  			</div>
        </div>
        <p class="fontBold">Product Types </p>
        <div class="span-16 last">
        	<div class="span-16"><span class="span-4 rightAlign">Loans&nbsp;:</span>
    				<span class="span-3"><input type="text" value="Loans" /></span>
  			</div>
            <div class="span-16"><span class="span-4 rightAlign">Savings&nbsp;:</span>
    				<span class="span-3"><input type="text" value="Savings" /></span>
  			</div>
        </div>
        <p class="fontBold">Personal Information </p>
        <div class="span-16 last">
        	<div class="span-16"><span class="span-4 rightAlign">State&nbsp;:</span>
    				<span class="span-3"><input type="text" value="State" /></span>
  			</div>
            <div class="span-16"><span class="span-4 rightAlign">Postal code&nbsp;:</span>
    				<span class="span-3"><input type="text" value="Postal code" /></span>
  			</div>
            <div class="span-16"><span class="span-4 rightAlign">Ethnicity&nbsp;:</span>
    				<span class="span-3"><input type="text" value="Ethnicity" /></span>
  			</div>
            <div class="span-16"><span class="span-4 rightAlign">Citizenship&nbsp;:</span>
    				<span class="span-3"><input type="text" value="Citizenship" /></span>
  			</div>
            <div class="span-16"><span class="span-4 rightAlign">Handicapped&nbsp;:</span>
    				<span class="span-3"><input type="text" value="Handicapped" /></span>
  			</div>
            <div class="span-16"><span class="span-4 rightAlign">Government ID&nbsp;:</span>
    				<span class="span-3"><input type="text" value="Government ID" /></span>
  			</div>
        </div>
        <p class="fontBold">Address  </p>
        <div class="span-16 last">
        	<div class="span-16"><span class="span-4 rightAlign">Address 1&nbsp;:</span>
    				<span class="span-3"><input type="text" value="Address1" /></span>
  			</div>
            <div class="span-16"><span class="span-4 rightAlign">Address 2&nbsp;:</span>
    				<span class="span-3"><input type="text" value="Address2" /></span>
  			</div>
            <div class="span-16"><span class="span-4 rightAlign">Address 3&nbsp;:</span>
    				<span class="span-3"><input type="text" value="Address3" /></span>
  			</div>
        </div>
        <p class="fontBold">Statuses </p>
        <div class="span-16 last">
        	<div class="span-16"><span class="span-4 rightAlign">Partial Application&nbsp;:</span>
    				<span class="span-3"><input type="text" value="Partial Application" /></span>
  			</div>
            <div class="span-16"><span class="span-4 rightAlign">Pending Approval&nbsp;:</span>
    				<span class="span-3"><input type="text" value="Application Pending Approval" /></span>
  			</div>
            <div class="span-16"><span class="span-4 rightAlign">Approved&nbsp;:</span>
    				<span class="span-3"><input type="text" value="Application Approved" /></span>
  			</div>
            <div class="span-16"><span class="span-4 rightAlign">Cancel&nbsp;:</span>
    				<span class="span-3"><input type="text" value="Cancel" /></span>
  			</div>
            <div class="span-16"><span class="span-4 rightAlign">Closed&nbsp;:</span>
    				<span class="span-3"><input type="text" value="Closed" /></span>
  			</div>
            <div class="span-16"><span class="span-4 rightAlign">On hold&nbsp;:</span>
    				<span class="span-3"><input type="text" value="On Hold" /></span>
  			</div>
            <div class="span-16"><span class="span-4 rightAlign">Active&nbsp;:</span>
    				<span class="span-3"><input type="text" value="Active" /></span>
  			</div>
            <div class="span-16"><span class="span-4 rightAlign">Inactive&nbsp;:</span>
    				<span class="span-3"><input type="text" value="Inactive" /></span>
  			</div>
            <div class="span-16"><span class="span-4 rightAlign">Active in good standing&nbsp;:</span>
    				<span class="span-3"><input type="text" value="Active in Good Standing" /></span>
  			</div>
            <div class="span-16"><span class="span-4 rightAlign">Active in bad standing&nbsp;:</span>
    				<span class="span-3"><input type="text" value="Active in Bad Standing" /></span>
  			</div>
            <div class="span-16"><span class="span-4 rightAlign">Closed-obligation met&nbsp;:</span>
    				<span class="span-3"><input type="text" value="Closed- Obligation met" /></span>
  			</div>
            <div class="span-16"><span class="span-4 rightAlign">Closed-rescheduled&nbsp;:</span>
    				<span class="span-3"><input type="text" value="Closed- Rescheduled" /></span>
  			</div>
            <div class="span-16"><span class="span-4 rightAlign">Closed-written off&nbsp;:</span>
    				<span class="span-3"><input type="text" value="Closed- Written Off" /></span>
  			</div>
        </div>
        <p class="fontBold">Grace types </p>
        <div class="span-16 last">
        	<div class="span-16"><span class="span-4 rightAlign">None&nbsp;:</span>
    				<span class="span-3"><input type="text" value="None" /></span>
  			</div>
            <div class="span-16"><span class="span-4 rightAlign">Grace on all repayments&nbsp;:</span>
    				<span class="span-3"><input type="text" value="Grace on all repayments" /></span>
  			</div>
            <div class="span-16"><span class="span-4 rightAlign">Principal only grace&nbsp;:</span>
    				<span class="span-3"><input type="text" value="Principal only grace" /></span>
  			</div>
        </div>
        <p class="fontBold">Miscellaneous </p>
        <div class="span-16 last">
        	<div class="span-16"><span class="span-4 rightAlign">Interest&nbsp;:</span>
    				<span class="span-3"><input type="text" value="Interest" /></span>
  			</div>
            <div class="span-16"><span class="span-4 rightAlign">External ID&nbsp;:</span>
    				<span class="span-3"><input type="text" value="External Id" /></span>
  			</div>
            <div class="span-16"><span class="span-4 rightAlign">Bulk entry&nbsp;:</span>
    				<span class="span-3"><input type="text" value="Bulk entry" /></span>
  			</div>
        </div>
        <div class="clear">&nbsp;</div>
        <hr />
        <div class="span-20 last">
        
        
        <div class="prepend-7">
             <input class="buttn"  type="submit" name="submit" value="[@spring.message "submit"/]"/>
            <input class="buttn2" type="submit" id="CANCEL" name="CANCEL" value="[@spring.message "cancel"/]"/>
        </div>
        
        </div>
	</div>
   	</form> 
  </div>
  <!--Main content ends-->
  [@mifos.footer /]