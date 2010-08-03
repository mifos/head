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
  <div class="bluedivs paddingLeft"><a href="admin.ftl">[@spring.message "admin"/]</a>&nbsp;/&nbsp;<span class="fontBold">[@spring.message "datadisplayandrules.defineLabels"/]</span></div>
  	<form method="" action="" name="formname">  	
    <div class="span-16">  		
        <div class="clear">&nbsp;</div>
        <div class="fontBold"><span class="orangeheading">[@spring.message "datadisplayandrules.defineLabels"/]</span></div>
        <p class="error"></p>
		<p class="fontBold">[@spring.message "datadisplayandrules.defineLabels.officeHierarchy"/] </p>
        <div class="span-16 last">
        	<div class="span-16"><span class="span-4 rightAlign">[@spring.message "datadisplayandrules.defineLabels.headoffice"/]</span>
    				<span class="span-3"><input type="text" value="Head office" /></span>
  			</div>
            <div class="span-16"><span class="span-4 rightAlign">[@spring.message "datadisplayandrules.defineLabels.regionaloffice"/]</span>
    				<span class="span-3"><input type="text" value="Regional office" /></span>
  			</div>
            <div class="span-16"><span class="span-4 rightAlign">[@spring.message "datadisplayandrules.defineLabels.subregionaloffice"/]</span>
    				<span class="span-3"><input type="text" value="Sub regional office" /></span>
  			</div>
            <div class="span-16"><span class="span-4 rightAlign">[@spring.message "datadisplayandrules.defineLabels.areaoffice"/]</span>
    				<span class="span-3"><input type="text" value="Area office" /></span>
  			</div>
            <div class="span-16"><span class="span-4 rightAlign">[@spring.message "datadisplayandrules.defineLabels.branchoffice"/]</span>
    				<span class="span-3"><input type="text" value="Branch office" /></span>
  			</div>
        </div>
        <p class="fontBold">[@spring.message "datadisplayandrules.defineLabels.clients"/]</p>
        <div class="span-16 last">
        	<div class="span-16"><span class="span-4 rightAlign">[@spring.message "datadisplayandrules.defineLabels.client"/]</span>
    				<span class="span-3"><input type="text" value="Client" /></span>
  			</div>
            <div class="span-16"><span class="span-4 rightAlign">[@spring.message "datadisplayandrules.defineLabels.group"/]</span>
    				<span class="span-3"><input type="text" value="Group" /></span>
  			</div>
            <div class="span-16"><span class="span-4 rightAlign">[@spring.message "datadisplayandrules.defineLabels.center"/]</span>
    				<span class="span-3"><input type="text" value="Center" /></span>
  			</div>
        </div>
        <p class="fontBold">[@spring.message "datadisplayandrules.defineLabels.productTypes"/]</p>
        <div class="span-16 last">
        	<div class="span-16"><span class="span-4 rightAlign">[@spring.message "datadisplayandrules.defineLabels.loans"/]</span>
    				<span class="span-3"><input type="text" value="Loans" /></span>
  			</div>
            <div class="span-16"><span class="span-4 rightAlign">[@spring.message "datadisplayandrules.defineLabels.savings"/]</span>
    				<span class="span-3"><input type="text" value="Savings" /></span>
  			</div>
        </div>
        <p class="fontBold">[@spring.message "datadisplayandrules.defineLabels.personalInformation"/] </p>
        <div class="span-16 last">
        	<div class="span-16"><span class="span-4 rightAlign">[@spring.message "datadisplayandrules.defineLabels.state"/]</span>
    				<span class="span-3"><input type="text" value="State" /></span>
  			</div>
            <div class="span-16"><span class="span-4 rightAlign">[@spring.message "datadisplayandrules.defineLabels.postalcode"/]</span>
    				<span class="span-3"><input type="text" value="Postal code" /></span>
  			</div>
            <div class="span-16"><span class="span-4 rightAlign">[@spring.message "datadisplayandrules.defineLabels.ethnicity"/]</span>
    				<span class="span-3"><input type="text" value="Ethnicity" /></span>
  			</div>
            <div class="span-16"><span class="span-4 rightAlign">[@spring.message "datadisplayandrules.defineLabels.citizenship"/]</span>
    				<span class="span-3"><input type="text" value="Citizenship" /></span>
  			</div>
            <div class="span-16"><span class="span-4 rightAlign">[@spring.message "datadisplayandrules.defineLabels.handicapped"/]</span>
    				<span class="span-3"><input type="text" value="Handicapped" /></span>
  			</div>
            <div class="span-16"><span class="span-4 rightAlign">[@spring.message "datadisplayandrules.defineLabels.governmentID"/]</span>
    				<span class="span-3"><input type="text" value="Government ID" /></span>
  			</div>
        </div>
        <p class="fontBold">[@spring.message "datadisplayandrules.defineLabels.address"/]  </p>
        <div class="span-16 last">
        	<div class="span-16"><span class="span-4 rightAlign">[@spring.message "datadisplayandrules.defineLabels.address1"/]</span>
    				<span class="span-3"><input type="text" value="Address1" /></span>
  			</div>
            <div class="span-16"><span class="span-4 rightAlign">[@spring.message "datadisplayandrules.defineLabels.address2"/]</span>
    				<span class="span-3"><input type="text" value="Address2" /></span>
  			</div>
            <div class="span-16"><span class="span-4 rightAlign">[@spring.message "datadisplayandrules.defineLabels.address3"/]</span>
    				<span class="span-3"><input type="text" value="Address3" /></span>
  			</div>
        </div>
        <p class="fontBold">[@spring.message "datadisplayandrules.defineLabels.statuses"/]</p>
        <div class="span-16 last">
        	<div class="span-16"><span class="span-4 rightAlign">[@spring.message "datadisplayandrules.defineLabels.partialApplication"/]</span>
    				<span class="span-3"><input type="text" value="Partial Application" /></span>
  			</div>
            <div class="span-16"><span class="span-4 rightAlign">[@spring.message "datadisplayandrules.defineLabels.pendingApproval"/]</span>
    				<span class="span-3"><input type="text" value="Application Pending Approval" /></span>
  			</div>
            <div class="span-16"><span class="span-4 rightAlign">[@spring.message "datadisplayandrules.defineLabels.approved"/]</span>
    				<span class="span-3"><input type="text" value="Application Approved" /></span>
  			</div>
            <div class="span-16"><span class="span-4 rightAlign">[@spring.message "datadisplayandrules.defineLabels.cancel"/]</span>
    				<span class="span-3"><input type="text" value="Cancel" /></span>
  			</div>
            <div class="span-16"><span class="span-4 rightAlign">[@spring.message "datadisplayandrules.defineLabels.closed"/]</span>
    				<span class="span-3"><input type="text" value="Closed" /></span>
  			</div>
            <div class="span-16"><span class="span-4 rightAlign">[@spring.message "datadisplayandrules.defineLabels.onhold"/]</span>
    				<span class="span-3"><input type="text" value="On Hold" /></span>
  			</div>
            <div class="span-16"><span class="span-4 rightAlign">[@spring.message "datadisplayandrules.defineLabels.active"/]</span>
    				<span class="span-3"><input type="text" value="Active" /></span>
  			</div>
            <div class="span-16"><span class="span-4 rightAlign">[@spring.message "datadisplayandrules.defineLabels.inactive"/]</span>
    				<span class="span-3"><input type="text" value="Inactive" /></span>
  			</div>
            <div class="span-16"><span class="span-4 rightAlign">[@spring.message "datadisplayandrules.defineLabels.activeingoodstanding"/]</span>
    				<span class="span-3"><input type="text" value="Active in Good Standing" /></span>
  			</div>
            <div class="span-16"><span class="span-4 rightAlign">[@spring.message "datadisplayandrules.defineLabels.activeinbadstanding"/]</span>
    				<span class="span-3"><input type="text" value="Active in Bad Standing" /></span>
  			</div>
            <div class="span-16"><span class="span-4 rightAlign">[@spring.message "datadisplayandrules.defineLabels.closed-obligationmet"/]</span>
    				<span class="span-3"><input type="text" value="Closed- Obligation met" /></span>
  			</div>
            <div class="span-16"><span class="span-4 rightAlign">[@spring.message "datadisplayandrules.defineLabels.closed-rescheduled"/]</span>
  			</div>
            <div class="span-16"><span class="span-4 rightAlign">[@spring.message "datadisplayandrules.defineLabels.closed-writtenoff"/]</span>
    				<span class="span-3"><input type="text" value="Closed- Written Off" /></span>
  			</div>
        </div>
        <p class="fontBold">[@spring.message "datadisplayandrules.defineLabels.gracetypes"/]</p>
        <div class="span-16 last">
        	<div class="span-16"><span class="span-4 rightAlign">[@spring.message "datadisplayandrules.defineLabels.none"/]</span>
    				<span class="span-3"><input type="text" value="None" /></span>
  			</div>
            <div class="span-16"><span class="span-4 rightAlign">[@spring.message "datadisplayandrules.defineLabels.graceonallrepayments"/]</span>
    				<span class="span-3"><input type="text" value="Grace on all repayments" /></span>
  			</div>
            <div class="span-16"><span class="span-4 rightAlign">[@spring.message "datadisplayandrules.defineLabels.principalonlygrace"/]</span>
    				<span class="span-3"><input type="text" value="Principal only grace" /></span>
  			</div>
        </div>
        <p class="fontBold">[@spring.message "datadisplayandrules.defineLabels.miscellaneous"/] </p>
        <div class="span-16 last">
        	<div class="span-16"><span class="span-4 rightAlign">[@spring.message "datadisplayandrules.defineLabels.interest"/]</span>
    				<span class="span-3"><input type="text" value="Interest" /></span>
  			</div>
            <div class="span-16"><span class="span-4 rightAlign">[@spring.message "datadisplayandrules.defineLabels.externalID"/]</span>
    				<span class="span-3"><input type="text" value="External Id" /></span>
  			</div>
            <div class="span-16"><span class="span-4 rightAlign">[@spring.message "datadisplayandrules.defineLabels.bulkentry"/]</span>
    				<span class="span-3"><input type="text" value="Bulk entry" /></span>
  			</div>
        </div>
        <div class="clear">&nbsp;</div>
        <hr />
        <div class="span-20 last">
        <div class="prepend-7">
          <input class="buttn" type="button" name="submit" value="Submit" onclick="window.location='admin.ftl'"/>
          <input class="buttn2" type="button" name="cancel" value="Cancel" onclick="window.location='admin.ftl'"/>
        </div>
        </div>
	</div>
   	</form> 
  </div>
  <!--Main content ends-->
  [@mifos.footer /]