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

[#assign freeText][@spring.message "questionnaire.quesiton.choices.freetext"/][/#assign]
[#assign date][@spring.message "questionnaire.quesiton.choices.date"/][/#assign]
[#assign number][@spring.message "questionnaire.quesiton.choices.number"/][/#assign]
[#assign multiSelect][@spring.message "questionnaire.quesiton.choices.multiselect"/][/#assign]
[#assign singleSelect][@spring.message "questionnaire.quesiton.choices.singleselect"/][/#assign]

[@mifos.header "title" /]
[@mifos.topNavigationNoSecurity currentTab="Admin" /]
<STYLE TYPE="text/css"><!-- @import url(pages/questionnaire/css/questionnaire.css); --></STYLE>
<script type="text/javascript" src="pages/questionnaire/js/jquery.keyfilter-1.7.js"></script>
<script src="pages/questionnaire/js/createQuestion.js" type="text/javascript"></script>
<div class="sidebar ht950">
    [#include "adminLeftPane.ftl" /]
</div>
<div class="content leftMargin180">
    [#assign breadcrumb = {"admin":"AdminAction.do?method=load", "questionnaire.add.questions":""}/]
    [@mifos.crumbpairs breadcrumb/]

    <div class="fontnormal marginLeft30">
        <div class="orangeheading marginTop15">
            [@spring.message "questionnaire.add.questions"/]
        </div>
        <div id="allErrorsDiv" class="allErrorsDiv">
            [@mifosMacros.showAllErrors "questionDefinition.*"/]
        </div>
        <form name="createquestionform" action="createQuestion.ftl?execution=${flowExecutionKey}" method="POST" focus="currentQuestion.title">
            <input type="hidden" id="number" name="number" value="${number}"/>
            <input type="hidden" id="multiSelect" name="multiSelect" value="${multiSelect}"/>
            <input type="hidden" id="singleSelect" name="singleSelect" value="${singleSelect}" />
            <input type="submit" id="_eventId_removeQuestion" name="_eventId_removeQuestion" value="" style="visibility:hidden"/>
            <input type="submit" id="_eventId_removeChoice" name="_eventId_removeChoice" value="" style="visibility:hidden"/>
            <fieldset>
			 <ol>
			    <li>
				  <label for="currentQuestion.title"><span class="red">*</span>[@spring.message "questionnaire.question.title"/]: </label>
				  [@spring.formInput "questionDefinition.currentQuestion.title", 'maxlength="50"' /]
			    </li>
			    <li>
				  <label for="currentQuestion.type"><span class="red">*</span>[@spring.message "questionnaire.answer.type"/]: </label>
				  [@spring.formSingleSelect "questionDefinition.currentQuestion.type", [freeText, date, number, multiSelect, singleSelect], ''/]
			    </li>
                <li>
                    <div id="numericDiv">
                        <label for="currentQuestion.numericMin">[@spring.message "questionnaire.quesiton.numeric.min"/]: </label>
                        [@spring.formInput "questionDefinition.currentQuestion.numericMin", 'maxlength="5" class="numeric"'/]
                        <br>
                        <label for="currentQuestion.numericMax">[@spring.message "questionnaire.quesiton.numeric.max"/]: </label>
                        [@spring.formInput "questionDefinition.currentQuestion.numericMax", 'maxlength="5" class="numeric"'/]
                    </div>
                </li>
			    <li>
                    <div id="choiceDiv">
                        <label for="currentQuestion.currentChoice"><span class="red">*</span>[@spring.message "questionnaire.quesiton.choice"/]: </label>
                        [@spring.formInput "questionDefinition.currentQuestion.currentChoice", 'maxlength="200"'/]
                        <input type="submit" id="_eventId_addChoice" name="_eventId_addChoice" class="buttn"
                               value="[@spring.message "questionnaire.quesiton.add"/] >>">
                        <fieldset>
                            <ol class="choiceOlStyle">
                                <li class="choiceHeaderStyle">
                                    <span class="choiceStyle">[@spring.message "questionnaire.choice"/]</span>Remove
                                </li>
                                [#list questionDefinition.currentQuestion.choices as choice]
                                <li>
                                    <span class="choiceStyle">${choice}&nbsp;</span>
                                    <a href="removeChoice#" choiceIndex="${choice_index}">[@spring.message "questionnaire.remove.link"/]</a>
                                </li>
                                [/#list]
                            </ol>
                        </fieldset>
                    </div>
			    </li>
                <li class="buttonWidth">
                    <input type="submit" name="_eventId_addQuestion" value="Add Question" class="buttn" id="_eventId_addQuestion">
                </li>
                <li>
                    <table id="questions.table" name="questions.table">
                      <tr>
                        <td class="drawtablehd" style="width:35%">[@spring.message "questionnaire.question.title"/]</td>
                        <td class="drawtablehd" style="width:10%">[@spring.message "questionnaire.answer.type"/]</td>
                        <td class="drawtablehd" style="width:45%">[@spring.message "questionnaire.choices"/]</td>
                        <td class="drawtablehd" style="width:10%">[@spring.message "questionnaire.remove"/]</td>
                      </tr>
                      [#list questionDefinition.questions as question]
                      <tr>
                        <td class="drawtablerow">${question.title}</td>
                        <td class="drawtablerow">${question.type}</td>
                          [#if question.commaSeparateChoices?exists]
                          <td class="drawtablerow">${question.commaSeparateChoices}</td>
                          [#else]
                          <td class="drawtablerow"><i>[@spring.message "questionnaire.quesiton.choices.notapplicable"/]</i></td>
                          [/#if]
                        <td class="drawtablerow"><a href="removeQuestion#" title="${question.title}">[@spring.message "questionnaire.remove.link"/]</a></td>
                      </tr>
                      [/#list]
                    </table>
                </li>
                 <li class="buttonWidth">
                     <input type="submit" id="_eventId_createQuestions" name="_eventId_createQuestions" value="[@spring.message "questionnaire.submit"/]" class="buttn"/>
                     &nbsp;
                     <input type="submit" id="_eventId_cancel" name="_eventId_cancel" value="[@spring.message "questionnaire.cancel"/]" class="cancelbuttn"/>
                 </li>
			 </ol>
            </fieldset>
	    </form>
    </div>
    <span id="page.id" title="createQuestion"/>
</div>
[@mifos.footer/]