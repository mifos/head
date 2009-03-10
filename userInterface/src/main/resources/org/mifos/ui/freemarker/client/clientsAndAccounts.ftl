[#ftl]
[#import "spring.ftl" as spring]
[#import "macros.ftl" as mifos]

[@mifos.header "clientsAndAccounts.title" /]
  [@mifos.topNavigation currentTab="ClientsAndAccounts" /]
  [#include "clientsAndAccountsLeftPane.ftl" ]
    <div id="page-content">
    	<div id="clientsAndAccountsPage">
	    	<h2>Clients and Accounts</h2>
      	</div>
    </div> <!-- main pane content -->
[@mifos.footer /]
