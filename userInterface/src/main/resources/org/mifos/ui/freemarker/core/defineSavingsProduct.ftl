[#ftl]
[#import "spring.ftl" as spring]
[#import "blueprintmacros.ftl" as mifos]
[@mifos.header "title" /]
  [@mifos.topNavigationNoSecurity currentTab="Admin" /]
  <!--  Main Content Begins-->
  <div class="content marginAuto">
    <div class="borders span-22">
      <div class="borderbtm span-22">
        <p class="span-15 arrowIMG orangeheading">[@spring.message "manageProducts.defineSavingsProducts.savingsproductinformation" /]</p>
        <p class="span-3 arrowIMG1 orangeheading last">[@spring.message "review&Submit" /]</p>
      </div>
      <div class="subcontent ">
        <form method="" action="" name="formname">
          <p class="font15"><span class="fontBold">[@spring.message "manageProducts.defineSavingsProducts.addanewSavingsProduct" /]</span>&nbsp;--&nbsp;<span class="orangeheading">[@spring.message "manageProducts.defineSavingsProducts.enterSavingsproductinformation" /]</span></p>
          <div>[@spring.message "manageProducts.defineSavingsProducts.completethefieldsbelow" /]</div>
          <div><span class="red">* </span>[@spring.message "fieldsmarkedwithanasteriskarerequired." /] </div>
          <p class="error" id="error1"></p>
          <p class="fontBold">[@spring.message "manageProducts.defineSavingsProducts.savingsproductdetails" /]</p>
          <div class="prepend-3  span-21 last">
          	<div class="span-20 "><span class="pull-3 span-8 rightAlign"><span class="red">* </span>[@spring.message "manageProducts.defineSavingsProducts.productinstancename" /]&nbsp;</span><span class="span-4">
    				<input type="text" id="CreateSavingsProduct.input.prdOfferingName" /></span>
  			</div>
            <div class="span-20 "><span class="pull-3 span-8 rightAlign"><span class="red">* </span>[@spring.message "manageProducts.defineSavingsProducts.shortname" /]&nbsp;</span><span class="span-4">
    				<input type="text" size="3" id="CreateSavingsProduct.input.prdOfferingShortName" /></span>
  			</div>
            <div class="span-20 "><span class="pull-3 span-8 rightAlign">[@spring.message "manageProducts.defineSavingsProducts.description" /]&nbsp;</span><span class="span-4">
    				<textarea rows="7" cols="55"></textarea></span>
  			</div>
            
        	<div class="span-20 "><span class="pull-3 span-8 rightAlign"><span class="red">* </span>[@spring.message "manageProducts.defineSavingsProducts.productcategory" /]&nbsp;</span><span class="span-4">
   					<select name="select" id="CreateSavingsProduct.input.prdCategory">
      					<option >--Select--</option>
				    </select></span>
			</div>
            <div class="span-20 last">
            	<span class="pull-3 span-8 rightAlign"><span class="red"> * </span>[@spring.message "manageProducts.defineSavingsProducts.fromDate" /]&nbsp;:</span>
            	<span class="span-2"><input type="text" size="1" maxlength="2" id="startDateDD" />[@spring.message "DD" /]</span>
            	<span class="span-2"><input type="text" size="1" maxlength="2" id="startDateMM" />[@spring.message "MM" /]</span>
            	<span class="span-3"><input type="text" size="2" maxlength="4" id="startDateYY" />[@spring.message "YYYY" /]</span>
        	</div>
        	<div class="span-20 last">
            	<span class="pull-3 span-8 rightAlign">[@spring.message "manageProducts.defineSavingsProducts.endDate" /]&nbsp;:</span><span class="span-2"><input type="text" size="1" maxlength="2" id="endDateDD" />[@spring.message "DD" /]</span>
            	<span class="span-2"><input type="text" size="1" maxlength="2" id="endDateMM" />[@spring.message "MM" /]</span>
            	<span class="span-3"><input type="text" size="2" maxlength="4" id="endDateYY" />[@spring.message "YYYY" /]</span>
        	</div>
            <div class="span-20 "><span class="pull-3 span-8 rightAlign"><span class="red">* </span>[@spring.message "manageProducts.defineSavingsProducts.applicablefor" /]&nbsp;</span><span class="span-4">
   					<select name="select" id="CreateSavingsProduct.input.applfor">
      					<option >--Select--</option>
                        <option >clients</option>
                        <option >groups</option>
                        <option >centers</option>
				    </select></span>
			</div>
          </div>
          <div class="clear">&nbsp;</div>
          <p class="fontBold">[@spring.message "manageProducts.defineSavingsProducts.targetedDepositsandWithdrawalRestrictions" /]</p>
          <div class="prepend-3  span-21 last">
          	<div class="span-20 "><span class="pull-3 span-8 rightAlign"><span class="red">* </span>[@spring.message "manageProducts.defineSavingsProducts.typeofdeposits" /]&nbsp;:</span><span class="span-4">
   					<select name="select" id="CreateSavingsProduct.input.typeofdep">
      					<option >--Select--</option>
                        <option >mandatory</option>
                        <option >voluntary</option>
				    </select></span>
			</div>
            <div class="span-20 "><span class="pull-3 span-8 rightAlign" id="recamnt">[@spring.message "manageProducts.defineSavingsProducts.recommendedAmountforDeposit" /]&nbsp;:</span><span class="span-4">
    				<input type="text" id="CreateSavingsProduct.input.recommendedAmount"/></span>
  			</div>
            <div class="span-20 "><span class="pull-3 span-8 rightAlign" id="appliesto"><span class="red">* </span>[@spring.message "manageProducts.defineSavingsProducts.amountAppliesto" /]&nbsp;</span><span class="span-4">
   					<select name="select" disabled="disabled">
      					<option >--Select--</option>
                        <option >perIndividual</option>
                        <option >completeGroup</option>
				    </select></span>
			</div>
            <div class="span-20 "><span class="pull-3 span-8 rightAlign">[@spring.message "manageProducts.defineSavingsProducts.maxamountperwithdrawal" /]&nbsp;</span><span class="span-4">
    				<input type="text" /></span>
  			</div>
          </div>
          <div class="clear">&nbsp;</div>
          <p class="fontBold">[@spring.message "manageProducts.defineSavingsProducts.interestrate" /] </p>
          <div class="prepend-3  span-21 last">
	      	<div class="span-20"><span class="pull-3 span-8 rightAlign"><span class="red">* </span>[@spring.message "manageProducts.defineSavingsProducts.interestrate" /]&nbsp;</span><span class="span-6">
    				<input type="text" id="CreateSavingsProduct.input.interestRate" />&nbsp;(0 - 100)%</span>
            
            </div>
            <div class="span-20 "><span class="pull-3 span-8 rightAlign"><span class="red">* </span>[@spring.message "manageProducts.defineSavingsProducts.balanceusedforInterestcalculation" /]&nbsp;</span><span class="span-4">
   					<select name="select" id="CreateSavingsProduct.input.balUsedForCalc">
      					<option >--Select--</option>
                        <option >minimumBalance</option>
                        <option >averageBalance</option>
				    </select></span>
			</div>
            <div class="span-20 "><span class="pull-3 span-8 rightAlign"><span class="red">* </span>[@spring.message "manageProducts.defineSavingsProducts.timeperiodforInterestcalculation" /]&nbsp;</span>
            	<span class="span-9">
                	<span class="span-2"><input type="text" size="3" id="CreateSavingsProduct.input.timeForInterestCalc"/></span>
                    <span class="span-4">
   					<select name="select" id="CreateSavingsProduct.input.typeOfTimeForInterestCalc">
                        <option >day(s)</option>
                        <option >month(s)</option>
				    </select></span></span>
			</div>
            <div class="span-20 "><span class="pull-3 span-8 rightAlign"><span class="red">* </span>[@spring.message "manageProducts.defineSavingsProducts.frequencyofInterestpostingtoaccounts" /]&nbsp;</span>
            	<span class="span-9">
                	<span class="span-4"><input type="text" size="3" id="CreateSavingsProduct.input.freqOfInterest" />&nbsp;&nbsp;[@spring.message "manageProducts.defineSavingsProducts.month(s)" /]</span>
			</div>
            <div class="span-20 "><span class="pull-3 span-8 rightAlign">[@spring.message "manageProducts.defineSavingsProducts.minimumbalancerequiredforInterestcalculation" /]&nbsp;</span><span class="span-4">
    				<input type="text" /></span>
  			</div>
          </div>
          <div class="clear">&nbsp;</div>
          <p class="fontBold">[@spring.message "manageProducts.defineSavingsProducts.accounting" /] </p>
          <div class="prepend-3  span-21 last">
            <div class="span-20 "><span class="pull-3 span-8 rightAlign"><span class="red">* </span>[@spring.message "manageProducts.defineSavingsProducts.gLcodefordeposits" /]&nbsp;</span><span class="span-4">
   					<select name="select" id="CreateSavingsProduct.input.depositGLCode">
      					<option >--Select--</option>
                        <option >24101</option>
                        <option >4606</option>
                        <option >23101</option>
                        <option >4601</option>
                        <option >4603</option>
                        <option >4602</option>
				    </select></span>
			</div>
            <div class="span-20 "><span class="pull-3 span-8 rightAlign"><span class="red">* </span>[@spring.message "manageProducts.defineSavingsProducts.gLcodeforinterest" /]&nbsp;</span>
                    <span class="span-4">
   					<select name="select" id="CreateSavingsProduct.input.interestGLCode">
                    	<option >--Select--</option>
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