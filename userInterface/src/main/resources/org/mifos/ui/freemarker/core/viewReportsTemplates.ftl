[#ftl]
[#import "spring.ftl" as spring]
[#import "blueprintmacros.ftl" as mifos]

[@mifos.header "title" /]
[@mifos.topNavigationNoSecurity currentTab="Admin" /]
<div class="sidebar htTotal">
[#include "adminLeftPane.ftl" ]
</div> 
 <!--  Main Content Begins-->  
  <div class=" content leftMargin180">
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
  [@mifos.footer/]