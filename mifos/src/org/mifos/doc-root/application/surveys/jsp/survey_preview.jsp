<%@ taglib uri="/tags/struts-tiles" prefix="tiles"%>
<%@ taglib uri="/tags/struts-html-el" prefix="html-el"%>
<%@ taglib uri="/tags/mifos-html" prefix="mifos"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="/sessionaccess" prefix="session"%>
<tiles:insert definition=".create">
	<tiles:put name="body" type="string">
	<script src="pages/application/surveys/js/questions.js" type="text/javascript"></script>
<html-el:form action="/surveysAction.do?method=create" focus="name">


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
                    <mifos:mifoslabel name="Surveys.survey_name" bundle="SurveysUIResources" />: <c:out value="${sessionScope.surveyActionForm.name}"/>   <br>
                    <mifos:mifoslabel name="Surveys.Appliesto" bundle="SurveysUIResources" />: 

<c:choose>
<c:when test="${sessionScope.surveyActionForm.appliesTo == 'customers'}"><mifos:mifoslabel name="Surveys.customers_type" bundle="SurveysUIResources" /></c:when>
<c:when test="${sessionScope.surveyActionForm.appliesTo == 'accounts'}"><mifos:mifoslabel name="Surveys.accounts_type" bundle="SurveysUIResources" /></c:when>
<c:when test="${sessionScope.surveyActionForm.appliesTo == 'both'}"><mifos:mifoslabel name="Surveys.both_type" bundle="SurveysUIResources" /></c:when>
</c:choose>
</p>

                    <p><a href="http://www.google.com"><mifos:mifoslabel name="Surveys.Printerversion" bundle="SurveysUIResources" /></a> <br>
                      <br>
                      <span class="fontnormalbold"><mifos:mifoslabel name="Surveys.Questions" bundle="SurveysUIResources" /></span><br>
                      <br>
                                      </p>
                    <table width="98%" border="0" cellpadding="3" cellspacing="0">
  <tr>

    <td width="39%" class="drawtablehd"> <mifos:mifoslabel name="Surveys.Questiontitle" bundle="SurveysUIResources" /></td>
    <td width="14%" class="drawtablehd"><mifos:mifoslabel name="Surveys.Answertype" bundle="SurveysUIResources" /></td>
    <td width="44%" class="drawtablehd"><mifos:mifoslabel name="Surveys.Answer" bundle="SurveysUIResources" /></td>
  </tr>
<c:choose>
<c:when test="${sessionScope.itemCount > 0}">
<c:forEach items="${sessionScope.addedQuestions}" var="question">
  <tr>
    <td width="39%" class="drawtablerow"><c:out value="${question.questionText}"/></td>
    <td width="14%" class="drawtablerow">
                  <c:choose>
                    	<c:when test="${question.answerType == 2}"><mifos:mifoslabel name="Surveys.Freetext"/></c:when>
                    	<c:when test="${question.answerType == 3}"><mifos:mifoslabel name="Surveys.Number"/></c:when>
                    	<c:when test="${question.answerType == 4}"><mifos:mifoslabel name="Surveys.Choice"/></c:when>
                    	<c:when test="${question.answerType == 5}"><mifos:mifoslabel name="Surveys.Date"/></c:when>
                  </c:choose>


</td>
    <td width="44%" class="drawtablerow">
<c:choose>
                               <c:when test="${question.answerType == 3}">Between <c:out value="${question.numericMin}"/> and <c:out value="${question.numericMax}"/></c:when>
                               <c:when test="${question.answerType == 4}"><c:forEach var="choice" items="${question.choices}" varStatus="ptr"><c:out value="${choice.choiceText}"/><c:if test="${not ptr.last}">, </c:if></c:forEach></c:when>

                               <c:otherwise>&nbsp;</c:otherwise></c:choose>

</td>
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
       <html-el:button property="calcelButton" onclick="submitSurveyForm('edit')" styleClass="buttn"><mifos:mifoslabel name="Surveys.button.edit" bundle="SurveysUIResources" /></html-el:button>                 </td>
                </tr>
              </table>
              <table width="93%" border="0" cellpadding="0" cellspacing="0">

                <tr>
                  <td align="center" class="blueline">&nbsp;                  </td>
                </tr>
              </table>              <br>
							<table width="93%" border="0" cellpadding="0" cellspacing="0">
								<tr>
									<td align="center"><html-el:submit style="width:65px;"
										property="button" styleClass="buttn">
										<mifos:mifoslabel name="Surveys.button.submit"
											bundle="SurveysUIResources" />
									</html-el:submit>&nbsp; <html-el:button property="calcelButton" style="width:65px;"
										styleClass="cancelbuttn">
										<mifos:mifoslabel name="Surveys.button.cancel" bundle="SurveysUIResources" />
									</html-el:button></td>
								</tr>
							</table>
            <br></td>
          </tr>
        </table>

</html-el:form> 
					</tiles:put> </tiles:insert>
