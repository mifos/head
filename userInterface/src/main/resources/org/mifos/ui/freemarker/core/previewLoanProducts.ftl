[#ftl]
[#import "spring.ftl" as spring]
[#import "blueprintmacros.ftl" as mifos]
[@mifos.header "title" /]
  [@mifos.topNavigationNoSecurity currentTab="Admin" /]
  <!--  Main Content Begins-->
  <div class="content marginAuto">
    <div class="borders span-22">
      <div class="borderbtm span-22">
        <p class="span-17 completeIMG silverheading">[@spring.message "manageLoanProducts.previewLoanProduct.Loanproductinformation"/]</p>
        <p class="span-3 arrowIMG orangeheading last">[@spring.message "review&Submit"/]</p>
      </div>
      <div class="subcontent ">
        <form method="" action="" name="formname">
          <p class="font15"><span class="fontBold">[@spring.message "manageLoanProducts.previewLoanProduct.addanewLoanProduct"/]</span>&nbsp;--&nbsp;<span class="orangeheading">[@spring.message "review&Submit"/]</span></p>
          <p>[@spring.message "reviewtheinformationbelow.ClickSubmit"/]</p>
          <p class="fontBold">[@spring.message "manageLoanProducts.previewLoanProduct.loanproductdetails"/] </p>
          <div class="span-21 last">
          	<div class="span-20"><span class="span-8 fontBold">[@spring.message "manageLoanProducts.previewLoanProduct.productinstancename"/]:&nbsp;</span><span class="span-4">&nbsp;</span>
  			</div>
            <div class="span-20 "><span class="span-8 fontBold">[@spring.message "manageLoanProducts.previewLoanProduct.shortname"/]:&nbsp;</span><span class="span-4">&nbsp;</span>
  			</div>
            <div class="span-20 "><span class="span-8 fontBold">[@spring.message "manageLoanProducts.previewLoanProduct.description"/]:&nbsp;</span><br />
            	<span class="span-4">&nbsp;</span>
  			</div>
        	<div class="span-20 "><span class="span-8 fontBold">[@spring.message "manageLoanProducts.previewLoanProduct.productcategory"/]:&nbsp;</span><span class="span-4">&nbsp;</span>
			</div>
            <div class="span-20 last">
            	<span class="span-8 fontBold">[@spring.message "manageLoanProducts.previewLoanProduct.startdate"/]:</span>
            	<span class="span-4">&nbsp;</span>
        	</div>
        	<div class="span-20 last">
            	<span class="span-8 fontBold">[@spring.message "manageLoanProducts.previewLoanProduct.enddate"/]:</span><span class="span-4">&nbsp;</span>
        	</div>
            <div class="span-20 "><span class="span-8 fontBold">[@spring.message "manageLoanProducts.previewLoanProduct.applicablefor"/]:&nbsp;</span><span class="span-4">&nbsp;</span>
			</div>
            <div class="span-20 "><span class="span-8 fontBold">[@spring.message "manageLoanProducts.previewLoanProduct.includeinLoancyclecounter"/]:&nbsp;</span><span class="span-4">&nbsp;</span>
			</div>
            <div class="span-20 "><span class="span-8 fontBold">[@spring.message "manageLoanProducts.previewLoanProduct.calculateLoanAmountas"/]:&nbsp;</span><span class="span-4">&nbsp;</span>
			</div>
          </div>
          <div class="span-20 ">
          	<div class="span-17 bluedivs fontBold paddingLeft">
            	<span class="span-4">[@spring.message "manageLoanProducts.previewLoanProduct.minloanamount"/]</span>
                <span class="span-4">[@spring.message "manageLoanProducts.previewLoanProduct.maxloanamount"/]</span>
                <span class="span-5 last">[@spring.message "manageLoanProducts.previewLoanProduct.defaultamount"/]</span>
            </div>
            <div class="span-17 paddingLeft">
                <span class="span-4 ">&nbsp;</span>
                <span class="span-4 ">&nbsp;</span>
                <span class="span-5 last">&nbsp;</span>
            </div>
          <div>&nbsp;</div>
          </div>
          <div class="clear">&nbsp;</div>
          <p class="fontBold">[@spring.message "manageLoanProducts.previewLoanProduct.interestrate"/] </p>
          <div class="span-21 last">
	      	<div class="span-20"><span class="span-8 fontBold">[@spring.message "manageLoanProducts.previewLoanProduct.interestratetype"/]:&nbsp;</span><span class="span-4">&nbsp;</span>
            </div>
            <div class="span-20 "><span class="span-8 fontBold">[@spring.message "manageLoanProducts.previewLoanProduct.maxInterestrate"/]:&nbsp;</span><span class="span-4">&nbsp;</span>
			</div>
            <div class="span-20 "><span class="span-8 fontBold">[@spring.message "manageLoanProducts.previewLoanProduct.minInterestrate"/]:&nbsp;</span>
            	<span class="span-4">&nbsp;</span>
			</div>
            <div class="span-20 "><span class="span-8 fontBold">[@spring.message "manageLoanProducts.previewLoanProduct.defaultInterestrate"/]:&nbsp;</span>
            	<span class="span-4">&nbsp;</span>
			</div>
          </div>
          <div class="clear">&nbsp;</div>
          <p class="fontBold">[@spring.message "manageLoanProducts.previewLoanProduct.repaymentSchedule"/]</p>
          <p class="span-20"><span class="span-8 fontBold">[@spring.message "manageLoanProducts.previewLoanProduct.frequencyofinstallments"/]:&nbsp;</span><span class="span-4">&nbsp;</span></p>
          <div class="span-20 "><span class="span-8 fontBold">[@spring.message "manageLoanProducts.previewLoanProduct.calculateofinstallmentsas"/]:&nbsp;</span>
            	<span class="span-4">&nbsp;</span>
		  </div>
          <div class="span-20 ">
          	<div class="span-17 bluedivs fontBold paddingLeft">
            	<span class="span-4">[@spring.message "manageLoanProducts.previewLoanProduct.minofinstallments"/]</span>
                <span class="span-4">[@spring.message "manageLoanProducts.previewLoanProduct.maxofinstallments"/]</span>
                <span class="span-5 last">[@spring.message "manageLoanProducts.previewLoanProduct.defaultofinstallments"/]</span>
            </div>
            <div class="span-17 paddingLeft">
                <span class="span-4 ">&nbsp;</span>
                <span class="span-4 ">&nbsp;</span>
                <span class="span-5 last">&nbsp;</span>
            </div>
          <div>&nbsp;</div>
          </div>
          <div class="span-20 "><span class="span-8 fontBold">[@spring.message "manageLoanProducts.previewLoanProduct.graceperiodtype"/]:&nbsp;</span><span class="span-4">&nbsp;</span>
		  </div>
          <div class="span-20 "><span class="span-8 fontBold">[@spring.message "manageLoanProducts.previewLoanProduct.graceperiodduration"/]:&nbsp;</span>
                    <span class="span-4">&nbsp;</span>
		  </div>
          <div class="clear">&nbsp;</div>
          <p class="fontBold">[@spring.message "manageLoanProducts.previewLoanProduct.fees"/]</p>
          <p class="span-20"><span class="span-8 fontBold">[@spring.message "manageLoanProducts.previewLoanProduct.feestypes"/]:&nbsp;</span><span class="span-4">&nbsp;</span></p>
          <p class="fontBold">[@spring.message "manageLoanProducts.previewLoanProduct.accounting"/] </p>
          <div class="span-21 last">
          	<div class="span-20 "><span class="span-8 fontBold">[@spring.message "manageLoanProducts.previewLoanProduct.sourcesoffunds"/]:&nbsp;</span><br /><span class="span-4">&nbsp;</span>
			</div>
            <div class="span-20 "><span class="span-8 fontBold">[@spring.message "manageLoanProducts.previewLoanProduct.productGLcode"/]:&nbsp;</span></div>
            <div class="span-20 "><span class="span-8 fontBold">[@spring.message "manageLoanProducts.previewLoanProduct.interest"/]:&nbsp;</span>
                    <span class="span-4">&nbsp;</span>
			</div>
            <div class="span-20 "><span class="span-8 fontBold">[@spring.message "manageLoanProducts.previewLoanProduct.principal"/]:&nbsp;</span>
                    <span class="span-4">&nbsp;</span>
			</div>
            <div class="clear">&nbsp;</div>
            <div class="span-20"><input class="buttn2" type="button" name="edit" value="Edit Loan product information" onclick="location.href='loanProduct.html'"/></div>
          </div>
          <div class="clear">&nbsp;</div>
          <hr />
          <div class="prepend-9">
            	<input class="buttn" type="button" name="submit" value="Submit" onclick="location.href='loanProductSuccess.html'"/>
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