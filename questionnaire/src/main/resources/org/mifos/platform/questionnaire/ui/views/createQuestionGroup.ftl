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
[#import "macros.ftl" as mifosmacros]
[@mifos.header "title" /]
[@mifos.topNavigationNoSecurity currentTab="Admin" /]
<STYLE TYPE="text/css"><!-- @import url(pages/questionnaire/css/questionnaire.css); --></STYLE>
<script src="pages/questionnaire/js/createQuestionGroup.js" type="text/javascript"></script>
<div class="sidebar ht950">
    [#include "adminLeftPane.ftl" /]
</div>
<div class="content leftMargin180">
    <span id="page.id" title="createQuestionGroup"/></span>

    [#assign breadcrumb = {"admin":"AdminAction.do?method=load", "questionnaire.addQuestionGroup":""}/]
    [@mifos.crumbpairs breadcrumb/]
    <div class=" fontnormal marginLeft30">
        <div class="orangeheading marginTop15">
            [@spring.message "questionnaire.addQuestionGroup"/]
        </div>
        <div class="allErrorsDiv">
            [@mifosmacros.showAllErrors "questionGroupForm.*"/]
        </div>
        <form name="createquestiongroupform"
              action="createQuestionGroup.ftl?execution=${flowExecutionKey}" method="POST">
            <fieldset>
                <ol>
                    <li>
                        <label for="title"><span class="red">*</span>[@spring.message
                            "questionnaire.questionGroupTitle"/]:</label>
                        [@spring.formInput "questionGroupForm.title",
                        'maxlength="50"
                        onkeypress="return FnCheckNumCharsOnPress(event,this);"
                        onblur="return FnCheckNumChars(event,this);return FnEscape(event,this)"'/]
                    </li>
                    <li>
                        <label for="eventSourceId"><span class="red">*</span>[@spring.message
                            "questionnaire.questionGroupAppliesTo"/]:</label>
                        [@mifosmacros.formSingleSelectWithPrompt "questionGroupForm.eventSourceId", EventSources,
                        "--select one--" /]
                    </li>
                    <li>
                        <label for="editable">[@spring.message "questionnaire.editable"/]:</label>
                        [@mifosmacros.formCheckbox "questionGroupForm.editable", ""/]
                    </li>
                    <li>
                        <label for="sectionName">[@spring.message "questionnaire.currentSectionTitle"/]:</label>
                        [@spring.formInput "questionGroupForm.sectionName",
                        'maxlength="50"
                        onkeypress="return FnCheckNumCharsOnPress(event,this);"
                        onblur="return FnCheckNumChars(event,this);return FnEscape(event,this)"'/]
                    </li>
                    <li style="padding-bottom: 0pt;">
                        <label for="txtListSearch"><span class="red">*</span>[@spring.message "questionnaire.select.questions"/]:</label>
                        <input type="text" autocomplete="off" id="txtListSearch" name="txtListSearch" style="width:21em;"/>
                    </li>
                    <li style="padding-top: 0pt;">
                        <label for="questionList">&nbsp;</label>
                        <ol class="questionList" id="questionList" style="overflow:auto; width:19em; height:180px; border:1px solid #336699; padding-left:5px">
                            [#list questionGroupForm.questionPool as sectionQuestion]
                            <li style="padding-bottom: 0pt;">
                               <input type="checkbox" id="${sectionQuestion.questionId?c}" name="selectedQuestionIds" value="${sectionQuestion.questionId?c}"/>
                                &nbsp;<label style="float:none;" for="${sectionQuestion.questionId?c}">${sectionQuestion.title}</label>
                            </li>
                            [/#list]
                        </ol>
                    </li>
                </ol>
            </fieldset>
            <div class="marginLeft15em">
                    <input type="submit" name="_eventId_addSection" id="_eventId_addSection" value='[@spring.message "questionnaire.add.questions"/]' class="buttn"/>
            </div>
            <div id="divSections">
                [#assign reversedSections=questionGroupForm.sections?reverse]
                [#list reversedSections as section]
                <b>${section.name}:&nbsp;&nbsp;</b>
                <a href="javascript:CreateQuestionGroup.removeSection('${section.name}')">[@spring.message
                    "questionnaire.remove.link"/]</a>
                <br/>
                <table id="sections.table" name="sections.table">
                    <tr>
                        <td class="drawtablehd" width="50%">[@spring.message "questionnaire.question.name"/]</td>
                        <td class="drawtablehd" width="20%" style="text-align:center">[@spring.message "questionnaire.question.mandatory"/]</td>
                        <td class="drawtablehd" width="30%">[@spring.message "questionnaire.question.delete"/]</td>
                    </tr>
                    [#list section.sectionQuestions as sectionQuestion]
                    <tr>
                        <td class="drawtablerow">${sectionQuestion.title}</td>
                        <td align="center" valign="center" class="drawtablerow" style="text-align:center">
                            [@mifosmacros.formCheckbox "questionGroupForm.sections[${reversedSections?size - section_index - 1}].sectionQuestions[${sectionQuestion_index}].mandatory", ""/]
                        </td>
                        <td class="drawtablerow"><a href="javascript:CreateQuestionGroup.removeQuestion('${section.name}','${sectionQuestion.questionId}')">[@spring.message "questionnaire.remove.link"/]</a></td>
                    </tr>
                    [/#list]
                    <tr>
                        <td class="drawtablerow">&nbsp;</td>
                        <td class="drawtablerow">&nbsp;</td>
                        <td class="drawtablerow">&nbsp;</td>
                    </tr>
                </table>
                [/#list]
                <input type="submit" id="_eventId_deleteSection" name="_eventId_deleteSection" value="" style="visibility:hidden"/>
                <input type="submit" id="_eventId_deleteQuestion" name="_eventId_deleteQuestion" value="" style="visibility:hidden"/>
                <input type="hidden" id="questionSection" name="questionSection" value=""/>
            </div>

            <div class="marginLeft12em">
                 <input type="submit" name="_eventId_defineQuestionGroup" id="_eventId_defineQuestionGroup" value='[@spring.message "submit"/]' class="buttn"/>
                 &nbsp;
                 <input type="submit" name="_eventId_cancel" id="_eventId_cancel" value='[@spring.message "cancel"/]' class="cancelbuttn"/>
            </div>
        </form>
    </div>
</div>
[@mifos.footer/]


