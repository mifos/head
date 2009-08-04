[#ftl]
[#import "spring.ftl" as spring]
[#import "macros.ftl" as mifos]
<html>
  <head>
    <title>Date Time Update</title>
  </head>
  <body>
      <span id="page.id" title="DateTimeUpdate" />
      Updating to time: <div id="DateTimeUpdate.text.result">${model.updateResult}</div>
  [@mifos.footer /]

