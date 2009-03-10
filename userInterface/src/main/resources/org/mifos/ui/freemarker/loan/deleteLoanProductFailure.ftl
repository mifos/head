[#ftl]
[#import "spring.ftl" as spring]
[#import "macros.ftl" as mifos]

[@mifos.header "deleteLoanProductFailure.title" /]
  [@mifos.topNavigation currentTab="Admin" /]
  [#include "adminLeftPane.ftl" ]
    <div class="content-pane">
      <span id="deleteLoanProductFailure.message">[@spring.message "deleteLoanProductFailure.couldNotDelete" /] '<span id="deleteLoanProductFailure.loanProduct.longName">${model.loanProduct.longName}</span>' [@spring.message "deleteLoanProductFailure.becauseLoansExist" /]</span>
      <p/>
      <a href="viewLoanProducts.ftl">Back to View Loan Products</a>
    </div>
[@mifos.footer /]
