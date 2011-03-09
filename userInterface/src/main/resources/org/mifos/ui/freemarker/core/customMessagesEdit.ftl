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

<form action="${flowExecutionUrl}" method="post" class="two-columns">
    <fieldset>
    <div class="row">
        [@form.label "Old Message" true /][@spring.message "customMessagesAdd.addMessage.oldMessage"/]
        [@form.input path="customMessageFormBean.oldMessage"  id="oldMessage" /]
    </div>
    <div class="row">
        [@form.label "New Message" true /][@spring.message "customMessagesAdd.addMessage.newMessage"/]
        [@form.input path="customMessageFormBean.newMessage" id="newMessage" /]
    </div>
    </fieldset>
    <div class="row webflow-controls">
        [@form.submitButton label="widget.form.buttonLabel.submit" id="addcustommessage.button.submit" webflowEvent="submit" /]
        [@form.cancelButton label="widget.form.buttonLabel.cancel" webflowEvent="cancel" /]
    </div>
</form>

  </div>
  <!--Main content ends-->
  [/@adminLeftPaneLayout]