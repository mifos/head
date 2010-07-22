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
    <span id="page.id" title="view_question_group_reponses"/></span>
    <div class=" fontnormal marginLeft30">
        <div class="orangeheading marginTop15">
            [@spring.message "questionnaire.view.question.group.responses"/]
        </div>
            <div id="questionGroupList" class="marginTop15">
                [#list questionGroupDetails as questionGroupDetail]
                <table class="bluetableborderFull" cellspacing="0" cellpadding="0" border="0" align="center" width="90%">
                    <tr>
                        <td>
                            <div id="questionGroup.sections" class="marginTop15">
                                [#list questionGroupDetail.sectionDetails as sectionDetail]
                                <b>${sectionDetail.name}</b><br/>
                                [#list sectionDetail.questions as sectionQuestionDetail]
                                <div>
                                    [#if sectionQuestionDetail.mandatory]
                                    <font color="#FF0000">*</font>
                                    [/#if]
                                    ${sectionQuestionDetail.title}:<span class="fontnormal">${sectionQuestionDetail.value?default("")}</span>
                                </div>
                                [/#list]
                                [/#list]
                            </div>
                        </td>
                    </tr>
                </table>
                [/#list]
            </div>
    </div>
</div>
[@mifos.footer/]
