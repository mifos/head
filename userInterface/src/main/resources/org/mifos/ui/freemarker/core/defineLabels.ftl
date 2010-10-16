[#ftl]
[#--
* Copyright (c) 2005-2010 Grameen Foundation USA
*  All rights reserved.
*
*  Licensed under the Apache License, Version 2.0 (the "License");
*  you may not use this file except in compliance with the License.
*  You may obtain a copy of the License at
*
*      http://www.apache.org/licenses/LICENSE-2.0
*
*  Unless required by applicable law or agreed to in writing, software
*  distributed under the License is distributed on an "AS IS" BASIS,
*  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
*  implied. See the License for the specific language governing
*  permissions and limitations under the License.
*
*  See also http://www.apache.org/licenses/LICENSE-2.0.html for an
*  explanation of the license and how it is applied.
--]
[#include "layout.ftl"]
[@adminLeftPaneLayout]
   <!--  Main Content Begins-->  
  <div class=" content">
  <span id="page.id" title="definelabels" />
  
    [@mifos.crumbs breadcrumbs /]
  
  	<form method="post" action="defineLabels.ftl" name="formname">  	
    <div class="margin15lefttop">
        <div class="fontBold"><span class="orangeheading">[@spring.message "datadisplayandrules.defineLabels"/]</span></div>
        <div class="allErrorsDiv">
	        [@mifosmacros.showAllErrors "formBean.*"/]
        </div>
  		
		<p class="fontBold margin10topbottom">[@spring.message "datadisplayandrules.defineLabels.officeHierarchy"/] </p>
        
        <div class="span-16 last width80prc marginLeft20prc ">
        	<div class="span-16 width80prc ">
        			<span class="span-4 rightAlign">[@spring.message "datadisplayandrules.defineLabels.headoffice"/]</span>
    				<span class="span-3">
    				    [@spring.bind "formBean.headOffice" /]
						<input type="text" id="headoffice" name="${spring.status.expression}" value="${spring.status.value?default("")}">
    				</span>
  			</div>
  			
			<div class="span-16 width80prc ">
					<span class="span-4 rightAlign">[@spring.message "datadisplayandrules.defineLabels.regionaloffice"/]</span>
    				<span class="span-3">
    				    [@spring.bind "formBean.regionalOffice" /]
						<input type="text" id="regionaloffice" name="${spring.status.expression}" value="${spring.status.value?default("")}">
    				</span>
  			</div>
  			
			<div class="span-16 width80prc ">
					<span class="span-4 rightAlign">[@spring.message "datadisplayandrules.defineLabels.subregionaloffice"/]</span>
    				<span class="span-3">
    				    [@spring.bind "formBean.subRegionalOffice" /]
						<input type="text" id="subregionaloffice" name="${spring.status.expression}" value="${spring.status.value?default("")}">
    				</span>
  			</div>

			<div class="span-16 width80prc ">
					<span class="span-4 rightAlign">[@spring.message "datadisplayandrules.defineLabels.areaoffice"/]</span>
    				<span class="span-3">
    				    [@spring.bind "formBean.areaOffice" /]
						<input type="text" id="areaoffice" name="${spring.status.expression}" value="${spring.status.value?default("")}">
    				</span>
  			</div>  			

			<div class="span-16 width80prc ">
					<span class="span-4 rightAlign">[@spring.message "datadisplayandrules.defineLabels.branchoffice"/]</span>
    				<span class="span-3">
    				    [@spring.bind "formBean.branchOffice" /]
						<input type="text" id="branchoffice" name="${spring.status.expression}" value="${spring.status.value?default("")}">
    				</span>
  			</div>  
        </div>
        
        <p class="fontBold margin10topbottom">[@spring.message "datadisplayandrules.defineLabels.clients"/]</p>
        <div class="span-16 last width80prc marginLeft20prc ">
        	<div class="span-16 width80prc ">
        			<span class="span-4 rightAlign">[@spring.message "datadisplayandrules.defineLabels.client"/]</span>
    				<span class="span-3">
    				    [@spring.bind "formBean.client" /]
						<input type="text" id="client" name="${spring.status.expression}" value="${spring.status.value?default("")}">
    				</span>
  			</div>
  			
			<div class="span-16 width80prc ">
				    <span class="span-4 rightAlign">[@spring.message "datadisplayandrules.defineLabels.group"/]</span>
    				<span class="span-3">
    				    [@spring.bind "formBean.group" /]
						<input type="text" id="group" name="${spring.status.expression}" value="${spring.status.value?default("")}">
    				</span>
  			</div>
        
        	<div class="span-16 width80prc ">
        			<span class="span-4 rightAlign">[@spring.message "datadisplayandrules.defineLabels.center"/]</span>
    				<span class="span-3">
    				    [@spring.bind "formBean.center" /]
						<input type="text" id="center" name="${spring.status.expression}" value="${spring.status.value?default("")}">
    				</span>
  			</div>
        </div>
        
        <p class="fontBold margin10topbottom">[@spring.message "datadisplayandrules.defineLabels.productTypes"/]</p>
        
        <div class="span-16 last width80prc marginLeft20prc ">
        
        	<div class="span-16 width80prc ">
   					<span class="span-4 rightAlign">[@spring.message "datadisplayandrules.defineLabels.loans"/]</span>     			
    				<span class="span-3">
    				    [@spring.bind "formBean.loans" /]
						<input type="text" id="loans" name="${spring.status.expression}" value="${spring.status.value?default("")}">
    				</span>
  			</div>
        
            <div class="span-16 width80prc ">
            		<span class="span-4 rightAlign">[@spring.message "datadisplayandrules.defineLabels.savings"/]</span>
    				<span class="span-3">
    				    [@spring.bind "formBean.savings" /]
						<input type="text" id="savings" name="${spring.status.expression}" value="${spring.status.value?default("")}">
    				</span>
  			</div>
        </div>
        
        <p class="fontBold margin10topbottom">[@spring.message "datadisplayandrules.defineLabels.personalInformation"/] </p>
        <div class="span-16 last width80prc marginLeft20prc ">
  			<div class="span-16 width80prc ">
        			<span class="span-4 rightAlign">[@spring.message "datadisplayandrules.defineLabels.ethnicity"/]</span>
    				<span class="span-3">
    				    [@spring.bind "formBean.ethnicity" /]
						<input type="text" id="ethnicity" name="${spring.status.expression}" value="${spring.status.value?default("")}">
    				</span>
  			</div>
  			
  			<div class="span-16 width80prc ">
        			<span class="span-4 rightAlign">[@spring.message "datadisplayandrules.defineLabels.citizenship"/]</span>
    				<span class="span-3">
    				    [@spring.bind "formBean.citizenship" /]
						<input type="text" id="definelabels.input.citizenship" name="${spring.status.expression}" value="${spring.status.value?default("")}">
    				</span>
  			</div>
  			
  			<div class="span-16 width80prc ">
        			<span class="span-4 rightAlign">[@spring.message "datadisplayandrules.defineLabels.handicapped"/]</span>
    				<span class="span-3">
    				    [@spring.bind "formBean.handicapped" /]
						<input type="text" id="handicapped" name="${spring.status.expression}" value="${spring.status.value?default("")}">
    				</span>
  			</div>
  			
			<div class="span-16 width80prc ">
       				<span class="span-4 rightAlign">[@spring.message "datadisplayandrules.defineLabels.governmentID"/]</span>
    				<span class="span-3">
    				    [@spring.bind "formBean.govtId" /]
						<input type="text" id="definelabels.input.govtId" name="${spring.status.expression}" value="${spring.status.value?default("")}">
    				</span>
  			</div>
        </div>
        
        <p class="fontBold margin10topbottom">[@spring.message "datadisplayandrules.defineLabels.address"/]</p>
        <div class="span-16 last width80prc marginLeft20prc ">
        	<div class="span-16 width80prc ">
        			<span class="span-4 rightAlign">[@spring.message "datadisplayandrules.defineLabels.address1"/]</span>
    				<span class="span-3">
    				    [@spring.bind "formBean.address1" /]
						<input type="text" id="address1" name="${spring.status.expression}" value="${spring.status.value?default("")}">
    				</span>
  			</div>

        	<div class="span-16 width80prc ">
        			<span class="span-4 rightAlign">[@spring.message "datadisplayandrules.defineLabels.address2"/]</span>
    				<span class="span-3">
    				    [@spring.bind "formBean.address2" /]
						<input type="text" id="address2" name="${spring.status.expression}" value="${spring.status.value?default("")}">
    				</span>
  			</div>
  			
  			<div class="span-16 width80prc ">
        			<span class="span-4 rightAlign">[@spring.message "datadisplayandrules.defineLabels.address3"/]</span>
    				<span class="span-3">
    				    [@spring.bind "formBean.address3" /]
						<input type="text" id="address3" name="${spring.status.expression}" value="${spring.status.value?default("")}">
    				</span>
  			</div>        

            <div class="span-16 width80prc ">
        			<span class="span-4 rightAlign">[@spring.message "datadisplayandrules.defineLabels.state"/]</span>
    				<span class="span-3">
    				    [@spring.bind "formBean.state" /]
						<input type="text" id="state" name="${spring.status.expression}" value="${spring.status.value?default("")}">
    				</span>
  			</div>
  			
			<div class="span-16 width80prc ">
        			<span class="span-4 rightAlign">[@spring.message "datadisplayandrules.defineLabels.postalcode"/]</span>			
    				<span class="span-3">
    				    [@spring.bind "formBean.postalCode" /]
						<input type="text" id="postalcode" name="${spring.status.expression}" value="${spring.status.value?default("")}">
    				</span>
  			</div>
        </div>
        
        <p class="fontBold margin10topbottom">[@spring.message "datadisplayandrules.defineLabels.statuses"/]</p>
        <div class="span-16 last width80prc marginLeft20prc ">
        
        	<div class="span-16 width80prc ">
        			<span class="span-4 rightAlign">[@spring.message "datadisplayandrules.defineLabels.partialApplication"/]</span>
    				<span class="span-3">
    				    [@spring.bind "formBean.partialApplication" /]
						<input type="text" id="partialapplication" name="${spring.status.expression}" value="${spring.status.value?default("")}">
    				</span>
  			</div>
  			
			<div class="span-16 width80prc ">
					<span class="span-4 rightAlign">[@spring.message "datadisplayandrules.defineLabels.pendingApproval"/]</span>
    				<span class="span-3">
    				    [@spring.bind "formBean.pendingApproval" /]
						<input type="text" id="pendingapproval" name="${spring.status.expression}" value="${spring.status.value?default("")}">
    				</span>
  			</div>
  			
			<div class="span-16 width80prc ">
        			<span class="span-4 rightAlign">[@spring.message "datadisplayandrules.defineLabels.approved"/]</span>
    				<span class="span-3">
    				    [@spring.bind "formBean.approved" /]
						<input type="text" id="approved" name="${spring.status.expression}" value="${spring.status.value?default("")}">
    				</span>
  			</div>

			<div class="span-16 width80prc ">
        			<span class="span-4 rightAlign">[@spring.message "datadisplayandrules.defineLabels.cancel"/]</span>
    				<span class="span-3">
    				    [@spring.bind "formBean.cancel" /]
						<input type="text" id="cancel" name="${spring.status.expression}" value="${spring.status.value?default("")}">
    				</span>
  			</div>
 
 			<div class="span-16 width80prc ">
        			<span class="span-4 rightAlign">[@spring.message "datadisplayandrules.defineLabels.closed"/]</span>
    				<span class="span-3">
    				    [@spring.bind "formBean.closed" /]
						<input type="text" id="closed" name="${spring.status.expression}" value="${spring.status.value?default("")}">
    				</span>
  			</div>

 			<div class="span-16 width80prc ">
        			<span class="span-4 rightAlign">[@spring.message "datadisplayandrules.defineLabels.onhold"/]</span>
    				<span class="span-3">
    				    [@spring.bind "formBean.onhold" /]
						<input type="text" id="onhold" name="${spring.status.expression}" value="${spring.status.value?default("")}">
    				</span>
  			</div>
  			
 			<div class="span-16 width80prc ">
        			<span class="span-4 rightAlign">[@spring.message "datadisplayandrules.defineLabels.active"/]</span>
    				<span class="span-3">
    				    [@spring.bind "formBean.active" /]
						<input type="text" id="active" name="${spring.status.expression}" value="${spring.status.value?default("")}">
    				</span>
  			</div>

 			<div class="span-16 width80prc ">
        			<span class="span-4 rightAlign">[@spring.message "datadisplayandrules.defineLabels.inactive"/]</span>
    				<span class="span-3">
    				    [@spring.bind "formBean.inActive" /]
						<input type="text" id="inactive" name="${spring.status.expression}" value="${spring.status.value?default("")}">
    				</span>
  			</div>
  			
 			<div class="span-16 width80prc ">
        			<span class="span-4 rightAlign">[@spring.message "datadisplayandrules.defineLabels.activeingoodstanding"/]</span>
    				<span class="span-3">
    				    [@spring.bind "formBean.activeInGoodStanding" /]
						<input type="text" id="activeInGoodStanding" name="${spring.status.expression}" value="${spring.status.value?default("")}">
    				</span>
  			</div>

 			<div class="span-16 width80prc ">
        			<span class="span-4 rightAlign">[@spring.message "datadisplayandrules.defineLabels.activeinbadstanding"/]</span>
    				<span class="span-3">
    				    [@spring.bind "formBean.activeInBadStanding" /]
						<input type="text" id="activeInBadStanding" name="${spring.status.expression}" value="${spring.status.value?default("")}">
    				</span>
  			</div>

 			<div class="span-16 width80prc ">
        			<span class="span-4 rightAlign">[@spring.message "datadisplayandrules.defineLabels.closed-obligationmet"/]</span>
    				<span class="span-3">
    				    [@spring.bind "formBean.closedObligationMet" /]
						<input type="text" id="closedObligationMet" name="${spring.status.expression}" value="${spring.status.value?default("")}">
    				</span>
  			</div>
  			
 			<div class="span-16 width80prc ">
        			<span class="span-4 rightAlign">[@spring.message "datadisplayandrules.defineLabels.closed-rescheduled"/]</span>
    				<span class="span-3">
    				    [@spring.bind "formBean.closedRescheduled" /]
						<input type="text" id="closedRescheduled" name="${spring.status.expression}" value="${spring.status.value?default("")}">
    				</span>
  			</div>
  			
 			<div class="span-16 width80prc ">
        			<span class="span-4 rightAlign">[@spring.message "datadisplayandrules.defineLabels.closed-writtenoff"/]</span>
    				<span class="span-3">
    				    [@spring.bind "formBean.closedWrittenOff" /]
						<input type="text" id="closedWrittenOff" name="${spring.status.expression}" value="${spring.status.value?default("")}">
    				</span>
  			</div>
        </div>
        
        <p class="fontBold margin10topbottom">[@spring.message "datadisplayandrules.defineLabels.gracetypes"/]</p>
        <div class="span-16 last width80prc marginLeft20prc ">
        	<div class="span-16 width80prc ">
        			<span class="span-4 rightAlign">[@spring.message "datadisplayandrules.defineLabels.none"/]</span>
    				<span class="span-3">
    				    [@spring.bind "formBean.none" /]
						<input type="text" id="gracenone" name="${spring.status.expression}" value="${spring.status.value?default("")}">
    				</span>
  			</div>
        	
        	<div class="span-16 width80prc ">
        			<span class="span-4 rightAlign">[@spring.message "datadisplayandrules.defineLabels.graceonallrepayments"/]</span>
    				<span class="span-3">
    				    [@spring.bind "formBean.graceOnAllRepayments" /]
						<input type="text" id="graceallrepayments" name="${spring.status.expression}" value="${spring.status.value?default("")}">
    				</span>
  			</div>
        	
        	<div class="span-16 width80prc ">
        			<span class="span-4 rightAlign">[@spring.message "datadisplayandrules.defineLabels.principalonlygrace"/]</span>
    				<span class="span-3">
    				    [@spring.bind "formBean.principalOnlyGrace" /]
						<input type="text" id="graceprincipalonly" name="${spring.status.expression}" value="${spring.status.value?default("")}">
    				</span>
  			</div>
        </div>
        
        <p class="fontBold margin10topbottom">[@spring.message "datadisplayandrules.defineLabels.miscellaneous"/] </p>
        <div class="span-16 last width80prc marginLeft20prc ">
        	<div class="span-16 width80prc ">
        			<span class="span-4 rightAlign">[@spring.message "datadisplayandrules.defineLabels.interest"/]</span>
    				<span class="span-3">
    				    [@spring.bind "formBean.interest" /]
						<input type="text" id="interest" name="${spring.status.expression}" value="${spring.status.value?default("")}">
    				</span>
  			</div>
        
            <div class="span-16 width80prc ">
        			<span class="span-4 rightAlign">[@spring.message "datadisplayandrules.defineLabels.externalID"/]</span>
    				<span class="span-3">
    				    [@spring.bind "formBean.externalId" /]
						<input type="text" id="externalId" name="${spring.status.expression}" value="${spring.status.value?default("")}">
    				</span>
  			</div>

            <div class="span-16 width80prc ">
        			<span class="span-4 rightAlign">[@spring.message "datadisplayandrules.defineLabels.bulkentry"/]</span>
    				<span class="span-3">
    				    [@spring.bind "formBean.bulkEntry" /]
						<input type="text" id="bulkEntry" name="${spring.status.expression}" value="${spring.status.value?default("")}">
    				</span>
  			</div>
        </div>
        
        <div class="clear">&nbsp;</div>

        <div class="buttonsSubmitCancel">
             <input class="buttn"  type="submit" id="definelabels.button.submit" name="submit" value="[@spring.message "submit"/]"/>
            <input class="buttn2" type="submit" id="CANCEL" name="CANCEL" value="[@spring.message "cancel"/]"/>
        </div>

	</div>
   	</form> 
  </div>
  <!--Main content ends-->
  [/@adminLeftPaneLayout]