<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="/tags/mifos-html" prefix="mifos"%>
<%@taglib uri="http://struts.apache.org/tags-html-el" prefix="html-el"%>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles"%>

<tiles:insert definition=".view">
<tiles:put name="body" type="string">

<table width="95%" border="0" cellpadding="0" cellspacing="0">
  <tr>
    <td class="bluetablehead05">
      <span class="fontnormal8pt">
        <html-el:link action="AdminAction.do?method=load&randomNUm=${sessionScope.randomNUm}">
          <mifos:mifoslabel name="Surveys.linkAdmin" bundle="SurveysUIResources"/>	
        </html-el:link> /
      </span>
      <span class="fontnormal8pt">
        <html-el:link action="surveysAction.do?method=viewQuestions">
          <mifos:mifoslabel name="Surveys.viewquestions" bundle="SurveysUIResources"/>
        </html-el:link>  /
      </span>
      <span class="fontnormal8pt">
          <c:out value="${question.shortName}"/>
      </span>
    </td>
  </tr>
</table>
<table width="95%" border="0" cellpadding="0" cellspacing="0">
  <tr>
    <td width="70%" align="left" valign="middle" class="paddingL15T15"> <!-- this td represents the entire central content area -->
			<div style="padding:3px" class="headingorange"><c:out value="${question.shortName}"/></div>
			<c:if test="${question.questionState == 0}">
          <img src="pages/framework/images/status_closedblack.gif" width="8" height="9"> <mifos:mifoslabel bundle="SurveysUIResources" name="Surveys.Inactive"/><br/></span>
	        </c:if>
      <br/>
<span class="fontnormal">
		<mifos:mifoslabel bundle="SurveysUIResources" name="Surveys.Question"/>: <c:out value="${question.questionText}"/><br/>
		<mifos:mifoslabel bundle="SurveysUIResources" name="Surveys.Answertype"/>: 
<c:choose>
<c:when test="${question.answerType == 1}"><mifos:mifoslabel name="Surveys.Multiselect"/></c:when>
<c:when test="${question.answerType == 2}"><mifos:mifoslabel name="Surveys.Freetext"/></c:when>
<c:when test="${question.answerType == 3}"><mifos:mifoslabel name="Surveys.Number"/></c:when>
<c:when test="${question.answerType == 4}"><mifos:mifoslabel name="Surveys.Choice"/></c:when>
<c:when test="${question.answerType == 5}"><mifos:mifoslabel name="Surveys.Date"/></c:when>
</c:choose><br/>
<c:choose>
<c:when test="${question.answerType == 3}"> <mifos:mifoslabel bundle="SurveysUIResources" name="Surveys.Answer"/>:
                  <mifos:mifoslabel name="Surveys.between"/><c:out value="${question.numericMin}"/> 
                  <mifos:mifoslabel name="Surveys.and"/> <c:out value="${question.numericMax}"/>
</c:when>
<c:when test="${question.answerType == 1 || question.answerType == 4}"><mifos:mifoslabel bundle="SurveysUIResources" name="Surveys.Answer"/>: 
<c:forEach var="choice" items="${question.choices}" varStatus="ptr"><c:out value="${choice.choiceText}"/>
                    <c:if test="${not ptr.last}">, </c:if>
</c:forEach>
</c:when>
</c:choose></span>
    </td>
    <td valign="top" align="right">
    <br><html-el:link action="questionsAction.do?method=edit_entry&questionId=${sessionScope.question.questionId}">
	<mifos:mifoslabel name="Surveys.EditQuestion" bundle="SurveysUIResources"/>
	</html-el:link>
	</td>
  </tr>
</table>
</tiles:put>
</tiles:insert>
