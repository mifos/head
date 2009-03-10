[#ftl]
[#import "spring.ftl" as spring]
[#import "macros.ftl" as mifos]
[#assign form=JspTaglibs["/WEB-INF/tld/spring-form.tld"]]

[@mifos.header "deleteLoanProduct.title" /]
  [@mifos.topNavigation currentTab="Admin" /]
  [#include "adminLeftPane.ftl" ] 
      
	<div id="page-content">
	<h2 id="page-content-heading">Delete loan product '${loanProduct.longName}'</h2>
      [@form.form action="deleteLoanProduct.ftl" commandName="deleteLoanProduct"]
         [@form.errors path="*" cssClass="error-messages"/]
         <fieldset class="noborder">
              [@spring.message "deleteLoanProduct.areYouSure" /] '${loanProduct.longName}'?<br/>
                 <input type="hidden" name="loanProductId" value="${loanProduct.id}" />
              <label for="kludge"></label>
                 <input type="submit" name="action" value="[@spring.message "delete"/]" class="buttn" id="client.form.submit.delete" tabindex="4"/>
              <label for="kludge"></label>
                 <input type="submit" name="action" value="[@spring.message "cancel"/]" class="buttn" id="client.form.submit.cancel" tabindex="4"/>
         </fieldset>
      [/@form.form]
	</div>
[@mifos.footer /]

