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
 <%@taglib uri="http://struts.apache.org/tags-html-el" prefix="html-el"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@taglib uri="/tags/mifos-html" prefix="mifos"%>
  <html-el:form action="custSearchAction.do?method=loadAllBranches">
        <tr>
          <td class="leftpanelinks"><table width="90%" border="0" cellspacing="0" cellpadding="0">
            <tr>
              <td class="paddingbottom03"><span class="fontnormal8ptbold"><mifos:mifoslabel name="framework.searchCriteria" bundle="FrameworkUIResources"></mifos:mifoslabel></span> </td>
            </tr>
          </table>
            <table width="90%" border="0" cellpadding="0" cellspacing="0">
              <tr>
                <td width="100%" colspan="2">
                <html-el:text property="searchString" size="20" maxlength="200"/>
				<c:choose>
				<c:when test='${sessionScope.UserContext.officeLevelId==5}'>
				<html-el:hidden property="officeId" value="${sessionScope.UserContext.branchId}"/> 
				</c:when>
				<c:otherwise>
				<html-el:hidden property="officeId" value="0"/> 
				</c:otherwise>
				</c:choose>	
                </td>
              </tr>
            </table>
            <table width="136" border="0" cellspacing="0" cellpadding="3">
              <tr>
                <td align="right">
                 <html-el:submit property="searchButton" styleClass="buttn">
                <mifos:mifoslabel name="framework.search" bundle="FrameworkUIResources"></mifos:mifoslabel>
                </html-el:submit>
                </td>
              </tr>
            </table> 
            </td>
            </tr>
            </html-el:form>
