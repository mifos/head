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
<span id="page.id" title="view_question_groups"></span>
<div class=" content">
    [#assign breadcrumb = {"admin":"AdminAction.do?method=load", "questionnaire.view.question.groups":""}/]
[@widget.crumbpairs breadcrumb/]
    <div class="content_panel">
        <p class="font15 orangeheading">
        [@spring.message "questionnaire.view.question.groups"/]
        </p>

        <p>
        [@spring.message "questionnaire.create.question.group.prompt"/]&nbsp;
        [@i18n.mlink dest="createQuestionGroup.ftl"][@spring.message "questionnaire.create.question.group.link"/][/@i18n.mlink]
        </p>

        <div id="questionGroupList">
            [#assign eventSources = questionGroups?keys]
            [#list eventSources as eventSource]

                <span class="fontnormalbold">${eventSource}</span>
                <ul class="questions">
                    [#list questionGroups[eventSource] as questionGroup]
                        <li>
                            <a href="viewAndEditQuestionGroup.ftl?questionGroupId=${questionGroup.id}"
                               id="questionGroupId_${questionGroup.id}">${questionGroup.title}</a>
                            [#if questionGroup.active == false]&nbsp;<img
                                    src="pages/framework/images/status_closedblack.gif" width="8" height="9">&nbsp;inactive[/#if]
                        </li>
                    [/#list]
                </ul>
            [/#list]
        </div>
    </div>
</div>
[/@adminLeftPaneLayout]
