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
 <!--  Main Content Begins-->  
  <div class=" content">
  	<form method="" action="" name="formname">
  	<p class="bluedivs paddingLeft"><a href="admin.ftl">[@spring.message "admin"/]</a>&nbsp;/&nbsp;<span class="fontBold">[@spring.message "viewreports"/]</span></p>
    <p class="font15 orangeheading">[@spring.message "viewreports"/]</p>
    <p>&nbsp;&nbsp;</p>
    <p>[@spring.message "clickontheEditlinktomakechangestoreport'sstatus,category,orreportdesigndocument"/]<br /> [@spring.message "toinstallanewBIRTreport,clickon"/] <a href="#">[@spring.message "uploadanewreport"/]</a></p>
    <p>&nbsp;&nbsp;</p>
    <div class="span-22">
    	<div class="span-22 borderbtm paddingLeft">
        	<span class="span-15 fontBold">[@spring.message "clientDetail"/]</span>
        </div>
        <div class="span-22 borderbtm paddingLeft ">
        	<span class="span-15">[@spring.message "collectionSheetReport"/]</span>
            <span class="span-4"><a href="#">[@spring.message "edit"/]</a>&nbsp;|&nbsp;<a href="#">[@spring.message "download"/]</a></span>
        </div>
            </div>
    <div class="span-22">
    	<div class="span-22 borderbtm paddingLeft">
        	<span class="span-15 fontBold">[@spring.message "performance"/]</span>
        </div>
           </div>
           <div class="span-22">
    	<div class="span-22 borderbtm paddingLeft">
        	<span class="span-15 fontBold">[@spring.message "center"/]</span>
        </div>
           </div>
           <div class="span-22">
    	<div class="span-22 borderbtm paddingLeft">
        	<span class="span-15 fontBold">[@spring.message "loanProductDetail"/]</span>
        </div>
           </div>
    <div class="span-22">
    	<div class="span-22 borderbtm paddingLeft">
        	<span class="span-15 fontBold">[@spring.message "status"/]</span>
        </div>
            </div>
    <div class="span-22">
    	<div class="span-22 borderbtm paddingLeft">
        	<span class="span-15 fontBold">[@spring.message "analysis"/]</span>
        </div>
        <div class="span-22 borderbtm paddingLeft ">
        	<span class="span-15">[@spring.message "branchCashConfirmationReport"/]</span>
            <span class="span-4"><a href="#">[@spring.message "edit"/]</a>&nbsp;|&nbsp;<a href="#">[@spring.message "download"/]</a></span>
        </div>
        <div class="span-22 borderbtm paddingLeft ">
        	<span class="span-15">[@spring.message "branchProgressReport"/]</span>
            <span class="span-4"><a href="#">[@spring.message "edit"/]</a>&nbsp;|&nbsp;<a href="#">[@spring.message "download"/] </a></span>
        </div>
        <div class="span-22 borderbtm paddingLeft ">
        	<span class="span-15">[@spring.message "detailedAgingOfPortfolioAtRiskReport"/]</span>
            <span class="span-4"><a href="#">[@spring.message "edit"/]</a>&nbsp;|&nbsp;<a href="#">[@spring.message "download"/]</a></span>
        </div>
        <div class="span-22 borderbtm paddingLeft ">
        	<span class="span-15">[@spring.message "generalLedgerReport"/]</span>
            <span class="span-4"><a href="#">[@spring.message "edit"/]</a>&nbsp;|&nbsp;<a href="#">[@spring.message "download"/] </a></span>
        </div>
           </div>
    <div class="span-22">
    	<div class="span-22 borderbtm paddingLeft">
        	<span class="span-15 fontBold">[@spring.message "miscellaneous"/]</span>
        </div>
    </div>
   	</form> 
  </div><!--Main Content Ends-->
[/@adminLeftPaneLayout]