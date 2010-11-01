[#ftl]
[#import "spring.ftl" as spring]
[#import "macros.ftl" as mifos]
<html>
  <head>
    <title>[@spring.message "dateTimeUpdate"/]</title>
  </head>
  <body>
      <span id="page.id" title="DateTimeUpdate" />
      [@spring.message "updatingToTime"/]&nbsp;: <div id="DateTimeUpdate.text.result">${model.updateResult}</div>
  [@mifos.footer /]

