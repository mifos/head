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
          <mifos:mifoslabel name="Surveys.viewquestions" bundle="SurveysUIResources"/>
      </span>
    </td>
  </tr>
</table>

<table width="95%" border="0" cellpadding="0" cellspacing="0">
  <tr>
    <td width="70%" align="left" valign="middle" class="paddingL15T15"> <!-- this td represents the entire central content area -->
        <div style="padding:3px" class="headingorange"><mifos:mifoslabel name="Surveys.viewquestions"/>
            <br/>
            <br/>
            <span class="fontnormal">
                <mifos:mifoslabel name="Surveys.viewQuestions_instructions"/>
                <a href="questionsAction.do?method=defineQuestions">
                    <mifos:mifoslabel name="Surveys.definequestion"/>
                </a>
            </span>
        </div>
        <c:if test="${requestScope.itemCount > 0}">
        <br/>
        <span class="fontnormal" style="color:orange; font-weight:bold">
            <mifos:mifoslabel name="Surveys.newquestionsmsgprefix"/>
            <c:out value="${requestScope.itemCount}"/>
            <mifos:mifoslabel name="Surveys.newquestionsmsgsuffix"/>
        </span>
        <br/>
        <a href="surveysAction.do?method=create_entry">
            <mifos:mifoslabel name="Surveys.definenewsurvey"/>
        </a>
        </c:if>

        <br/>
      <!-- the question list table -->
      <table width="90%" border="0" cellspacing="0" cellpadding="0">
      <c:set var="count" value="0"/>
	  <c:forEach var="question" items="${requestScope.questionsList}">
    	<tr class="fontnormal">
	      <td width="1%"><img src="pages/framework/images/bullet_circle.gif" width="9" height="11"/></td>
    	  <td width="99%">
        <html-el:link href="questionsAction.do?method=get&questionId=${question.questionId}&randomNUm=${sessionScope.randomNUm}">
          <c:out value="${question.shortName}"/>
        </html-el:link>
        <c:if test="${question.questionState == 0}">
          <img src="pages/framework/images/status_closedblack.gif" width="8" height="9"> <mifos:mifoslabel bundle="SurveysUIResources" name="Surveys.Inactive"/>
        </c:if>
      </td>
    </tr>
    <c:set var="count" value="${count + 1}" />
  </c:forEach>
  </table><br>
  <table width="95%" border="0" cellpadding="0" cellspacing="0">
	<tr>
    	<td align="center" class="blueline">&nbsp;</td>
  	</tr>
  </table>
  <br>
  <table border="0" align="center" cellpadding="0" cellspacing="0">
	  <tr>
		<td width="75" class="fontnormalboldgray">
		<c:choose>
		<c:when test="${itemOffset > 1}"><html-el:link action="questionsAction.do?method=viewQuestions&itemOffset=${itemOffset - 20}">
			Previous</html-el:link></c:when>
		<c:otherwise>Previous</c:otherwise>
		</c:choose>
		</td>
		<td align="center" class="fontnormalbold">
		Questions <c:out value="${itemOffset}"/>-<c:out value="${itemOffset + count - 1}"/>
		of <c:out value="${length}"/></td>
		<td width="75" align="right" class="fontnormalboldgray">
		<c:choose>
		<c:when test="${itemOffset + 20 < length}"><html-el:link action="questionsAction.do?method=viewQuestions&itemOffset=${itemOffset + 20}">
			Next</html-el:link></c:when>
		<c:otherwise>Next</c:otherwise>
		</c:choose>
		</td>
	  </tr>
  </table>
 </td>
 </tr>
 </table>
</tiles:put>
</tiles:insert>