[#ftl]
[#import "spring.ftl" as spring]
[#import "blueprintmacros.ftl" as mifos]
[@mifos.header "title" /]
  [@mifos.topNavigationNoSecurity currentTab="Admin" /]
  <!--  Main Content Begins-->
  <div class="content marginAuto">
    <div class="borders span-22">
      <div class="borderbtm span-22">
        <p class="span-15 arrowIMG orangeheading">[@spring.message "savingsproductinformation" /]</p>
        <p class="span-3 arrowIMG1 orangeheading last">[@spring.message "review&Submit" /]</p>
      </div>
      <div class="subcontent ">
        <form method="" action="" name="formname">
          <p class="font15"><span class="fontBold">[@spring.message "addanewSavingsProduct" /]</span>&nbsp;--&nbsp;<span class="orangeheading">[@spring.message "enterSavingsproductinformation" /]</span></p>
          <div>[@spring.message "completethefieldsbelow" /]</div>
          <div><span class="red">* </span>[@spring.message "fieldsmarkedwithanasteriskarerequired." /] </div>
          <p class="error" id="error1"></p>
          <p class="fontBold">[@spring.message "savingsproductdetails" /]</p>
          <div class="prepend-3  span-21 last">
          	<div class="span-20 "><span class="pull-3 span-8 rightAlign"><span class="red">* </span>[@spring.message "productinstancename" /]&nbsp;</span><span class="span-4">
    				<input type="text" id="CreateSavingsProduct.input.prdOfferingName" /></span>
  			</div>
            <div class="span-20 "><span class="pull-3 span-8 rightAlign"><span class="red">* </span>[@spring.message "shortname" /]&nbsp;</span><span class="span-4">
    				<input type="text" size="3" id="CreateSavingsProduct.input.prdOfferingShortName" /></span>
  			</div>
            <div class="span-20 "><span class="pull-3 span-8 rightAlign">[@spring.message "description" /]&nbsp;</span><span class="span-4">
    				<textarea rows="7" cols="55"></textarea></span>
  			</div>
            
        	<div class="span-20 "><span class="pull-3 span-8 rightAlign"><span class="red">* </span>[@spring.message "productcategory" /]&nbsp;</span><span class="span-4">
   					<select name="select" id="CreateSavingsProduct.input.prdCategory">
      					<option >[@spring.message "--Select--" /]</option>
				    </select></span>
			</div>
            <div class="span-20 last">
            	<span class="pull-3 span-8 rightAlign"><span class="red"> * </span>[@spring.message "fromDate" /]</span>
            	<span class="span-2"><input type="text" size="1" maxlength="2" id="startDateDD" />[@spring.message "DD" /]</span>
            	<span class="span-2"><input type="text" size="1" maxlength="2" id="startDateMM" />[@spring.message "MM" /]</span>
            	<span class="span-3"><input type="text" size="2" maxlength="4" id="startDateYY" />[@spring.message "YYYY" /]</span>
        	</div>
        	<div class="span-20 last">
            	<span class="pull-3 span-8 rightAlign">[@spring.message "endDate" /]</span><span class="span-2"><input type="text" size="1" maxlength="2" id="endDateDD" />[@spring.message "DD" /]</span>
            	<span class="span-2"><input type="text" size="1" maxlength="2" id="endDateMM" />[@spring.message "MM" /]</span>
            	<span class="span-3"><input type="text" size="2" maxlength="4" id="endDateYY" />[@spring.message "YYYY" /]</span>
        	</div>
            <div class="span-20 "><span class="pull-3 span-8 rightAlign"><span class="red">* </span>[@spring.message "applicablefor" /]&nbsp;</span><span class="span-4">
   					<select name="select" id="CreateSavingsProduct.input.applfor">
      					<option >[@spring.message "--Select--" /]</option>
                        <option >[@spring.message "clients" /]</option>
                        <option >[@spring.message "groups" /]</option>
                        <option >[@spring.message "centers" /]</option>
				    </select></span>
			</div>
          </div>
          <div class="clear">&nbsp;</div>
          <p class="fontBold">[@spring.message "targetedDepositsandWithdrawalRestrictions" /]</p>
          <div class="prepend-3  span-21 last">
          	<div class="span-20 "><span class="pull-3 span-8 rightAlign"><span class="red">* </span>[@spring.message "typeofdeposits" /]&nbsp;</span><span class="span-4">
   					<select name="select" id="CreateSavingsProduct.input.typeofdep">
      					<option >[@spring.message "--Select--" /]</option>
                        <option >[@spring.message "mandatory" /]</option>
                        <option >[@spring.message "voluntary" /]</option>
				    </select></span>
			</div>
            <div class="span-20 "><span class="pull-3 span-8 rightAlign" id="recamnt">[@spring.message "recommendedAmountforDeposit" /]&nbsp;</span><span class="span-4">
    				<input type="text" id="CreateSavingsProduct.input.recommendedAmount"/></span>
  			</div>
            <div class="span-20 "><span class="pull-3 span-8 rightAlign" id="appliesto"><span class="red">* </span>[@spring.message "amountAppliesto" /]&nbsp;</span><span class="span-4">
   					<select name="select" disabled="disabled">
      					<option >[@spring.message "--Select--" /]</option>
                        <option >[@spring.message "perIndividual" /]</option>
                        <option >[@spring.message "completeGroup" /]</option>
				    </select></span>
			</div>
            <div class="span-20 "><span class="pull-3 span-8 rightAlign">[@spring.message "maxamountperwithdrawal" /]&nbsp;</span><span class="span-4">
    				<input type="text" /></span>
  			</div>
          </div>
          <div class="clear">&nbsp;</div>
          <p class="fontBold">[@spring.message "interestrate" /] </p>
          <div class="prepend-3  span-21 last">
	      	<div class="span-20"><span class="pull-3 span-8 rightAlign"><span class="red">* </span>[@spring.message "interestrate" /]&nbsp;</span><span class="span-6">
    				<input type="text" id="CreateSavingsProduct.input.interestRate" />&nbsp;(0 - 100)%</span>
            
            </div>
            <div class="span-20 "><span class="pull-3 span-8 rightAlign"><span class="red">* </span>[@spring.message "balanceusedforInterestcalculation" /]&nbsp;</span><span class="span-4">
   					<select name="select" id="CreateSavingsProduct.input.balUsedForCalc">
      					<option >[@spring.message "--Select--" /]</option>
                        <option >[@spring.message "minimumBalance" /]</option>
                        <option >[@spring.message "averageBalance" /]</option>
				    </select></span>
			</div>
            <div class="span-20 "><span class="pull-3 span-8 rightAlign"><span class="red">* </span>[@spring.message "timeperiodforInterestcalculation" /]&nbsp;</span>
            	<span class="span-9">
                	<span class="span-2"><input type="text" size="3" id="CreateSavingsProduct.input.timeForInterestCalc"/></span>
                    <span class="span-4">
   					<select name="select" id="CreateSavingsProduct.input.typeOfTimeForInterestCalc">
                        <option >[@spring.message "day(s)" /]</option>
                        <option >[@spring.message "month(s)" /]</option>
				    </select></span></span>
			</div>
            <div class="span-20 "><span class="pull-3 span-8 rightAlign"><span class="red">* </span>[@spring.message "frequencyofInterestpostingtoaccounts" /]&nbsp;</span>
            	<span class="span-9">
                	<span class="span-4"><input type="text" size="3" id="CreateSavingsProduct.input.freqOfInterest" />&nbsp;&nbsp;[@spring.message "month(s)" /]</span>
			</div>
            <div class="span-20 "><span class="pull-3 span-8 rightAlign">[@spring.message "minimumbalancerequiredforInterestcalculation" /]&nbsp;</span><span class="span-4">
    				<input type="text" /></span>
  			</div>
          </div>
          <div class="clear">&nbsp;</div>
          <p class="fontBold">[@spring.message "accounting" /] </p>
          <div class="prepend-3  span-21 last">
            <div class="span-20 "><span class="pull-3 span-8 rightAlign"><span class="red">* </span>[@spring.message "gLcodefordeposits" /]&nbsp;</span><span class="span-4">
   					<select name="select" id="CreateSavingsProduct.input.depositGLCode">
      					<option >[@spring.message "--Select--" /]</option>
                        <option >24101</option>
                        <option >4606</option>
                        <option >23101</option>
                        <option >4601</option>
                        <option >4603</option>
                        <option >4602</option>
				    </select></span>
			</div>
            <div class="span-20 "><span class="pull-3 span-8 rightAlign"><span class="red">* </span>[@spring.message "gLcodeforinterest" /]&nbsp;</span>
                    <span class="span-4">
   					<select name="select" id="CreateSavingsProduct.input.interestGLCode">
                    	<option >[@spring.message "--Select--" /]</option>
                        <option >41101</option>
                        <option >41102</option>
				    </select></span>
			</div>
          </div>
          <div class="clear">&nbsp;</div>
          <hr />
          <div class="prepend-9">
            	<input class="buttn" type="button" id="CreateSavingsProduct.button.preview" class="buttn" name="preview" value="Preview" onclick="#"/>
            	<input class="buttn2" type="button" name="cancel" value="Cancel" onclick="window.location='admin.ftl'"/>
          </div>
          <div class="clear">&nbsp;</div>
        </form>
      </div>
      <!--Subcontent Ends-->
    </div>
  </div>
  <!--Main Content Ends-->
  [@mifos.footer/]