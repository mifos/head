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
[@mifos.topNavigationNoSecurity currentTab="ClientsAndAccounts" /]
<STYLE TYPE="text/css"><!-- @import url(pages/questionnaire/css/questionnaire.css); --></STYLE>
<div class="sidebar ht950">
    [#include "ClientLeftPane.ftl" /]
</div>
<form action="${backPageUrl}" id="displayResponsesForm" name="displayResponsesForm" method="post">
    <div class="content leftMargin180">
        <span id="page.id" title="display_question_group_reponses"></span>
        [#assign breadcrumb = Session.urlMap/]
        [@mifos.crumbpairs breadcrumb "false"/]
        <div class=" fontnormal marginLeft30">
            <div class="orangeheading marginTop15">
                [@spring.message "questionnaire.view.question.group.responses"/]
            </div>
            <div id="questionGroupList" class="marginTop15">
                [#list questionGroupInstanceDetails as questionGroupInstanceDetail]
                <fieldset id="questionGroup.sections" class="bluetableborderFull">
                    [#list questionGroupInstanceDetail.questionGroupDetail.sectionDetails as sectionDetail]
                    <br/>
                    <span class="paddingleft10 fontnormalbold">${sectionDetail.name}</span>
                    <ol>
                        [#list sectionDetail.questions as sectionQuestionDetail]
                        <li>
                            <label>[#if sectionQuestionDetail.mandatory]<span class="red">*</span>[/#if]
                            ${sectionQuestionDetail.title}:</label>
                            <label class="rightCol"><span class="fontnormal">${sectionQuestionDetail.answer}</span></label>
                        </li>
                        [/#list]
                        <br/>
                    </ol>
                    [/#list]
                </fieldset>
                [/#list]
            </div>
            <div class="buttonWidth">
                <input id="backToDetailsPage" name="backToDetailsPage" type="submit" class="buttn" value="[@spring.message "questionnaire.back.to.details"/]"/>
            </div>
        </div>
    </div>
</form>
[@mifos.footer/]
