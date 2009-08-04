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
<!-- survey_details.jsp -->

<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="/tags/mifos-html" prefix="mifos"%>
<%@taglib uri="http://struts.apache.org/tags-html-el" prefix="html-el"%>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles"%>

<tiles:insert definition=".view">
	<tiles:put name="body" type="string">
	<span id="page.id" title="survey_details" />
	<table width="95%" border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td class="bluetablehead05"><span class="fontnormal8pt">
					<html-el:link href="AdminAction.do?method=load">
						<mifos:mifoslabel name="admin.shortname" bundle="adminUIResources" />
					</html-el:link> /
					<html-el:link href="surveysAction.do?method=mainpage">	
					<mifos:mifoslabel name="admin.viewsurvey" bundle="adminUIResources" />					
					</html-el:link> / </span>
					<span class="fontnormal8ptbold">					
					<c:out value="${sessionScope.BusinessKey.name}"/>
					</span>
					</td>
				</tr>
			</table>
	<script src="pages/application/surveys/js/questions.js" type="text/javascript"></script>
    <table width="95%" border="0" cellpadding="0" cellspacing="0">
          <tr>
            <td width="85%" align="left" valign="top" class="paddingL15T15"><table width="96%" border="0" cellpadding="3" cellspacing="0">
              <tr>
                <td height="23" class="headingorange"><c:out value="${sessionScope.BusinessKey.name}"/></td>
                </tr>
              <tr>

                <td height="23" class="fontnormal"><p><span class="fontnormal">                  </span>
                <span class="fontnormal">
                <c:choose>
                  <c:when test="${sessionScope.BusinessKey.state == 1}"><img src="pages/framework/images/status_activegreen.gif" width="8" height="9"> <mifos:mifoslabel name="Surveys.Active"/></c:when>
                  <c:when test="${sessionScope.BusinessKey.state == 0}"><img src="pages/framework/images/status_closedblack.gif" width="8" height="9"> <mifos:mifoslabel name="Surveys.Inactive"/></c:when>
                </c:choose>            
                </span>
                <br>                  
                      <br>                  
                  <mifos:mifoslabel name="Surveys.Appliesto" isColonRequired="yes"/>
                  	<c:choose>
                               <c:when test="${sessionScope.BusinessKey.appliesTo == \"client\"}"><mifos:mifoslabel name="Surveys.client_type"/></c:when>
                               <c:when test="${sessionScope.BusinessKey.appliesTo == \"loan\"}"><mifos:mifoslabel name="Surveys.loan_type"/></c:when>
                               <c:when test="${sessionScope.BusinessKey.appliesTo == \"center\"}"><mifos:mifoslabel name="Surveys.center_type"/></c:when>
                               <c:when test="${sessionScope.BusinessKey.appliesTo == \"group\"}"><mifos:mifoslabel name="Surveys.group_type"/></c:when>
                               <c:when test="${sessionScope.BusinessKey.appliesTo == \"savings\"}"><mifos:mifoslabel name="Surveys.savings_type"/></c:when>
                               <c:when test="${sessionScope.BusinessKey.appliesTo == \"all\"}"><mifos:mifoslabel name="Surveys.all_type"/></c:when>
                  	</c:choose>
                  	<br>
  <br>
  <a href="surveysAction.do?method=printVersion&value(surveyId)=<c:out value="${sessionScope.BusinessKey.surveyId}"/>" target="_new"><mifos:mifoslabel name="Surveys.Printerversion"/></a></p>
                  <p><span class="fontnormalbold"><mifos:mifoslabel name="Surveys.Questions"/></span></p></td>
                </tr>

              <tr>
                <td height="23" class="fontnormal"><table width="98%" border="0" cellpadding="3" cellspacing="0">
                  <tr>
                  	<td width="39%" class="drawtablehd"><mifos:mifoslabel name="Surveys.QuestionName"/></td>
                    <td width="39%" class="drawtablehd"><mifos:mifoslabel name="Surveys.Questiontitle"/></td>
                    <td width="14%" class="drawtablehd"><mifos:mifoslabel name="Surveys.Answertype"/></td>
                    <td width="44%" class="drawtablehd"><mifos:mifoslabel name="Surveys.Answer"/></td>
                    <td width="44%" class="drawtablehd"><mifos:mifoslabel name="Surveys.Mandatory"/></td>
                  </tr>

                  <c:choose>
                  <c:when test="${itemCount !=0}">
		  <c:forEach var="question" items="${sessionScope.BusinessKey.questions}">
                  <tr>
                  	<td class="drawtablerow"><c:out value="${question.question.shortName}"/></td>
                    <td width="39%" class="drawtablerow"><c:out value="${question.question.questionText}"/></td>
                    <td width="14%" class="drawtablerow">
                    <c:choose>
                    	<c:when test="${question.question.answerType == 1}"><mifos:mifoslabel name="Surveys.Multiselect"/></c:when>
                    	<c:when test="${question.question.answerType == 2}"><mifos:mifoslabel name="Surveys.Freetext"/></c:when>
                    	<c:when test="${question.question.answerType == 3}"><mifos:mifoslabel name="Surveys.Number"/></c:when>
                    	<c:when test="${question.question.answerType == 4}"><mifos:mifoslabel name="Surveys.Choice"/></c:when>
                    	<c:when test="${question.question.answerType == 5}"><mifos:mifoslabel name="Surveys.Date"/></c:when>

                    </c:choose>
                    </td>
                    <td width="44%" class="drawtablerow">&nbsp;</td>
                    <td class="drawtablerow">
                    <c:choose>
                    <c:when test="${question.mandatory == 1}"><mifos:mifoslabel name="Surveys.Yes"/></c:when>
                    <c:when test="${question.mandatory == 0}"><mifos:mifoslabel name="Surveys.No"/></c:when>
                    </c:choose>
                    </td>
                  </tr>
		  </c:forEach>
                  </c:when>
                  <c:otherwise>
                    <tr><td class="drawtablerow"><em><mifos:mifoslabel name="Surveys.noquestionsadded"/></em></td></tr>
                  </c:otherwise>
                  </c:choose>
                  
                </table></td>
              </tr>
            </table>              
            <br></td>
            <c:if test="${!requestScope.isPPISurvey}">
	            <td valign="top" align="right"><br><html-el:link action="surveysAction.do?method=edit_entry&value(surveyId)=${sessionScope.BusinessKey.surveyId}">
	            <mifos:mifoslabel name="Surveys.EditSurvey" bundle="SurveysUIResources"/>
	            </html-el:link>
	            </td>
            </c:if>
          </tr>
        </table>    

	</tiles:put>
</tiles:insert>
