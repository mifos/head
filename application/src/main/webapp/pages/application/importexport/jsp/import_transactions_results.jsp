<%--
Copyright (c) 2005-2008 Grameen Foundation USA
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

<%@ page contentType="text/html; charset=utf-8"%>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html"%>
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="/tags/mifos-html" prefix="mifos"%>
<%@ taglib uri="/tags/struts-html-el" prefix="html-el"%>
<%@ taglib uri="/sessionaccess" prefix="session"%>
<%@ taglib uri="/tags/struts-tiles" prefix="tiles"%>

<fmt:setLocale value='${sessionScope["LOCALE"]}'/>
<fmt:setBundle basename="org.mifos.config.localizedResources.adminUIResources"/>

<tiles:insert definition=".create">
    <tiles:put name="body" type="string">
        <span id="page.id" title="import.transactions.results" /> <html-el:form action="/manageImportAction.do?method=confirm"
            enctype="multipart/form-data">
            <table width="100%" border="0" cellspacing="0" cellpadding="0">
                <tr>
                    <td height="470" align="left" valign="top" bgcolor="#FFFFFF">
                    <table width="90%" border="0" align="center" cellpadding="0" cellspacing="0">
                        <tr>
                            <td align="center" class="heading">&nbsp;</td>
                        </tr>
                    </table>
                    <table width="90%" border="0" align="center" cellpadding="0" cellspacing="0">
                        <tr>
                            <td class="bluetablehead">
                            <table width="100%" border="0" cellspacing="0" cellpadding="0">
                                <tr>
                                    <td width="33%">
                                    <table border="0" cellspacing="0" cellpadding="0">
                                        <tr>
                                            <td><img src="pages/framework/images/timeline/tick.gif" width="17"
                                                height="17" alt=""></td>
                                            <td class="timelineboldgray"><mifos:mifoslabel
                                                name="admin.importexport.importinformation" bundle="adminUIResources" /></td>
                                        </tr>
                                    </table>
                                    </td>
                                    <td width="33%" align="right">
                                    <table border="0" cellspacing="0" cellpadding="0">
                                        <tr>
                                            <td><img src="pages/framework/images/timeline/orangearrow.gif"
                                                width="17" height="17"></td>
                                            <td class="timelineboldorange"><mifos:mifoslabel
                                                name="admin.importexport.reviewandsubmit" bundle="adminUIResources" /></td>
                                        </tr>
                                    </table>
                                    </td>
                                </tr>
                            </table>
                            </td>
                        </tr>
                    </table>
                    <table width="90%" border="0" align="center" cellpadding="0" cellspacing="0" class="bluetableborder">
                        <tr>
                            <td width="70%" height="24" align="left" valign="top" class="paddingleftCreates">
                            <table width="98%" border="0" cellspacing="0" cellpadding="3">
                                <tr>
                                    <td class="headingorange"><span class="heading"> <mifos:mifoslabel
                                        name="admin.importexport.importtransactions" bundle="adminUIResources" /> - </span> <mifos:mifoslabel
                                        name="admin.importexport.reviewandsubmit" bundle="adminUIResources" /></td>
                                </tr>
                                <tr>
                                    <td class="fontnormalbold"><span class="fontnormal"> <mifos:mifoslabel
                                        name="configuration.preview_instruction" /> </span></td>
                                </tr>
                            </table>
                            <br>
                            <table>
                                <tr class="fontnormalbold">
                                    <td><mifos:mifoslabel name="admin.importexport.importinformation"
                                        bundle="adminUIResources" /> <br />
                                    <br />
                                    </td>
                                </tr>
                                <tr class="fontnormal">
                                    <td><mifos:mifoslabel name="admin.importexport.importformat"
                                        isColonRequired="Yes" bundle="adminUIResources" /> <c:out
                                        value="${importTransactionsForm.importPluginName}" /></td>
                                </tr>
                                <tr class="fontnormal">
                                    <td><mifos:mifoslabel name="admin.importexport.importfilename"
                                        isColonRequired="Yes" bundle="adminUIResources" /> <c:out
                                        value="${importTransactionsForm.importTransactionsFileName}" /></td>
                                </tr>
                                <tr class="fontnormal">
                                    <td><mifos:mifoslabel name="admin.importexport.importstatus"
                                        isColonRequired="Yes" bundle="adminUIResources" />
                                    <c:choose>
                                        <c:when test="${0 == requestScope.numSuccessfulRows}">
                                            <fmt:message key="admin.importexport.zeroImportableRows"/>
                                        </c:when>
                                        <c:otherwise>
                                            <fmt:message key="admin.importexport.successfulImportRows">
                                                <fmt:param><c:out value="${requestScope.numSuccessfulRows}" /></fmt:param>
                                            </fmt:message>
                                        </c:otherwise>
                                    </c:choose>
                                    </td>
                                </tr>
                                <tr class="fontnormalred">
                                    <td><br />
                                    <c:if test="${!empty importTransactionsErrors}">
                                    	<fmt:message key="admin.importexport.rowsWithErrors"/>
                                    </c:if>
                                    <br />
                                    <c:forEach var="error" items="${importTransactionsForm.importTransactionsErrors}">
                                        <br />
                                        <c:out value="${error}" />
                                    </c:forEach></td>
                                </tr>
                                <tr class="fontnormal">
                                    <td><html-el:submit property="edit"
                                        onclick="this.form.action='manageImportAction.do?method=load'">
                                        <mifos:mifoslabel name="configuration.edit" />:<mifos:mifoslabel
                                            name="admin.importexport.importinformation" bundle="adminUIResources" />
                                    </html-el:submit></td>
                                </tr>
                            </table>
                            <br>
                            <table width="90%" border="0" cellpadding="0" cellspacing="0">
                                <tr>
                                    <td align="center" class="blueline">&nbsp;</td>
                                </tr>
                            </table>
                            <br>
                            <table width="90%" border="0" cellpadding="0" cellspacing="0">
                                <tr>
                                    <td align="center"><html-el:submit styleId="import_transactions_results.button.submit" styleClass="buttn">
                                        <mifos:mifoslabel name="configuration.submit"></mifos:mifoslabel>
                                    </html-el:submit> &nbsp; <html-el:submit onclick="this.form.action='AdminAction.do?method=load'"
                                        property="cancelButton" styleId="import_transactions_results.button.cancel" value="Cancel" styleClass="cancelbuttn">
                                        <mifos:mifoslabel name="configuration.cancel" />
                                    </html-el:submit></td>
                                </tr>
                            </table>
                            <br>
                            </td>
                        </tr>
                    </table>
                    <br>
                    </td>
                </tr>
            </table>
            <html-el:hidden property="method" value="search" />
            <html-el:hidden property="currentFlowKey" value="${requestScope.currentFlowKey}" />
        </html-el:form>
    </tiles:put>
</tiles:insert>
