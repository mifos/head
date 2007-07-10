<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="/tags/mifos-html" prefix="mifos"%>
<%@taglib uri="http://struts.apache.org/tags-html-el" prefix="html-el"%>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles"%>
<%@page import="org.mifos.application.surveys.helpers.AnswerType"%>
<tiles:insert definition=".clientsacclayoutsearchmenu">
<tiles:put name="body" type="string">
	<script src="pages/application/surveys/js/questions.js" type="text/javascript"></script>
			<table width="95%" border="0" cellpadding="0" cellspacing="0">
			  <tr>
          		<td class="bluetablehead05">
	             <span class="fontnormal8pt">
	          	 	<html-el:link action="AdminAction.do?method=load&randomNUm=${sessionScope.randomNUm}">
						<mifos:mifoslabel name="Surveys.linkAdmin" bundle="SurveysUIResources"/>	
					</html-el:link> /
	              </span>
	              <span class="fontnormal8pt">
	              	<html-el:link action="surveysAction.do?method=load&randomNUm=${sessionScope.randomNUm}&currentFlowKey=${requestScope.currentFlowKey}">
          				<mifos:mifoslabel name="Surveys.AddQuestions" bundle="SurveysUIResources"/>
          			</html-el:link> 
	              </span>
    	        </td>
	    	  </tr>
			</table>
      <table width="95%" border="0" cellpadding="0" cellspacing="0">
        <tr>
          <td width="70%" align="left" valign="middle" class="paddingL15T15"><table width="98%" border="0" cellspacing="0" cellpadding="3">
            <tr>
              <td width="35%" class="headingorange"><mifos:mifoslabel name="Surveys.AddQuestions" bundle="SurveysUIResources"/></td>
            </tr>

          </table>     
          <br>

