[#ftl]
[#import "spring.ftl" as spring]
[#import "macros.ftl" as mifos]
<html>
  <head>
    <title>Custom Properties Update</title>
  </head>
  <body>
      <input type="hidden" id="page.id" value="CustomPropertiesUpdate"/>
      [#if model.localeResult??]
	      Updating locale: <div id="CustomProperties.locale.result">${model.localeResult}</div>
      [/#if]
      [#if model.accountingRulesResult??]
	      Updating accountingRules: <div id="CustomProperties.accountingRules.result">${model.accountingRulesResult}</div>
      [/#if]
      [#if model.fiscalCalendarRulesResult??]
	      Updating fiscalCalendarRules: <div id="CustomProperties.accountingRules.result">${model.fiscalCalendarRulesResult}</div>
      [/#if]
      [#if model.clientRulesResult??]
	      Updating clientRules: <div id="CustomProperties.clientRules.result">${model.clientRulesResult}</div>
      [/#if]
      [#if (status.errorMessages?size > 0)]
        Errors: <div id="CustomProperties.error.messages">[#list status.errorMessages as errorMessage]${errorMessage}[/#list]</div>
      [/#if]
  [@mifos.footer /]

