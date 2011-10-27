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
    <script src="pages/questionnaire/js/selectQuestionnaire.js" type="text/javascript"></script>
    <span id="page.id" title="selectQuestionnaire"></span>
 <div class="content">
    [#if Session.urlMap??]
        [#assign thisSite][@spring.message "questionnaire.attach"/][/#assign]
        [#assign breadcrumb = Session.urlMap + {"${thisSite}":""} /]
        [@widget.crumbpairs breadcrumb /]
    [/#if]
    <div class="content_panel">
        <h1>
            <span class="black">${Session.questionnaireFor} -</span> [@spring.message "questionnaire.attach"/]
        </h1>
        <div class="marginTop15">
            [@spring.message "questionnaire.instructions"/]
        </div>
        <form name="selectQuestionGroup" action="questionnaire.ftl?execution=${flowExecutionKey}" method="POST">
            <fieldset>
                <ol>
                    <li>
                        <label for="questionGroupId"><span class="red">*</span>[@spring.message
                            "questionnaire.selectquestionnaire"/]:</label>
                        <select id="questionGroupId" name="questionGroupId">
                            <option value="selectOne">--[@spring.message "questionnaire.selectone"/]--</option>
                            [#list questionGroupDetails.details as questionGroup]
                            <option value="${questionGroup_index}">${questionGroup.title}</option>
                            [/#list]
                        </select>
                    </li>
                    <li class="buttonWidth">
                        <input type="submit" id="_eventId_selectQuestionnaire" name="_eventId_selectQuestionnaire"
                               value="[@spring.message "questionnaire.continue"/]" class="buttn"/>
                        &nbsp;
                        <input type="submit" id="_eventId_cancel" name="_eventId_cancel"
                               value="[@spring.message "questionnaire.cancel"/]" class="cancelbuttn"/>
                    </li>
                </ol>
            </fieldset>
        </form>
    </div>
 </div>
[/@adminLeftPaneLayout]