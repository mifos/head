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

[#-- 
[#import "/questionnaireWidgets.ftl" as questionnaire /]
--]

[@layout.webflow currentState="createSavingsAccount.flowState.reviewAndSubmit" 
                 states=["createSavingsAccount.flowState.selectCustomer", 
                         "createSavingsAccount.flowState.enterAccountInfo", 
                         "createSavingsAccount.flowState.reviewAndSubmit"]]
                          
[#-- would be widget start --]
    <STYLE TYPE="text/css"><!-- @import url(pages/css/datepicker/datepicker.css); --></STYLE>
    <STYLE TYPE="text/css"><!-- @import url(pages/questionnaire/css/questionnaire.css); --></STYLE>
    <script type="text/javascript" src="pages/js/jquery/jquery.datePicker.min-2.1.2.js"></script>
    <script type="text/javascript" src="pages/js/jquery/jquery.keyfilter-1.7.js"></script>
    <script type="text/javascript" src="pages/js/jquery/jquery.validate.min.js"></script>
    <script type="text/javascript" src="pages/js/datejs/date.js"></script>
    <script type="text/javascript" src="pages/js/jquery/jquery.datePicker.configuration.js"></script>
    <!--[if IE]><script type="text/javascript" src="pages/js/jquery/jquery.bgiframe.js"></script><![endif]-->
    <script src="pages/questionnaire/js/questionnaire.js" type="text/javascript"></script>
    <span id="page.id" title="questionnaire"></span>
    <div class="content_panel">
        [#if questionnaireFor?has_content]
        <h1>${questionnaireFor} - [@spring.message "questionnaire.enterdata"/]</h1>
        [/#if]
        
        [@form.errors "savingsAccountFormBean.*"/]
        <form action="${flowExecutionUrl}" method="post">
[#list savingsAccountFormBean.questionGroups as questionGroup]        
            [#list questionGroup.sectionDetails as sectionDetail]
            <div class="marginTop15">
                <b>${sectionDetail.name}</b>
            </div>
            <fieldset>
                <ol id="responses">
                    [#list sectionDetail.questions as question]
                    <li class="marginTop15" style='background-color: ${((question_index % 2)==0)?string("#F2F2F2", "#FFFFFF")}'>
                        <label for="questionGroups[${questionGroup_index}].sectionDetails[${sectionDetail_index}].questions[${question_index}].value">
                            [#if question.mandatory]<span class="red">*</span>[/#if]
                            ${question.text}
                            [#if question.questionType=="DATE"](dd/mm/yyyy)&nbsp[/#if]:
                        </label>
                        [#switch question.questionType]
                          [#case "FREETEXT"]
                            [@spring.formTextarea "savingsAccountFormBean.questionGroups[${questionGroup_index}].sectionDetails[${sectionDetail_index}].questions[${question_index}].value", 'rows="4" cols="50" maxlength="200"' /]
                          [#break]
                          [#case "NUMERIC"]
                            [@spring.formInput "savingsAccountFormBean.questionGroups[${questionGroup_index}].sectionDetails[${sectionDetail_index}].questions[${question_index}].value", 'maxlength="30"' /]
                          [#break]
                          [#case "DATE"]
                            [@spring.formInput "savingsAccountFormBean.questionGroups[${questionGroup_index}].sectionDetails[${sectionDetail_index}].questions[${question_index}].value", 'maxlength="10" class="date-pick"' /]
                          [#break]
                          [#case "MULTI_SELECT"]
                          <fieldset class="right_section" style="width:68%">
                            <ol class="noPadding">
                              <li class="noPadding">
                                [@form.checkboxesWithTags "savingsAccountFormBean.questionGroups[${questionGroup_index}].sectionDetails[${sectionDetail_index}].questions[${question_index}].values", question.answerChoices ,'</li><li class="noPadding">', ''/]
                              </li>
                            </ol>
                          </fieldset>
                          [#break]
                          [#case "SMART_SELECT"]
                          <fieldset class="right_section" style="width:68%">
                                <li class="noPadding">
                                  <input type="text" autocomplete="off" id="txtListSearch" name="txtListSearch" style="width:21em;" class="txtListSearch"/>
                                </li>
                                <ol class="questionList" id="questionList" style="overflow:auto; width:19em; height:180px; border:1px solid #336699; padding-left:5px">
                                    <li style="padding-bottom: 0pt;">
                                        [@form.checkboxesWithTags "questionGroupDetails.details[${selectedQuestionnaireIndex}].sectionDetails[${sectionDetail_index}].questions[${question_index}].values", question.answerChoices ,'</li><li class="noPadding">', ''/]
                                    </li>
                                </ol>
                          </fieldset>
                          [#break]
                          [#case "SINGLE_SELECT"]
                          <fieldset class="right_section" style="width:68%">
                            <ol  class="noPadding">
                              <li class="noPadding">
                                  [#if question.answerChoices?size > 6]
                                  [@form.singleSelectWithPrompt "savingsAccountFormBean.questionGroups[${questionGroup_index}].sectionDetails[${sectionDetail_index}].questions[${question_index}].value", question.answerChoices, "--selectone--", ''/]
                                  [#else]
                                  [@form.radioButtons "savingsAccountFormBean.questionGroups[${questionGroup_index}].sectionDetails[${sectionDetail_index}].questions[${question_index}].value", question.answerChoices,' </li><li class="noPadding">', ''/]
                                  [/#if]
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
[/#list]            
            <div class="row webflow-controls">
                [@form.submitButton "createSavingsAccount.enterAccountInfo.continueButton" "questionsAnswered" /]
                [@form.cancelButton "createSavingsAccount.enterAccountInfo.cancelButton" "cancel" /]
            </div>
        </form>
    </div>
[#-- would be widget end --]

[/@layout.webflow]