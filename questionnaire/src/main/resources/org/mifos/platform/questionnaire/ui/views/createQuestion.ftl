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
<STYLE TYPE="text/css"><!-- @import url(pages/questionnaire/css/questionnaire.css); --></STYLE>
<script src="pages/questionnaire/js/createQuestion.js" type="text/javascript"></script>
<span id="page.id" title="createQuestion"></span>
<div class=" content">
    [#assign breadcrumb = {"admin":"AdminAction.do?method=load", "questionnaire.add.questions":""}/]
[@mifos.crumbpairs breadcrumb/]
    <div class="content_panel">
        <p class="font15 orangeheading">
        [@spring.message "questionnaire.add.questions"/]
        </p>
        [@mifosmacros.showAllErrors "questionDefinition.*"/]
        <form name="createquestionform" action="createQuestion.ftl?execution=${flowExecutionKey}" method="POST"
              focus="currentQuestion.text">
            <input type="submit" id="_eventId_removeQuestion" name="_eventId_removeQuestion" value=""
                   style="visibility:hidden"/>

            <div class="create_question">
                [#include "questionDefinition.ftl"]
                <div class="add_question">
                    <div class="button_container">
                        <input type="submit" name="_eventId_addQuestion" value="[@spring.message "questionnaire.add.question" /]" class="buttn"
                               id="_eventId_addQuestion">
                    </div>
                </div>
                [#include "questionListing.ftl"]
                <div class="button_footer">
                    <div class="button_container">
                        <input type="submit" id="_eventId_createQuestions" name="_eventId_createQuestions"
                               value="[@spring.message "questionnaire.submit"/]" class="buttn"/>
                        &nbsp;
                        <input type="submit" id="_eventId_cancel" name="_eventId_cancel"
                               value="[@spring.message "questionnaire.cancel"/]" class="cancelbuttn"/>
                    </div>
                </div>
            </div>
        </form>
    </div>
</div>
[/@adminLeftPaneLayout]
