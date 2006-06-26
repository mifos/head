<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles"%>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html"%>
<%@taglib uri="http://struts.apache.org/tags-html-el" prefix="html-el"%>
<%@ taglib uri="/userlocaledate" prefix="userlocalefn"%>

<html-el:html locale="true">
<head>
<title><tiles:getAsString name="title" /></title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">

<tiles:importAttribute name="mifoscss" scope="request" />
<link href='<%=request.getAttribute("mifoscss")%>' rel="stylesheet"
	type="text/css">
</head>

<body>

<tiles:insert attribute="header">
	<tiles:put name="menutitle" beanName="menutitle" beanScope="tile" />
</tiles:insert>
<table width="100%" border="0" cellspacing="0" cellpadding="0">
	<tr>

		<tiles:insert attribute="menu" ignore="true">
		</tiles:insert>

		<td align="left" valign="top" bgcolor="#FFFFFF"
			class="paddingleftmain" height="500" ><tiles:insert attribute="body" /> <html-el:hidden
			property="h_user_locale" value="${userlocalefn:getUserPreferredLocaleHelper(sessionScope.UserContext)}" /></td>
	</tr>
</table>

</body>
</html-el:html>
