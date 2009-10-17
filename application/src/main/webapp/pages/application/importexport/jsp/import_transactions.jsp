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
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="/tags/mifos-html" prefix="mifos"%>
<%@ taglib uri="/tags/struts-html-el" prefix="html-el"%>
<%@ taglib uri="/sessionaccess" prefix="session"%>
<%@ taglib uri="/tags/struts-tiles" prefix="tiles"%>

<tiles:insert definition=".create">
    <tiles:put name="body" type="string">
        <span id="page.id" title="import.transactions.form" /> <html-el:form action="/manageImportAction.do?method=upload"
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
                                            <td><img src="pages/framework/images/timeline/bigarrow.gif" width="17"
                                                height="17"></td>
                                            <td class="timelineboldorange"><mifos:mifoslabel
                                                name="admin.importexport.importinformation" bundle="adminUIResources" />
                                            </td>
                                        </tr>
                                    </table>
                                    </td>
                                    <td width="33%" align="right">
                                    <table border="0" cellspacing="0" cellpadding="0">
                                        <tr>
                                            <td><img src="pages/framework/images/timeline/orangearrow.gif"
                                                width="17" height="17"></td>
                                            <td class="timelineboldorangelight"><mifos:mifoslabel
                                                name="admin.importexport.reviewandsubmit" bundle="adminUIResources" />
                                            </td>
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
                                        name="admin.importexport.enterfileinfo" bundle="adminUIResources" /></td>
                                </tr>
                                <tr>
                                    <td class="fontnormalbold"><span class="fontnormal"> <mifos:mifoslabel
                                        name="reports.statement" /> <font color="#ff0000">*</font> <mifos:mifoslabel
                                        name="reports.markedFieldStatement" /> </span></td>
                                </tr>
                                <tr>
                                    <td><font class="fontnormalRedBold"> <html-el:errors /> </font></td>
                                </tr>
                            </table>
                            <br />
                            <br />
                            <table align="center" >
                                <tr class="fontnormal">
                                    <td><mifos:mifoslabel name="admin.importexport.importformat" mandatory="yes"
                                        bundle="adminUIResources" /></td>
                                      
                                    <!-- FIXME: these "style" should be inside css framework -->
                                    <td>
                                    <mifos:select property="importPluginName">
										<c:forEach items="${requestScope.importPlugins}" var="plugin">
											<html-el:option value="${plugin.id}">${plugin.displayValue}</html-el:option>
										</c:forEach>
									</mifos:select>
                                    </td>
                                    <td width="50%"></td>
                                </tr>
                                <tr class="fontnormal">
                                    <td><mifos:mifoslabel name="admin.importexport.selectimportfile"
                                        mandatory="yes" isColonRequired="Yes" bundle="adminUIResources" /></td>
                                    <td><html-el:file property="importTransactionsFile"/></td>
                                </tr>
                            </table>
                            <br />
                            <br />
                            <br />
                            <table width="90%" border="0" cellpadding="0" cellspacing="0">
                                <tr>
                                    <td align="center" class="blueline">&nbsp;</td>
                                </tr>
                            </table>
                            <br>
                            <table width="90%" border="0" cellpadding="0" cellspacing="0">
                                <tr>
                                    <td align="center"><html-el:submit styleClass="buttn">
                                        <mifos:mifoslabel name="configuration.review"></mifos:mifoslabel>
                                    </html-el:submit> &nbsp; <html-el:submit onclick="this.form.action='AdminAction.do?method=load'"
                                        property="cancelButton" value="Cancel" styleClass="cancelbuttn">
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
        </html-el:form>
    </tiles:put>
</tiles:insert>
