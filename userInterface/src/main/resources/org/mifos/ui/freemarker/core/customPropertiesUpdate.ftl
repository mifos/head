[#ftl]
[#--
* Copyright (c) 2005-2011 Grameen Foundation USA
*  All rights reserved.
*
*  Licensed under the Apache License, Version 2.0 (the "License");
*  you may not use this file except in compliance with the License.
*  You may obtain a copy of the License at
*
*      http://www.apache.org/licenses/LICENSE-2.0
*
*  Unless required by applicable law or agreed to in writing, software
*  distributed under the License is distributed on an "AS IS" BASIS,
*  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
*  implied. See the License for the specific language governing
*  permissions and limitations under the License.
*
*  See also http://www.apache.org/licenses/LICENSE-2.0.html for an
*  explanation of the license and how it is applied.
--]
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
      [#if model.processFlowResult??]
          [@spring.message "updatingProcessFlow"/]&nbsp;: <div id="CustomProperties.processFlow.result">${model.processFlowResult}</div>
      [/#if]
      [#if model.ImportResult??]
	      [@spring.message "updatingImport"/]&nbsp;: <div id="CustomProperties.import.result">${model.ImportResult}</div>
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
  [@layout.footer /]

