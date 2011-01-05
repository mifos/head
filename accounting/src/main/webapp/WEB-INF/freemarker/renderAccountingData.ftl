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
<a target="_blank" href="pages/accounting/jsp/processTallyXMLOutput.jsp?fromDate=${fromDate}&toDate=${toDate}">Download Tally XML format</a>
<br />
[#if hasAlreadyRanQuery]
<font color="red">This query has been already ran and probably imported also.</font>
[/#if]
<br />
<a href="javascript:void(processPrint('table'));">Print</a>
<div id='table'>
<table>
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