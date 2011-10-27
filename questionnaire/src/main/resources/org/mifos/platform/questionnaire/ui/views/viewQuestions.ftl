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
<span id="page.id" title="view_questions"></span>
<div class=" content">
    [#assign breadcrumb = {"admin":"AdminAction.do?method=load", "questionnaire.view.questions":""}/]
[@widget.crumbpairs breadcrumb/]
    <div class="content_panel">
        <p class="font15 orangeheading margin5top10bottom">
        [@spring.message "questionnaire.view.questions"/]
        </p>

        <p>
        [@spring.message "questionnaire.create.question.prompt"/]&nbsp;
        [@i18n.mlink dest="createQuestion.ftl"][@spring.message "questionnaire.create.question.link"/][/@i18n.mlink]
        </p>
        <ul class="questions">
            [#list questions as question]
                <li>
                    <a href="viewAndEditQuestion.ftl?questionId=${question.id}"
                       id="questionId_${question.id}">${question.text}</a>
                    [#if question.active == false]&nbsp;<img
                        src="pages/framework/images/status_closedblack.gif" width="8" height="9">&nbsp;inactive[/#if]
                </li>
            [/#list]
        </ul>
    </div>
</div>
[/@adminLeftPaneLayout]
