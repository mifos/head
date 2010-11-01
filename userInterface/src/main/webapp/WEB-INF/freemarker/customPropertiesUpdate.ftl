[#ftl]
[#import "spring.ftl" as spring]
[#import "macros.ftl" as mifos]
<html>
  <head>
    <title>[@spring.message "customPropertiesUpdate"/]</title>
  </head>
  <body>
      <input type="hidden" id="page.id" value="CustomPropertiesUpdate"/>
      [#if model.localeResult??]
	      [@spring.message "updatingLocale"/]&nbsp;: <div id="CustomProperties.locale.result">${model.localeResult}</div>
      [/#if]
      [#if model.accountingRulesResult??]
	      [@spring.message "updatingAccountingRules"/]&nbsp;: <div id="CustomProperties.accountingRules.result">${model.accountingRulesResult}</div>
      [/#if]
      [#if model.fiscalCalendarRulesResult??]
	      [@spring.message "updatingFiscalCalendarRules"/]&nbsp;: <div id="CustomProperties.accountingRules.result">${model.fiscalCalendarRulesResult}</div>
      [/#if]
      [#if model.clientRulesResult??]
	      [@spring.message "updatingClientRules"/]&nbsp;: <div id="CustomProperties.clientRules.result">${model.clientRulesResult}</div>
      [/#if]
      [#if (status.errorMessages?size > 0)]
        [@spring.message "errors"/]&nbsp;: <div id="CustomProperties.error.messages">[#list status.errorMessages as errorMessage]${errorMessage}[/#list]</div>
      [/#if]
  [@mifos.footer /]

