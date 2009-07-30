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
<!-- create_survey.jsp -->

<%@ taglib uri="/tags/struts-tiles" prefix="tiles"%>
<%@ taglib uri="/tags/struts-html-el" prefix="html-el"%>
<%@ taglib uri="/tags/mifos-html" prefix="mifos"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="/sessionaccess" prefix="session"%>
<tiles:insert definition=".create">
  <tiles:put name="body" type="string">
  <span style="display: none" id="page.id">create_survey</span>
    <script src="pages/application/surveys/js/questions.js" type="text/javascript"></script>
    <html-el:form action="/surveysAction.do?method=preview" focus="name">
      <table width="100%" border="0" cellspacing="0" cellpadding="0">
	<tr>
	  <td height="450" align="left" valign="top" bgcolor="#FFFFFF">
	    <table width="90%" border="0" align="center" cellpadding="0" cellspacing="0">
	      <tr>
		<td align="center" class="heading">&nbsp;</td>
	      </tr>
	    </table>

	    <table width="90%" border="0" align="center" cellpadding="0" cellspacing="0">
	      <tr>
	        <td class="bluetablehead">
		  <table width="100%" border="0" cellspacing="0" cellpadding="0">
		    <tr>
		      <td width="27%">
			<table border="0" cellspacing="0" cellpadding="0">
			  <tr>
			    <td>
                              <img src="pages/framework/images/timeline/bigarrow.gif" width="17" height="17">
                            </td>
			    <td class="timelineboldorange">
                              <mifos:mifoslabel name="Surveys.surveyInformation" bundle="SurveysUIResources" />
                            </td>
			  </tr>
			</table>
		      </td>
		      <td width="73%" align="right">
			<table border="0" cellspacing="0" cellpadding="0">
			  <tr>
			    <td>
                              <img src="pages/framework/images/timeline/orangearrow.gif" width="17" height="17">
                            </td>
			    <td class="timelineboldorangelight">
                              <mifos:mifoslabel name="Surveys.review" bundle="SurveysUIResources" />
                            </td>
			  </tr>
			</table>
		      </td>
		    </tr>
		  </table>
		</td>
	      </tr>
	    </table>

            <table width="90%" border="0" align="center" cellpadding="0" cellspacing="0" class="bluetableborder">
              <tr>
                <td align="left" valign="top" class="paddingleftCreates">

                  <table width="93%" border="0" cellpadding="3" cellspacing="0">
                    <tr>
                      <td class="headingorange">
                        <span class="heading"> 
                          <mifos:mifoslabel name="Surveys.definenewsurvey" bundle="SurveysUIResources" /> - 
                        </span> 
                        <mifos:mifoslabel name="Surveys.enter_survey" bundle="SurveysUIResources" />
                      </td>
                    </tr>
                    <tr>
                      <td>
                        <font class="fontnormalRedBold"> 
                          <html-el:errors bundle="SurveysUIResources" /> 
                        </font>
                      </td>
                    </tr>
                    <tr>
                      <td class="fontnormal">
                        <mifos:mifoslabel name="Surveys.create_page_instructions" bundle="SurveysUIResources" />
                        <br/>
                        <mifos:mifoslabel name="funds.mandatoryinstructions" mandatory="yes" bundle="fundUIResources" />
                      </td>
                    </tr>
                  </table>

                  <br>

                  <table width="93%" border="0" cellpadding="3" cellspacing="0">
                    <tr>
                      <td colspan="2" class="fontnormalbold">
                        <mifos:mifoslabel name="Surveys.survey_details" bundle="SurveysUIResources" /> 
                        <br>
                        <br>
                      </td>
                    </tr>
                    <tr class="fontnormal">
                      <td width="27%" align="right">
                        <mifos:mifoslabel name="Surveys.survey_name" mandatory="yes" bundle="SurveysUIResources" isColonRequired="yes" />
                      </td>
                      <td width="73%" valign="top">
                      <c:choose>
					  <c:when test="${edit}"><html-el:text property="value(name)" disabled="true"/><html-el:hidden property="value(name)"/></c:when>
					  <c:otherwise><mifos:mifosalphanumtext property="value(name)" maxlength="100"/></c:otherwise>
                      </c:choose>
                      </td>
                    </tr>
                    <tr class="fontnormal">
                      <td align="right" valign="top">
                        <mifos:mifoslabel name="Surveys.Appliesto" mandatory="yes" bundle="SurveysUIResources" isColonRequired="yes"/>
                      </td>
                      <td valign="top">
                      <c:choose>
					  <c:when test="${edit}"><html-el:hidden property="value(appliesTo)"/>
					  <mifos:select property="value(appliesTo)" style="width:136px;" disabled="true">
                           <html-el:option value="client"><mifos:mifoslabel name="Surveys.client_type" bundle="SurveysUIResources" /></html-el:option>
                           <html-el:option value="center"><mifos:mifoslabel name="Surveys.center_type" bundle="SurveysUIResources" /></html-el:option>
                           <html-el:option value="group"><mifos:mifoslabel name="Surveys.group_type" bundle="SurveysUIResources" /></html-el:option>
                           <html-el:option value="loan"><mifos:mifoslabel name="Surveys.loan_type" bundle="SurveysUIResources" /></html-el:option>
                           <html-el:option value="savings"><mifos:mifoslabel name="Surveys.savings_type" bundle="SurveysUIResources" /></html-el:option>
                           <html-el:option value="all"><mifos:mifoslabel name="Surveys.all_type" bundle="SurveysUIResources" /></html-el:option>
                        </mifos:select></c:when>
					  <c:otherwise><mifos:select property="value(appliesTo)" style="width:136px;">
                           <html-el:option value="client"><mifos:mifoslabel name="Surveys.client_type" bundle="SurveysUIResources" /></html-el:option>
                           <html-el:option value="center"><mifos:mifoslabel name="Surveys.center_type" bundle="SurveysUIResources" /></html-el:option>
                           <html-el:option value="group"><mifos:mifoslabel name="Surveys.group_type" bundle="SurveysUIResources" /></html-el:option>
                           <html-el:option value="loan"><mifos:mifoslabel name="Surveys.loan_type" bundle="SurveysUIResources" /></html-el:option>
                           <html-el:option value="savings"><mifos:mifoslabel name="Surveys.savings_type" bundle="SurveysUIResources" /></html-el:option>
                           <html-el:option value="all"><mifos:mifoslabel name="Surveys.all_type" bundle="SurveysUIResources" /></html-el:option>
                        </mifos:select>
                        </c:otherwise>
                        </c:choose>
                      </td>
                    </tr>
                    <c:choose>
                    <c:when test="${edit}">
                    <tr class="fontnormal">
                    <td><mifos:mifoslabel name="Surveys.Status" bundle="SurveysUIResources"/>:</td>
                    <td><mifos:select property="value(state)">
                    <html-el:option value="INACTIVE"><mifos:mifoslabel name="Surveys.Inactive"/></html-el:option>
                    <html-el:option value="ACTIVE"><mifos:mifoslabel name="Surveys.Active"/></html-el:option>
                    </mifos:select>
                    </td>
					</tr>
					</c:when>
					<c:otherwise><html-el:hidden property="value(state)" value="ACTIVE"/></c:otherwise>
                    </c:choose>
                    <tr class="fontnormal">
                      <td align="right" valign="top">
                        <mifos:mifoslabel name="Surveys.select_questions" bundle="SurveysUIResources" isColonRequired="yes"/>
                      </td>
                      <td valign="top">
                        <html-el:select size="10" style="width:40em" property="value(newQuestion)">
                          <c:forEach var="question" items="${sessionScope.questionsList}">
                            <html-el:option value="${question.questionId}">
                              <c:out value="${question.shortName}"/>
                            </html-el:option>
                          </c:forEach>
                        </html-el:select>
                      <td valign="top">
                        <html-el:button property="button" styleClass="insidebuttn" onclick="submitSurveyForm('add_new_question')">
                        	<mifos:mifoslabel name="Surveys.Add" bundle="SurveysUIResources" /> &gt;&gt;
                        </html-el:button>
                      </td>
                    </tr>
                  </table>

                  <br/>


                  <table width="98%" border="0" cellpadding="3" cellspacing="0">
                     <tr>
                        <td class="drawtablehd"><mifos:mifoslabel name="Surveys.QuestionName" bundle="SurveysUIResources"/></td>
                        <td class="drawtablehd"><mifos:mifoslabel name="Surveys.Question" bundle="SurveysUIResources"/></td>
                        <td class="drawtablehd"><mifos:mifoslabel name="Surveys.Mandatory" bundle="SurveysUIResources"/></td>
                        <td class="drawtablehd"><mifos:mifoslabel name="Surveys.Delete" bundle="SurveysUIResources"/></td>
                     </tr>
                       <c:forEach items="${sessionScope.addedQuestions}" var="question" varStatus="status">
                         <tr>
                           <td class="drawtablerow"><c:out value="${question.shortName}"/></td>
                           <td class="drawtablerow"><c:out value="${question.questionText}" /></td>
                           <td class="drawtablerow">
                           <html-el:checkbox property="value(mandatory_${question.questionId})" value="1"/>
                           </td>
                           <td class="drawtablerow">
                           	<html-el:button property="button" styleClass="buttn" onclick="submitSurveyForm('delete_new_question&value(questionNum)=${question.questionId}')">
                            	<mifos:mifoslabel name="Surveys.Delete" bundle="SurveysUIResources" /> 
                            </html-el:button>
                           </td>
                         </tr>
                       </c:forEach>
                     </tr>
                  </table>

                  <table width="93%" border="0" cellpadding="0" cellspacing="0">
                    <tr>
                      <td align="center" class="blueline">&nbsp;</td>
                    </tr>
                  </table>

                  <br>

                  <table width="93%" border="0" cellpadding="0" cellspacing="0">
                    <tr>
                      <td align="center">
                        <html-el:submit property="button" styleClass="buttn">
                          <mifos:mifoslabel name="Surveys.button.preview" bundle="SurveysUIResources" />
                        </html-el:submit>&nbsp; 
                        <html-el:button property="calcelButton" styleClass="cancelbuttn" onclick="window.location='AdminAction.do?method=load'">
                          <mifos:mifoslabel name="Surveys.button.cancel" bundle="SurveysUIResources" />
                        </html-el:button>
                      </td>
                    </tr>
                  </table>

                  <br>
                </td>
              </tr>
            </table>

            <br>

            <html-el:hidden property="currentFlowKey" value="${requestScope.currentFlowKey}" />
          </html-el:form> 
        </tiles:put> 
      </tiles:insert>
