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
        <span id="page.id" title="view_accounting_data_detail"/>

<div class="content "> <!--  Main Content Begins-->
   [@mifos.crumbs breadcrumbs /]
 <div class="margin10lefttop">


        <p class="font15 orangeheading margin5topbottom">${fileName}</p>


    [#if hasAlreadyRanQuery]
        <p class="margin5top10bottom"><font color="red">[@spring.message "accounting.viewaccountingexports.warning"/]</font></p>
    [/#if]

    <br />

    [#if accountingData?size = 0]
        <b>[@spring.message "accounting.viewaccountingexports.nodata"/] : ${fromDate} - ${toDate}</b>
    [#else]
        [@spring.message "accounting.viewaccountingexports.instruction"/]


        <div id='table'>
            <table>
                <tr>
                  <td colspan='7'>
                      <div align='right'>
                           <a target="_blank" href="pages/accounting/jsp/processTallyXMLOutput.jsp?fromDate=${fromDate}&toDate=${toDate}">Download Tally XML format</a>
                      </div>
                      <div align='right'><a href="javascript:void(processPrint('table'));">Print</a> </div>
                  </td>
                </tr>
                <tr>
                    <th class='drawtablehd'>Branch</th>
                    <th class='drawtablehd'>Voucher Date</th>
                    <th class='drawtablehd'>Voucher Type</th>
                    <th class='drawtablehd'>GL Code</th>
                    <th class='drawtablehd'>GL Name</th>
                    <th class='drawtablehd'>Debit</th>
                    <th class='drawtablehd'>Credit</th>
                </tr>
        [#list accountingData as instance]
                <tr>
                    <td class="drawtablerow">${instance.branchName}</td>
                    <td class="drawtablerow">${instance.voucherDate}</td>
                    <td class="drawtablerow">${instance.voucherType}</td>
                    <td class="drawtablerow">${instance.glCode}</td>
                    <td class="drawtablerow">${instance.glCodeName}</td>
                    <td class="drawtablerow">${instance.debit}</td>
                    <td class="drawtablerow">${instance.credit}</td>
                </tr>
        [/#list]
            </table>
        </div>
    [/#if]
  </div>
</div><!--Main Content Ends-->
[/@adminLeftPaneLayout]