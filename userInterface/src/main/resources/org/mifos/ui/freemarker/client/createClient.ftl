[#ftl]
[#import "spring.ftl" as spring]
[#import "macros.ftl" as mifos]
[#assign form=JspTaglibs["/WEB-INF/tld/spring-form.tld"]]

[@mifos.header "createClient.title" /]
   [@mifos.topNavigation currentTab="ClientsAndAccounts" /]
   [#include "clientsAndAccountsLeftPane.ftl"]
    <div class="content-pane">
      [@form.form action="createClient.ftl" commandName="client"]
         [@form.errors path="*" cssClass="error-messages"/]
         <fieldset class="noborder">
           <legend id="form.legend">[@spring.message "createClient.title" /]</legend>
              <label for="createClient.form.first.name" accesskey="n">[@spring.message "firstName" /]:</label>
                [@form.input path="firstName"/]<br/>
              <label for="createClient.form.last.name" accesskey="l">[@spring.message "lastName" /]: </label>
                [@form.input path="lastName"/]<br/>
              <label for="createClient.form.date.of.birth" accesskey="l">[@spring.message "dateOfBirth" /] (<span id="datePattern">${datePattern}</span>):</label>
                [@form.input path="localDateOfBirth"/]<br/>
              <label for="kludge"></label>
                 <input type="submit" value="[@spring.message "create"/]" class="buttn" id="client.form.submit" tabindex="4"/>
         </fieldset>
      [/@form.form]
     
  </div> <!-- content-pane -->
[@mifos.footer /]
