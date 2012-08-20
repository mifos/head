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
                 currentState="admin.importexport.savings.flowstate.selectFile" 
                 states=["admin.importexport.savings.flowstate.selectFile", 
                         "admin.importexport.savings.flowstate.review"]]
<span id="page.id" title="ImportSavingsSelectFile"></span>                    
<!-- Main Content Begins -->
<h1>[@spring.message "admin.importexport.savings.importSavings" /] - <span class="standout">[@spring.message "admin.importexport.enterfileinfo" /]</span></h1>
<p>[@spring.message "admin.importexport.savings.statement" /] <font color="#ff0000">*</font>[@spring.message "admin.importexport.savings.markedStatement" /]</p>

[@form.errors "importSavingsFormBean.*"/]
<form action="${flowExecutionUrl}" method="post" class="two-columns" enctype="multipart/form-data">
    <fieldset>
    <div class="row">
        <label for="importSavings.file"><span class="mandatory">*</span>[@spring.message "admin.importexport.selectimportfile" /]:</label>
        [@form.input path="importSavingsFormBean.file" id="importSavings.file" attributes="" fieldType="file"/]
    </div>
    </fieldset>
    <div class="row webflow-controls">
        [@form.submitButton label="admin.importexport.savings.review" id="importSavings.review" webflowEvent="fileSelected" /]
        [@form.cancelButton label="widget.form.buttonLabel.cancel" webflowEvent="cancel" /]
    </div>
</form>
<!-- Main Content Ends -->
[/@layout.webflow]