<font class="fontnormalRedBold"><html-el:errors bundle="SurveysUIResources" /></font>
<!-- actual form content -->
            <html-el:form action="/questionsAction.do?method=defineQuestions" focus="questionText">
            <table width="98%" border="0" cellpadding="3" cellspacing="0">
              <tr class="fontnormal">
                <td width="24%" align="right"><mifos:mifoslabel name="Surveys.QuestionName" bundle="SurveysUIResources"/></td>
                <td width="76%"><html-el:text property="value(shortName)"/></td>
              </tr>
              <tr class="fontnormal">
                <td width="24%" align="right"><mifos:mifoslabel name="Surveys.Question" bundle="SurveysUIResources"/></td>
                <td width="76%"><html-el:text property="value(questionText)"/></td>
              </tr>

              <tr class="fontnormal">
                <td align="right"><mifos:mifoslabel name="Surveys.Answertype" bundle="SurveysUIResources"/></td>

                <td><html-el:select property="value(answerType)" styleId="answerType" onchange="setDisable();">
                  <html-el:option value="1"><mifos:mifoslabel name="Surveys.Multiselect"/></html-el:option>
                  <html-el:option value="2"><mifos:mifoslabel name="Surveys.Freetext"/></html-el:option>
                  <html-el:option value="3"><mifos:mifoslabel name="Surveys.Number"/></html-el:option>
                  <html-el:option value="4"><mifos:mifoslabel name="Surveys.Singleselect"/></html-el:option>
                  <html-el:option value="5"><mifos:mifoslabel name="Surveys.Date"/></html-el:option>
                </html-el:select></td>
              </tr>
              <tr id="choiceInputsElement1" class="fontnormal">
                <td align="right"><mifos:mifoslabel name="Surveys.Answerchoice" bundle="SurveysUIResources"/></td>
                <td><html-el:text property="value(choice)" styleId="choice" disabled="true"/>
                  <button id="AddButton" class="insidebuttn" style="width:65px" onclick="submitQuestionForm('addChoice')" disabled="true">
                  <mifos:mifoslabel name="Surveys.Add" bundle="SurveysUIResources"/> &gt;&gt;
                  </button>
                  </td>
              </tr>
                  <tr valign="top" class="fontnormal">
                    <td>&nbsp;</td>
                    <td align="left">
            <table border="0" cellpadding="3" cellspacing="0">
              <tr>
                <td width="28%" class="drawtablehd"><mifos:mifoslabel name="Surveys.Choice" bundle="SurveysUIResources"/></td>
                <td width="19%" class="drawtablehd"><mifos:mifoslabel name="Surveys.removelink" bundle="SurveysUIResources"/></td>
              </tr>

              <c:forEach var="choice" varStatus="status" items="${newQuestionChoices}">
              <tr>
                <td width="28%" class="drawtablerow"><c:out value="${choice}"/></td>
                <td width="19%" class="drawtablerow"><html-el:link action="questionsAction?method=deleteChoice&choiceNum=${status.index}&randomNUm=${sessionScope.randomNUm}"><mifos:mifoslabel name="Surveys.removelink" bundle="SurveysUIResources"/></html-el:link> </td>
              </tr>
	      </c:forEach>
            </table>
                    </td>
                  </tr>
            <tr>
              <td>&nbsp;</td><td>&nbsp;</td>
            </tr>
            <tr>
               <td>&nbsp;</td>
               <td><button id="AddQuestionButton" class="buttn" onclick="submitQuestionForm('addQuestion')">
                     <mifos:mifoslabel name="Surveys.AddQuestion" bundle="SurveysUIResources"/>
                   </button>
               </td>
            </tr>
            <tr>
             <td colspan="2" class="blueline" align="left">&nbsp;</td>
            </tr>

            </table>
            <table width="98%" border="0" cellpadding="3" cellspacing="0">
              <tr>
                <td class="drawtablehd"><mifos:mifoslabel name="Surveys.QuestionName" bundle="SurveysUIResources"/></td>
                <td class="drawtablehd"><mifos:mifoslabel name="Surveys.Question" bundle="SurveysUIResources"/></td>
                <td class="drawtablehd"><mifos:mifoslabel name="Surveys.Answertype" bundle="SurveysUIResources"/> </td>
                <td class="drawtablehd"><mifos:mifoslabel name="Surveys.Choices" bundle="SurveysUIResources"/></td>
                <td class="drawtablehd"><mifos:mifoslabel name="Surveys.Remove" bundle="SurveysUIResources"/></td>

              </tr>
              <c:forEach var="question" items="${newQuestions}" varStatus="status">
              <tr>
              	<td class="drawtablerow"><c:out value="${question.shortName}"/></td>
                <td class="drawtablerow"><c:out value="${question.questionText}"/></td>
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
                <td class="drawtablerow"><html-el:link action="questionsAction?method=deleteNewQuestion&newQuestionNum=${status.index}&randomNUm=${sessionScope.randomNUm}">
                [<mifos:mifoslabel name="Surveys.removelink" bundle="SurveysUIResources"/>]
                </html-el:link></td>
              </tr>
              </c:forEach>
              <tr>

                <td class="drawtablerow">&nbsp;</td>
                <td class="drawtablerow">&nbsp;</td>
                <td class="drawtablerow">&nbsp;</td>
                <td class="drawtablerow">&nbsp;</td>
                <td class="drawtablerow">&nbsp;</td>
              </tr>
            </table>

							<br>
							<table width="93%" border="0" cellpadding="0" cellspacing="0">
								<tr>
									<td align="center">
<button id="AddQuestionButton" class="buttn" onclick="submitQuestionForm('createQuestions')">
                     <mifos:mifoslabel name="Surveys.button.submit" bundle="SurveysUIResources"/>
                   </button>

&nbsp; <html-el:button property="cancelButton" style="width:65px;"
										styleClass="cancelbuttn" onclick="window.location='AdminAction.do?method=load'">
										<mifos:mifoslabel name="Surveys.button.cancel" bundle="SurveysUIResources" />
									</html-el:button></td>
								</tr>
							</table>

              </html-el:form>
<!-- end actual form content -->
            </table>

<script type="text/javascript">setDisable()</script>
</tiles:put>
</tiles:insert>
