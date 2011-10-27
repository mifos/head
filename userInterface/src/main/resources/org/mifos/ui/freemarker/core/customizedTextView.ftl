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
[@adminLeftPaneLayout]
<!--  Main Content Begins-->
<div class="customized-text content">
	<span id="page.id" title="customizeTextView"></span>
	[@widget.flowCrumbs breadcrumbs/]
	        
	<form action="${flowExecutionUrl}" method="post" class="two-column">
		<div class="fontBold"><span class="orangeheading">[@spring.message "customizeTextView.customizedTextList"/]</span></div>
		<div>[@spring.message "customizeTextView.instructions" /]</div>
		&nbsp;
		[@form.errors "customizedTextSelectFormBean.*"/]  
		<center>
	    	<div class="row">	
	    	[@form.singleSelectWithPrompt path="customizedTextSelectFormBean.message" id="customizeTextView.select.customizedText" options=customizedTextMap selectPrompt="" attributes="size=12, style=width:330px;"/]
	    	</div>
	    	<div class="row">
		        [@form.submitButton label="widget.form.buttonLabel.add" id="customizeTextView.button.add" webflowEvent="add" /]
		        [@form.submitButton label="widget.form.buttonLabel.edit" id="customizeTextView.button.edit" webflowEvent="edit" /]                
		        [@form.submitButton label="widget.form.buttonLabel.remove" id="customizeTextView.button.remove" webflowEvent="remove" /]
		        [@form.cancelButton label="widget.form.buttonLabel.done" id="customizeTextView.button.done" webflowEvent="done"  /]
		    </div>
    	</center>
	</form>
</div>
<!--Main content ends-->
[/@adminLeftPaneLayout]