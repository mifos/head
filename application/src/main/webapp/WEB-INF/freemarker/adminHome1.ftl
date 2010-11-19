[#ftl]
[#import "spring.ftl" as spring]
[#import "macros.ftl" as mifos]

[@mifos.header "title" /]
  [@mifos.topNavigation currentTab="Admin" /]
  [#include "adminLeftPane.ftl" ] 
        
	<div id="page-content">
	  <div id ="adminPageContent">
		<h2 id="adminHome.heading">[@spring.message "administrativeTasks" /]</h2>
		
			<p>
			[@spring.message "administrativeTasksWelcome" /] More or less
			</p>
		
			<h3>[@spring.message "manageOrganization" /]</h3>
			
				<h4>[@spring.message "users" /]</h4>
				
					<ul class="navigation-list">
					<li>[@spring.message "viewUsers" /]</li>
					<li><a href="createUser.ftl" id="create.user">[@spring.message "defineNewUser" /]</a></li>
					</ul>

				<h4>[@spring.message "offices" /]</h4>

					<ul class="navigation-list">
					<li><a href="viewOffices.ftl" id="view.offices">[@spring.message "viewOffices" /]</a></li>
					<li>[@spring.message "defineNewOffice" /]</li>
					</ul>
				
			<h3>[@spring.message "manageProducts" /]</h3>
	
				<h4>[@spring.message "manageLoanProducts" /]</h4>
		
					<ul class="navigation-list">
						<li> <a href="viewLoanProducts.ftl" id="view.loan.products">[@spring.message "viewLoanProducts" /]</a> </li>
						<li> <a href="createLoanProduct.ftl"id="create.loan.product">[@spring.message "defineLoanProduct" /]</a></li>
					</ul>
		</div>	
	</div>
[@mifos.footer /]
