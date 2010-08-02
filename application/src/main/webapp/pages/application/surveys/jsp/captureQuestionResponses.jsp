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

<tiles:insert definition=".withoutmenu">
	<tiles:put name="body" type="string">
		<span id="page.id" title="QuestionGroup"></span>
		<SCRIPT type="text/javascript" SRC="pages/framework/js/date.js"></SCRIPT>
		<script type="text/javascript" src="pages/framework/js/jquery/jquery-1.4.2.min.js"></script>
		<STYLE TYPE="text/css"><!-- @import url(pages/questionnaire/css/datepicker.css); --></STYLE>
		<STYLE TYPE="text/css"><!-- @import url(pages/questionnaire/css/questionnaire.css); --></STYLE>
		<script type="text/javascript" src="pages/questionnaire/js/jquery.datePicker.min-2.1.2.js"></script>
		<script type="text/javascript" src="pages/questionnaire/js/jquery.keyfilter-1.7.js"></script>
		<script type="text/javascript" src="pages/questionnaire/js/jquery.validate.min.js"></script>
		<script type="text/javascript" src="pages/questionnaire/js/date.js"></script>
		<script type="text/javascript" src="pages/questionnaire/js/dateConfiguration.js"></script>
		<!--[if IE]><script type="text/javascript" src="pages/questionnaire/jquery.bgiframe.js"></script><![endif]-->
		<script type="text/javascript" src="pages/application/surveys/js/captureQuestionResponses_struts.js"></script>
		
		<script type="text/css">
		  .validationErr{
	        color:#FF0000;
	        float:left;
          }
		</script>
            
        <fmt:setLocale value='${sessionScope["LOCALE"]}'/>
		<fmt:setBundle basename="org.mifos.config.localizedResources.LoanUIResources"/>
		<html-el:form action="${requestScope.origFlowRequestURI}">
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
                                 <tr class="fontnormal">
                                     <td width="17%" align="right">
                                         <span id="create_ClientPersonalInfo.label.question">
                                             <c:if test="${question.mandatory}">
                                                 <span class="mandatorytext">
                                                     <font color="#FF0000">*</font>
                                                 </span>
                                             </c:if>
                                             <c:out value="${question.title}" />
                                         </span>:
                                     </td>
                                     <td width="83%">
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
                                         <mifos:select	property="questionGroups[${groupIdx}].sectionDetails[${sectionIdx}].questions[${questionIdx}].value" size="1">
										   <c:forEach var="choiceValue" items="${question.answerChoices}" >
											  <html-el:option value="${choiceValue}">${choiceValue}</html-el:option>
										   </c:forEach>
									     </mifos:select>
                                     </c:if>
                                     
                                      <c:if test="${question.questionType == 'MULTI_SELECT'}">
                                         <fieldset style="width:70%">
				                            <ol class="noPadding">
				                              <c:forEach var="choiceValue" items="${question.answerChoices}" >
				                                 <li class="noPadding">
								    				<html:multibox property="questionGroups[${groupIdx}].sectionDetails[${sectionIdx}].questions[${questionIdx}].valuesAsArray" value="${choiceValue}" /> ${choiceValue}
								                 </li>  
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
							<input type="submit" class="buttn" name="captureQuestionResponses.button.continue" id="captureQuestionResponses.button.continue" value="Continue"/>
							<input type="button" class="cancelbuttn" name="captureQuestionResponses_button_cancel" id="captureQuestionResponses_button_cancel" value="Cancel"/>
                        </td>
                    </tr>
                 </table>
			</c:if>
			<!-- Question Groups end -->
			<input type="hidden" name="captureResponse_cancel" id="captureResponse_cancel" value="${requestScope.cancelToURL}"/>
			<html-el:hidden property="currentFlowKey" value="${requestScope.currentFlowKey}" />
			<html-el:hidden property="method" value="captureQuestionResponses" />
		</html-el:form>
		
	</tiles:put>
</tiles:insert>