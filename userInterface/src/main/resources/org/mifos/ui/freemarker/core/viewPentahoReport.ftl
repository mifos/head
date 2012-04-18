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
		
		[@spring.bind "pentahoReportFormBean" /]
		[@form.errors "pentahoReportFormBean.*"/]
		<form action="execPentahoReport.ftl" method="post">
			[@form.input path="pentahoReportFormBean.reportId" id="input.reportId" fieldType="hidden" /]
		    
		    <div class="row">
		        <label for="input.outputType"><span class="mandatory">*</span>[@spring.message "OutputFormat" /]: </label>
		        [@form.singleSelectWithPrompt path="pentahoReportFormBean.outputType" id="input.outputType" options=pentahoReportFormBean.allowedOutputTypes /]
		    </div>
		    
		    [#list pentahoReportFormBean.reportDateParams as param]
		    	[#assign item]pentahoReportFormBean.reportDateParams[${param_index}][/#assign]
	    		<div class="row">
	    		 	[@form.input path="${item}.paramName" id="${param.paramName}_paramName" fieldType="hidden" /]
	    		 	[@form.input path="${item}.mandatory" id="${param.paramName}_mandatory," fieldType="hidden" /]
	    		 	<label for="${param.paramName}_DD">
	    		 	    [#if param.mandatory == true]<span class="mandatory">*</span>[/#if]
	    		 		${param.paramName}:
	    		 	</label>
			        [@form.input path="${item}.dateDD" id="${param.paramName}_DD" attributes="size=1 maxlength=2" /]<span>[@spring.message "datefield.dd"/]</span>
	    			[@form.input path="${item}.dateMM" id="${param.paramName}_MM" attributes="size=1 maxlength=2" /]<span>[@spring.message "datefield.mm"/]</span>
    				[@form.input path="${item}.dateYY" id="${param.paramName}_YY" attributes="size=3 maxlength=4" /]<span>[@spring.message "datefield.yyyy"/]</span>
	    		</div>
		    [/#list]
		    
		    [#list pentahoReportFormBean.reportInputParams as param]
		    	[#assign item]pentahoReportFormBean.reportInputParams[${param_index}][/#assign]
	    		<div class="row">
	    		 	[@form.input path="${item}.paramName" id="${param.paramName}_paramName" fieldType="hidden" /]
	    		 	[@form.input path="${item}.mandatory" id="${param.paramName}_mandatory," fieldType="hidden" /]
	    		 	<label for="${param.paramName}_value">
	    		 	    [#if param.mandatory == true]<span class="mandatory">*</span>[/#if]
	    		 		${param.paramName}:
	    		 	</label>
			        [@form.input path="${item}.value" id="${param.paramName}_value" /]
	    		</div>
		    [/#list]
		    
		    [#list pentahoReportFormBean.reportSingleSelectParams as param]
		    	[#assign item]pentahoReportFormBean.reportSingleSelectParams[${param_index}][/#assign]
	    		<div class="row">
	    		 	[@form.input path="${item}.paramName" id="${param.paramName}_paramName" fieldType="hidden" /]
	    		 	[@form.input path="${item}.mandatory" id="${param.paramName}_mandatory," fieldType="hidden" /]
	    		 	<label for="${param.paramName}_value">
	    		 	    [#if param.mandatory == true]<span class="mandatory">*</span>[/#if]
	    		 		${param.paramName}:
	    		 	</label>
			        [@form.input path="${item}.selectedValue" id="${param.paramName}_value" /]
	    		</div>
		    [/#list]
		    
		    [#list pentahoReportFormBean.reportMultiSelectParams as param]
		    	[#assign item]pentahoReportFormBean.reportMultiSelectParams[${param_index}][/#assign]
	    		<div class="row">
	    		 	[@form.input path="${item}.paramName" id="${param.paramName}_paramName" fieldType="hidden" /]
	    		 	[@form.input path="${item}.mandatory" id="${param.paramName}_mandatory," fieldType="hidden" /]
	    		 	<label for="${param.paramName}_value">
	    		 	    [#if param.mandatory == true]<span class="mandatory">*</span>[/#if]
	    		 		${param.paramName}:
	    		 	</label>
			        [@form.input path="${item}.inputValue" id="${param.paramName}_value" /]
	    		</div>
		    [/#list]
		    
		    <div class="row">
		    	[@form.submitButton label="widget.form.buttonLabel.submit" id="input.submit" /]
		    </div>
		</form>
	</div>
	
[/@layout.reportsLeftPaneLayout]