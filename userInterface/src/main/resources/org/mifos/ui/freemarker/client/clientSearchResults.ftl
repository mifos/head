[#ftl]
[#import "spring.ftl" as spring]
[#import "macros.ftl" as mifos]

[@mifos.header "title" /]
  [@mifos.topNavigation currentTab="ClientsAndAccounts" /]
  [#include "clientsAndAccountsLeftPane.ftl" ]
  
  <div id="page-content">      
    <h2>Client Search Results</h2>
   
[#if model.clients?size == 0] 
    	
    	<p> No matching clients found</p>
    	
[#else]
    
    	[#assign clientNum = 0]
    	
        <table id="client-list-table">
	     [#list model.clients as client]
	     [#assign clientNum = clientNum + 1]
		 <tr>
		 	<td id="client-num-${clientNum}">${clientNum}.</td>
	        <td id="client-name-${clientNum}"><a href="clientDetail.ftl?id=${client.id}">${client.firstName} ${client.lastName}</a></td>
	     </tr>
	    [/#list]
    <table>
    
[/#if]

  </div>
[@mifos.footer /]

