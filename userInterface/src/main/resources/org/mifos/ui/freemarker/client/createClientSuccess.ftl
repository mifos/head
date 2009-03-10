[#ftl]
[#import "spring.ftl" as spring]
[#import "macros.ftl" as mifos]

[@mifos.header "createClientSuccess.title" /]
  [@mifos.topNavigation currentTab="ClientsAndAccounts" /]
  [#include "clientsAndAccountsLeftPane.ftl" ]
    <div class="content-pane">
      <span id="createClientSuccess.message">[@spring.message "createClientSuccess.clientCreatedSuccessfully" /]</span>
    </div>
[@mifos.footer /]
