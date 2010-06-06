[#ftl]
[#import "spring.ftl" as spring]
[#import "macros.ftl" as mifos]

[@mifos.header "page.fees" /]

	<div id="login-page-header">
	  sometext
	</div>
	
	${FeeParameters.glCodes?size} <br/>
  
	<#list FeeParameters.glCodes?keys as key>
 	   <br/> ${FeeParameters.glCodes.get(key?String)}
    </#list>
	

[@mifos.footer /]

   