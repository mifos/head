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
    <STYLE TYPE="text/css"><!-- @import url(pages/questionnaire/css/questionnaire.css); --></STYLE>
    <script src="pages/questionnaire/js/questionnaire.js" type="text/javascript"></script>
    <span id="page.id" title="questionnaire"></span>
    <div class="content">
	    [#if Session.urlMap??]
	        [#assign breadcrumb = Session.urlMap/]
	        [@widget.crumbpairs breadcrumb "false"/]
	    [/#if]
    	<div class="content_panel">
	        <h1>
	            ${Session.questionnaireFor} - [@spring.message "questionnaire.enterdata"/]
	        </h1>
	        <div id="allErrors" class="allErrorsDiv">
	            [@form.showAllErrors "questionGroupDetails.*"/]
	        </div>
	        <form id="questionnaire" name="questionnaire" action="questionnaire.ftl?execution=${flowExecutionKey}" method="POST">
	            [#list questionGroupDetails.details[selectedQuestionnaireIndex].sectionDetails as sectionDetail]
	            <div class="marginTop15">
	                <b>${sectionDetail.name}</b>
	            </div>
	            <fieldset>
	                <ol id="responses">
	                    [#list sectionDetail.questions as question]
	                    <li class="marginTop15" style='background-color: ${((question_index % 2)==0)?string("#F2F2F2", "#FFFFFF")}'>
	                        <label for="details[${selectedQuestionnaireIndex}].sectionDetails[${sectionDetail_index}].questions[${question_index}].value">
	                            [#if question.mandatory]<span class="red">*</span>[/#if]
	                            ${question.text}
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
	                          <fieldset class="right_section" style="width:68%">
	                            <ol class="noPadding">
	                              <li class="noPadding">
	                                [@form.formCheckboxesWithTags "questionGroupDetails.details[${selectedQuestionnaireIndex}].sectionDetails[${sectionDetail_index}].questions[${question_index}].values", question.answerChoices ,'</li><li class="noPadding">', ''/]
	                              </li>
	                            </ol>
	                          </fieldset>
	                          [#break]
	                          [#case "SMART_SELECT"]
	                          <fieldset class="right_section" style="width:68%">
	                                <div class="noPadding">
	                                  <input type="text" autocomplete="off" id="txtListSearch" name="txtListSearch" style="width:21em;" class="txtListSearch"/>
	                                </div>
	                                <ol class="questionList" id="questionList" style="overflow:auto; width:19em; height:180px; border:1px solid #336699; padding-left:5px">
	                                    <li style="padding-bottom: 0pt;">
	                                        [@form.formCheckboxesWithTags "questionGroupDetails.details[${selectedQuestionnaireIndex}].sectionDetails[${sectionDetail_index}].questions[${question_index}].values", question.answerChoices ,'</li><li class="noPadding">', ''/]
	                                    </li>
	                                </ol>
	                          </fieldset>
	                          [#break]
	                          [#case "SINGLE_SELECT"]
	                          <fieldset class="right_section" style="width:68%">
	                            <ol  class="noPadding">
	                              <li class="noPadding">
	                                  [#if question.answerChoices?size > 6]
	                                  [@form.formSingleSelectWithPrompt "questionGroupDetails.details[${selectedQuestionnaireIndex}].sectionDetails[${sectionDetail_index}].questions[${question_index}].value", question.answerChoices, "--selectone--", ''/]
	                                  [#else]
	                                  [@form.formRadioButtons "questionGroupDetails.details[${selectedQuestionnaireIndex}].sectionDetails[${sectionDetail_index}].questions[${question_index}].value", question.answerChoices,' </li><li class="noPadding">', ''/]
	                                  [/#if]
	                              </li>
	                            </ol>
	                          </fieldset>
	                          [#break]
	                          [#case "SMART_SINGLE_SELECT"]
                                  <fieldset class="right_section" style="width:68%">
	                                <div class="noPadding">
	                                  <input type="text" autocomplete="off" id="txtListSearch" name="txtListSearch" style="width:21em;" class="txtListSearch"/>
	                                </div>
	                                <ol class="questionList" id="questionList" style="overflow:auto; width:19em; height:180px; border:1px solid #336699; padding-left:5px">
	                                    <li style="padding-bottom: 0pt;">
	                                        [@form.radioWithTags "questionGroupDetails.details[${selectedQuestionnaireIndex}].sectionDetails[${sectionDetail_index}].questions[${question_index}].values", question.answerChoices ,'</li><li class="noPadding">', ''/]
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
[/@adminLeftPaneLayout]
