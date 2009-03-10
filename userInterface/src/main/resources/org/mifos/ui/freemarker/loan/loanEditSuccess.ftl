[#ftl]
[#import "spring.ftl" as spring]
[#import "macros.ftl" as mifos]

[@mifos.header "title" /]
  [@mifos.topNavigation currentTab="Home" /]
      
	<div id="page-content">
	
	 <h2 id="loanEditMessage">The loan has been created</h2>
	 
	 <table>
	 	<tr><td>Loan Product:</td><td>${model.loan.loanProductDto.longName}</td></tr>
	 	<tr><td>Amount:</td><td>${model.loan.amount}</td></tr>
	 	<tr><td>Interest rate:</td><td>${model.loan.interestRate}</td></tr>
	 </table>
	   
	   </div>
[@mifos.footer /]

