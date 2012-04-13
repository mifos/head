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
[@layout.reportsLeftPaneLayout]
	
	[@widget.crumbs breadcrumbs /]
	
	<span id="page.id" title="PentahoReport"></span>
	[@i18n.formattingInfo /]
	
	<div class="content">
		<br />
		<h1>${reportName}</h1>
		<br/>
		
		[@form.errors "pentahoReportFormBean.*"/]
		<form action="execPentahoReport.ftl" method="post">
			[@form.input path="pentahoReportFormBean.reportId" id="input.reportId" fieldType="hidden" /]
		    
		    <div class="row">
		        <label for="input.outputType"><span class="mandatory">*</span>[@spring.message "accounts.mode_of_payment" /]:</label>
		        [@form.singleSelectWithPrompt path="pentahoReportFormBean.outputType" id="input.outputType" options=pentahoReportFormBean.allowedOutputTypes /]
		    </div>
		    
		    <div class="row">
		    	[@form.submitButton label="widget.form.buttonLabel.submit" id="input.submit" /]
		    </div>
		</form>
	</div>
	
[/@layout.reportsLeftPaneLayout]