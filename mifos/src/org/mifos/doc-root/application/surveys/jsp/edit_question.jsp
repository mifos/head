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
	              	<html-el:link action="surveysAction.do?method=load&randomNUm=${sessionScope.randomNUm}&currentFlowKey=${sessionScope.currentFlowKey}">
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
              <tr class="fontnormal">
                <td width="24%" align="right"><mifos:mifoslabel name="Surveys.QuestionName" bundle="SurveysUIResources"/></td>
                <td width="76%"><html-el:text property="shortName"/></td>
              </tr>
              <tr class="fontnormal">
                <td width="24%" align="right"><mifos:mifoslabel name="Surveys.Question" bundle="SurveysUIResources"/></td>
                <td width="76%"><html-el:text property="questionText"/></td>
              </tr>
			  <tr class="fontnormal">
                <td width="24%" align="right"><mifos:mifoslabel name="Surveys.Status" bundle="SurveysUIResources"/></td>
                <td width="76%">
                <html-el:select property="questionState" styleId="questionState">
				<html-el:option value="0"><mifos:mifoslabel bundle="SurveysUIResources" name="Surveys.Inactive"/></html-el:option>                
				<html-el:option value="1"><mifos:mifoslabel bundle="SurveysUIResources" name="Surveys.Active"/></html-el:option>                
				</html-el:select>
                </td>
              </tr>
              <tr class="fontnormal">
                <td align="right"><mifos:mifoslabel name="Surveys.Answertype" bundle="SurveysUIResources"/></td>

                <td><html-el:select property="answerType" value="${sessionScope.question.answerType}" styleId="answerType" disabled="true">
                  <html-el:option value="1"><mifos:mifoslabel name="Surveys.Multiselect"/></html-el:option>
                  <html-el:option value="2"><mifos:mifoslabel name="Surveys.Freetext"/></html-el:option>
                  <html-el:option value="3"><mifos:mifoslabel name="Surveys.Number"/></html-el:option>
                  <html-el:option value="4"><mifos:mifoslabel name="Surveys.Singleselect"/></html-el:option>
                  <html-el:option value="5"><mifos:mifoslabel name="Surveys.Date"/></html-el:option>
                </html-el:select></td>
              </tr>
              <tr id="choiceInputsElement1" class="fontnormal">
                <td align="right"><mifos:mifoslabel name="Surveys.Answerchoice" bundle="SurveysUIResources"/></td>
                <td><html-el:text property="choice" styleId="choice" disabled="true"/>
                  <input id="AddButton" type="button" class="insidebuttn" value="Add &gt;&gt;" style="width:65px"  onclick="submitQuestionForm('addChoice')" disabled="true"></td>
              </tr>
                  <tr valign="top" class="fontnormal">
                    <td>&nbsp;</td>
                    <td align="left">
            <table border="0" cellpadding="3" cellspacing="0">
              <c:forEach var="choice" varStatus="status" items="${newQuestionChoices}">
              <tr>
                <td width="28%" class="fontnormal"><c:out value="${choice.choiceText}"/></td>
              </tr>
	      </c:forEach>
            </table>
                    </td>
                  </tr>
							<br>
							<table width="93%" border="0" cellpadding="0" cellspacing="0">
								<tr>
									<td align="center">
<button id="AddQuestionButton" class="buttn" onclick="submitQuestionForm('preview_entry')">
                     <mifos:mifoslabel name="Surveys.button.preview" bundle="SurveysUIResources"/>
                   </button>

&nbsp; <html-el:button property="cancelButton" style="width:65px;"
										styleClass="cancelbuttn">
										<mifos:mifoslabel name="Surveys.button.cancel" bundle="SurveysUIResources" />
									</html-el:button></td>
								</tr>
							</table>

              </html-el:form>
<!-- end actual form content -->
            </table>
</tiles:put>
</tiles:insert>
