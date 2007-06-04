<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="/tags/mifos-html" prefix="mifos"%>
<%@taglib uri="http://struts.apache.org/tags-html-el" prefix="html-el"%>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles"%>

<tiles:insert definition=".view">
<tiles:put name="body" type="string">
      <table width="95%" border="0" cellpadding="0" cellspacing="0">
        <tr>
          <td align="left" valign="top" class="paddingL15T15"><table width="95%" border="0" cellpadding="3" cellspacing="0">
            <tr>
              <td class="headingorange"><span class="headingorange"><mifos:mifoslabel name="Surveys.viewsurveys"/></span></td>

            </tr>
            <tr>
              <td class="fontnormalbold"><span class="fontnormal">
              <mifos:mifoslabel name="Surveys.mainpage_instructions"/>
               <a href="http://www.google.com">
               <mifos:mifoslabel name="Surveys.definesurvey"/>
                </a><br>
                  <br>
                </span><span class="fontnormalbold"><span class="fontnormalbold"><br>
                </span></span>
                <span class="fontnormalbold"> </span>

<mifos:mifoslabel bundle="ClientUIResources" name="client.ClientLabel"/>
<table width="90%" border="0" cellspacing="0" cellpadding="0">
                  <c:forEach var="survey" items="${sessionScope.customerSurveysList}">
                  	<tr class="fontnormal">
                    	<td width="1%"><img src="pages/framework/images/bullet_circle.gif" width="9" height="11"/></td>
                    	<td width="99%"><html-el:link href="surveysAction.do?method=get&surveyId=${survey.surveyId}&randomNUm=${sessionScope.randomNUm}"><c:out value="${survey.name}"/></html-el:link>
                    		<c:if test="${survey.state == 0}">
                    			<img src="pages/framework/images/status_closedblack.gif" width="8" height="9"> Inactive</span>
                    		</c:if>
                    	</td>
                  	</tr>
                  </c:forEach>
                </table>
                <br>
                Loan
                <table width="90%" border="0" cellspacing="0" cellpadding="0">
                  <c:forEach var="survey" items="${sessionScope.accountsSurveysList}">
                  	<tr class="fontnormal">
                    	<td width="1%"><img src="pages/framework/images/bullet_circle.gif" width="9" height="11"/></td>
                    	<td width="99%"><html-el:link href="surveysAction.do?method=get&surveyId=${survey.surveyId}&randomNUm=${sessionScope.randomNUm}"><c:out value="${survey.name}"/></html-el:link>
                    		<c:if test="${survey.state == 0}">
                    			<img src="pages/framework/images/status_closedblack.gif" width="8" height="9"> Inactive</span>
                    		</c:if>
                    	</td>
                  	</tr>
                  </c:forEach>
                </table></td>
            </tr>
          </table>            <br>
            </td>

</tiles:put>
</tiles:insert>
