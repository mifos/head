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
[#if files?size = 0]
NO DATA FOUND
[#else]
<table>
<tr>
<th class='drawtablehd'>File name</th><th class='drawtablehd'>last modified</th><th class='drawtablehd'>Import Type</th><th class='drawtablehd'>Accounting Details</th>
</tr>
 [#list files as instance ] 
     <tr>
     <td class="drawtablerow">${instance.mfiPrefix} ${instance.fileName}</td>
     <td class="drawtablerow">${instance.lastModified}</td>
     <td class="drawtablerow"><a target='_blank' href='pages/accounting/jsp/processTallyXMLOutput.jsp?fromDate=${instance.startDateInString}&toDate=${instance.endDateInString}'>Tally XML</a></td>
     <td class="drawtablerow"><a href='#' onclick='javascript:getAccountionDataDetails("${instance.startDateInString}","${instance.endDateInString}")'>Details</a></td>
     </tr>
[/#list]
</table>
[/#if]