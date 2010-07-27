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
[#import "macros.ftl" as mifosMacros]

[@mifos.header "title" /]
[@mifos.topNavigationNoSecurity currentTab="Admin" /]
<STYLE TYPE="text/css"><!-- @import url(pages/questionnaire/css/questionnaire.css); --></STYLE>
<script src="pages/questionnaire/js/selectQuestionGroup.js" type="text/javascript"></script>
<div class="sidebar ht950">
    [#include "adminLeftPane.ftl" /]
</div>
<div class="content leftMargin180">
    <span id="page.id" title="selectSurvey"></span>
    [#assign breadcrumb = Session.urlMap/]
    [@mifos.crumbpairs breadcrumb "false"/]
    <div class="marginLeft30">
        <div class="orangeheading marginTop15">
            ${Session.surveyFor} - [@spring.message "questionnaire.selectsurvey.attachsurvey"/]
        </div>
        <div class="marginTop15">
            [@spring.message "questionnaire.selectsurvey.instructions"/]
        </div>
        <form name="selectQuestionGroup" action="selectSurvey.ftl?execution=${flowExecutionKey}" method="POST">
            <fieldset>
                <ol>
                    <li>
                        <label for="questionGroupId"><span class="red">*</span>[@spring.message
                            "questionnaire.selectsurvey"/]:</label>
                        <select id="questionGroupId" name="questionGroupId">
                            <option value="selectOne">--[@spring.message "questionnaire.selectone"/]--</option>
                            [#list questionGroupDetails.details as questionGroup]
                            <option value="${questionGroup_index}">${questionGroup.title}</option>
                            [/#list]
                        </select>
                    </li>
                    <li class="buttonWidth">
                        <input type="submit" id="_eventId_selectSurvey" name="_eventId_selectSurvey"
                               value="[@spring.message "questionnaire.submit"/]" class="buttn"/>
                        &nbsp;
                        <input type="submit" id="_eventId_cancel" name="_eventId_cancel"
                               value="[@spring.message "questionnaire.canecl"/]" class="cancelbuttn"/>
                    </li>
                </ol>
            </fieldset>
        </form>
    </div>
</div>
[@mifos.footer/]