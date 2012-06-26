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
                 currentState="admin.importexport.loans.flowstate.selectFile" 
                 states=["admin.importexport.loans.flowstate.selectFile", 
                         "admin.importexport.loans.flowstate.review"]]
<span id="page.id" title="ImportLoansSelectFile"></span>                    
<!-- Main Content Begins -->
<h1>[@spring.message "admin.importexport.loans.importLoans" /] - <span class="standout">[@spring.message "admin.importexport.enterfileinfo" /]</span></h1>
<p>[@spring.message "admin.importexport.loans.statement" /] <font color="#ff0000">*</font>[@spring.message "admin.importexport.loans.markedStatement" /]</p>

[@form.errors "importLoansFormBean.*"/]
<form action="${flowExecutionUrl}" method="post" class="two-columns" enctype="multipart/form-data">
    <fieldset>
    <div class="row">
        <label for="importLoans.file"><span class="mandatory">*</span>[@spring.message "admin.importexport.selectimportfile" /]:</label>
        [@form.input path="importLoansFormBean.file" id="importLoans.file" attributes="" fieldType="file"/]
    </div>
    </fieldset>
    <div class="row webflow-controls">
        [@form.submitButton label="admin.importexport.loans.review" id="importLoans.review" webflowEvent="fileSelected" /]
        [@form.cancelButton label="widget.form.buttonLabel.cancel" webflowEvent="cancel" /]
    </div>
</form>
<!-- Main Content Ends -->
[/@layout.webflow]