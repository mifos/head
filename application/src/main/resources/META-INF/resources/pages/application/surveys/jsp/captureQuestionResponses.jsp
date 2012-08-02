<%-- 
Copyright (c) 2005-2011 Grameen Foundation USA
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

<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@taglib uri="http://struts.apache.org/tags-html-el" prefix="html-el"%>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles"%>
<%@taglib uri="/tags/struts-html" prefix="html"%>
<%@taglib uri="/tags/struts-bean" prefix="bean"%>
<%@taglib uri="/tags/mifos-html" prefix="mifos"%>
<%@ taglib uri="/mifos/customtags" prefix="mifoscustom"%>
<%@ taglib uri="/loan/loanfunctions" prefix="loanfn"%>
<%@ taglib uri="/tags/date" prefix="date"%>
<%@ taglib uri="/sessionaccess" prefix="session"%>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="/mifos/custom-tags" prefix="customtags"%>


<c:set  var="tilestheme" scope="page" value=".withoutmenu" />
<c:if test="${!empty sessionScope.urlMap}">
    <c:set  var="tilestheme" scope="page" value=".withmenu_andsearch" />
</c:if>

<tiles:insert definition="${pageScope.tilestheme}" >
	<tiles:put name="body" type="string">
		<span id="page.id" title="captureQuestionResponse"></span>
		<SCRIPT type="text/javascript" SRC="pages/framework/js/date.js"></SCRIPT>
		<script type="text/javascript" src="pages/js/jquery/jquery-1.4.2.min.js"></script>
	    <script type="text/javascript" src="pages/js/jquery/jquery.ui.datepicker.min.js"></script>
     	<script type="text/javascript" src="pages/js/jquery/jquery-ui-i18n.js"></script>
	    <script type="text/javascript" src="pages/js/jquery/jquery.datePicker.min-2.1.2.js"></script>
	    <script type="text/javascript" src="pages/js/jquery/jquery.keyfilter-1.7.js"></script>
	    <STYLE TYPE="text/css"><!-- @import url(pages/css/datepicker/datepicker.css); --></STYLE>
		<STYLE TYPE="text/css"><!-- @import url(pages/questionnaire/css/questionnaire.css); --></STYLE>
		<script type="text/javascript" src="pages/js/jquery/jquery.validate.min.js"></script>
    	<script type="text/javascript" src="pages/js/jquery/jquery.datePicker.configuration.js"></script>
		<script type="text/javascript" src="pages/application/surveys/js/captureQuestionResponses_struts.js"></script>

		<STYLE type="text/css">
		  .validationErr{
	        color:#FF0000;
	        float:left;
          }
          /* used for altering background */
       tr.bg1 {
            font-family: Arial, Verdana, Helvetica, sans-serif;
            font-size: 9pt;
            font-weight: normal;
            text-decoration: none;
            background-color: #FFFFFF;
            padding-top: 2px;
            padding-right: 5px;
            padding-bottom: 2px;
            padding-left: 5px;
            border-top-width: 1px;
            border-top-style: solid;
            border-top-color: #D7DEEE;
        }
        tr.bg0 {
            font-family: Arial, Verdana, Helvetica, sans-serif;
            font-size: 9pt;
            font-weight: normal;
            text-decoration: none;
            background-color: #F2F2F2;
            padding-top: 0px;
            padding-right: 0px;
            padding-bottom: 0px;
            padding-left: 0px;
        }
		</STYLE>
        
        <fmt:setLocale value='${sessionScope["org.apache.struts.action.LOCALE"]}'/>
		<fmt:setBundle basename="org.mifos.config.localizedResources.SurveysUIResources"/>
		<html-el:form action="${requestScope.origFlowRequestURI}">
            <c:if test="${!empty sessionScope.urlMap}">
            <table width="95%" border="0" cellpadding="0" cellspacing="0">
                <tr>
                    <td class="bluetablehead05">
						<span class="fontnormal8pt">
	          				<customtags:headerLink/>
	        			</span>
                    </td>
                </tr>
            </table>
            </c:if>
			<logic:messagesPresent>
                <table width="93%" border="0" cellspacing="0" cellpadding="0">
                <tr>
                    <td>
                        <font class="fontnormalRedBold">
                            <span id="captureQuestionResponse.error.message">
                              <html-el:errors bundle="ClientUIResources" />
                            </span>
                        </font>
                    </td>
                </tr>
                </table>
                <br>
            </logic:messagesPresent>
			<c:set value="${requestScope.questionsHostForm.questionGroups}" var="questionGroups" />
			<!-- Question Groups -->
			<c:if test="${!empty questionGroups}">
                 <table width="93%" border="0" cellpadding="3" cellspacing="0">
                     <c:forEach var="group" items="${questionGroups}" varStatus="groupLoopStatus">
                         <bean:define id="groupIdx"> <c:out value="${groupLoopStatus.index}" /> </bean:define>
                         <c:forEach var="section" items="${group.sectionDetails}" varStatus="sectionLoopStatus">
                             <bean:define id="sectionIdx"><c:out value="${sectionLoopStatus.index}" /></bean:define>
                             <tr><td>&nbsp;</td></tr>
                             <tr>
                                 <td colspan="2" class="fontnormalbold"><c:out value="${section.name}" /><br></td>
                             </tr>
                             <c:forEach var="question" items="${section.questions}" varStatus="questionLoopStatus">
                                 <bean:define id="questionIdx">
                                     <c:out value="${questionLoopStatus.index}" />
                                 </bean:define>
                                 <tr class="fontnormal bg${(questionLoopStatus.index + 1) % 2}">
                                     <td width="26%" align="right" valign="top">
                                         <span id="create_ClientPersonalInfo.label.question">
                                             <c:if test="${question.mandatory}">
                                                 <span class="mandatorytext">
                                                     <font color="#FF0000">*</font>
                                                 </span>
                                             </c:if>
                                             <c:out value="${question.text}" />
                                         </span>:
                                     </td>
                                     <td width="74%">
                                     <html-el:hidden property='questionGroups[${groupIdx}].sectionDetails[${sectionIdx}].questions[${questionIdx}].id' value="${question.id}"></html-el:hidden>
                                     <c:if test="${question.questionType == 'FREETEXT'}">
                                         <mifos:mifosalphanumtext styleId="create_ClientPersonalInfo.input.customField"
                                             property='questionGroups[${groupIdx}].sectionDetails[${sectionIdx}].questions[${questionIdx}].value' maxlength="200" />
                                     </c:if>
                                     <c:if test="${question.questionType == 'NUMERIC'}">
                                         <mifos:mifosnumbertext styleId="create_ClientPersonalInfo.input.customField"
                                             property='questionGroups[${groupIdx}].sectionDetails[${sectionIdx}].questions[${questionIdx}].value' maxlength="200" />
                                     </c:if>
                                     <c:if test="${question.questionType == 'DATE'}">
                                         <mifos:mifosalphanumtext styleId="create_ClientPersonalInfo.input.customField" styleClass="date-pick"
                                             property='questionGroups[${groupIdx}].sectionDetails[${sectionIdx}].questions[${questionIdx}].value' maxlength="10" />
                                     </c:if>

                                     <c:if test="${question.questionType == 'SINGLE_SELECT'}">
                                         <c:if test="${fn:length(question.answerChoices) > 6}">
                                             <mifos:select	property="questionGroups[${groupIdx}].sectionDetails[${sectionIdx}].questions[${questionIdx}].value" size="1">
                                                 <c:forEach var="choiceValue" items="${question.answerChoices}" >
                                                     <html-el:option value="${choiceValue}">${choiceValue}</html-el:option>
                                                 </c:forEach>
                                             </mifos:select>
                                         </c:if>
                                         <c:if test="${fn:length(question.answerChoices) <= 6}">
                                             <c:forEach var="choiceValue" items="${question.answerChoices}">
                                                 <html-el:radio property="questionGroups[${groupIdx}].sectionDetails[${sectionIdx}].questions[${questionIdx}].value" value="${choiceValue}">
                                                     <c:out value="${choiceValue}"/>
                                                 </html-el:radio>
                                                 <br>
                                             </c:forEach>
                                         </c:if>
                                     </c:if>

                                      <c:if test="${question.questionType == 'MULTI_SELECT'}">
                                         <fieldset style="width:70%" class="right_section">
				                            <ol class="noPadding">
				                              <input type="checkbox" checked="checked" name="questionGroups[${groupIdx}].sectionDetails[${sectionIdx}].questions[${questionIdx}].valuesAsArray" value="" style="display:none;" />
				                              <c:forEach var="choiceValue" items="${question.answerChoices}" >
				                                 <li class="noPadding">
								    				<html:multibox property="questionGroups[${groupIdx}].sectionDetails[${sectionIdx}].questions[${questionIdx}].valuesAsArray" value="${choiceValue}" /> ${choiceValue}
								                 </li>
							                  </c:forEach>
				                            </ol>
				                          </fieldset>
                                     </c:if>
                                     <c:if test="${question.questionType == 'SMART_SELECT'}">
                                         <fieldset style="width:70%" class="right_section">

                                            <div class="noPadding" style="list-style: none;">
				                                <input type="text" autocomplete="off" id="txtListSearch" name="txtListSearch" style="width:21em;" class="txtListSearch" />
				                            </div>
				                            <ol class="questionList" id="questionList" style="overflow:auto; width:20em; height:180px; border:1px solid #336699; padding:5px; margin:12px 0;">
                                              <input type="checkbox" checked="checked" name="questionGroups[${groupIdx}].sectionDetails[${sectionIdx}].questions[${questionIdx}].valuesAsArray" value="" style="display:none;" />
				                              <c:forEach var="choiceValue" items="${question.answerChoices}" >
		            							<c:choose>
                                                  <c:when test="${choiceValue.tags !=null && !empty choiceValue.tags}">
                                                    <c:forEach var="tagValue" items="${choiceValue.tags}" >
                                                         <li class="noPadding" style="overflow:hidden; margin-right: -3px;">
                                                            <html:multibox property="questionGroups[${groupIdx}].sectionDetails[${sectionIdx}].questions[${questionIdx}].valuesAsArray" value="${choiceValue}:${tagValue}" style="float:left;" />
                                                            <label tag="${tagValue}" choice="${choiceValue}" style="float:left; width: 180px; margin: 0;">${choiceValue}&nbsp;:&nbsp;${tagValue}</label>
                                                         </li>
                                                    </c:forEach>
                                                  </c:when>
            									  <c:otherwise>
                                                     <li class="noPadding" style="overflow:hidden; margin-right: -3px;">
                                                        <html:multibox property="questionGroups[${groupIdx}].sectionDetails[${sectionIdx}].questions[${questionIdx}].valuesAsArray" value="${choiceValue}" style="float:left;" />
                                                        <label tag="" choice="${choiceValue}" style="float:left; width: 180px; margin: 0;">${choiceValue}</label>
                                                     </li>
            									  </c:otherwise>
            									</c:choose>
							                  </c:forEach>
				                            </ol>
				                          </fieldset>
                                     </c:if>
                                     <c:if test="${question.questionType == 'SMART_SINGLE_SELECT'}">
                                         <fieldset style="width:70%" class="right_section">

                                            <div class="noPadding" style="list-style: none;">
				                                <input type="text" autocomplete="off" id="txtListSearch" name="txtListSearch" style="width:21em;" class="txtListSearch" />
				                            </div>
				                            <ol class="questionList" id="questionList" style="overflow:auto; width:20em; height:180px; border:1px solid #336699; padding:5px; margin:12px 0;">
                                              <input type="radiobutton" checked="checked" name="questionGroups[${groupIdx}].sectionDetails[${sectionIdx}].questions[${questionIdx}].valuesAsArray" value="" style="display:none;" />
				                              <c:forEach var="choiceValue" items="${question.answerChoices}" >
		            							<c:choose>
                                                  <c:when test="${choiceValue.tags !=null && !empty choiceValue.tags}">
                                                    <c:forEach var="tagValue" items="${choiceValue.tags}" >
                                                         <li class="noPadding" style="overflow:hidden; margin-right: -3px;">
                                                            <html:radio property="questionGroups[${groupIdx}].sectionDetails[${sectionIdx}].questions[${questionIdx}].valuesAsArray" value="${choiceValue}:${tagValue}" style="float:left;" />
                                                            <label tag="${tagValue}" choice="${choiceValue}" style="float:left; width: 180px; margin: 0;">${choiceValue}&nbsp;:&nbsp;${tagValue}</label>
                                                         </li>
                                                    </c:forEach>
                                                  </c:when>
            									  <c:otherwise>
                                                     <li class="noPadding" style="overflow:hidden; margin-right: -3px;">
                                                        <html:radio property="questionGroups[${groupIdx}].sectionDetails[${sectionIdx}].questions[${questionIdx}].valuesAsArray" value="${choiceValue}" style="float:left;" />
                                                        <label tag="" choice="${choiceValue}" style="float:left; width: 180px; margin: 0;">${choiceValue}</label>
                                                     </li>
            									  </c:otherwise>
            									</c:choose>
							                  </c:forEach>
				                            </ol>
				                          </fieldset>
                                     </c:if>
                                     </td>
                                 </tr>

                             </c:forEach>
                         </c:forEach>
                     </c:forEach>
                     <tr>
                        <td>&nbsp;</td>
						<td align="left">
							<input type="submit" class="buttn" name="captureQuestionResponses.button.continue" id="captureQuestionResponses.button.continue" value="<fmt:message key='Surveys.button.continue'/>" />
							<input type="button" class="cancelbuttn" name="captureQuestionResponses_button_cancel" id="captureQuestionResponses_button_cancel" value="<fmt:message key='Surveys.button.cancel'/>" />
                        </td>
                    </tr>
                 </table>
			</c:if>
			<!-- Question Groups end -->
			<input type="hidden" name="captureResponse_cancel" id="captureResponse_cancel" value="${requestScope.cancelToURL}" />
			<html-el:hidden property="currentFlowKey" value="${requestScope.currentFlowKey}" />
			<html-el:hidden property="method" value="captureQuestionResponses" />
            <html-el:hidden property="perspective" value="${requestScope.perspective}" />
		</html-el:form>

	</tiles:put>
</tiles:insert>