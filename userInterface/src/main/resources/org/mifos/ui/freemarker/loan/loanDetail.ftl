[#ftl]
[#import "spring.ftl" as spring]
[#import "macros.ftl" as mifos]
[@mifos.header "clientsAndAccounts.title" /]
  [@mifos.topNavigation currentTab="ClientsAndAccounts" /]
  [#include "clientsAndAccountsLeftPane.ftl" ]
    <div id="page-content">
    	<div id="loanDetailPage">
	    	<h2 id="loanProductName">${model.loan.loanProductDto.longName}</h2>
	    	<p>[@spring.message "client" /]: ${model.clientName}</p>
	    	<p>[@spring.message "loanAmount" /]: ${model.loan.amount}</p>
	    	<p>[@spring.message "interestRate" /]: ${model.loan.interestRate}</p>
	    	<p>[@spring.message "disbursalDate" /]: <span id="disbursalDate">${(model.loanDisbursalDate?date?string.short)!"None"}</span></p>
	    	<p><a id="disburseLoan" href="disburseLoan.ftl?id=${model.loan.id}">[@spring.message "disbursalLoan" /]</a></p>
      	</div>
    </div> <!-- main pane content -->
[@mifos.footer /]
