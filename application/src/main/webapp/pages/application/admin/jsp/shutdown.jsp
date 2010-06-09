<%--
Copyright (c) 2005-2010 Grameen Foundation USA
1029 Vermont Avenue, NW, Suite 400, Washington DC 20005
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
<%@taglib uri="/tags/mifos-html" prefix="mifos"%>
<%@taglib uri="http://struts.apache.org/tags-html-el" prefix="html-el"%>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles"%>

<tiles:insert definition=".view">
	<tiles:put name="body" type="string">
        <span id="page.id" title="Shutdown" ></span>
		<html-el:form action="shutdownAction.do?method=shutdown">
                <table width="95%" border="0" cellpadding="0" cellspacing="0">
                    <tr>
                        <td class="bluetablehead05">
                            <span class="fontnormal8pt">
                                <a id="viewusers.link.admin" href="AdminAction.do?method=load">
                                    <mifos:mifoslabel name="admin.shortname" bundle="adminUIResources" /></a> /
                            </span>
                            <span class="fontnormal8ptbold">
                                <mifos:mifoslabel name="admin.shutdown.link" bundle="adminUIResources" />
                            </span></td>
                    </tr>
                </table>
				<table width="95%" border="0" cellpadding="0" cellspacing="0"
                       style="padding-left:8px; padding-top:10px;">
					<tr>
						<td colspan="2"><span class="headingorange"><mifos:mifoslabel name="admin.shutdown.title" bundle="adminUIResources" /></span><br />
							<span class="fontnormal"><mifos:mifoslabel name="admin.shutdown.welcome" bundle="adminUIResources" /></span></td>
					</tr>
					<tr><td colspan="2">&nbsp;</td></tr>
                    <tr>
						<td><span class="fontnormal"><mifos:mifoslabel name="admin.shutdown.status" bundle="adminUIResources" /></span></td>
						<td><span class="fontnormal" id="shutdown.text.status"><c:out value="${requestScope.shutdownStatus}"/></span></td>
					</tr>
                    <tr>
						<td><span class="fontnormal"><mifos:mifoslabel name="admin.shutdown.timeout" bundle="adminUIResources" /></span></td>
						<td>
                            <span class="fontnormal" id="shutdown.text.timeout">
                                <mifos:mifosnumbertext name="shutdownActionForm" property="shutdownTimeout" maxlength="7" />
                                <mifos:mifoslabel name="admin.shutdown.timeout.unit" bundle="adminUIResources" />
                            </span>
                        </td>
					</tr>
				</table>
            <table width="98%" border="0" cellpadding="0" cellspacing="0">
                <tr>
                    <td class="blueline">&nbsp;</td>
                </tr>
            </table>
            <br>
            <table width="98%" border="0" cellpadding="0" cellspacing="0">
                <tr>
                    <td align="center">
                        <c:choose>
                            <c:when test="${requestScope.submitButtonDisabled}">
                                <html-el:submit styleId="admin.shutdown.button.shutdown" property="submitButton" styleClass="disabledbuttn" disabled="true">
                                    <mifos:mifoslabel name="admin.shutdown.button.submit" bundle="adminUIResources"/>
                                </html-el:submit>
                            </c:when>
                            <c:otherwise>
                                <html-el:submit styleId="admin.shutdown.button.shutdown" property="submitButton" styleClass="buttn">
                                    <mifos:mifoslabel name="admin.shutdown.button.submit" bundle="adminUIResources"/>
                                </html-el:submit>
                            </c:otherwise>
                        </c:choose>
                        &nbsp;
                        <c:choose>
                            <c:when test="${requestScope.submitButtonDisabled}">
                                <html-el:button styleId="admin.shutdown.button.cancel" property="cancelButton"
                                                onclick="location.href='shutdownAction.do?method=cancelShutdown'" styleClass="cancelbuttn">
                                    <mifos:mifoslabel name="admin.shutdown.button.cancel" bundle="adminUIResources"/>
                                </html-el:button>
                            </c:when>
                            <c:otherwise>
                                <html-el:button styleId="admin.shutdown.button.cancel" property="cancelButton" disabled="true"
                                                onclick="location.href='shutdownAction.do?method=cancelShutdown'" styleClass="disabledbuttn">
                                    <mifos:mifoslabel name="admin.shutdown.button.cancel" bundle="adminUIResources"/>
                                </html-el:button>
                            </c:otherwise>
                        </c:choose>
                        &nbsp;
                        <html-el:button styleId="admin.shutdown.button.refresh" property="refreshButton"
                                        onclick="location.href='shutdownAction.do?method=load'" styleClass="buttn">
                            <mifos:mifoslabel name="admin.shutdown.button.refresh" bundle="adminUIResources"/>
                        </html-el:button>
                    </td>
                </tr>
            </table>
				<br>
                <span class="headingorange"><mifos:mifoslabel name="admin.shutdown.users" bundle="adminUIResources" /></span>
                <table width="95%">
                    <c:forEach var="user" varStatus="loopStatus1" items="${requestScope.activeSessions}">
                        <tr>
                            <td valign="top" class="drawtablerow">
                                <span class="fontnormalbold">
                                    <c:out value="${loopStatus1.index + 1}." />
                                </span>
                            </td>
                            <td valign="top" class="drawtablerow">
                                <span class="fontnormal">
                                    <c:out value="${user.offices} / " />
                                </span>
                                <span class="fontnormalbold">
                                    <c:out value="${user.names}" />
                                </span>
                            </td>
                        </tr>
                        <tr>
                            <td valign="top">&nbsp;</td>
                            <td valign="top">
                                <span class="fontnormalbold">
                                    <c:out value="${user.activityTime}" />
                                </span>
                                <span class="fontnormal">
                                    <c:out value="${user.activityContext}" />
                                </span>
                            </td>
                        </tr>
                    </c:forEach>
                </table>
		</html-el:form>
	</tiles:put>
</tiles:insert>
