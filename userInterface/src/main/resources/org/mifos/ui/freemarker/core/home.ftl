[#ftl]
[#import "spring.ftl" as spring]
[#import "macros.ftl" as mifos]
[#assign form=JspTaglibs["/WEB-INF/tld/spring-form.tld"]]

[@mifos.header "title" /]
  [@mifos.topNavigation currentTab="Home" /]
  [#include "homeLeftPane.ftl" ]  
  
  
  <div id="page-content">
  	<div id="homePageContent">
	  [@form.form action="clientSearchResults.ftl" commandName="searchString"]
	  [@form.errors path="*" cssClass="error-messages"/]

      <h2 id="hello.heading">[@spring.message "welcomeToMifos" /]</h2>
      <h4>[@spring.message "toQuicklyFindAClientYouCan"/]</h4>
      <label id="clientSearch">[@spring.message "searchByName" /]</label> 
      [@form.input path="searchString"/]
	  <input type="submit" value="[@spring.message "search" /]" class="buttn" id="clientSearch.form.submit" tabindex="6" />
      <h4>[@spring.message "toViewAListOfGroups"/]</h4>
      <a href="viewGroups.ftl" id="home.list.groups">See a list of groups</a>
      [/@form.form]
    </div>
  </div>

[@mifos.footer /]