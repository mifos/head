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
<span id="page.id" title="createQuestionGroup"></span>
    [#assign breadcrumb = {"admin":"AdminAction.do?method=load", "questionnaire.addQuestionGroup":""}/]
<div class=" content">
[@mifos.crumbpairs breadcrumb/]
    <div class="content_panel">
        <p class="font15 orangeheading">
        [@spring.message "questionnaire.addQuestionGroup"/]
        </p>
        [#include "questionGroupDefinition.ftl"/]
    </div>
</div>
[/@adminLeftPaneLayout]
