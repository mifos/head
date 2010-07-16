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
    <style type="text/css">
       .mandatoryField {
          color:#FF0000;
        }
	    .normalFontFixedDiv {
		  color:#000000;
		  font-family:Arial,Verdana,Helvetica,sans-serif;
		  font-size:9pt;
		  font-weight:normal;
		  padding:1em 1em 0;
		  text-decoration:none;
		  width:600px;
		}
        .choiceStyle{
            float:left;
            margin-right:1em;
            margin-left:1em;
            width:20em;
            word-wrap:break-word;
        }
        .choiceHeaderStyle{
            background-color:#D7DEEE;
            border-top:1px solid #D7DEEE;
            font-family:Arial,Verdana,Helvetica,sans-serif;
            font-size:9pt;
            font-weight:bold;
            padding:2px 0px;
            text-decoration:none;
        }
        .choiceOlStyle{
            margin-left:10em;
            width:50%
        }
    </style>
    <STYLE TYPE="text/css"><!-- @import url(pages/questionnaire/css/questionnaire.css); --></STYLE>
    <script src="pages/application/surveys/js/questions.js" type="text/javascript"></script>
    <script type="text/javascript">
        $(document).ready(function(){
            disableSubmitButtonOnEmptyQuestionList();

            $("a[href*=removeQuestion#]").click(function(event) {
                var questionToDeleteBtn = document.getElementById('_eventId_removeQuestion');
                questionToDeleteBtn.value = $(this).attr("title");
                event.preventDefault();
                questionToDeleteBtn.click();
            });

            $("a[href*=removeChoice#]").click(function(event) {
                var choiceToDeleteBtn = document.getElementById('_eventId_removeChoice');
                choiceToDeleteBtn.value = $(this).attr("choiceIndex");
                event.preventDefault();
                choiceToDeleteBtn.click();
            });

            $("#currentQuestion\\.type").change(function(){
                var selectedOption = $(this).val();
                if(selectedOption == $("#multiSelect").val()  ||  selectedOption == $("#singleSelect").val()) {
                    $("#choiceDiv").show();
                }else{
                    $("#choiceDiv").hide();
                }
            });

            $("#currentQuestion\\.type").change();
        });
     </script>
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
        <form name="createquestionform" action="createQuestion.ftl?execution=${flowExecutionKey}" method="POST" focus="questionText">
            <fieldset>
			 <ol>
			    <li>
				  <label for="title"><span class="mandatoryField">*</span>Question Title: </label>
				  <script src="pages/framework/js/func.js"></script>
				  <script src="pages/framework/js/func_en_GB.js"></script>
				  [@spring.formInput "questionDefinition.currentQuestion.title",
				     'maxlength="50" onkeypress="return FnCheckNumCharsOnPress(event,this);"
				        onblur="return FnCheckNumChars(event,this);return FnEscape(event,this)"' /]
			    </li>
			    <li>
				  <label for="eventSourceId"><span class="mandatoryField">*</span>Answer Type: </label>
                  [#assign freeText][@spring.message "questionnaire.quesiton.choices.freetext"/][/#assign]
                  [#assign date][@spring.message "questionnaire.quesiton.choices.date"/][/#assign]
                  [#assign multiSelect][@spring.message "questionnaire.quesiton.choices.multiselect"/][/#assign]
                  [#assign singleSelect][@spring.message "questionnaire.quesiton.choices.singleselect"/][/#assign]
				  [@spring.formSingleSelect "questionDefinition.currentQuestion.type",
                    [freeText, date, multiSelect, singleSelect],
                    ''
                  /]
                    <input type="hidden" id="multiSelect" name="multiSelect" value="${multiSelect}"/>
                    <input type="hidden" id="singleSelect" name="singleSelect" value="${singleSelect}" />
			    </li>
			    <li>
                    <div id="choiceDiv">
                        <label for="choice"><span class="mandatoryField">*</span>Answer Choice: </label>
                        [@spring.formInput "questionDefinition.currentQuestion.choice", 'maxlength="200"' /]
                        <input type="submit" name="_eventId_addChoice" value="Add >>" class="insidebuttn"
                               id="_eventId_addChoice">
                        <fieldset>
                            <ol class="choiceOlStyle">
                                <li class="choiceHeaderStyle">
                                    <span class="choiceStyle">Choice</span>Remove
                                </li>
                                [#list questionDefinition.currentQuestion.choices as choice]
                                <li>
                                    <span class="choiceStyle">${choice}&nbsp;</span>
                                    <a href="removeChoice#" choiceIndex="${choice_index}">remove</a>
                                </li>
                                [/#list]
                                <input type="submit" id="_eventId_removeChoice" name="_eventId_removeChoice" value=""
                                       style="visibility:hidden"/>
                            </ol>
                        </fieldset>
                    </div>
			    </li>
			 </ol>
            </fieldset>
            <fieldset id="submitQuestion" class="submit">
                 <input type="submit" name="_eventId_addQuestion" value="Add Question" class="buttn" id="_eventId_addQuestion">
            </fieldset>
		    <div id="divQuestions" class="normalFontFixedDiv" style="width:98%;">
			  <table id="questions.table" name="questions.table">
			    <tr>
			      <td class="drawtablehd" style="width:35%">Question Title</td>
			      <td class="drawtablehd" style="width:10%">Answer type</td>
			      <td class="drawtablehd" style="width:45%">Choices</td>
			      <td class="drawtablehd" style="width:10%">Remove</td>
			    </tr>
			    [#list questionDefinition.questions as question]
			    <tr>
			      <td class="drawtablerow">${question.title}</td>
                  <td class="drawtablerow">${question.type}</td>
                  [#if question.commaSeparateChoices == '']
                    <td class="drawtablerow"><i>[@spring.message "questionnaire.quesiton.choices.notapplicable"/]</i></td>
                  [#else]
                    <td class="drawtablerow">${question.commaSeparateChoices}</td>
                  [/#if]
			      <td class="drawtablerow"><a href="removeQuestion#" title="${question.title}">remove</a></td>
			    </tr>
			    [/#list]
			    <tr>
			      <td class="drawtablerow">&nbsp;</td>
			      <td class="drawtablerow">&nbsp;</td>
			      <td class="drawtablerow">&nbsp;</td>
			      <td class="drawtablerow">&nbsp;</td>
			    </tr>
			  </table>
			  <input type="submit" id="_eventId_removeQuestion" name="_eventId_removeQuestion" value="" style="visibility:hidden"/>
		    </div>
		    <div id="divSumitQD">
		      <fieldset class="submit">
		        <input type="submit" id="_eventId_createQuestions" name="_eventId_createQuestions" value="Submit" class="buttn"/>
		        &nbsp;
		        <input type="submit" id="_eventId_cancel" name="_eventId_cancel" value="Cancel" class="cancelbuttn"/>
		      </fieldset>
	        </div>
		    <input type="hidden" name="h_user_locale" value="en_GB"/>
	    </form>
    </div>
    <span id="page.id" title="createQuestion"/>
</div>
[@mifos.footer/]