[#ftl]
[#import "spring.ftl" as spring]
[#import "blueprintmacros.ftl" as mifos]
[@mifos.header "title" /]
  [@mifos.topNavigationNoSecurity currentTab="Admin" /]
     <!--  Main Content Begins-->
  <div class="content marginAuto">
    <div class="borders span-22">
      <div class="borderbtm span-22">
        <p class="span-15 arrowIMG orangeheading">[@spring.message "manageImports.importTransactions.importinformation"/]</p>
        <p class="span-3 arrowIMG1 orangeheading last">[@spring.message "reviewAndSubmit"/]</p>
      </div>
      <p>&nbsp;&nbsp;</p>
      <div class="subcontent">
        <form method="" action="" name="formname">
          <p class="font15"><span class="fontBold">[@spring.message "manageImports.importTransactions.importinformation"/]</span>&nbsp;--&nbsp;<span class="orangeheading">[@spring.message "manageImports.importTransactions.enterfileinformation"/]</span></p>
          <div>[@spring.message "manageImports.importTransactions.completethefieldsbelow.ClickCanceltoreturntoAdminwithoutsubmittinginformation."/]</div>
          <div><span class="red">* </span>[@spring.message "fieldsmarkedwithanasteriskarerequired."/] </div>
          <p>&nbsp;&nbsp;</p>
          <p class="error" id="error1"></p>
          <div class="prepend-6  span-21 last">
          	<div class="span-20 "><span class="span-3 rightAlign"><span class="red">* </span>[@spring.message "manageImports.importTransactions.importformat"/]</span><span class="span-4">&nbsp;
    				<select name="select">
                        <option >[@spring.message "--Select--"/]</option>
				    </select></span>
  			</div>
        	<div class="span-20 "><span class="span-3 rightAlign"><span class="red">* </span>[@spring.message "manageImports.importTransactions.selectimportfile"/]</span><span>&nbsp;
   					<input type="file" name="choose" value="Choose File" /></span>
			</div>
			<p>&nbsp;&nbsp;</p>
          </div>
          <div class="clear">&nbsp;</div>
          <hr />
          <div class="prepend-9">
            	<input class="buttn" id="import_transactions.button.review" type="button" name="preview" value="[@spring.message "preview"/]" onclick="#"/>
            	<input class="buttn2" id="import_transactions.button.cancel" type="button"  name="cancel" value="[@spring.message "cancel"/]" onclick="window.location='admin.ftl'"/>
          </div>
          <div class="clear">&nbsp;</div>
        </form>
      </div>
      <!--Subcontent Ends-->
    </div>
  </div>
  <!--Main Content Ends-->
  [@mifos.footer/]