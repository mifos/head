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
	
	<script type="text/javascript" src="pages/js/jquery/jquery.regexSelector.js"></script>
	<script type="text/javascript">

	function addOption(root, text, value)
	{
	  var newOpt = new Option(text, value);
	  var rootLength = root.length;
	  root.options[rootLength] = newOpt;
	}
	
	function deleteOption(root, index)
	{
	  var rootLength= root.length;
	  if(rootLength>0)
	  {
	    root.options[index] = null;
	  }
	}

	function moveOptions(rootId, destinationId)
	{
	  rootId = rootId.replace(/([\[\]\.])/g, "\\$1");
	  destinationId = destinationId.replace(/([\[\]\.])/g, "\\$1");

	  var root = $(rootId).get(0);
	  var destination = $(destinationId).get(0);	
	
	  var rootLength= root.length;
	  var rootText = new Array();
	  var rootValues = new Array();
	  var rootCount = 0;
	
	  var i;
	  for(i=rootLength-1; i>=0; i--)
	  {
	    if(root.options[i].selected)
	    {
	      rootText[rootCount] = root.options[i].text;
	      rootValues[rootCount] = root.options[i].value;
	      deleteOption(root, i);
	      rootCount++;
	    }
	  }
	  for(i=rootCount-1; i>=0; i--)
	  {
	    addOption(destination, rootText[i], rootValues[i]);
	  }
	}
	
	function selectAllOptions()
	{
	    $(":regex(id, reportMultiSelectParams\\[[0-9]+\\]\\.selectedValues)").each(function() {
	        var selLength = this.length;
	        this.multiple=true;
	        for(i=selLength-1; i>=0; i--)
	        {
	            this.options[i].selected=true;
	        }
	    });
	}
	
	function updateDropdown(form){ 			 		
		 form.action="viewPentahoReport.ftl"; 			
		 form.submit(); 		
	}
	</script>
	
	[@widget.crumbs breadcrumbs /]
	
	<span id="page.id" title="PentahoReport"></span>
	[@i18n.formattingInfo /]
	
	<div class="content">
		<br />
		<h1>${reportName}</h1>
		
		<p><span class="mandatory">*</span>[@spring.message "accounts.asterisk" /]</p>
		<br />
		
		[@form.errors "pentahoReportFormBean.*"/]
		<form action="execPentahoReport.ftl" method="post" class="two-columns">
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
	    		 	<label for="${param.paramName}_Date">
	    		 	[@form.input path="${item}.labelName" id="input.labelName" fieldType="hidden"/]
	    		 	    [#if param.mandatory == true]<span class="mandatory">*</span>[/#if]${param.labelName}:
	    		 	</label>
			        <script>
$(document).ready(function() {
	$.datepicker.setDefaults($.datepicker.regional[""]);
    $("#${param.paramName}_Date").datepicker({	
        showOn: "button",
        buttonImage: "pages/framework/images/mainbox/calendaricon.gif",
		buttonImageOnly: true,
		dateFormat: 'dd/mm/yy'
    });
  }
);
</script>
			        [@form.input path="${item}.date" id="${param.paramName}_Date" /]
			        </div>
		    [/#list]
		    
		    [#list pentahoReportFormBean.reportInputParams as param]
		    	[#assign item]pentahoReportFormBean.reportInputParams[${param_index}][/#assign]
	    		<div class="row">
	    		 	[@form.input path="${item}.paramName" id="${param.paramName}_paramName" fieldType="hidden" /]
	    		 	[@form.input path="${item}.mandatory" id="${param.paramName}_mandatory," fieldType="hidden" /]
	    		 	<label for="${param.paramName}_value">
	    		 	[@form.input path="${item}.labelName" id="input.labelName" fieldType="hidden"/]
	    		 	    [#if param.mandatory == true]<span class="mandatory">*</span>[/#if]${param.labelName}:
	    		 	</label>
			        [@form.input path="${item}.value" id="${param.paramName}_value" /]
	    		</div>
		    [/#list]
		    
		    [#list pentahoReportFormBean.reportSingleSelectParams as param]
		    	[#assign item]pentahoReportFormBean.reportSingleSelectParams[${param_index}][/#assign]
	    		<div class="row">
	    		 	[@form.input path="${item}.paramName" id="${param.paramName}_paramName" fieldType="hidden" /]
	    		 	[@form.input path="${item}.mandatory" id="${param.paramName}_mandatory," fieldType="hidden" /]
	    		 	<label for="${param.paramName}_slectedValue">
	    		 	[@form.input path="${item}.labelName" id="input.labelName" fieldType="hidden"/]
	    		 	    [#if param.mandatory == true]<span class="mandatory">*</span>[/#if]${param.labelName}:
	    		 	</label>
			        [@form.singleSelectWithPrompt path="${item}.selectedValue" id="${param.paramName}_selectedValue" 
			        	options=param.possibleValues attributes="onchange=updateDropdown(this.form)"/]
	    		</div>
		    [/#list]
		    
		    [#list pentahoReportFormBean.reportMultiSelectParams as param]
		    	[#assign item]pentahoReportFormBean.reportMultiSelectParams[${param_index}][/#assign]
	    		<div class="row">
	    		 	[@form.input path="${item}.paramName" id="${param.paramName}_paramName" fieldType="hidden" /]
	    		 	[@form.input path="${item}.mandatory" id="${param.paramName}_mandatory," fieldType="hidden" /]
	    		 	<div> 
                        <label for="${item}.possibleValues">
                        [@form.input path="${item}.labelName" id="input.labelName" fieldType="hidden"/]
                        	[#if param.mandatory == true]<span class="mandatory">*</span>[/#if]${param.labelName}:
                        </label>
                    </div>
                    <div style="display: inline-block; vertical-align: top"> 
                        [@spring.formMultiSelect "${item}.possibleValues", param.possibleValuesOptions, "class=listSize" /]
                    </div>
                    <div style="display: inline-block; vertical-align: top"> 
                        <br />
                        <input class="buttn2" name="add" style="width:80px;" type="button" id="roles.button.add"  value="[@spring.message "add"/] >>" 
                        	onclick="moveOptions('#reportMultiSelectParams[${param_index}].possibleValues', '#reportMultiSelectParams[${param_index}].selectedValues');" />
                       	<br />
                        <input class="buttn2" name="remove" type="button" style="width:80px;" value="<< [@spring.message "remove"/]" 
                        	onclick="moveOptions('#reportMultiSelectParams[${param_index}].selectedValues', '#reportMultiSelectParams[${param_index}].possibleValues');" />
                    </div>
                    <div style="display: inline-block; vertical-align: top">     
                        [@spring.formMultiSelect "${item}.selectedValues", param.selectedValuesOptions, "class=listSize" /]
                    </div>
	    		</div>
		    [/#list]
		    </br>
		    <div class="row">
		    	[@form.submitButton label="widget.form.buttonLabel.submit" id="input.submit" attributes="onClick='selectAllOptions();'" /]
		    	<input id="input.cancel" type="submit" class="cancel" value="[@spring.message "widget.form.buttonLabel.cancel" /]" name="CANCEL" />
		    </div>
		    [#if Session.isDW == "true"]
		    <div class="row">
		    	</br>
		    	[#if Session.dwNotRun == "true"]
		    		<span>[@spring.message "etlDoesntRun"/] </span>
		    	[#else]
		    		<span>[@spring.message "lastSuccessfullRunETL"/] ${pentahoReportFormBean.etlLastUpdate?datetime} </span>
		    	[/#if]
		    </div>
		    [/#if]
		</form>
	</div>
	
[/@layout.reportsLeftPaneLayout]