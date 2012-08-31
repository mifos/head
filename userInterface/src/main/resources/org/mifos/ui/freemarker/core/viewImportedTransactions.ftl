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
<span id="page.id" title="ImportedTransactions"></span>
<div class="content">
    <table id="importedTransactions" class="datatable">
        <thead>
            <tr>
                <th>
                    <span class='fontnormalbold'>
                    [@spring.message "admin.viewimportedfiles.file.name" /]
                    <span>
                </th>
                <th>
                    <span class='fontnormalbold'>
                    [@spring.message "admin.viewimportedfiles.date" /]
                    <span>
                </th>
                <th>
                    <span class='fontnormalbold'>
                    [@spring.message "admin.viewimportedfiles.status" /]
                    <span>
                </th>    
            </tr>
        </thead>
            [#list importedFiles as file]
                <tr>
                    <td>
                    ${file.importedFileName}
                    </td>
                    <td>
                    ${i18n.date_formatter(file.submittedOn, "dd/MM/yyyy", Application.LocaleSetting.locale)}       
                    </td>
                    <td>
                    [#if !file.phaseOut]
                        <a href='viewImportedTransactions.ftl?fileName=${file.importedFileName}'>
                            [@spring.message "admin.viewimportedfiles.undo"/]
                        </a>
                    [#else]
                        [@spring.message "admin.importexport.undo.phaseout" /]
                    [/#if]
                    </td>
                </tr>
            [/#list]
        <tbody>

        </tbody>
    </table>
    [@widget.datatable "importedTransactions" /]
    [@form.returnToPage  "AdminAction.do?method=load" "button.back" "admin.importexport.button.back"/]
</div>
[/@adminLeftPaneLayout]