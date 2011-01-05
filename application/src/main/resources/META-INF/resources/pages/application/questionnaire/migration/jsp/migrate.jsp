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
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html"%>
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="/tags/mifos-html" prefix="mifos"%>
<%@taglib uri="http://struts.apache.org/tags-html-el" prefix="html-el"%>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="/sessionaccess" prefix="session"%>
<tiles:insert definition=".view">
    <tiles:put name="body" type="string">
        <span id="page.id" title="migrate_questionnaire"/>
        <script language="javascript">
            function submitForm(method){
                form = document.forms['genericActionForm'];
                form.action='migrateAction.do?method=' + method;
                form.submit();
            }
        </script>
        <table width="95%" border="0" cellpadding="0" cellspacing="0">
            <tr>
                <td class="bluetablehead05">
         <span class="fontnormal8pt">
            <html-el:link action="AdminAction.do?method=load&randomNUm=${sessionScope.randomNUm}">
                <mifos:mifoslabel name="admin.shortname" bundle="adminUIResources"/>
            </html-el:link> /
          </span>
          <span class="fontnormal8ptbold">
            <mifos:mifoslabel name="admin.questionnaire.migration" bundle="adminUIResources"/>
          </span>
                </td>
            </tr>
        </table>
        <table width="95%" border="0" cellpadding="0" cellspacing="0">
            <tr>
                <td width="35%" class="headingorange">
                    <mifos:mifoslabel name="admin.questionnaire.migration" bundle="adminUIResources"/>
            </tr>
        </table>
        <br>

        <font class="fontnormalRedBold">
            <html-el:errors bundle="SurveysUIResources"/>
        </font>
        <html-el:form action="/migrateAction.do?method=get">
            <p>This page is used to migrate/convert all Surveys and Additional Fields currently in Mifos into Question
                Groups. Note that the existing Surveys and Additional Fields data will not be affected.</p>

            <p>It is intended to be used for testing only and shall be removed before Mifos 1.7 go-live.</p>
            <table width="93%" border="0" cellpadding="0" cellspacing="0">
                <tr>
                    <td align="center">
                        <button id="migrateSurveys" class="buttn" onclick="submitForm('migrateSurveys')">
                            <mifos:mifoslabel name="admin.questionnaire.migration.migrate.surveys"
                                              bundle="adminUIResources"/>
                        </button>

                        &nbsp;
                        <button id="migrateAdditionalFields" class="buttn"
                                onclick="submitForm('migrateAdditionalFields')">
                            <mifos:mifoslabel name="admin.questionnaire.migration.migrate.additional.fields"
                                              bundle="adminUIResources"/>
                        </button>
                    </td>
                </tr>
            </table>
        </html-el:form>
    </tiles:put>
</tiles:insert>
