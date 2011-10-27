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

<head>
    <link rel="stylesheet" type="text/css" href="pages/css/viewAdmin4Testing.css" />
</head>

[#include "layout.ftl"]

[@adminLeftPaneLayout]

    <span id="page.id" title="editAdministrativeDocuments"></span>
    [#assign breadcrumb = {"admin":"AdminAction.do?method=load", "manageReports.viewadministrativedocuments":""}/]
    [@widget.crumbpairs breadcrumb /]
    <div class="content_panel">
        <p>
            Work in progress.
        </p>
        <fieldset>
            <legend>Updated data</legend>
            <ol>
            <li><label>Document Name:</label> ${formBean.name}</li>
            <li><label>Account Type:</label> ${formBean.accountType}</li>
            <li><label>Status:</label>
            [#list formBean.showStatus as fields]
                ${fields}
            [/#list]
            </li>
            </ol>
        </fieldset>
    </div>

[/@adminLeftPaneLayout]