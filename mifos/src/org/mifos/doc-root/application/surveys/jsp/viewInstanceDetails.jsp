<!-- /**

* viewClientDetails.jsp    version: 1.0



* Copyright (c) 2005-2006 Grameen Foundation USA

* 1029 Vermont Avenue, NW, Suite 400, Washington DC 20005

* All rights reserved.



* Apache License
* Copyright (c) 2005-2006 Grameen Foundation USA
*

* Licensed under the Apache License, Version 2.0 (the "License"); you may
* not use this file except in compliance with the License. You may obtain
* a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
*

* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and limitations under the

* License.
*
* See also http://www.apache.org/licenses/LICENSE-2.0.html for an explanation of the license

* and how it is applied.

*

*/
 -->
<%@taglib uri="/tags/date" prefix="date"%>
<%@ taglib uri="/mifos/customtags" prefix="mifoscustom"%>
<%@taglib uri="/tags/mifos-html" prefix="mifos"%>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles"%>
<%@taglib uri="http://struts.apache.org/tags-html-el" prefix="html-el"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@ taglib uri="/userlocaledate" prefix="userdatefn"%>
<%@ taglib uri="/mifos/custom-tags" prefix="customtags"%>
<%@ taglib uri="/sessionaccess" prefix="session"%>
<%@ taglib uri="/customer/customerfunctions" prefix="customerfn"%>
<!-- Tils definition for the header and menu -->
<tiles:insert definition=".clientsacclayoutsearchmenu">
	<tiles:put name="body" type="string">
    		<script src="pages/application/surveys/js/questions.js" type="text/javascript"></script>
		<html-el:form action="surveyInstanceAction.do?method=create">

			<table width="95%" border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td class="bluetablehead05">
            <span class="fontnormal8pt"> <customtags:headerLink selfLink="false" /> </span>
          </td>
				</tr>
			</table>

      <table width="95%" border="0" cellpadding="0" cellspacing="0">
        <tr>
          <td align="left" valign="top" class="paddingL15T15" >              <table width="95%" border="0" cellpadding="0" cellspacing="0">
            <tr>
              <td width="83%" class="headingorange"><span class="heading">
                  <c:out value="${sessionScope.businessObjectName}"/> -</span> 
               <mifos:mifoslabel name="Surveys.previewsurveydata" bundle="SurveysUIResources"/> </td>
            </tr>
          </table>

            <table width="95%" border="0" cellpadding="0" cellspacing="0">
              <tr>
                <td><br>
                    <span class="fontnormal"> <mifos:mifoslabel name="Surveys.instance.instructions" bundle="SurveysUIResources"/></span></td>
                    <html-el:errors bundle="SurveysUIResources"/>
              </tr>
              <tr>
                <td class="blueline"><img src="pages/framework/images/trans.gif" width="10" height="12"></td>
              </tr>

            </table>
            <br>
            <table width="93%" border="0" cellpadding="3" cellspacing="0">
              <tr>
                <td width="77%" class="headingorange"><span class="heading"><c:out value="${sessionScope.retrievedSurvey.name}"/></span></td>
              </tr>
            </table>
            <br>

            <table width="95%" border="0" cellpadding="3" cellspacing="0">
              <tr>
                <td width="29%" height="30" align="right" class="drawtablerow">
                  <span class="fontnormalRed">*</span><span class="fontnormal8ptbold">
                    <mifos:mifoslabel name="Surveys.instance.dateofsurvey" bundle="SurveysUIResources"/>:
                  </span>
                </td>
                <td width="71%" class="drawtablerow">
                  <span class="fontnormal8pt">
                    <html-el:text property="value(dateSurveyed_DD)" disabled="true"maxlength="2" size="2" style="width:1.5em"/>
                    &nbsp;DD&nbsp;
                    <html-el:text property="value(dateSurveyed_MM)" disabled="true" maxlength="2" size="2" style="width:1.5em"/>
                    &nbsp;MM&nbsp;
                    <html-el:text property="value(dateSurveyed_YY)" disabled="true" maxlength="4" size="4" style="width:3em"/>
                    &nbsp;YYYY&nbsp;
                  </span>
                </td>
              </tr>

              <tr>
                <td height="30" align="right" class="drawtablerow">
                <span class="fontnormal8ptbold"><mifos:mifoslabel name="Surveys.instance.surveyedby" bundle="SurveysUIResources"/>:</span></td>
                <td height="30" class="drawtablerow">
                <html-el:text property="value(officerName)" disabled="true"/>
                </td>
              </tr>            

	            <c:forEach var="question" items="${sessionScope.retrievedSurvey.questions}" varStatus="status">
                <tr>
                  <td height="30" colspan="2" class="drawtablerow">&nbsp;</td>
                </tr>

                <tr>
                  <td height="30" colspan="2" class="drawtablerow">
                    <span class="fontnormal8ptbold">
                      <c:out value="${status.index + 1}"/>.  <c:out value="${question.question.questionText}"/>
                    </span>
                  </td>
                </tr>

                <tr>
                  <td height="30" colspan="2" class="drawtablerow">
                    <c:choose>
                      <c:when test="${question.question.answerType == 4}"> <!-- single select question -->
                        <c:forEach var="choice" items="${question.question.choices}">
                          <html-el:radio disabled="true" property="value(response_${question.question.questionId})" value="${choice.choiceId}">
                            <c:out value="${choice.choiceText}"/>
                          </html-el:radio>
                          <br>
                        </c:forEach>
                        <html-el:radio disabled="true" property="value(response_${question.question.questionId})" value="" style="visibility:hidden;checked:true"/>
                      </c:when>
                      <c:when test="${question.question.answerType == 2}"> <!-- freetext question -->
                        <html-el:textarea disabled="true" property="value(response_${question.question.questionId})" cols="70" rows="10" />
                      </c:when>
                      <c:when test="${question.question.answerType == 5}"> <!-- date question -->
                        <date:datetag isDisabled="true" property="response_${question.question.questionId})" renderstyle="simplemapped"/>
                      </c:when>
                      <c:when test="${question.question.answerType == 3}"> <!-- number question -->
                        <html-el:text disabled="true" property="value(response_${question.question.questionId})"/>
                      </c:when>
                    </c:choose>
                  </td>
                </tr>
              </c:forEach>

              <tr>
                <td height="30" colspan="2" valign="bottom">&nbsp;</td>
              </tr>
            </table>

            <table width="95%" border="0" cellpadding="0" cellspacing="0">

              <tr>
                <td>                  <input type="clear" class="insidebuttn" value="Clear All" style="width:65px;" >                </td>
              </tr>
            </table>
            <br>
            <table width="95%" border="0" cellpadding="0" cellspacing="0">
              <tr>
                <td class="blueline">&nbsp;</td>

              </tr>
            </table>            <br>
              <table width="95%" border="0" cellpadding="0" cellspacing="0">
                <tr>
                  <td align="center">
                    <html-el:submit style="width:65px;" property="button" styleClass="buttn">
                      <mifos:mifoslabel name="Surveys.button.submit" bundle="SurveysUIResources" />
                    </html-el:submit>&nbsp; 
                    <html-el:button property="calcelButton" style="width:65px;" styleClass="cancelbuttn" onclick="window.location='adminAction.do?method=load'">
                      <mifos:mifoslabel name="Surveys.button.cancel" bundle="SurveysUIResources" />
                    </html-el:button>
</td>
                </tr>
              </table>              <br>

              <br>
          </td>
        </tr>
      </table>
      <html-el:hidden property="value(globalNum)" value="${param.globalNum}"/>
    </html-el:form>
  </tiles:put>
</tiles:insert>
