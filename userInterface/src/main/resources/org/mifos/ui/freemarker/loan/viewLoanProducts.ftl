[#ftl]
[#import "spring.ftl" as spring]
[#import "macros.ftl" as mifos]

[@mifos.header "title" /]

  <!-- page: viewLoanProducts.ftl -->
  
  [@mifos.topNavigation currentTab="Admin" /]
  [#include "adminLeftPane.ftl" ]  
  
  <div id="page-content">      
    <h2>[@spring.message "loanProducts.view.heading" /]</h2>
   
[#if model.loanProducts?size == 0] 
    	
    	<p> [@spring.message "loanProducts.view.noneDefined" /] </p>
    	
[#else]
    
    	[#assign lpnum = 0]
    	
        <table id="loan-product-table">
	    <tr>
	    	<th>[@spring.message "loanProducts.view.tableHeading.shortName" /]</th>
	    	<th>[@spring.message "loanProducts.view.tableHeading.longName" /]</th>
	    </tr>
	     [#list model.loanProducts as product]
	     [#assign lpnum = lpnum + 1]
		 <tr>
	        <td id="short-name-${lpnum}"><a href="viewLoanProduct.ftl?id=${product.id}" id="${product.shortName}">${product.shortName}</a></td>
	        <td id="long-name-${lpnum}"><a href="viewLoanProduct.ftl?id=${product.id}" id="${product.shortName}">${product.longName}</a></td>
	     </tr>
	    [/#list]
    <table>
    
[/#if]

  </div>
[@mifos.footer /]

