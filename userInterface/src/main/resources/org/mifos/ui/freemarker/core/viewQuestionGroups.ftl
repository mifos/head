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
[#import "spring.ftl" as spring]
[#import "blueprintmacros.ftl" as mifos]
[@mifos.header "title" /]
[@mifos.topNavigationNoSecurity currentTab="Admin" /]
<div class="sidebar ht950">
    [#include "adminLeftPane.ftl" /]
</div>
<div class="content leftMargin180">
    <span id="page.id" title="view_question_groups"/></span>

    [#assign breadcrumb = {"admin":"AdminAction.do?method=load", "questionnaire.view.question.groups":""}/]
    [@mifos.crumbpairs breadcrumb/]

    <div class=" fontnormal marginLeft30">
        <div class="orangeheading marginTop15">
            [@spring.message "questionnaire.view.question.groups"/]
        </div>
        <div class="marginTop15">
            [@spring.message "questionnaire.create.question.group.prompt"/]
            <a href="createQuestionGroup.ftl">[@spring.message "questionnaire.create.question.group.link"/]</a>
        </div>
        <div class="marginTop15">
            [#list questionGroups as questionGroup]
                <img src="pages/framework/images/bullet_circle.gif" width="9" height="11"/>
                <a href="viewQuestionGroupDetail.ftl?questionGroupId=${questionGroup.id}" id="questionGroupId_${questionGroup.id}">${questionGroup.title}<br/></a>
            [/#list]
        </div>
    </div>
</div>
[@mifos.footer/]