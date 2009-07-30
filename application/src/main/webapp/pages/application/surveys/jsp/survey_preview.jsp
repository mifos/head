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
<!-- survey_preview.jsp -->

<%@ taglib uri="/tags/struts-tiles" prefix="tiles"%>
<%@ taglib uri="/tags/struts-html-el" prefix="html-el"%>
<%@ taglib uri="/tags/mifos-html" prefix="mifos"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="/sessionaccess" prefix="session"%>
<tiles:insert definition=".create">
	<tiles:put name="body" type="string">
	<span style="display: none" id="page.id">survey_preview</span>
	<script src="pages/application/surveys/js/questions.js" type="text/javascript"></script>
<html-el:form action="/surveysAction.do?method=create">


      <table width="90%" border="0" align="center" cellpadding="0" cellspacing="0">
          <tr>

            <td class="bluetablehead">  <table width="100%" border="0" cellspacing="0" cellpadding="0">
                <tr>
                  <td width="27%">
                    <table border="0" cellspacing="0" cellpadding="0">
                      <tr>
                        <td><img src="pages/framework/images/timeline/tick.gif" width="17" height="17"></td>
                        <td class="timelineboldgray"><mifos:mifoslabel name="Surveys.surveyInformation" bundle="SurveysUIResources" /></td>
                      </tr>

                    </table>
                  </td>
                  <td width="73%" align="right">
                    <table border="0" cellspacing="0" cellpadding="0">
                      <tr>
                        <td><img src="pages/framework/images/timeline/bigarrow.gif" width="17" height="17"></td>
                        <td class="timelineboldorange"><mifos:mifoslabel name="Surveys.review" bundle="SurveysUIResources" /></td>

                      </tr>
                    </table>
                  </td>
                </tr>
              </table></td>
          </tr>
        </table>
        <table width="90%" border="0" align="center" cellpadding="0" cellspacing="0" class="bluetableborder">
          <tr>

            <td align="left" valign="top" class="paddingleftCreates">              <table width="93%" border="0" cellpadding="3" cellspacing="0">
                <tr>
                  <td class="headingorange"><span class="heading"><mifos:mifoslabel name="Surveys.definenewsurvey" bundle="SurveysUIResources"/> - </span><mifos:mifoslabel name="Surveys.review" bundle="SurveysUIResources"/></td>
                </tr>
                <tr>
                  <td class="fontnormal"><mifos:mifoslabel name="Surveys.reviewInstructions" bundle="SurveysUIResources" /></td>

                </tr>
              </table>
              <br>  
              <table width="93%" border="0" cellpadding="3" cellspacing="0">
                <tr>
                  <td width="100%" height="23" class="fontnormal"><p><span class="fontnormalbold"><mifos:mifoslabel name="Surveys.surveyInformation" bundle="SurveysUIResources" /></span><br>
                    <mifos:mifoslabel name="Surveys.survey_name" bundle="SurveysUIResources" />: <c:out value="${validatedValues[\"value(name)\"]}"/>   <br>
                    <mifos:mifoslabel name="Surveys.Appliesto" bundle="SurveysUIResources" />: 

<c:choose>
<c:when test="${validatedValues[\"value(appliesTo)\"].value == \"client\"}"><mifos:mifoslabel name="Surveys.client_type" bundle="SurveysUIResources" /></c:when>
<c:when test="${validatedValues[\"value(appliesTo)\"].value == \"center\"}"><mifos:mifoslabel name="Surveys.center_type" bundle="SurveysUIResources" /></c:when>
<c:when test="${validatedValues[\"value(appliesTo)\"].value == \"group\"}"><mifos:mifoslabel name="Surveys.group_type" bundle="SurveysUIResources" /></c:when>
<c:when test="${validatedValues[\"value(appliesTo)\"].value == \"loan\"}"><mifos:mifoslabel name="Surveys.loan_type" bundle="SurveysUIResources" /></c:when>
<c:when test="${validatedValues[\"value(appliesTo)\"].value == \"savings\"}"><mifos:mifoslabel name="Surveys.savings_type" bundle="SurveysUIResources" /></c:when>
<c:when test="${validatedValues[\"value(appliesTo)\"].value == \"all\"}"><mifos:mifoslabel name="Surveys.all_type" bundle="SurveysUIResources" /></c:when>
</c:choose>
</p>

                    <p><a href="surveysAction.do?method=prePrintVersion" target="_new"><mifos:mifoslabel name="Surveys.Printerversion" bundle="SurveysUIResources" /></a> <br>
                      <br>
                      <span class="fontnormalbold"><mifos:mifoslabel name="Surveys.Questions" bundle="SurveysUIResources" /></span><br>
                      <br>
                                      </p>
                    <table width="98%" border="0" cellpadding="3" cellspacing="0">
  <tr>

    <td width="39%" class="drawtablehd"> <mifos:mifoslabel name="Surveys.QuestionName" bundle="SurveysUIResources" /></td>
    <td width="39%" class="drawtablehd"> <mifos:mifoslabel name="Surveys.Question" bundle="SurveysUIResources" /></td>
    <td width="14%" class="drawtablehd"><mifos:mifoslabel name="Surveys.Mandatory" bundle="SurveysUIResources" /></td>
  </tr>
<c:choose>
<c:when test="${itemCount > 0}">
<c:forEach items="${sessionScope.addedQuestions}" var="question">
  <tr>
    <td width="39%" class="drawtablerow"><c:out value="${question.shortName}"/></td>
    <td width="14%" class="drawtablerow"><c:out value="${question.questionText}"/></td>
    <td width="44%" class="drawtablerow"><html-el:checkbox property="value(mandatory_${question.questionId})" disabled="true" value="1"/></td>
  </tr>
</c:forEach>
</c:when>
<c:otherwise>
          <tr><td class="drawtablerow"><em><mifos:mifoslabel name="Surveys.noquestionsadded"/></em></td></tr>
</c:otherwise>
</c:choose>
</table>
<br>
                    <br>                    
      <span class="fontnormal"></span>
       <html-el:button property="calcelButton" onclick="submitSurveyForm('edit')" styleClass="insidebuttn"><mifos:mifoslabel name="Surveys.button.editsurvey" bundle="SurveysUIResources" /></html-el:button>                 </td>
                </tr>
              </table>
              <table width="93%" border="0" cellpadding="0" cellspacing="0">

                <tr>
                  <td align="center" class="blueline">&nbsp;                  </td>
                </tr>
              </table>              <br>
							<table width="93%" border="0" cellpadding="0" cellspacing="0">
								<tr>
									<td align="center"><html-el:submit property="button" styleClass="buttn">
										<mifos:mifoslabel name="Surveys.button.submit"
											bundle="SurveysUIResources" />
									</html-el:submit>&nbsp; <html-el:button property="calcelButton" 
										styleClass="cancelbuttn" onclick="window.location='AdminAction.do?method=load'">
										<mifos:mifoslabel name="Surveys.button.cancel" bundle="SurveysUIResources" />
									</html-el:button></td>
								</tr>
							</table>
            <br></td>
          </tr>
        </table>

</html-el:form> 
					</tiles:put> </tiles:insert>
