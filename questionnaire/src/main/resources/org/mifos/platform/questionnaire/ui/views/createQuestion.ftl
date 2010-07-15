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
				  <label for="eventSourceId"><span class="mandatoryField">*</span>Answer type: </label>
				  [@spring.formSingleSelect "questionDefinition.currentQuestion.type", ["Free text", "Date"], 'styleId="answerType" onchange="setDisable();"' /]			
			    </li>		    
			 </ol>
            </fieldset>
            <fieldset id="submitQuestion" class="submit">
                 <input type="submit" name="_eventId_addQuestion" value="Add Question" class="buttn" id="_eventId_addQuestion">
            </fieldset>
		    <div id="divQuestions" class="normalFontFixedDiv">
			  <table id="questions.table" name="questions.table">
			    <tr>
			      <th class="drawtablehd">Question Title</th>
			      <th colspan="2" class="drawtablehd">Answer type</th>
			    </tr>
			    [#list questionDefinition.questions as question]
			    <tr>
			      <td class="drawtablerow">${question.title}</td>
			      <td class="drawtablerow">${question.type}</td>
			      <td class="drawtablerow"><a href="removeQuestion#" title="${question.title}">remove</a></td>
			    </tr>
			    [/#list]
			    <tr>
			      <td class="drawtablerow">&nbsp;</td>
			      <td colspan="2" class="drawtablerow">&nbsp;</td>
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