[#ftl]
[#--
* Copyright (c) 2005-2011 Grameen Foundation USA
*  All rights reserved.
*
*  Licensed under the Apache License, Version 2.0 (the "License");
*  you may not use this file except in compliance with the License.
*  You may obtain a copy of the License at
*
*      http://www.apache.org/licenses/LICENSE-2.0
*
*  Unless required by applicable law or agreed to in writing, software
*  distributed under the License is distributed on an "AS IS" BASIS,
*  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
*  implied. See the License for the specific language governing
*  permissions and limitations under the License.
*
*  See also http://www.apache.org/licenses/LICENSE-2.0.html for an
*  explanation of the license and how it is applied.
--]

[#include "layout.ftl"]

[@adminLeftPaneLayout]

    <span id="page.id" title="viewAdministrativeDocuments"></span>
    [#assign breadcrumb = {"admin":"AdminAction.do?method=load", "manageReports.viewadministrativedocuments":""}/]
    [@widget.crumbpairs breadcrumb /]
    <div class="content_panel">
        <h1>
            [@spring.message "manageReports.viewadministrativedocuments"/]
        </h1>
        <p>
            [@spring.message "manageReports.belowislistofadministrativedocuments"/]
            [@spring.message "manageReports.toeditclickon"/]
            [@spring.message "manageReports.todownloadclickon"/]
            <a href="birtAdminDocumentUploadAction.do?method=getBirtAdminDocumentUploadPage&viewPath=administerreports_path">
            [@spring.message "manageReports.uploadanewadmindoc"/]</a>
        </p>
        <table width="75%" border="0" cellpadding="3" cellspacing="0" >
            [#list listofadministrativedocuments as adminDocument]
                <tr>
                    <td height="30" colspan="2" class="blueline">${adminDocument.name}</td>
                    <td width="45%" class="blueline">
                        <a href="editAdminDocs.ftl?id=${adminDocument.id}">
                        [@spring.message "manageReports.edit"/]</a>
                        |
                        <a href="birtAdminDocumentUploadAction.do?method=downloadAdminDocument&admindocId=${adminDocument.id}">
                        [@spring.message "manageReports.downloadadmindocuments" /]</a>
                    </td>
                </tr>
            [/#list]
        </table>

    </div>
[/@adminLeftPaneLayout]
