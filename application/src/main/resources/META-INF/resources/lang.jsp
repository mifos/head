<%--
Copyright (c) 2005-2011 Grameen Foundation USA
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
<%@page import="java.util.Locale"%>
<%@page import="org.apache.commons.lang.StringUtils"%>
<%@page import="org.mifos.dto.domain.ValueListElement"%>
<%@page import="org.mifos.config.Localization"%>
<%@page import="org.mifos.framework.util.StandardTestingService"%>
<%
    Localization l = Localization.getInstance();
    String param = request.getParameter("langId");

    if (StringUtils.isNotBlank(param)) {
        Short id = Short.parseShort(param);
        Locale locale = l.getLocaleById(id);
        new StandardTestingService().setLocale(locale.getLanguage(), locale.getCountry());
        response.sendRedirect(request.getContextPath() + "/login.ftl");
        return;
    }
%>
<br />
<br />
Current Language : <%=l.getConfiguredLocale().getDisplayName()%>
<br />
<br />
<form method="post" action="lang.jsp">
    <select name="langId">
    <% for(ValueListElement e : l.getLocaleForUI()) { %>
		<option value="<%=e.getId()%>"><%=e.getName()%></option>
	<% } %>
	</select> 
	<input type="submit" value="Change"/>
</form>
<b>NOTE:</b> Some languages are not completely translated. <a href="http://translatewiki.net/wiki/Translating:Mifos/stats" target="blank">stats</a><br/>
<b>MORE ON </b> <a href="http://mifosforge.jira.com/wiki/display/projects/Mifos+Localization" target="blank">Mifos Localization</a>
<br/> <a href="http://mifosforge.jira.com/wiki/display/MIFOS/i18n,+L10n" target="blank">Learn i18n/L10n</a>