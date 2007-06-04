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
	              	<html-el:link action="surveysAction.do?method=viewQuestions&randomNUm=${sessionScope.randomNUm}&currentFlowKey=${requestScope.currentFlowKey}">
          				<mifos:mifoslabel name="Surveys.viewquestions" bundle="SurveysUIResources"/>
          			</html-el:link> 
	              </span>
    	        </td>
	    	  </tr>
			</table>


      <table width="95%" border="0" cellpadding="0" cellspacing="0">
        <table width="95%" border="0" cellpadding="0" cellspacing="0">
        <tr>
          <td align="left" valign="top" class="paddingL15T15"><table width="95%" border="0" cellpadding="3" cellspacing="0">
            <tr>
              <td class="headingorange"><span class="headingorange"><mifos:mifoslabel name="Surveys.viewquestions"/></span></td>

            </tr>
            <br>
            <table width="98%" border="0" cellpadding="3" cellspacing="0">
              <tr>
                <td width="28%" class="drawtablehd"> Question </td>
                <td width="19%" class="drawtablehd">Answer type </td>
                <td width="53%" class="drawtablehd">Answer</td>
              </tr>
              <c:forEach var="question" items="${requestScope.questionsList}">
              <tr>
                <td width="28%" class="drawtablerow"><c:out value="${question.questionText}"/></td>
                <td width="19%" class="drawtablerow"><c:choose>
                       <c:when test="${question.answerType == 2}"><mifos:mifoslabel name="Surveys.Freetext"/></c:when>
                       <c:when test="${question.answerType == 3}"><mifos:mifoslabel name="Surveys.Number"/></c:when>
                       <c:when test="${question.answerType == 4}"><mifos:mifoslabel name="Surveys.Choice"/></c:when>
                       <c:when test="${question.answerType == 5}"><mifos:mifoslabel name="Surveys.Date"/></c:when>
                    </c:choose></td>
                <td width="53%" class="drawtablerow"><c:choose>
                               <c:when test="${question.answerType == 3}">Between <c:out value="${question.numericMin}"/> and <c:out value="${question.numericMax}"/></c:when>
                               <c:when test="${question.answerType == 4}"><c:forEach var="choice" items="${question.choices}" varStatus="ptr"><c:out value="${choice.choiceText}"/><c:if test="${not ptr.last}">, </c:if></c:forEach></c:when>

                               <c:otherwise>&nbsp;</c:otherwise></c:choose></td>
              </tr>
              </c:forEach>
            </table>
          </td>
          </tr>
      </table>
    </td>
  </tr>
</table>
</tiles:put>
</tiles:insert>
