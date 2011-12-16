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
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@taglib uri="/tags/mifos-html" prefix="mifos"%>
<%@taglib uri="http://struts.apache.org/tags-html-el" prefix="html-el"%>
<table width="100%" border="0" cellspacing="0" cellpadding="0">
  <tr>
    <td width="200" rowspan="2"><img src="pages/framework/images/logo.jpg" width="200" height="70"></td>
    <td align="right" bgcolor="#FFFFFF" class="fontnormal"><a href="yourSettings.do?method=get&randomNUm=${sessionScope.randomNUm}"><mifos:mifoslabel name="framework.yoursettings" bundle="FrameworkUIResources"></mifos:mifoslabel></a>
     &nbsp;|&nbsp; <a id="logout_link" href="j_spring_security_logout"><mifos:mifoslabel name="framework.logout" bundle="FrameworkUIResources"></mifos:mifoslabel></a>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
  </tr>
  <tr>
    <td align="left" valign="bottom" bgcolor="#FFFFFF"><table border="0" cellspacing="1" cellpadding="0">
        <tr>
          <td class="tablightorange"><a href="custSearchAction.do?method=getHomePage"><mifos:mifoslabel name="framework.home" bundle="FrameworkUIResources"></mifos:mifoslabel></a></td>
          <td class="tablightorange"><a href="custSearchAction.do?method=loadMainSearch"><mifos:mifoslabel name="framework.clientsAndAccounts" bundle="FrameworkUIResources"></mifos:mifoslabel></a></td>
          <td class="tablightorange"><a href="reportsAction.do?method=load"><mifos:mifoslabel name="framework.reports" bundle="FrameworkUIResources"></mifos:mifoslabel></a></td>
          <td class="tablightorange"><a id="homeheader.link.pentahoreports" href="pentahoReportsAction.do?method=load"><mifos:mifoslabel name="framework.pentahoreports" bundle="FrameworkUIResources"></mifos:mifoslabel></a></td>
          <td class="tablightorange"><a href="AdminAction.do?method=load"><mifos:mifoslabel name="framework.admin" bundle="FrameworkUIResources"></mifos:mifoslabel></a></td>
        </tr>
      </table>
        <c:if test="${requestScope.shutdownIsImminent == true}">
            <span class="fontnormalRedBold"><mifos:mifoslabel name="admin.shutdown.status.imminent" bundle="adminUIResources" /></span>
        </c:if>
    </td>
  </tr>
  <tr>
    <td colspan="2" class="bgorange"><img src="pages/framework/images/trans.gif" width="6" height="6"></td>
  </tr>
  <tr>
    <td colspan="2" class="bgwhite"><img src="pages/framework/images/trans.gif" width="100" height="2"></td>
  </tr>
</table>
