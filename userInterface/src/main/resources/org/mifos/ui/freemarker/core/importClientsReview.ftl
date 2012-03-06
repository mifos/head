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
[#include "layout.ftl"]
[@layout.webflow currentTab="Admin"
                 currentState="admin.importexport.clients.flowstate.review" 
                 states=["admin.importexport.clients.flowstate.selectFile", 
                         "admin.importexport.clients.flowstate.review"]]
<span id="page.id" title="ImportClientsReview"></span>                    
<!-- Main Content Begins -->
<h1>[@spring.message "admin.importexport.clients.importClients" /] - <span class="standout">[@spring.message "admin.importexport.reviewandsubmit" /]</span></h1>
<p>[@spring.message "admin.importexport.clients.review.reviewInformation" /]</p>
<br />

<p class="fontnormalbold">[@spring.message "admin.importexport.importinformation" /]</p>
<br />
<p>[@spring.message "admin.importexport.importstatus" /]: 
    [#if parsedClientsDto.noValidRows]
        [@spring.message "admin.importexport.zeroImportableRows" /]        
    [#else]
        [#assign args=[ parsedClientsDto.parsedRowsCount ] /]
        [@spring.messageArgs "admin.importexport.clients.review.successfullyParsedRows" args /]
    [/#if]
</p>

[#if parsedClientsDto.inError]
    <p>[@spring.message "admin.importexport.rowsWithErrors" /]</p>
    <br />
    [#list parsedClientsDto.parseErrors as error]
        <p><font color="red">${error}</font></p>
    [/#list]
[/#if]
<form action="${flowExecutionUrl}" method="post">
    [@form.submitButton label="admin.importexport.clients.review.edit" id="importClients.back" webflowEvent="back" /]
</form>
<br />

<form action="${flowExecutionUrl}" method="post" class="two-columns">
    <div class="row webflow-controls">
        [#if parsedClientsDto.noValidRows]        
            [@form.disabledButton label="widget.form.buttonLabel.submit" id="importClients.submit" /]
        [#else]
            [@form.submitButton label="widget.form.buttonLabel.submit" id="importClients.submit" webflowEvent="save" /]
        [/#if]
        [@form.cancelButton label="widget.form.buttonLabel.cancel" webflowEvent="cancel" /]
    </div>
</form>
<!-- Main Content Ends -->
[/@layout.webflow]