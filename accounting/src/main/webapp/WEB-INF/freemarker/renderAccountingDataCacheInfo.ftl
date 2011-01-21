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
<span id="page.id" title="accounting_data"/>
        <STYLE TYPE="text/css"><!-- @import url(pages/css/jquery/jquery-ui.css); --></STYLE>
        <script type="text/javascript" src="pages/js/jquery/jquery-1.4.2.min.js"></script>
        <script type="text/javascript" src="pages/js/jquery/jquery-ui.min.js"></script>
        <script type="text/javascript" src="pages/js/jquery/jquery.datePicker.min-2.1.2.js"></script>
        <script type="text/javascript" src="pages/js/jquery/jquery.datePicker.configuration.js"></script>
        <STYLE TYPE="text/css"><!-- @import url(pages/css/datepicker/datepicker.css); --></STYLE>
        <script type="text/javascript" src="pages/framework/js/CommonUtilities.js"></script>
		<!--[if IE]><script type="text/javascript" src="pages/js/jquery/jquery.bgiframe.js"></script><![endif]-->
		<SCRIPT SRC="pages/framework/js/CommonUtilities.js"></SCRIPT>
<script type="text/javascript" src="pages/accounting/js/accounting.js"></script>
[@adminLeftPaneLayout] <!--  Main Content Begins-->
[@mifos.crumbs breadcrumbs /]
<br />
<p class="font15 orangeheading">[@spring.message "accounting.viewaccountingexports"/]</p>
<br />
<br />
[#if files?size = 0]
[@spring.message "accounting.viewaccountingexports.nodata"/]
[#else]
[@spring.message "accounting.viewaccountingexports.instruction"/]
<br />
<br />
<table>
<tr>
<th class='drawtablehd'>File name</th><th class='drawtablehd'>Last generated</th><th class='drawtablehd'>Import Type</th><th class='drawtablehd'>Accounting Details</th>
</tr>
 [#list files as instance ] 
     <tr>
     <td class="drawtablerow">${instance.mfiPrefix} ${instance.fileName}</td>
     <td class="drawtablerow">${instance.lastModified}</td>
     <td class="drawtablerow">
<a target='_blank' href='pages/accounting/jsp/processTallyXMLOutput.jsp?fromDate=${instance.startDateInString}&toDate=${instance.endDateInString}'>Tally&nbs; XML</a>
</td>
     <td class="drawtablerow"><a href='renderAccountingData.ftl?fromDate=${instance.startDateInString}&toDate=${instance.endDateInString}'>View Details</a></td>
     </tr>
[/#list]
</table>
<div class="clear">&nbsp;</div>
 <div class="buttonsSubmitCancel margin20right">
 <input type="button" class="buttn" value="[@spring.message "accounting.clearexports"/]" onclick="javascript:deleteCacheDir()" />
 <input type="button" class="buttn2" value="[@spring.message "cancel"/]" onclick="javascript:goToAdmin()" />
</div>
<div class="clear">&nbsp;</div>
[/#if]
[/@adminLeftPaneLayout]