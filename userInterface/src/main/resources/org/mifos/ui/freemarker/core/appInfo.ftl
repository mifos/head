[#ftl]
[#import "spring.ftl" as spring]
[#import "macros.ftl" as mifos]

[@mifos.header "systemInformation" /]
  [@mifos.topNavigation currentTab="Admin" /]
  [#include "adminLeftPane.ftl" ] 
        
	<div id="page-content">
	  <div id="standardPageContent">
	    <h2>[@spring.message "systemInformation" /]</h2>
	    <ul>
	      <li><span id="appInfo.svn.revision.message">[@spring.message "svnRevision" /]</span>: <span id="appInfo.svn.revision">${model.appInfo.svnRevision}</span></li>
	      <li><span id="appInfo.build.tag.message">[@spring.message "buildTag" /]</span>: <span id="appInfo.build.tag">${model.appInfo.buildTag}</span></li>
	      <li><span id="appInfo.build.id.message">[@spring.message "buildId" /]</span>: <span id="appInfo.build.id">${model.appInfo.buildId}</span></li>
	    </ul>
	  </div>
	</div>
[@mifos.footer /]

