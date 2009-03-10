[#ftl]
[#import "spring.ftl" as spring]
[#import "macros.ftl" as mifos]

[@mifos.header "deleteLoanProductSuccess.title" /]
  [@mifos.topNavigation currentTab="Admin" /]
  [#include "adminLeftPane.ftl" ]
    <div class="content-pane">
      <span id="deleteLoanProductSuccess.message">[@spring.message "deleteLoanProductSuccess.successfullyDeleted" /] '<span id="deleteLoanProductSuccess.loanProduct.longName">${model.loanProduct.longName}</span>'.</span>
      <p/>
      <a href="viewLoanProducts.ftl">Back to View Loan Products</a>
    </div>
[@mifos.footer /]
