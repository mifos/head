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

<%@ page import="org.mifos.config.persistence.ConfigurationPersistence"%> 
<%@ page import="org.mifos.reports.business.ReportsBO"%> 
<%
	boolean isDisplay = (new ConfigurationPersistence().getConfigurationValueInteger(ConfigurationPersistence.CONFIGURATION_KEY_JASPER_REPORT_IS_HIDDEN) == 0);
%>
<%@ taglib uri="/tags/struts-tiles" prefix="tiles"%>
<%@ taglib uri="/tags/mifos-html" prefix="mifos"%>
<%@ taglib uri="/tags/struts-html-el" prefix="html-el"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<tiles:insert definition=".pentahoReportLayout">
	<tiles:put name="body" type="string">
	<span id="page.id" title="pentahoreports"></span>

	<html-el:form action="/pentahoReportsAction.do">
	</html-el:form>
	<p>This is a test pentaho reports page!</p>

	</tiles:put>
</tiles:insert>
