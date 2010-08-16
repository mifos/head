[#ftl]
[#import "spring.ftl" as spring]
[#import "blueprintmacros.ftl" as mifos]
[@mifos.header "title" /]
  [@mifos.topNavigationNoSecurity currentTab="Admin" /]
  <!--  Main Content Begins-->
  <div class="content marginAuto">
    <div class="borders span-22">
      <div class="borderbtm span-22">
        <p class="span-17 completeIMG silverheading">[@spring.message "manageSavngsProducts.previewSavingsProducts.savingsproductinformation" /]</p>
        <p class="span-3 arrowIMG orangeheading last">[@spring.message "review&Submit" /]</p>
      </div>
      <div class="subcontent ">
        <form method="" action="" name="formname">
          <p class="font15"><span class="fontBold">[@spring.message "manageSavngsProducts.previewSavingsProducts.addanewSavingsProduct" /]</span>&nbsp;--&nbsp;<span class="orangeheading">[@spring.message "review&Submit" /]</span></p>
          <p>[@spring.message "reviewtheinformationbelow.ClickSubmit" /]</p>
          <p class="fontBold">[@spring.message "manageSavngsProducts.previewSavingsProducts.savingsproductdetails" /] </p>
          <div class="span-21 last">
          	<div class="span-20"><span class="span-8 fontBold">[@spring.message "manageSavngsProducts.previewSavingsProducts.productinstancename" /]:&nbsp;</span><span class="span-4">&nbsp;</span>
  			</div>
            <div class="span-20 "><span class="span-8 fontBold">[@spring.message "manageSavngsProducts.previewSavingsProducts.shortname" /]:&nbsp;</span><span class="span-4">&nbsp;</span>
  			</div>
            <div class="span-20 "><span class="span-8 fontBold">[@spring.message "manageSavngsProducts.previewSavingsProducts.description" /]:&nbsp;</span><br />
            	<span class="span-4">&nbsp;</span>
  			</div>
        	<div class="span-20 "><span class="span-8 fontBold">[@spring.message "manageSavngsProducts.previewSavingsProducts.productcategory" /]:&nbsp;</span><span class="span-4">&nbsp;</span>
			</div>
            <div class="span-20 last">
            	<span class="span-8 ">[@spring.message "manageSavngsProducts.previewSavingsProducts.startdate" /]:</span>
            	<span class="span-4">&nbsp;</span>
        	</div>
        	<div class="span-20 last">
            	<span class="span-8 ">[@spring.message "manageSavngsProducts.previewSavingsProducts.enddate" /]:</span><span class="span-4">&nbsp;</span>
        	</div>
            <div class="span-20 "><span class="span-8 ">[@spring.message "manageSavngsProducts.previewSavingsProducts.applicablefor" /]:&nbsp;</span><span class="span-4">&nbsp;</span>
			</div>
          </div>
          <div class="clear">&nbsp;</div>
          <p class="fontBold">[@spring.message "manageSavngsProducts.previewSavingsProducts.targetedDepositsandWithdrawalRestrictions" /] </p>
          <div class="span-21 last">
          	<div class="span-20 "><span class="span-8 fontBold">[@spring.message "manageSavngsProducts.previewSavingsProducts.typeofdeposits" /]:&nbsp;</span><span class="span-4">&nbsp;</span>
			</div>
            <div class="span-20 "><span class="span-8 fontBold">[@spring.message "manageSavngsProducts.previewSavingsProducts.mandatoryamountfordeposit" /]:&nbsp;</span><span class="span-4">&nbsp;</span>
  			</div>
            <div class="span-20 "><span class="span-8 fontBold">[@spring.message "manageSavngsProducts.previewSavingsProducts.amountAppliesto" /]:&nbsp;</span><span class="span-4">&nbsp;</span>
			</div>
            <div class="span-20 "><span class="span-8 fontBold">[@spring.message "manageSavngsProducts.previewSavingsProducts.maxamountperwithdrawal" /]:&nbsp;</span><span class="span-4">&nbsp;</span>
  			</div>
          </div>
          <div class="clear">&nbsp;</div>
          <p class="fontBold">[@spring.message "manageSavngsProducts.previewSavingsProducts.interestrate" /] </p>
          <div class="span-21 last">
	      	<div class="span-20"><span class="span-8 fontBold">[@spring.message "manageSavngsProducts.previewSavingsProducts.interestrate" /]:&nbsp;</span><span class="span-4">&nbsp;</span>
            </div>
            <div class="span-20 "><span class="span-8 fontBold">[@spring.message "manageSavngsProducts.previewSavingsProducts.balanceusedforInterestcalculation" /]:&nbsp;</span><span class="span-4">&nbsp;</span>
			</div>
            <div class="span-20 "><span class="span-8 fontBold">[@spring.message "manageSavngsProducts.previewSavingsProducts.timeperiodforInterestcalculation" /]:&nbsp;</span>
            	<span class="span-4">&nbsp;</span>
			</div>
            <div class="span-20 "><span class="span-8 fontBold">[@spring.message "manageSavngsProducts.previewSavingsProducts.frequencyofInterestpostingtoaccounts" /]:&nbsp;</span>
            	<span class="span-4">&nbsp;</span>
			</div>
            <div class="span-20 "><span class="span-8 fontBold">[@spring.message "manageSavngsProducts.previewSavingsProducts.minimumbalancerequiredforInterestcalculation" /]:&nbsp;</span><span class="span-4">&nbsp;</span>
  			</div>
          </div>
          <div class="clear">&nbsp;</div>
          <p class="fontBold">[@spring.message "manageSavngsProducts.previewSavingsProducts.accounting" /] </p>
          <div class="span-21 last">
            <div class="span-20 "><span class="span-8 fontBold">[@spring.message "manageSavngsProducts.previewSavingsProducts.gLcodefordeposits" /]:&nbsp;</span><span class="span-4">&nbsp;</span>
			</div>
            <div class="span-20 "><span class="span-8 fontBold">[@spring.message "manageSavngsProducts.previewSavingsProducts.gLcodeforInterest" /]:&nbsp;</span>
                    <span class="span-4">&nbsp;</span>
			</div>
            <div class="clear">&nbsp;</div>
            <div class="span-20"><input class="buttn2" type="button" name="edit" value="Edit Savings product information" onclick="location.href='savingsProduct.html'"/></div>
          </div>
          <div class="clear">&nbsp;</div>
          <hr />
          <div class="prepend-9">
            	<input class="buttn" type="button" name="submit" value="Submit" onclick="location.href='savingsProductSuccess.html'"/>
            	<input class="buttn2" type="button" name="cancel" value="Cancel" onclick="location.href='admin.html'"/>
          </div>
          <div class="clear">&nbsp;</div>
        </form>
      </div>
      <!--Subcontent Ends-->
    </div>
  </div>
  <!--Main Content Ends-->
    [@mifos.footer/]