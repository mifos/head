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
[#assign security=JspTaglibs["http://www.springframework.org/security/tags"] /]
[@clientLeftPane]
    <STYLE TYPE="text/css"><!-- @import url(pages/questionnaire/css/questionnaire.css); --></STYLE>
    <script src="pages/questionnaire/js/display.js" type="text/javascript"></script>
    <span id="page.id" title="display_question_group_reponse"></span>
    <div class="content">
	    [#if Session.urlMap??]
	        [#assign breadcrumb = Session.urlMap/]
	        [@widget.crumbpairs breadcrumb "false"/]
	    [/#if]
	    <div class="content_panel">
	        <h1>
	            ${questionGroupInstance.questionGroupTitle} - ${questionGroupInstance.dateCompletedAsString}
	        </h1>
	        <form action="viewAndEditQuestionnaire.ftl?execution=${flowExecutionKey}" id="displayResponseForm"
	              name="displayResponseForm" method="post">
	            <input type="submit" id="_eventId_questionnaire" name="_eventId_questionnaire" value="" style="visibility:hidden"/>
	            <fieldset id="questionGroupInstance.questionGroupDetail.sections" class="bluetableborderFull marginTop15">
	                [#if questionGroupInstance.questionGroupDetail.active && questionGroupInstance.questionGroupDetail.editable]
	                    [@security.authorize access="isFullyAuthenticated() and hasRole('CAN_EDIT_QUESTION_GROUP_RESPONSES')"]
	                    <span class="topRight">
	                        <a href="editQuestionnaire#" questionGroupInstanceDetailIndex="0">[@spring.message "questionnaire.edit"/]</a>
	                    </span>
	                    [/@security.authorize]
	                [/#if]
	                [#list questionGroupInstance.questionGroupDetail.sectionDetails as sectionDetail]
	                <br/>
	                <span class="paddingleft10 fontnormalbold">${sectionDetail.name?html}</span>
	                <ol>
	                    [#list sectionDetail.questions as sectionQuestionDetail]
	                    <li style='background-color: ${((sectionQuestionDetail_index % 2)==0)?string("#F2F2F2", "#FFFFFF")}'>
	                        <label>[#if sectionQuestionDetail.mandatory]<span class="red">*</span>[/#if]
	                            <span id="displayQuestionGroupReponse.text.section[${sectionDetail_index}].question[${sectionQuestionDetail_index}].questionName">${sectionQuestionDetail.text?html}</span>:</label>[#if sectionQuestionDetail.multiSelectQuestion && sectionQuestionDetail.values?size > 1]
	                        <ol>
	                            [#list sectionQuestionDetail.values as answer]
	                            <li>
	                                <span id="displayQuestionGroupReponse.text.section[${sectionDetail_index}].question[${sectionQuestionDetail_index}].questionAnswer[${answer_index}]">${answer?html}</span>
	                            </li>
	                            [/#list]
	                        </ol>
	                        [#else]
	                            <span id="displayQuestionGroupReponse.text.section[${sectionDetail_index}].question[${sectionQuestionDetail_index}].questionAnswer">${sectionQuestionDetail.answer?html}</span>
	                        [/#if]
	                    </li>
	                    [/#list]
	                </ol>
	                [/#list]
	            </fieldset>
	            <div class="buttonWidth">
	                <input id="_eventId_cancel" name="_eventId_cancel" type="submit" class="buttn" value="[@spring.message "questionnaire.back.to.previous"/]"/>
	            </div>
	        </form>
	    </div>
	</div>
[/@clientLeftPane]
