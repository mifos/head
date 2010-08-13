[#ftl]
[#--
* Copyright (c) 2005-2010 Grameen Foundation USA
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
    <script type="text/javascript" src="pages/js/jquery/jquery.keyfilter-1.7.js"></script>
    <script src="pages/questionnaire/js/createQuestion.js" type="text/javascript"></script>
    <span id="page.id" title="createQuestion"/>
    [#assign breadcrumb = {"admin":"AdminAction.do?method=load", "questionnaire.add.questions":""}/]
    [@mifos.crumbpairs breadcrumb/]
    <div class="content_panel">
        <h1>
            [@spring.message "questionnaire.add.questions"/]
        </h1>
        <div id="allErrorsDiv" class="allErrorsDiv">
            [@mifosmacros.showAllErrors "questionDefinition.*"/]
        </div>
        <form name="createquestionform" action="createQuestion.ftl?execution=${flowExecutionKey}" method="POST" focus="currentQuestion.title">
            <input type="submit" id="_eventId_removeQuestion" name="_eventId_removeQuestion" value="" style="visibility:hidden"/>
            <fieldset>
             <ol>
                [#include "questionDefinition.ftl"]
                <li class="buttonWidth">
                    <input type="submit" name="_eventId_addQuestion" value="Add Question" class="buttn" id="_eventId_addQuestion">
                </li>
                [#include "questionListing.ftl"]
                <li class="buttonWidth">
                    <input type="submit" id="_eventId_createQuestions" name="_eventId_createQuestions" value="[@spring.message "questionnaire.submit"/]" class="buttn"/>
                    &nbsp;
                    <input type="submit" id="_eventId_cancel" name="_eventId_cancel" value="[@spring.message "questionnaire.cancel"/]" class="cancelbuttn"/>
                </li>
             </ol>
            </fieldset>
        </form>
    </div>
[/@adminLeftPaneLayout]