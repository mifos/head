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
<STYLE TYPE="text/css"><!--
@import url(pages/questionnaire/css/questionnaire.css);
--></STYLE>
<span id="page.id" title="uploadQuestionGroup"></span>
<div class="content">
    [#assign breadcrumb = {"admin":"AdminAction.do?method=load", "questionnaire.uploadQuestionGroup":""}/]
[@widget.crumbpairs breadcrumb/]
    <div class="content_panel">

        <h1>
        [@spring.message "questionnaire.uploadQuestionGroup"/]
        </h1>
        [@form.showAllErrors "uploadQuestionGroupForm.*"/]
        <div class="fontnormal">
        [@spring.message "questionnaire.uploadQuestionGroupMessage"/]
        </div>
        <form name="uploadQuestionGroupForm" action="uploadQuestionGroup.ftl?execution=${flowExecutionKey}"
              method="POST">
            <fieldset>
                <ol>
                    <li>
                        <label for="selectedCountry"><span class="red">*</span>[@spring.message
                        "questionnaire.select.questionGroup"/]:</label>
                    [@form.formSingleSelectWithPrompt "uploadQuestionGroupForm.selectedCountry", uploadQuestionGroupForm.countries,
                    "--selectone--" /]
                    </li>
                </ol>
            </fieldset>
            <div class="marginLeft12em">
                <input type="submit" name="_eventId_uploadQuestionGroupContinue"
                       id="_eventId_uploadQuestionGroupContinue" value='[@spring.message "questionnaire.submit"/]'
                       class="buttn"/>
                &nbsp;
                <input type="submit" name="_eventId_cancel" id="_eventId_cancel"
                       value='[@spring.message "questionnaire.cancel"/]' class="cancelbuttn"/>
            </div>
        </form>
    </div>
</div>
[/@adminLeftPaneLayout]
