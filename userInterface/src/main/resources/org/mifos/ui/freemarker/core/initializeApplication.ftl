[#ftl]
[#import "spring.ftl" as spring]
[#import "macros.ftl" as mifos]
<html>
  <head>
    <title>[@spring.message "initializerApplication"/]</title>
  </head>
  <body>
      <span id="page.id" title="InitializeApplication" />
      <div id="initializeApplication.message">[@spring.message "applicationHasBeenReInitialized"/]</div>
  [@mifos.footer /]

