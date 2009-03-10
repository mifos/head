[#ftl]
[#import "spring.ftl" as spring]
[#import "macros.ftl" as mifos]
[#assign form=JspTaglibs["/WEB-INF/tld/spring-form.tld"]]

[@mifos.header "title" /]
  <!-- page: createLoanProduct.ftl -->
  
  [@mifos.topNavigation currentTab="Admin" /]
  [#include "adminLeftPane.ftl" ] 

	<div id="page-content">
	
		<h2>[@spring.message "loanProduct.create.heading"/]</h2>
				
		 [@form.form action="createLoanProduct.ftl" commandName="loanProduct"]
		 [@form.errors path="*" cssClass="error-messages"/]
		
			<fieldset>
			
				<legend>Loan Product Details</legend>
			
				<label for="loanproduct.form.longname" accesskey="l">[@spring.message "loanProduct.longName.description"/]:</label>
					[@form.input path="longName"/]
					<br/>
			
				<label  for="loanproduct.form.shortname" accesskey="s">[@spring.message "loanProduct.shortName.description"/]:</label>
					[@form.input path="shortName"/]
					<br/>
			
				<label for="loanproduct.form.mininterest" accesskey="m">[@spring.message "loanProduct.minInterestRate.description"/]:</label>
					[@form.input path="minInterestRate"/]
					<br/>
			
				<label for="loanproduct.form.maxinterest" accesskey="a">[@spring.message "loanProduct.maxInterestRate.description"/]:</label>
					[@form.input path="maxInterestRate"/]
					<br/>
			
				<label for="loanproduct.form.status" accesskey="t">[@spring.message "loanProduct.status.description"/]:</label>
					[@form.select path="status"
								  items=availableCategories /]
					<br/>
			
				<label for="kludge"></label>
					<input type="submit" value="[@spring.message "submit"/]" class="buttn" id="login.form.submit" tabindex="6" />
			
			</fieldset>
			
		[/@form.form]
  	
  	</div>
  
      [#include "footer.ftl"]    
[@mifos.footer /]
  