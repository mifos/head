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
	margin-top: 25px;
	margin-bottom: 25px;
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
.bottomBorder {
	border-bottom-width: 1px;
	border-bottom-style: solid;
	border-bottom-color: #D7DEEE;
}
.sideBorders {
	border-right: 1px solid #D7DEEE;
	border-left: 1px solid #D7DEEE;
}
tr.bg1 {
	font-family: Arial, Verdana, Helvetica, sans-serif;
	font-size: 9pt;
	font-weight: normal;
	text-decoration: none;
	background-color: #FFFFFF;
	padding-top: 0px;
	padding-right: 0px;
	padding-bottom: 0px;
	padding-left: 0px;
	border-top-width: 1px;
	border-top-style: solid;
	border-top-color: #D7DEEE;
}
tr.bg0 {
	font-family: Arial, Verdana, Helvetica, sans-serif;
	font-size: 9pt;
	font-weight: normal;
	text-decoration: none;
	background-color: #F2F2F2;
	padding-top: 0px;
	padding-right: 0px;
	padding-bottom: 0px;
	padding-left: 0px;
</style>
<tiles:insert definition=".create">
<tiles:put name="body" type="string">
<script src="pages/application/surveys/js/questions.js" type="text/javascript"></script>
<html-el:form action="/surveyInstanceAction.do?method=create">
<table width="95%" border="0" cellpadding="0" cellspacing="0">
	<tr>
		<td class="bluetablehead05">
		<span class="fontnormal8pt"> <customtags:headerLink selfLink="false" /> </span>
		</td>
	</tr>
</table>
<h1><c:out value="${businessObjectName}"/> - 
<orange><mifos:mifoslabel name="Surveys.instance.entersurveydata" bundle="SurveysUIResources"/></orange></h1>
<font class="fontnormalRedBold"><html-el:errors bundle="SurveysUIResources" /></font>
<br/>
<span class="fontnormal"><mifos:mifoslabel name="Surveys.instance.instructions" bundle="SurveysUIResources"/></span>
<hr>
<h1><c:out value="${sessionScope.retrievedSurvey.name}"/></h1>
<table width="95%" border="0" cellpadding="3" cellspacing="0">
	<tr>
		<td width="25%" height="30" align="right">
		<red>*</red><span class="fontnormal8ptbold"><mifos:mifoslabel name="Surveys.instance.dateofsurvey" bundle="SurveysUIResources"/>:</span>
		</td>
		<td width="70%">
		<span class="fontnormal8pt"><date:datetag property="dateSurveyed" renderstyle="simplemapped" isDisabled="yes"/></span>
		</td>
	</tr>
	<tr>
		<td height="30" align="right">
		<span class="fontnormal8ptbold"><mifos:mifoslabel name="Surveys.instance.surveyedby" bundle="SurveysUIResources"/>:</span></td>
		<td height="30" class="fontnormal">
		<c:out value="${requestScope.officerName}"/>&nbsp;
		</td>
	</tr>
	<tr>
		<td>&nbsp;</td>
	</tr>
</table>
<table border="0" cellpadding="0" cellspacing="0" width="95%">
	<tr>
		<td class="bottomBorder">&nbsp;</td>
		<td class="fontnormal8ptbold bottomBorder" align="center" width="6%">
		<mifos:mifoslabel name="PPI.Points" bundle="PPIUIResources"/>:
		</td>
	</tr>
</table>
<table border="0" cellpadding="0" cellspacing="0" width="95%">
	<c:set var="count" value="1"/>
	<c:forEach var="response" items="${surveyInstance.surveyResponses}">
	<tr class="bg${count % 2}">
		<td>
			<table border="0" width="100%">
				<tr>
					<td colspan="100">
					<h2><c:out value="${count}"/>. <c:out value="${response.question.questionText}"/>
					</h2>
					</td>
				</tr>
				<tr class="bg${count % 2}">
            	<c:forEach var="choice" items="${response.question.choices}">
            		<td class="fontnormal8pt" width="3%" align="right" valign="top">
		            <html-el:radio disabled="true" property="value(response_${response.surveyQuestion.surveyQuestionId})" value="${choice.choiceId}">
        		    </td>
		            <td class="fontnormal8pt" width="15%" align="left">
        		    <c:out value="${choice.choiceText}"/>
		            </html-el:radio>
        		    </td>
		        </c:forEach>
        		    <td colspan="100"> </td>
				</tr>
				<tr class="bg${count % 2}">
		            <td colspan="100"> </td>
				</tr>
			</table>
		</td>
		<td class="fontnormal8ptbold sideBorders" width="6%" valign="center" align="center">
		<strong><c:out value="${response.choiceValue.points}"/></strong>
		</td>
	</tr>
	<c:set var="count" value="${count+1}"/>
	</c:forEach>
</table>
<table border="0" cellpadding="0" cellspacing="0" width="95%">
	<tr class="bg1">
		<td class="bottomBorder" align="right">
		<strong><mifos:mifoslabel name="PPI.TOTAL" bundle="PPIUIResources"/>:&nbsp;</strong>
		</td>
		<td class="bottomBorder sideBorders" height="24" width="6%" align="center">
		<strong><c:out value="${povertyBand.points}"/></strong>
		</td>
	</tr>
	<tr class="bg1">
		<td colspan="2" align="right">
		<strong>
		<mifos:mifoslabel name="PPI.Status" bundle="PPIUIResources"/>: 
		<mifos:mifoslabel name="PPI.Band.${povertyBand.enumBand.value}" bundle="PPIUIResources"/>
		</strong>
		</td>
	</tr>
	<tr>
		<td>
		<html-el:button property="edit" style="width:65px;" styleClass="cancelbuttn" onclick="submitSurveyInstanceForm('edit')">
		<mifos:mifoslabel name="Surveys.button.edit" bundle="SurveysUIResources" />
		</html-el:button>
		</td>
	</tr>
</table>
<br><hr>
<table width="93%" border="0" cellpadding="0" cellspacing="0">
<tr>
		<td align="center">
		<html-el:submit style="width:65px;" property="button" styleClass="buttn">
		<mifos:mifoslabel name="Surveys.button.submit" bundle="SurveysUIResources" />
		</html-el:submit>&nbsp; 
		<html-el:button property="cancelButton" style="width:65px;" styleClass="cancelbuttn" onclick="window.location='AdminAction.do?method=load">
		<mifos:mifoslabel name="Surveys.button.cancel" bundle="SurveysUIResources" />
		</html-el:button>
		</td>
	</tr>
</table>
</html-el:form>
</tiles:put> 
</tiles:insert>
