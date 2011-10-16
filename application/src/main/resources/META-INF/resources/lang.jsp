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
<%@page import="org.mifos.customers.personnel.business.PersonnelBO"%>
<%@page
	import="org.mifos.framework.hibernate.helper.StaticHibernateUtil"%>
<%@page import="org.mifos.security.MifosUser"%>
<%@page
	import="org.springframework.security.core.context.SecurityContextHolder"%>
<%@page import="java.util.Locale"%>
<%@page import="org.apache.commons.lang.StringUtils"%>
<%@page import="org.mifos.dto.domain.ValueListElement"%>
<%@page import="org.mifos.config.Localization"%>
<%@page import="org.mifos.framework.util.StandardTestingService"%>
<%
    Localization l = Localization.getInstance();
    String param = request.getParameter("langId");
    Short localeId = Localization.getInstance().getConfiguredLocaleId();
    // TODO : move to PersonnelServiceFacade/FTL
    if (StringUtils.isNotBlank(param)) {
        Short id = Short.parseShort(param);
        MifosUser user = (MifosUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Locale locale = l.getLocaleById(id);
        localeId = Localization.getInstance().getLocaleId(locale);
        user.setPreferredLocaleId(localeId);
        StaticHibernateUtil.startTransaction();
        PersonnelBO p = (PersonnelBO) StaticHibernateUtil.getSessionTL().get(PersonnelBO.class,
                (short) user.getUserId());
        p.setPreferredLocale(localeId);
        StaticHibernateUtil.getSessionTL().update(p);
        StaticHibernateUtil.commitTransaction();

        return;
    }
%>

<%
    MifosUser user = (MifosUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    localeId = user.getPreferredLocaleId();
%>
<br />
Current Language :
<select id="langId">
	<% for (ValueListElement e : l.getLocaleForUI()) { %>
	<option <%if (localeId == e.getId().shortValue()) {%> selected <%}%> value="<%=e.getId()%>"><%=e.getName()%></option>
	<%
	   }
	%>
</select>
<input id="langSubmit" type="button" value="Change" />
<br />
<br />
<b>NOTE:</b>
Some languages are not completely translated.
<a href="http://translatewiki.net/wiki/Translating:Mifos/stats"
	target="blank">stats</a>
<br />
<a
	href="http://mifosforge.jira.com/wiki/display/projects/Mifos+Localization"
	target="blank">Mifos Localization</a>
<br />
<a href="http://mifosforge.jira.com/wiki/display/MIFOS/i18n,+L10n"
	target="blank">Learn i18n/L10n</a>