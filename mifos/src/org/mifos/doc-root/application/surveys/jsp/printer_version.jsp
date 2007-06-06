<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="/tags/mifos-html" prefix="mifos"%>
<%@taglib uri="http://struts.apache.org/tags-html-el" prefix="html-el"%>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles"%>
<html>
<head>
<title>Mifos</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<style type="text/css">
body {
	margin:12px;
	background-color:#FFFFFF;
}
h1 {

	text-decoration:none; 
	font-family: Arial, Verdana, Helvetica, sans-serif;
	color:#000000;
	font-size: 11pt;
	font-weight: bold;
}
table {
	border-spacing: 0px;
	padding: 0px;
	font-family: Arial, Verdana, Helvetica, sans-serif;
	font-size: 9pt;
	font-weight: normal;
	text-decoration: none;
	color: #000000;
}
.encased{
	border:1px #D7DEEE solid;
	border-spacing: 0px;
	padding: 0px;
	width: 100%;
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
.padLeft {
	padding-top: 15px;
	padding-left: 30px;
}
h2{
	text-decoration:none; 
	color:#000000;
	font-family: Arial, Verdana, Helvetica, sans-serif;
	font-size: 8pt;
	font-weight: bold;
}
.printBtn
{
	font-family: Arial, Verdana, Helvetica, sans-serif;
	font-size: 9pt;
	font-weight: normal;
	color: #000000;
    border-right: #926030 1px solid;
	border-bottom: #926030 1px solid;
	border-left: #C29F7C 1px solid;
	border-top: #C29F7C 1px solid;
    background-image: url(../images/buttons/buttonbg.jpg);
	background-repeat: repeat-x;
    cursor: pointer;
    height: 20px
}
.cancelBtn
{
	font-family: Arial, Verdana, Helvetica, sans-serif;
	font-size: 9pt;
	font-weight: normal;
	color: #000000;
    border-right: #696969 1px solid;
	border-bottom: #696969 1px solid;
	border-left: #A2A2A2 1px solid;
	border-top: #A2A2A2 1px solid;
    background-image: url(../images/buttons/buttonbgcancel.jpg);
	background-repeat: repeat-x;
    cursor: pointer;
    height: 20px
}
</style>
</head>
<body>
<table width="100%" class="encased">
	<tr>
		<td align="left" valign="top" class="padLeft">
			<table width="100%" border="0" cellpadding="3" cellspacing="0">
				<tr>
					<td><h1><c:out value="${sessionScope.BusinessKey.name}"/></h1></td>
				</tr>
				<tr>
					<td>
						<table width="590">
							<tr>
								<td>
									<table class="encased">
										<tr class="fontnormal">
											<td>
											<input type="radio" disabled>
											<em>Indicates that you can select only one option</em>
											</td>
											<td>
											<input type="checkbox" disabled>
											<em>Indicates that you can select more than one option</em>
											</td>
										</tr>
									</table>
								</td>
							</tr>
						</table><br>
					</td>
				</tr>
				<tr>
					<td>
						<table width="95%">
							<c:set var="count" value="1"/>
							<c:forEach var="question" items="${sessionScope.BusinessKey.questions}">
							<tr>
								<td class="entry">
								<h2><c:out value="${count}"/>. <c:out value="${question.question.questionText}"/></h2>
								</td>
							</tr>
							<tr>
								<td class="entry">
								<c:choose>
								<c:when test="${question.question.answerType == 4}">
								<c:forEach var="choice" items="${question.question.choices}">
								<input type="radio">
								<c:out value="${choice.choiceText}"/><br>
								</c:forEach>
								</c:when>
								<c:when test="${question.question.answerType == 2}">
								<textarea name="textarea" cols="70" rows="10" style=" overflow:hidden;"></textarea>
								</c:when>
								<c:otherwise><input type="text"></c:otherwise>
								</c:choose>
								</td>
							</tr>
							<tr>
								<td class="entry">&nbsp;</td>
							</tr>
							<c:set var="count" value="${count+1}"/>
							</c:forEach>
						</table>
						<table width="93%">
							<tr>
								<td align="center">
								<input type="button" class="printBtn" value="Print" onClick="window.print();">
								&nbsp;
								<input type="button" value="Cancel" class="cancelBtn" onClick="window.close();">
								</td>
							</tr>
						</table>
					</td>
				</tr>
			</table>
		</td>
	</tr>
</table>
</body>
</html>