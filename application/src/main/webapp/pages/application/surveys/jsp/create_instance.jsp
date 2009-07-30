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
<!-- create_instance.jsp -->

<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles"%>
<%@taglib uri="http://struts.apache.org/tags-html-el" prefix="html-el"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@ taglib uri="/tags/mifos-html" prefix="mifos"%>
<%@taglib uri="/tags/date" prefix="date"%>
<%@ taglib uri="/mifos/custom-tags" prefix="customtags"%>
<%@ taglib uri="/mifos/customtags" prefix="mifoscustom"%>
<%@ taglib uri="/sessionaccess" prefix="session"%>
<style type="text/css">
h1 {

	text-decoration:none; 
	color:#000000;
	font-family: Arial, Verdana, Helvetica, sans-serif;
	font-size: 11pt;
	font-weight: bold;
       margin-top: 10px;
       margin-bottom: 10px;
}
h2{
	text-decoration:none; 
	color:#000000;
	font-family: Arial, Verdana, Helvetica, sans-serif;
	font-size: 8pt;
	font-weight: bold;
}
orange{
	color:#CC6601;
}
red {
	color:#FF0000;
}
hr {
	color: #D7DEEE;
	background-color: #D7DEEE;
	text-align: left;
	margin: 15px auto 5px 0;
	height: 1px;
	width: 95%;
	border: 0px;
}
.entry {
	height: 30px;
	padding-top: 4px;
	padding-right: 5px;
	padding-bottom: 0px;
	padding-left: 5px;
	border-top-width: 1px;
	border-top-style: solid;
	border-top-color: #D7DEEE;
}
</style>
<tiles:insert definition=".clientsacclayoutsearchmenu">
<tiles:put name="body" type="string">
<span style="display: none" id="page.id">create_instance</span>
<script src="pages/application/surveys/js/questions.js" type="text/javascript"></script>
<html-el:form action="/surveyInstanceAction.do?method=preview">
<table width="95%" border="0" cellpadding="0" cellspacing="0">
	<tr>
		<td class="bluetablehead05">
		<span class="fontnormal8pt"> <customtags:headerLink/> </span>
		</td>
	</tr>
</table>

<h1>
	<c:out value="${requestScope.businessObjectName}"/> - 
	<orange><mifos:mifoslabel name="Surveys.instance.entersurveydata" bundle="SurveysUIResources"/></orange>
</h1>

<span class="fontnormal">
	<mifos:mifoslabel name="Surveys.instance.instructions" bundle="SurveysUIResources"/>
</span>
<br/>

<font class="fontnormalRedBold"><html-el:errors bundle="SurveysUIResources" /></font>
<hr>

<h1>
	<c:out value="${sessionScope.retrievedSurvey.name}"/>
</h1>
<hr>

<table width="95%" border="0" cellpadding="3" cellspacing="0">
	<tr>
		<td width="25%" height="30" align="right">
		<red>*</red><span class="fontnormal8ptbold"><mifos:mifoslabel name="Surveys.instance.dateofsurvey" bundle="SurveysUIResources"/>:</span>
		</td>
		<td width="70%">
		<span class="fontnormal8pt"><date:datetag property="dateSurveyed" renderstyle="simplemapped"/></span>
		</td>
	</tr>
	<tr>
		<td height="30" align="right" class="drawtablerow">
		<span class="fontnormal8ptbold"><mifos:mifoslabel name="Surveys.instance.surveyedby" bundle="SurveysUIResources"/>:</span></td>
		<td height="30" class="drawtablerow">
		<html-el:text property="value(officerName)" />
		</td>
	</tr>
</table>

<table width="95%">
	<c:set var="count" value="1"/>
	<c:forEach var="question" items="${sessionScope.retrievedSurvey.questions}">
		<tr>
			<td class="entry">
				<h2>
					<c:if test="${question.mandatory == 1}"><red>*</red></c:if>
					<c:out value="${count}"/>. <c:out value="${question.question.questionText}"/>
				</h2>
			</td>
		</tr>
		<tr>
			<td colspan="2" class="fontnormal8pt drawtablerow">
        		<c:choose>
        		
          			<%-- multi-select response --%>
          			
          			<c:when test="${question.question.answerType == 1}">
            			<c:set var="opt" value="1"/>
            			<c:forEach var="choice" items="${question.question.choices}">
              				<html-el:checkbox property="value(response_${question.surveyQuestionId}.${opt})" value="1">
                				<c:out value="${choice.choiceText}"/>
              				</html-el:checkbox>
              				<html-el:hidden property="value(response_${question.surveyQuestionId}.${opt})" value="0"/>
              				<br>
            				<c:set var="opt" value="${opt+1}"/> 
            			</c:forEach>
          			</c:when>
          			
          			<%-- single-select response --%>
          			
          			<c:when test="${question.question.answerType == 4}">
            			<c:forEach var="choice" items="${question.question.choices}">
              				<html-el:radio property="value(response_${question.surveyQuestionId})" value="${choice.choiceId}">
                				<c:out value="${choice.choiceText}"/>
              				</html-el:radio>
              				<br>
            			</c:forEach>
            			<html-el:radio property="value(response_${question.surveyQuestionId})" value="" style="visibility:hidden;checked:true"/>
          			</c:when>
          			
          			<%-- free-text response --%>
          			
          			<c:when test="${question.question.answerType == 2}">
            			<html-el:textarea property="value(response_${question.surveyQuestionId})" cols="70" rows="10" />
          			</c:when>
          			
          			<%-- date response --%>
          			
          			<c:when test="${question.question.answerType == 5}">
		        		<span class="fontnormal8pt">
		        			<date:datetag property="response_${question.surveyQuestionId}" renderstyle="simplemapped"/>
		        		</span>
          			</c:when>
          			
          			<%-- number response? --%>
          			
          			<c:otherwise>
          				<html-el:text property="value(response_${question.surveyQuestionId})"/>
          			</c:otherwise>
          			
        		</c:choose>
			</td>
		</tr>
		<c:set var="count" value="${count+1}"/>
	</c:forEach>
		<tr>
			<td>
				<html-el:button property="clear" styleClass="cancelbuttn" onclick="submitSurveyInstanceForm('clear')">
					<mifos:mifoslabel name="Surveys.button.clearall" bundle="SurveysUIResources" />
				</html-el:button>
			</td>
			</tr>
</table>
<br>
<hr>

<table width="93%" border="0" cellpadding="0" cellspacing="0">
	<tr>
		<td align="center">
			<html-el:submit property="button" styleClass="buttn">
				<mifos:mifoslabel name="Surveys.button.preview" bundle="SurveysUIResources" />
			</html-el:submit>&nbsp; 
			<html-el:button property="cancelButton" styleClass="cancelbuttn" onclick="submitSurveyInstanceForm('back')">
				<mifos:mifoslabel name="Surveys.button.cancel" bundle="SurveysUIResources" />
			</html-el:button>
		</td>
	</tr>
</table>
</html-el:form>
</tiles:put> 
</tiles:insert>
