[#ftl]
[#import "spring.ftl" as spring]
[#import "macros.ftl" as mifos]

[@mifos.header "clientsAndAccounts.title" /]
  [@mifos.topNavigation currentTab="ClientsAndAccounts" /]
  [#include "clientsAndAccountsLeftPane.ftl" ]
    <div class="content-pane">
      <h2>Client search results</h2>
    </div> <!-- main pane content -->
[@mifos.footer /]
