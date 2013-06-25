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
    <script>
            $(document).ready(function() {
                
                var updateSectionsAndQuestionsVisibility = function(response) {
                    for (var questionId in response.questions) {
                        if (response.questions.hasOwnProperty(questionId)) {
                            var isVisible = response.questions[questionId];
                            $("#question" + questionId).css("display", 
                                    isVisible ? "table-row" : "none");
                        }
                    }
                    for (var sectionId in response.sections) {
                        if (response.sections.hasOwnProperty(sectionId)) {
                            var isVisible = response.sections[sectionId];
                            $("#section" + sectionId).css("display", 
                                    isVisible ? "table" : "none");
                        }
                    }
                },
                updateQuestions = function() {                
                    var questionId = $(this).closest("li").attr("data-question-id"),
                        questionResponse = $(this).val();
                    
                    $.ajax({
                        type: "POST",
                        url: "getHiddenVisibleQuestions.ftl",
                        data: {questionId: questionId, response: questionResponse},
                        success: function(response) {
                            updateSectionsAndQuestionsVisibility(response);
                        }
                     });
                };
                
                $('.question.date-pick').change(updateQuestions);
                $('.question').blur(updateQuestions);
                $('input[type=radio]').click(updateQuestions);
            
                
                var allQuestionsIds = ""
                        
                $('li').filter(function() { 
                        return /^question[0-9]+$/.test(this.id); 
                    }).each(function() {
                        allQuestionsIds += $(this).attr("data-question-id") + ",";
                    });
                
                if (allQuestionsIds !== "") {
                    allQuestionsIds = allQuestionsIds.substring(0, allQuestionsIds.length-1);
                }
                
                var allSectionsIds = ""
                    
                $('div').filter(function() { 
                        return /^section[0-9]+$/.test(this.id); 
                    }).each(function() {
                        allSectionsIds += $(this).attr("data-section-id") + ",";
                    });
                
                if (allSectionsIds !== "") {
                    allSectionsIds = allSectionsIds.substring(0, allSectionsIds.length-1);
                }
                   
                $.ajax({
                    type: "POST",
                    url: "hideAttachedQuestions.ftl",
                    data: {questionsId: allQuestionsIds, sectionsId: allSectionsIds},
                    success: function(response) {
                        for (var questionId in response.questions) {
                            $("#question" + response.questions[questionId]).css("display", "none");
                        }
                        for (var sectionId in response.sections) {
                           $("#section" + response.sections[sectionId]).css("display", "none");    
                        }
                    }
                 });
             
            });
        </script>
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
	            <div id="section${sectionDetail.id}" class="marginTop15" data-section-id="${sectionDetail.id}">
	                <b>${sectionDetail.name}</b>
    	            <fieldset>
    	                <ol id="responses">
    	                    [#list sectionDetail.questions as question]
    	                    <li id="question${question.id}" data-question-id="${question.id}" class="marginTop15" style='background-color: ${((question_index % 2)==0)?string("#F2F2F2", "#FFFFFF")}'>
    	                        <label for="details[${selectedQuestionnaireIndex}].sectionDetails[${sectionDetail_index}].questions[${question_index}].value">
    	                            [#if question.mandatory]<span class="red">*</span>[/#if]
    	                            ${question.text}
    	                            [#if question.questionType=="DATE"](dd/mm/yyyy)&nbsp[/#if]:
    	                        </label>
    	                        [#switch question.questionType]
    	                          [#case "FREETEXT"]
    	                            [@spring.formTextarea "questionGroupDetails.details[${selectedQuestionnaireIndex}].sectionDetails[${sectionDetail_index}].questions[${question_index}].value", 'rows="4" cols="50" maxlength="200" class="question"' /]
    	                          [#break]
    	                          [#case "NUMERIC"]
    	                            [@spring.formInput "questionGroupDetails.details[${selectedQuestionnaireIndex}].sectionDetails[${sectionDetail_index}].questions[${question_index}].value", 'maxlength="30" class="question"' /]
    	                          [#break]
    	                          [#case "DATE"]
    	                            [@spring.formInput "questionGroupDetails.details[${selectedQuestionnaireIndex}].sectionDetails[${sectionDetail_index}].questions[${question_index}].value", 'maxlength="10" class="question date-pick"' /]
    	                          [#break]
    	                          [#case "MULTI_SELECT"]
    	                          <fieldset class="right_section" style="width:68%">
    	                            <ol class="noPadding">
    	                              <li class="noPadding" data-question-id="${question.id}">
    	                                [@form.formCheckboxesWithTags "questionGroupDetails.details[${selectedQuestionnaireIndex}].sectionDetails[${sectionDetail_index}].questions[${question_index}].values", question.answerChoices ,'</li><li class="noPadding" data-question-id="${question.id}">', ''/]
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
    	                                    <li style="padding-bottom: 0pt;" data-question-id="${question.id}">
    	                                        [@form.formCheckboxesWithTags "questionGroupDetails.details[${selectedQuestionnaireIndex}].sectionDetails[${sectionDetail_index}].questions[${question_index}].values", question.answerChoices ,'</li><li class="noPadding" data-question-id="${question.id}">', ''/]
    	                                    </li>
    	                                </ol>
    	                          </fieldset>
    	                          [#break]
    	                          [#case "SINGLE_SELECT"]
    	                          <fieldset class="right_section" style="width:68%">
    	                            <ol  class="noPadding">
    	                              <li class="noPadding" data-question-id="${question.id}">
    	                                  [#if question.answerChoices?size > 6]
    	                                  [@form.formSingleSelectWithPrompt "questionGroupDetails.details[${selectedQuestionnaireIndex}].sectionDetails[${sectionDetail_index}].questions[${question_index}].value", question.answerChoices, "--selectone--", ''/]
    	                                  [#else]
    	                                  [@form.formRadioButtons "questionGroupDetails.details[${selectedQuestionnaireIndex}].sectionDetails[${sectionDetail_index}].questions[${question_index}].value", question.answerChoices,' </li><li class="noPadding" data-question-id="${question.id}">', ''/]
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
    	                                    <li style="padding-bottom: 0pt;" data-question-id="${question.id}">
    	                                        [@form.radioWithTags "questionGroupDetails.details[${selectedQuestionnaireIndex}].sectionDetails[${sectionDetail_index}].questions[${question_index}].values", question.answerChoices ,'</li><li class="noPadding" data-question-id="${question.id}">', ''/]
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
	            </div>
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
