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
[#import "newblueprintmacros.ftl" as mifos]
[#import "macros.ftl" as mifosMacros]
[@mifos.header "title" /]
[@mifos.topNavigationNoSecurity currentTab="Admin" /]
<STYLE TYPE="text/css"><!-- @import url(pages/questionnaire/css/datepicker.css); --></STYLE>
<STYLE TYPE="text/css"><!-- @import url(pages/questionnaire/css/questionnaire.css); --></STYLE>
<script type="text/javascript" src="pages/questionnaire/js/jquery.datePicker.min-2.1.2.js"></script>
<script type="text/javascript" src="pages/questionnaire/js/jquery.keyfilter-1.7.js"></script>
<script type="text/javascript" src="pages/questionnaire/js/jquery.validate.min.js"></script>
<script type="text/javascript" src="pages/questionnaire/js/date.js"></script>
<script type="text/javascript" src="pages/questionnaire/js/dateConfiguration.js"></script>
<!--[if IE]><script type="text/javascript" src="pages/questionnaire/js/jquery.bgiframe.js"></script><![endif]-->
<script src="pages/questionnaire/js/questionnaire.js" type="text/javascript"></script>
<div class="colmask leftmenu">
    <div class="colleft">
        <div class="col1wrap">
            <div class="col1">
            <div class="main_content">
                <span id="page.id" title="questionnaire"></span>
                [#if Session.urlMap??]
                    [#assign breadcrumb = Session.urlMap/]
                    [@mifos.crumbpairs breadcrumb "false"/]
                [/#if]
                <div class="content_panel">
                    <h1>
                        ${Session.questionnaireFor} - [@spring.message "questionnaire.enterdata"/]
                    </h1>
                    <div id="allErrors" class="allErrorsDiv">
                        [@mifosMacros.showAllErrors "questionGroupDetails.*"/]
                    </div>
                    <form id="questionnaire" name="questionnaire" action="questionnaire.ftl?execution=${flowExecutionKey}" method="POST">
                        [#list questionGroupDetails.details[selectedQuestionnaireIndex].sectionDetails as sectionDetail]
                        <div class="marginTop15">
                            <b>${sectionDetail.name}</b>
                        </div>
                        <fieldset>
                            <ol id="responses">
                                [#list sectionDetail.questions as question]
                                <li class="marginTop15">
                                    <label for="details[${selectedQuestionnaireIndex}].sectionDetails[${sectionDetail_index}].questions[${question_index}].value">
                                        [#if question.mandatory]<span class="red">*</span>[/#if]
                                        ${question.title}
                                        [#if question.questionType=="DATE"](dd/mm/yyyy)&nbsp[/#if]:
                                    </label>
                                    [#switch question.questionType]
                                      [#case "FREETEXT"]
                                        [@spring.formTextarea "questionGroupDetails.details[${selectedQuestionnaireIndex}].sectionDetails[${sectionDetail_index}].questions[${question_index}].value", 'rows="4" cols="50" maxlength="200"' /]
                                      [#break]
                                      [#case "NUMERIC"]
                                        [@spring.formInput "questionGroupDetails.details[${selectedQuestionnaireIndex}].sectionDetails[${sectionDetail_index}].questions[${question_index}].value", 'maxlength="30"' /]
                                      [#break]
                                      [#case "DATE"]
                                        [@spring.formInput "questionGroupDetails.details[${selectedQuestionnaireIndex}].sectionDetails[${sectionDetail_index}].questions[${question_index}].value", 'maxlength="10" class="date-pick"' /]
                                      [#break]
                                      [#case "MULTI_SELECT"]
                                      <fieldset class="right_section" style="width:70%">
                                        <ol class="noPadding">
                                          <li class="noPadding">
                                            [@mifosMacros.formCheckboxes "questionGroupDetails.details[${selectedQuestionnaireIndex}].sectionDetails[${sectionDetail_index}].questions[${question_index}].values", question.answerChoices ,'</li><li class="noPadding">', ''/]
                                          </li>
                                        </ol>
                                      </fieldset>
                                      [#break]
                                      [#case "SMART_SELECT"]
                                      <fieldset class="right_section" style="width:70%">
                                            <li class="noPadding">
                                              <input type="text" autocomplete="off" id="txtListSearch" name="txtListSearch" style="width:21em;" class="txtListSearch"/>
                                            </li>
                                            <ol class="questionList" id="questionList" style="overflow:auto; width:19em; height:180px; border:1px solid #336699; padding-left:5px">
                                                <li style="padding-bottom: 0pt;">
                                                    [@mifosMacros.formCheckboxes "questionGroupDetails.details[${selectedQuestionnaireIndex}].sectionDetails[${sectionDetail_index}].questions[${question_index}].values", question.answerChoices ,'</li><li class="noPadding">', ''/]
                                                </li>
                                            </ol>
                                      </fieldset>
                                      [#break]
                                      [#case "SINGLE_SELECT"]
                                      <fieldset class="right_section" style="width:70%">
                                        <ol  class="noPadding">
                                          <li class="noPadding">
                                            [@mifosMacros.formRadioButtons "questionGroupDetails.details[${selectedQuestionnaireIndex}].sectionDetails[${sectionDetail_index}].questions[${question_index}].value", question.answerChoices ,'</li><li class="noPadding">', ''/]
                                          </li>
                                        </ol>
                                      </fieldset>
                                      [#break]
                                      [#default]
                                         Unknown question type ${question.questionType}
                                    [/#switch]
                                </li>
                                [/#list]
                            </ol>
                        </fieldset>
                        [/#list]
                        <fieldset>
                            <ol>
                                <li class="buttonWidth">
                                    <input type="submit" id="_eventId_saveQuestionnaire" name="_eventId_saveQuestionnaire"
                                           value="[@spring.message "questionnaire.submit"/]" class="buttn"/>
                                    &nbsp;
                                    <input type="submit" id="_eventId_cancel" name="_eventId_cancel"
                                           value="[@spring.message "questionnaire.cancel"/]" class="cancel cancelbuttn"/>
                                </li>
                            </ol>
                        </fieldset>
                    </form>
                </div>
            </div>
            </div>
        </div>
        <div class="col2">
            <div class="side_bar">
                [#include "newadminLeftPane.ftl" /]
            </div>
        </div>
    </div>
</div>
[@mifos.footer/]