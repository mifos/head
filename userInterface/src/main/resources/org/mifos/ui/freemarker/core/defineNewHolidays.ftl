[#ftl]
[#import "spring.ftl" as spring]
[#import "blueprintmacros.ftl" as mifos]
[@mifos.header "title" /]
  [@mifos.topNavigationNoSecurity currentTab="Admin" /]
   <!--  Left Sidebar Begins-->
  <div class="sidebar ht750">
    <p class="orangetab">[@spring.message "clients&AccountsTasks"/]</p>
    <form name="custSearchActionForm" method="post"
                              action="/mifos/custSearchAction.do?method=loadAllBranches">
        <p class="paddingLeft marginTop10 fontBold">[@spring.message "searchbynamesystemIDoraccountnumber"/]<br />
    <input type="text" name="searchString" maxlength="200" size="15" value="">
      <br />
      <input type="submit" name="searchButton" value="[@spring.message "search" /]" class="buttn floatRight">
    </p>
        <p>&nbsp;&nbsp;</p>
    
    </form>
    <p>&nbsp;&nbsp;</p>
    <p class="paddingLeft"><span class="fontBold">[@spring.message "manageCollectionSheets"/]</span><br />
      <a href="#">[@spring.message "enterCollectionSheetData"/]</a> </p>
      <p>&nbsp;&nbsp;</p>
    <p class="paddingLeft"><span class="fontBold">[@spring.message "createnewClients"/]</span><br />
      <a href="#">[@spring.message "createnewCenter"/]</a><br />
      <a href="#">[@spring.message "createnewGroup"/]</a><br />
      <a href="#">[@spring.message "createnewClient"/]</a> </p>
      <p>&nbsp;&nbsp;</p>
    <p class="paddingLeft"><span class="fontBold">[@spring.message "createnewAccounts"/]</span><br />
      <a href="#">[@spring.message "createSavingsaccount"/]</a><br />
      <a href="newLoan.html">[@spring.message "createLoanaccount"/]</a><br />
      <a href="#">[@spring.message "createmultipleLoanaccounts"/]</a> </p>
      <p>&nbsp;&nbsp;</p>
    <p class="paddingLeft"><span class="fontBold">[@spring.message "manageaccountstatus"/]</span><br />
      <a href="#">[@spring.message "changeaccountstatus"/]</a> </p>
  </div>
  <!--  Left Sidebar Ends-->
   <!--  Main Content Begins-->  
  <div class=" content leftMargin180">
  	<form method="" action="" name="formname">
    <div class="span-24">
  		<div class="bluedivs paddingLeft"><a href="admin.ftl">[@spring.message "admin" /]</a>&nbsp;/&nbsp;<a href="viewHolidays.ftl">[@spring.message "viewHolidays" /]</a>&nbsp;/&nbsp;<span class="fontBold">[@spring.message "addHoliday" /]</span></div>
        <div class="clear">&nbsp;</div>
    	<p class="font15"><span class="orangeheading">[@spring.message "addHoliday" /]</span></p>
    	<p>&nbsp;&nbsp;</p>
        <div><span class="red">* </span>[@spring.message "fieldsmarkedwithanasteriskarerequired." /] </div>
        <p>&nbsp;&nbsp;</p>
                <div class="prepend-3 span-22 last">
        	<span class="span-4 rightAlign"><span class="red"> * </span>[@spring.message "holidayName" /]</span><span class="span-4"><input type="text" size="45" id="holiday.input.name"/></span>
        </div>
        <p>&nbsp;&nbsp;</p>
        <div class="prepend-3 span-22 last">
            <span class="span-4 rightAlign"><span class="red"> * </span>[@spring.message "fromDat" /] </span>
            <span class="span-2"><input type="text" id="holidayFromDateDD" size="1" maxlength="2" />[@spring.message "DD" /]</span>
            <span class="span-2"><input type="text" id="holidayFromDateMM" size="1" maxlength="2" />[@spring.message "MM" /]</span>
            <span class="span-3"><input type="text" id="holidayFromDateYY" size="2" maxlength="4" />[@spring.message "YYYY" /]</span>
        </div>
        <p>&nbsp;&nbsp;</p>
        <div class="prepend-3 span-22 last">
            <span class="span-4 rightAlign">[@spring.message "toDate"/]</span><span class="span-2"><input type="text" id="holidayThruDateDD" size="1" maxlength="2" />[@spring.message "DD" /]</span>
            <span class="span-2"><input type="text" id="holidayThruDateMM" size="1" maxlength="2" />[@spring.message "MM" /]</span>
            <span class="span-3"><input type="text" id="holidayThruDateYY" size="2" maxlength="4" />[@spring.message "YYYY" /]</span>
        </div>
        <p>&nbsp;&nbsp;</p>
        <div class="prepend-3 span-22 last">
            <span class="span-4 rightAlign"><span class="red"> * </span>[@spring.message "repaymentRule" /]</span><span class="span-5">
            									<select id="holiday.input.repaymentrule">
                                                	<option>[@spring.message "--Select--" /]</option>
                                                    <option>[@spring.message "sameDay" /]</option>
                                                    <option>[@spring.message "nextMeeting/Repayment" /]</option>
                                                    <option>[@spring.message "nextWorkingDay" /]</option>
            									</select></span>
        </div>
        <p>&nbsp;&nbsp;</p>
        <div class="prepend-3 span-22 last">
            <span class="span-4 rightAlign"><span class="red"> * </span>[@spring.message "appliesto" /] &nbsp;:</span><span class="span-5"></div>
        <div class="clear">&nbsp;</div>
        <hr />
        <div class="prepend-10">
            <input class="buttn" type="button" id="holiday.button.preview" name="preview"  value="Preview" onclick="#"/>
            <input class="buttn2" type="button" name="cancel" value="Cancel" onclick="window.location='viewHolidays.ftl'"/>
        </div>
	</div>
   	</form> 
  </div><!--Main Content Ends-->
  [@mifos.footer/]