<%--
Copyright (c) 2005-2009 Grameen Foundation USA
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
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles"%>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html"%>
<%@taglib uri="http://struts.apache.org/tags-html-el" prefix="html-el"%>
<%@ taglib uri="/userlocaledate" prefix="userlocalefn"%>
<%@taglib uri="/tags/mifos-html" prefix="mifos"%>

<mifos:html>
<head>
<title><tiles:getAsString name="title" /></title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">

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
</mifos:html>
