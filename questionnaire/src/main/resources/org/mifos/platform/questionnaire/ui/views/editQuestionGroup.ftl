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
    <script src="pages/questionnaire/js/createQuestionGroup.js" type="text/javascript"></script>
    <script src="pages/questionnaire/js/createQuestion.js" type="text/javascript"></script>
    <script src="pages/questionnaire/js/editQuestionGroup.js" type="text/javascript"></script>
    <span id="page.id" title="editQuestionGroup"></span>
    <div class="content">
    [#assign breadcrumb = {"admin":"AdminAction.do?method=load", "questionnaire.view.question.groups":"viewQuestionGroups.ftl", "questionnaire.editQuestionGroup":""}/]
    [@widget.crumbpairs breadcrumb/]
        <div class="content_panel">
            <h1>
                [@spring.message "questionnaire.editQuestionGroup"/]
            </h1>
            [#include "questionGroupDefinition.ftl"/]
        </div>
    </div>
[/@adminLeftPaneLayout]