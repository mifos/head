[#ftl]
[#import "spring.ftl" as spring]
[#import "macros.ftl" as mifos]
[#assign form=JspTaglibs["/WEB-INF/tld/spring-form.tld"]]

[@mifos.header "collectionSheetEntry.select.title" /]
  [@mifos.topNavigation currentTab="ClientsAndAccounts" /]
    <div id="page-content">
    	<div id="content">
    		    	
			[@form.form action="selectBulkEntryParameters.ftl" commandName="bulkEntrySelection"]
			
			[@form.errors path="*" cssClass="error-messages"/]
			
				<label for="collectionSheetEntry.select.form.branchOffice">
					[@spring.message "collectionSheetEntry.select.form.label.branchOffice"/]:
				</label>
				[@form.select path="branchOfficeId" items=branchOffices /]
				</br>
	
				<label for="collectionSheetEntry.select.form.loanOfficer">
				[@spring.message "collectionSheetEntry.select.form.label.loanOfficer"/]:
				</label>
				[@form.select path="loanOfficerId" items=loanOfficers /]
				</br>
	
				<label for="collectionSheetEntry.select.form.paymentMode">
				[@spring.message "collectionSheetEntry.select.form.label.paymentMode"/]:
				</label>
				[@form.select path="paymentMode" items=paymentModes /]
				</br>
				
				
			[/@form.form]
      	</div>
    </div> <!-- main pane content -->
[@mifos.footer /]
