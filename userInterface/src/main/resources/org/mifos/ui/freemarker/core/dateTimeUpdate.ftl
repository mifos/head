[#ftl]
[#import "spring.ftl" as spring]
[#import "macros.ftl" as mifos]
<html>
  <head>
    <title>Date Time Update</title>
  </head>
  <body>
      <input type="hidden" id="page.id" value="DateTimeUpdate"/>
      Updating to time: <div id="DateTimeUpdate.text.result">${model.updateResult}</div>
  [@mifos.footer /]

