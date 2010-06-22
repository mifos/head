[#ftl]
[#import "spring.ftl" as spring]
[#import "macros.ftl" as mifos]
<html>
<body>
	<div class="left-pane">
	  <div class="left-pane-header">[@spring.message "quickStart" /]</div>
	  <div class="left-pane-content">
	      <h2>[@spring.message "manageClients" /]</h2>
	      <div class="left-pane-heading-group">
	         [@spring.message "createNewGroup" /]<br/> 
	         <a href="createClient.ftl" >[@spring.message "createNewClient" /]</a>
	      </div>
	      <h2>[@spring.message "manageAccounts" /]</h2>
	      <div class="left-pane-heading-group">
	         <a href="createLoan.ftl" id="left.pane.create.loan">[@spring.message "openNewLoanAccount" /]</a>
	      </div>
   	  </div>
	</div>
	</body>
</html>