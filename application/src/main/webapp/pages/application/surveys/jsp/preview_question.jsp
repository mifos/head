<%-- 
Copyright (c) 2005-2008 Grameen Foundation USA
All rights reserved.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
implied. See the License for the specific language governing
permissions and limitations under the License.

See also http://www.apache.org/licenses/LICENSE-2.0.html for an
explanation of the license and how it is applied.
--%>
<!-- preview_question.jsp -->

<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="/tags/mifos-html" prefix="mifos"%>
<%@taglib uri="http://struts.apache.org/tags-html-el" prefix="html-el"%>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles"%>
<%@page import="org.mifos.application.surveys.helpers.AnswerType"%>
<tiles:insert definition=".clientsacclayoutsearchmenu">
<tiles:put name="body" type="string">
<span id="page.id" title="preview_question" />
	<script src="pages/application/surveys/js/questions.js" type="text/javascript"></script>
			<table width="95%" border="0" cellpadding="0" cellspacing="0">
			  <tr>
          		<td class="bluetablehead05">
	             <span class="fontnormal8pt">
	          	 	<html-el:link action="AdminAction.do?method=load&randomNUm=${sessionScope.randomNUm}">
						<mifos:mifoslabel name="Surveys.linkAdmin" bundle="SurveysUIResources"/>	
					</html-el:link> /
	              </span>
	              <span class="fontnormal8ptbold">
	              	<mifos:mifoslabel name="Surveys.PreviewEditQuestion" bundle="SurveysUIResources"/>
	              </span>
    	        </td>
	    	  </tr>
			</table>
      <table width="95%" border="0" cellpadding="0" cellspacing="0">
        <tr>
          <td width="70%" align="left" valign="middle" class="paddingL15T15"><table width="98%" border="0" cellspacing="0" cellpadding="3">
            <tr>
              <td width="35%">
              <span class="heading"><c:out value="${sessionScope.question.shortName}"/></span> - 
              <span class="headingorange"><mifos:mifoslabel name="Surveys.EditQuestionInfo" bundle="SurveysUIResources"/></span>
              </td>
            </tr>
			<tr>
              <td class="fontnormal">
              <mifos:mifoslabel name="Surveys.EditNote" bundle="SurveysUIResources"/><br>
              <mifos:mifoslabel name="Surveys.EditInstructions" bundle="SurveysUIResources"/>
              </td>
            </tr>
          </table>     
          <br>

<font class="fontnormalRedBold"><html-el:errors bundle="SurveysUIResources" /></font>
<!-- actual form content -->
            <html-el:form action="/questionsAction.do?method=defineQuestions" focus="questionText">
            <table width="98%" border="0" cellpadding="3" cellspacing="0">
                <tr>
                <td class="drawtablehd"><mifos:mifoslabel name="Surveys.QuestionName" bundle="SurveysUIResources"/></td>
                <td class="drawtablehd"><mifos:mifoslabel name="Surveys.Question" bundle="SurveysUIResources"/></td>
                <td class="drawtablehd"><mifos:mifoslabel name="Surveys.Answertype" bundle="SurveysUIResources"/> </td>
                <td class="drawtablehd"><mifos:mifoslabel name="Surveys.Choices" bundle="SurveysUIResources"/></td>  
                </tr>
                <tr>
              	<td class="drawtablerow"><c:out value="${shortName}"/></td>
                <td class="drawtablerow"><c:out value="${questionText}"/></td>
                <td class="drawtablerow">

<c:choose>
<c:when test="${question.answerType == 1}"><mifos:mifoslabel name="Surveys.Multiselect" bundle="SurveysUIResources"/></c:when>
<c:when test="${question.answerType == 2}"><mifos:mifoslabel name="Surveys.Freetext" bundle="SurveysUIResources"/></c:when>
<c:when test="${question.answerType == 3}"><mifos:mifoslabel name="Surveys.Number" bundle="SurveysUIResources"/></c:when>
<c:when test="${question.answerType == 4}"><mifos:mifoslabel name="Surveys.Singleselect" bundle="SurveysUIResources"/></c:when>
<c:when test="${question.answerType == 5}"><mifos:mifoslabel name="Surveys.Date" bundle="SurveysUIResources"/></c:when>
</c:choose>
</td>
                <td class="drawtablerow">

<c:choose>
<c:when test="${question.answerType == 4 || question.answerType == 1}">
<c:forEach var="choice" items="${question.choices}" varStatus="ptr"><c:out value="${choice.choiceText}"/><c:if test="${not ptr.last}">, </c:if></c:forEach>
</c:when>
<c:otherwise><em><mifos:mifoslabel name="Surveys.notapplicable" bundle="SurveysUIResources"/></em></c:otherwise>
</c:choose>
</td>
</tr>
							<br>
							<table width="93%" border="0" cellpadding="0" cellspacing="0">
							<tr>
									<td colspan="2" class="drawtablerow">&nbsp;</td>
									<td class="drawtablerow">&nbsp;</td>
									<td class="drawtablerow">&nbsp;</td>
									<td class="drawtablerow">&nbsp;</td>
							</tr>
								<tr>
									<td align="center">
<button id="AddQuestionButton" class="buttn" onclick="submitQuestionForm('update_entry')">
                     <mifos:mifoslabel name="Surveys.button.submit" bundle="SurveysUIResources"/>
                   </button>

&nbsp; <html-el:button property="cancelButton" 
										styleClass="cancelbuttn" onclick="window.location='AdminAction.do?method=load'">
										<mifos:mifoslabel name="Surveys.button.cancel" bundle="SurveysUIResources" />
									</html-el:button></td>
								</tr>
							</table>

              </html-el:form>
<!-- end actual form content -->
            </table>
</tiles:put>
</tiles:insert>
