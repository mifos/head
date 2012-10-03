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
        <script type="text/javascript" src="pages/accounting/js/accounting.js"></script>
        <span id="page.id" title="view_accounting_data_exports"></span>
<script>
function addExportListLink(listStartDay, totalNumberOfExports) {
	if(listStartDay == 0) {
	   $("#previous").html("<font color='grey'>[@spring.message "accounting.previous"/]</font>");
	}
	else
	{
        $("#previous").html("<a href='#' onclick=\"javascript:loadExportsList("+ (listStartDay-10) + ","+totalNumberOfExports+");\">[@spring.message "accounting.previous"/]</a>");
    }

	if(listStartDay > (totalNumberOfExports - 11)) {
	    $("#results").html("[@spring.message "accounting.results"/] "+(listStartDay+1)+"-"+(totalNumberOfExports)+" of "+totalNumberOfExports);
	    $("#next").html("<font color='grey'>[@spring.message "accounting.next"/]</font>");
	}
	else 
	{
	    $("#results").html("[@spring.message "accounting.results"/] "+(listStartDay+1)+"-"+(listStartDay +10)+" of "+totalNumberOfExports);
        $("#next").html("<a href='#' onclick=\"javascript:loadExportsList("+ (listStartDay+10) + ","+totalNumberOfExports+");\">[@spring.message "accounting.next"/]</a>");
    }
}
</script>

<div class="content "> <!--  Main Content Begins-->
   [@widget.crumbs breadcrumbs /]
 <div class="margin10lefttop">

        <p class="font15 orangeheading margin5topbottom">[@spring.message "accounting.viewaccountingexports"/]</p>

[#if numberDaysFromStartOfFinancialTransactions = 0]
      <b>[@spring.message "accounting.viewaccountingexports.nodata"/]:</b>
      <p> transactions should exist to convert into Accounting data
[#else]
        <p class="margin5top10bottom">[@spring.message "accounting.viewaccountingexports.cache.instruction"/]<p>
        <br />
    <br />
    <div id='table'>
         
            <div id='export_list'><script>loadExportsList(0,${numberDaysFromStartOfFinancialTransactions?c});</script></div>
    </div>
    <div align='center'>
    <b>
    <span id='previous'></span> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
    <span id='results'></span>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
    <span id='next'></span>
    </b>
    </div>
        <div class="buttonsSubmitCancel margin20right">
            <input id='clearexport' type="button" class="buttn" value="[@spring.message "accounting.clearexports"/]" onclick="javascript:gotToConfirmExportDeletePage()" />
            <input id='cancel' type="button" class="buttn2" value="[@spring.message "cancel"/]" onclick="javascript:goToAdmin()" />
        </div>
[/#if]
    </div>
    <br />
</div><!--Main Content Ends-->
[/@adminLeftPaneLayout]