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
  <div class=" content">
  <span id="page.id" title="customizeMessages"></span>
    [@mifos.crumbs breadcrumbs/]
    
<form action="${flowExecutionUrl}" method="post" class="one-column">
<div class="fontBold"><span class="orangeheading">[@spring.message "customMessagesView.messageList"/]</span></div>

    [@form.singleSelectWithPrompt path="customMessageSelectFormBean.message" options=customMessagesMap selectPrompt="" attributes="size=10, style=width:200px;"/]
    
<!--
	<select size=10>
[#list customMessagesMap?keys as key]
    <option value="${key}">${key} > ${customMessagesMap[key]}</option>
[/#list]
	</select>
-->
    <div class="row">
        [@form.submitButton label="widget.form.buttonLabel.add" webflowEvent="add" /]
        [@form.submitButton label="widget.form.buttonLabel.remove" webflowEvent="remove" /]
        [@form.submitButton label="widget.form.buttonLabel.edit" webflowEvent="edit" /]                
        [@form.cancelButton label="widget.form.buttonLabel.done" webflowEvent="done"  /]
    </div>
</form>
  </div>
  <!--Main content ends-->
  [/@adminLeftPaneLayout]