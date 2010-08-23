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
        <form method="post" action="previewSavingsProducts.ftl" name="previewsavingsproduct">
          <p class="font15"><span class="fontBold">[@spring.message "manageSavngsProducts.previewSavingsProducts.addanewSavingsProduct" /]</span>&nbsp;--&nbsp;<span class="orangeheading">[@spring.message "review&Submit" /]</span></p>
          <p>[@spring.message "reviewtheinformationbelow.ClickSubmit" /]</p>
          <p class="fontBold">[@spring.message "manageSavngsProducts.previewSavingsProducts.savingsproductdetails" /] </p>
          <div class="span-21 last">
          	<div class="span-20">
          		<span class="span-8 fontBold">[@spring.message "manageSavngsProducts.previewSavingsProducts.productinstancename" /]:&nbsp;</span>
          		<span class="span-4">${savingsProduct.generalDetails.name}</span>
  			</div>
            <div class="span-20 ">
            	<span class="span-8 fontBold">[@spring.message "manageSavngsProducts.previewSavingsProducts.shortname" /]:&nbsp;</span>
            	<span class="span-4">${savingsProduct.generalDetails.shortName}</span>
  			</div>
            <div class="span-20 ">
            	<span class="span-8 fontBold">[@spring.message "manageSavngsProducts.previewSavingsProducts.description" /]:&nbsp;</span><br />
            	<span class="span-4">${savingsProduct.generalDetails.description}</span>
  			</div>
        	<div class="span-20 ">
        		<span class="span-8 fontBold">[@spring.message "manageSavngsProducts.previewSavingsProducts.productcategory" /]:&nbsp;</span>
        		<span class="span-4">${categoryName}</span>
			</div>
            <div class="span-20 last">
            	<span class="span-8 ">[@spring.message "manageSavngsProducts.previewSavingsProducts.startdate" /]:</span>
            	<span class="span-4">${startDateFormatted}</span>
        	</div>
        	<div class="span-20 last">
            	<span class="span-8 ">[@spring.message "manageSavngsProducts.previewSavingsProducts.enddate" /]:</span>
            	<span class="span-4">${endDateFormatted}</span>
        	</div>
            <div class="span-20 ">
            	<span class="span-8 ">[@spring.message "manageSavngsProducts.previewSavingsProducts.applicablefor" /]:&nbsp;</span>
            	<span class="span-4">${applicableTo}</span>
			</div>
          </div>
          <div class="clear">&nbsp;</div>
          <p class="fontBold">[@spring.message "manageSavngsProducts.previewSavingsProducts.targetedDepositsandWithdrawalRestrictions" /] </p>
          <div class="span-21 last">
          	<div class="span-20 ">
          		<span class="span-8 fontBold">[@spring.message "manageSavngsProducts.previewSavingsProducts.typeofdeposits" /]:&nbsp;</span>
          		<span class="span-4">${depositType}</span>
			</div>
            <div class="span-20 ">
            	<span class="span-8 fontBold">[@spring.message "manageSavngsProducts.previewSavingsProducts.mandatoryamountfordeposit" /]:&nbsp;</span>
            	<span class="span-4">${savingsProduct.amountForDeposit}</span>
  			</div>
            <div class="span-20 ">
            	<span class="span-8 fontBold">[@spring.message "manageSavngsProducts.previewSavingsProducts.amountAppliesto" /]:&nbsp;</span>
            	<span class="span-4">${appliesTo}</span>
			</div>
            <div class="span-20 ">
            	<span class="span-8 fontBold">[@spring.message "manageSavngsProducts.previewSavingsProducts.maxamountperwithdrawal" /]:&nbsp;</span>
            	<span class="span-4">${savingsProduct.maxWithdrawalAmount}</span>
  			</div>
          </div>
          <div class="clear">&nbsp;</div>
          <p class="fontBold">[@spring.message "manageSavngsProducts.previewSavingsProducts.interestrate" /] </p>
          <div class="span-21 last">
	      	<div class="span-20">
	      		<span class="span-8 fontBold">[@spring.message "manageSavngsProducts.previewSavingsProducts.interestrate" /]:&nbsp;</span>
	      		<span class="span-4">${savingsProduct.interestRate}</span>
            </div>
            <div class="span-20 ">
            	<span class="span-8 fontBold">[@spring.message "manageSavngsProducts.previewSavingsProducts.balanceusedforInterestcalculation" /]:&nbsp;</span>
            	<span class="span-4">${interestCalculationUsed}</span>
			</div>
            <div class="span-20 ">
            	<span class="span-8 fontBold">[@spring.message "manageSavngsProducts.previewSavingsProducts.timeperiodforInterestcalculation" /]:&nbsp;</span>
            	<span class="span-4">${savingsProduct.interestCalculationFrequency} ${interestCalculationTimePeriod}</span>
			</div>
            <div class="span-20 ">
            	<span class="span-8 fontBold">[@spring.message "manageSavngsProducts.previewSavingsProducts.frequencyofInterestpostingtoaccounts" /]:&nbsp;</span>
            	<span class="span-4">${savingsProduct.interestPostingMonthlyFrequency} [@spring.message "manageProducts.defineSavingsProducts.month(s)" /]</span>
			</div>
            <div class="span-20 ">
            	<span class="span-8 fontBold">[@spring.message "manageSavngsProducts.previewSavingsProducts.minimumbalancerequiredforInterestcalculation" /]:&nbsp;</span>
            	<span class="span-4">${savingsProduct.minBalanceRequiredForInterestCalculation}</span>
  			</div>
          </div>
          <div class="clear">&nbsp;</div>
          <p class="fontBold">[@spring.message "manageSavngsProducts.previewSavingsProducts.accounting" /] </p>
          <div class="span-21 last">
            <div class="span-20 ">
            	<span class="span-8 fontBold">[@spring.message "manageSavngsProducts.previewSavingsProducts.gLcodefordeposits" /]:&nbsp;</span>
            	<span class="span-4">${depositGlCode}</span>
			</div>
            <div class="span-20 ">
            	<span class="span-8 fontBold">[@spring.message "manageSavngsProducts.previewSavingsProducts.gLcodeforInterest" /]:&nbsp;</span>
                <span class="span-4">${interestGlCode}</span>
			</div>
            <div class="clear">&nbsp;</div>
            <div class="span-20">
            	<input class="buttn2" type="submit" name="EDIT" value="Edit Savings product information"/>
            </div>
          </div>
          <div class="clear">&nbsp;</div>
          <hr />
          <div class="prepend-9">
            	<input class="buttn" type="submit" name="submit" value="Submit"/>
            	<input class="buttn2" type="submit" name="cancel" value="Cancel"/>
          </div>
          <div class="clear">&nbsp;</div>
        </form>
      </div>
      <!--Subcontent Ends-->
    </div>
  </div>
  <!--Main Content Ends-->
    [@mifos.footer/]