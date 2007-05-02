<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="/tags/mifos-html" prefix="mifos"%>
<%@taglib uri="http://struts.apache.org/tags-html-el" prefix="html-el"%>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles"%>

<tiles:insert definition=".view">
	<tiles:put name="body" type="string">
    <table width="95%" border="0" cellpadding="0" cellspacing="0">
          <tr>
            <td width="70%" align="left" valign="top" class="paddingL15T15"><table width="96%" border="0" cellpadding="3" cellspacing="0">
              <tr>
                <td height="23" class="headingorange"><c:out value="${sessionScope.BusinessKey.name}"/></td>
                </tr>
              <tr>

                <td height="23" class="fontnormal"><p><span class="fontnormal">                  </span>
                <span class="fontnormal">
                <c:choose>
                  <c:when test="${sessionScope.BusinessKey.state == 1}"><img src="pages/framework/images/status_activegreen.gif" width="8" height="9"><mifos:mifoslabel name="Surveys.Active"/></c:when>
                  <c:when test="${sessionScope.BusinessKey.state == 0}"><img src="pages/framework/images/status_closedblack.gif" width="8" height="9"><mifos:mifoslabel name="Surveys.Inactive"/></c:when>      
                </c:choose>            
                </span>
                <br>                  
                      <br>                  
                  <mifos:mifoslabel name="Surveys.Appliesto"/>
                  	<c:choose>
                  		<c:when test="${sessionScope.BusinessKey.appliesTo == \"customers\"}"><mifos:mifoslabel name="Surveys.Customers"/></c:when>
                  		<c:when test="${sessionScope.BusinessKey.appliesTo == \"accounts\"}"><mifos:mifoslabel name="Surveys.Loans"/></c:when> 
                  		<c:when test="${sessionScope.BusinessKey.appliesTo == \"both\"}"><mifos:mifoslabel name="Surveys.Customersandloans"/></c:when>                  		                 		
                  	</c:choose>
                  	<br>
  <br>
  <a href="../SurverDetailsPrintPreview.htm" onClick="javascript:openfile(this.href); return false"><mifos:mifoslabel name="Surveys.Printerversion"/></a></p>
                  <p><span class="fontnormalbold"><mifos:mifoslabel name="Surveys.Questions"/></span></p></td>
                </tr>

              <tr>
                <td height="23" class="fontnormal"><table width="98%" border="0" cellpadding="3" cellspacing="0">
                  <tr>
                    <td width="39%" class="drawtablehd"><mifos:mifoslabel name="Surveys.Questiontitle"/></td>
                    <td width="14%" class="drawtablehd"><mifos:mifoslabel name="Surveys.Answertype"/></td>
                    <td width="44%" class="drawtablehd"><mifos:mifoslabel name="Surveys.Answer"/></td>
                  </tr>

		  <c:forEach var="question" items="${sessionScope.BusinessKey.questions}">
                  <tr>
                    <td width="39%" class="drawtablerow"><c:out value="${question.question.questionText}"/></td>
                    <td width="14%" class="drawtablerow">
                    <c:choose>
                    	<c:when test="${question.question.answerType == 2}"><mifos:mifoslabel name="Surveys.Freetext"/></c:when>
                    	<c:when test="${question.question.answerType == 3}"><mifos:mifoslabel name="Surveys.Number"/></c:when>
                    	<c:when test="${question.question.answerType == 4}"><mifos:mifoslabel name="Surveys.Choice"/></c:when>
                    	<c:when test="${question.question.answerType == 5}"><mifos:mifoslabel name="Surveys.Date"/></c:when>

                    </c:choose>
                    </td>
                    <td width="44%" class="drawtablerow">&nbsp;</td>
                  </tr>
		  </c:forEach>
                </table></td>
              </tr>
            </table>              
            <br></td>
          </tr>
        </table>    

	</tiles:put>
</tiles:insert>
