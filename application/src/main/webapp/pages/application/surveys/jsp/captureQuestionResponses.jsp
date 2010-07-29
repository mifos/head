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
                                         <date:datetag property="questionGroups[${groupIdx}].sectionDetails[${sectionIdx}].questions[${questionIdx}].value" />
                                     </c:if>
                                     </td>
                                 </tr>
                                 
                             </c:forEach>
                         </c:forEach>
                     </c:forEach>
                     <tr>
                        <td>&nbsp;</td>
						<td align="left">
							<input type="submit" class="buttn" id="captureQuestionResponses.button.continue" value="Continue" name="continueButton">
							<input type="button" class="cancelbuttn" id="captureQuestionResponses.button.cancel" value="Cancel" name="cancelButton">
                        </td>
                    </tr>
                 </table>
			</c:if>
			<!-- Question Groups end -->
			<div style="display: none;" id="hiddenDivBox">
                <a alt="" href="${requestScope.cancelToURL}" id="captureQuestionResponses.link.cancel">cancel</a>    
            </div>


			<html-el:hidden property="currentFlowKey" value="${requestScope.currentFlowKey}" />
			<html-el:hidden property="method" value="captureQuestionResponses" />
		</html-el:form>
		<script type="text/javascript">
            $(document).ready(function(evt){
                $("#captureQuestionResponses.button.cancel").click(function(event) {
                    event.preventDefault();
                    $("#captureQuestionResponses.link.cancel").click();
                });
            });
	    </script>
	</tiles:put>
</tiles:insert>