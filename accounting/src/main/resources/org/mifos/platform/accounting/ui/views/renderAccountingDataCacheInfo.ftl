 [#ftl]
[#--
* Copyright (c) 2005-2010 Grameen Foundation USA
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
        <script type="text/javascript" src="pages/accounting/js/accounting.js"></script>
        <span id="page.id" title="view_accounting_data_exports"/>


<div class="content "> <!--  Main Content Begins-->
   [@mifos.crumbs breadcrumbs /]
 <div class="margin10lefttop">

        <p class="font15 orangeheading margin5topbottom">[@spring.message "accounting.viewaccountingexports"/]</p>

    [#if files?size = 0]
        [@spring.message "accounting.viewaccountingexports.nodata"/]
    [#else]
        <p class="margin5top10bottom">[@spring.message "accounting.viewaccountingexports.cache.instruction"/]<p>
        <br />
    <br />
    <div id='table'>
        <table>
            <tr>
                <th class='drawtablehd'>File name</th>
                <th class='drawtablehd'>Last generated</th>
                <th class='drawtablehd'>Import Type</th>
                <th class='drawtablehd'>Accounting Details</th>
            </tr>
         [#list files as instance ]
             <tr>
                 <td class="drawtablerow">${instance.fileName}</td>
                 <td class="drawtablerow">${instance.lastModified}</td>
                 <td class="drawtablerow">
                <a target='_blank' href='pages/accounting/jsp/processTallyXMLOutput.jsp?fromDate=${instance.startDate}&toDate=${instance.endDate}'>Tally&nbsp; XML</a>
                </td>
                 <td class="drawtablerow"><a href='renderAccountingData.ftl?fromDate=${instance.startDate}&toDate=${instance.endDate}'>View Details</a></td>
             </tr>
        [/#list]
        </table>
    </div>

        <div class="buttonsSubmitCancel margin20right">
            <input id='clearexport' type="button" class="buttn" value="[@spring.message "accounting.clearexports"/]" onclick="javascript:gotToConfirmExportDeletePage()" />
            <input id='cancel' type="button" class="buttn2" value="[@spring.message "cancel"/]" onclick="javascript:goToAdmin()" />
        </div>

    [/#if]
    </div>
    <br />
    <div id='auto_export_table'></div>
    <div id='add_auto_export'><a href='#' onclick='javascript:loadExportsList(10);'>Show Alls Days' Export</a></div>
</div><!--Main Content Ends-->
[/@adminLeftPaneLayout]