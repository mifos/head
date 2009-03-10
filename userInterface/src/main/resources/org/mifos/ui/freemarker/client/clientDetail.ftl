[#ftl]
[#import "spring.ftl" as spring]
[#import "macros.ftl" as mifos]

[@mifos.header "clientsAndAccounts.title" /]
  [@mifos.topNavigation currentTab="ClientsAndAccounts" /]
  [#include "clientsAndAccountsLeftPane.ftl" ]
    <div id="page-content">
    	<div id="clientDetailPage">
	    	<h2>${model.client.firstName} ${model.client.lastName}</h2>
	      	<h2>Account Information</h2>
	      	<div id="clientDetailPage-loanSection">
	    		<h4 id="clientDetailPage-loanSectionHeading">Loan</h4>
	    		<div id="clientDetailPage-loanList">
					[#if model.loans?size == 0] 
					    <p>No loans found</p>
					[#else]
				    	[#assign loanNum = 0]					    	
				        <table id="loan-product-table">
						    [#list model.loans as loan]
							     [#assign loanNum = loanNum + 1]
								 <tr>
							        <td id="loan-${loanNum}"><a href="loanDetail.ftl?id=${loan.id}">${loan.loanProductDto.longName}</a></td>
							     </tr>
						    [/#list]
					    <table>
					[/#if]
				</div>
	      	</div>
      	</div>
    </div> <!-- main pane content -->
[@mifos.footer /]
