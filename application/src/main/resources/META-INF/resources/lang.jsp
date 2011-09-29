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
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
</head>
<body>
<br />
<br />
<br />
<br />
<%
	org.mifos.config.Localization l = org.mifos.config.Localization.getInstance();
	String param = request.getParameter("lang");
	if (param != null && !param.equals("")) {
		String [] codes = param.split("_");
		new org.mifos.framework.util.StandardTestingService().setLocale(codes[0], codes[1]);
		session.setAttribute(org.apache.struts.Globals.LOCALE_KEY, l.getConfiguredLocale());
         org.mifos.security.util.UserContext userContext = (org.mifos.security.util.UserContext) session.getAttribute("UserContext");
         userContext.setPreferredLocale(l.getConfiguredLocale());
         org.springframework.context.i18n.LocaleContextHolder.resetLocaleContext();
         org.springframework.context.i18n.LocaleContextHolder.setLocale(l.getConfiguredLocale());
         
	}
%>

Language (Country) :
<%=l.getConfiguredLocale().getDisplayName()%>
<br />
<br />
<br />
<br />
<form method="post"><select name="lang">
	<option value="">-- SELECT --</option>
    <option value="en_GB">English (UK)</option>
    <option value="te_IN">Telugu</option>
    <option value="zh_CN">Chinese</option>
	<option value="fr_FR">French (France)</option>
	<option value="es_ES">Spanish (Spain)</option>
</select> <input type="submit" value="Change language" /></form>
</body>
</html>
