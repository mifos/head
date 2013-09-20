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

[#assign security=JspTaglibs["http://www.springframework.org/security/tags"] /]
[@layout.webflow currentTab="ClientsAndAccounts"
                 currentState="createLoanAccount.flowState.enterAccountInfo" 
                 states=["createLoanAccount.flowState.selectCustomer", 
                         "createLoanAccount.flowState.enterAccountInfo",
                         "createLoanAccount.flowState.reviewInstallments", 
                         "createLoanAccount.flowState.reviewAndSubmit"]]
                         
<span id="page.id" title="LoanCreationDetail"></span>
[@i18n.formattingInfo /]
<style type="text/css">

    #changeOrder {
        font-size: 10px;
        color: #000166;
        cursor: pointer;
        text-decoration: underline;
        float: right;
        padding-right: 20px;
        font-family: sans-serif;
    }
    
   #accountDetails.changing #changeOrder {
        font-size: 13px;
        color: green;
        font-weight: bold;
    }
    
    .changeOrderArrows {
        display: none;
        width: 50px;
    }
    
    .changeOrderArrows img {
        cursor: pointer;
    }
    
    #accountDetails.changing .changeOrderArrows {
        display: block;
    }
    
    #accountDetails.changing #accountDetailsInner > tbody > tr {
        cursor: pointer;
    }
    
    #accountDetails.changing #accountDetailsInner > tbody > tr:hover {
        background-color: #FFFFA7;
    }
        
    #accountDetailsInner td {
        padding-top: 2px;
        padding-bottom: 2px;
        width: 100%;
    }
    
</style>
 [@security.authorize access="isFullyAuthenticated() and hasRole('ROLE_CAN_DEFINE_HIDDEN_MANDATORY_FIELDS')"]
<script type="text/javascript">
    
    $(document).ready(function() {
    
        $("#changeOrder").click(function() {
            if ($("#accountDetails").hasClass("changing")) {
                
                $("#accountDetailsInner > tbody").sortable('disable').enableSelection();
                
                var order = {};
                
                $('#accountDetailsInner > tbody > tr').each(function() {
                    order[$(this).attr("data-order-id")] = $('#accountDetailsInner > tbody > tr').index(this);
                });
                
                $.ajax({
                     contentType: "application/json",
                        type: "POST",
                        url: "saveInformationOrder.ftl",
                        data: JSON.stringify(order),
                        dataType: "json"
                });
                
                $("#accountDetails").removeClass("changing");
                $(this).html("[@spring.message "createLoanAccount.enterAccountInfo.changeFieldsOrder" /]");
            } else {
                $("#accountDetails").addClass("changing");
                $(this).html("Save changes");
                
                $("#accountDetailsInner > tbody").sortable({
                    helper: function(e, ui) {
                        ui.children().each(function() {
                            $(this).width($(this).width());
                        });
                        return ui;
                    }
                }).disableSelection();
                $("#accountDetailsInner > tbody").sortable("enable");
            }
        });
        
        $(".moveUp").click(function() {
            var parentTr = $(this).parents("tr:first"); 
            parentTr.insertBefore(parentTr.prev());
        });
        
        $(".moveDown").click(function() {
            var parentTr = $(this).parents("tr:first"); 
            parentTr.insertAfter(parentTr.next());
        });
    
    });
    
    </script>
    [/@security.authorize]
<script type="text/javascript" src="pages/js/singleitem.js"></script>

[#if customerSearchFormBean.redoLoanAccount]
<p class="red standout" style="margin-bottom: 5px;">[@spring.message "redoLoanAccount.wizard.highlightedNote" /]</p>
<h1>[@spring.message "redoLoanAccount.wizard.title" /] - <span class="standout">[@spring.message "createLoanAccount.selectProduct.pageSubtitle" /]</span></h1>
[#else]
<h1>[@spring.message "createLoanAccount.wizard.title" /] - <span class="standout">[@spring.message "createLoanAccount.selectProduct.pageSubtitle" /]</span></h1>
[/#if]
<p>[@spring.message "createLoanAccount.selectProduct.instructions" /]</p>
<p><span class="mandatory">*</span>[@spring.message "requiredFieldsInstructions" /]</p>
<br/>

<p><span class="standout">[@spring.message "selectProduct.accountOwnerName" /]</span> ${loanProductReferenceData.customerDetailDto.displayName}</p>
<br/>

<span id="loancreationdetails.error.message">[@form.errors "loanAccountFormBean.*"/]</span>
<form action="${flowExecutionUrl}&_eventId=newProductSelected" method="post" class="two-columns">
    <fieldset>
    <div class="row">
        [@form.label "loancreationprodofferingselect.select.loanProduct" true][@spring.message "selectProduct.selectProductLabel" /][/@form.label]
        [@form.singleSelectWithPrompt path="loanAccountFormBean.productId" options=loanProductReferenceData.productOptions id="loancreationprodofferingselect.select.loanProduct" attributes="class='selectnewproduct'" /]
    </div>
    </fieldset>
</form>

<p><span class="standout">[@spring.message "createLoanAccount.enterAccountInfo.productSummary.header" /]</span></p>
<div class="product-summary">
    <div class="row">
        <div class="attribute">[@spring.message "productSummary.description"/]</div>
        <div class="value">${loanProductReferenceData.productDto.description}</div>
    </div>
    <div class="row">
        <div class="attribute">[@spring.message "productSummary.currency"/]</div>
        <div class="value">${loanProductReferenceData.currency}</div>
    </div>
    <div class="row">
        <div class="attribute">[@spring.message "productSummary.interestRateType"/]</div>
        <div class="value">${loanProductReferenceData.interestRateType}</div>
    </div>
    <div class="row">
        <div class="attribute">[@spring.message "productSummary.freqOfInstallments"/]</div>
        <div class="value">${loanProductReferenceData.loanOfferingMeetingDetail.meetingDetailsDto.every}&nbsp;${loanProductReferenceData.loanOfferingMeetingDetail.meetingDetailsDto.recurrenceName}</div>
    </div>
    <div class="row">
        <div class="attribute">[@spring.message "productSummary.fixedRepaymentSchedule"/]</div>
        [#if loanProductReferenceData.fixedRepaymentSchedule]
	        <div class="value">[@spring.message "boolean.yes"/]</div>
	    [#else]
	        <div class="value">[@spring.message "boolean.no"/]</div>
	    [/#if]
    </div>
    [#if loanProductReferenceData.variableInstallmentsAllowed]
    <div class="row">
        <div class="attribute">[@spring.message "productSummary.variableInstallmentsAllowed"/]</div>
        <div class="value">[@spring.message "boolean.yes"/]</div>
    </div>
    <div class="row">
        <div class="attribute">[@spring.message "productSummary.variabeInstallments.minGap"/]</div>
        <div class="value">${loanProductReferenceData.minGapInDays?string.number}<span>&nbsp;[@spring.message "productSummary.variabeInstallments.days"/]</span></div>
    </div>
    <div class="row">
        <div class="attribute">[@spring.message "productSummary.variabeInstallments.maxGap"/]</div>
        [#if loanProductReferenceData.maxGapInDays??]
        <div class="value">${loanProductReferenceData.maxGapInDays?string.number}<span>&nbsp;[@spring.message "productSummary.variabeInstallments.days"/]</span></div>
        [#else]
		<div class="value">[@spring.message "productSummary.variabeInstallments.minInstallmentAmount.notapplicable"/]</div>        
        [/#if]
    </div>
    <div class="row">
        <div class="attribute">[@spring.message "productSummary.variabeInstallments.minInstallmentAmount"/]</div>
        [#if loanProductReferenceData.minInstallmentAmount == 0.0]
        <div class="value">[@spring.message "productSummary.variabeInstallments.minInstallmentAmount.notapplicable"/]</div>
        [#else]
        <div class="value">${loanProductReferenceData.minInstallmentAmount?string.number}</div>
        [/#if]
    </div>
    [/#if]
</div>
<br/>
<br/>
<form action="${flowExecutionUrl}" method="post" class="two-columns" enctype="multipart/form-data">
    <fieldset>
[#if loanProductReferenceData.glimApplicable || (loanProductReferenceData.group && loanProductReferenceData.groupLoanWithMembersEnabled)]
<p><span class="standout">[@spring.message "createLoanAccount.enterAccountInfo.accountDetail.glim.individualdetails.header" /]</span></p>
[#else]
<table id="accountDetails">
    <tr>
        <td>
        <span class="standout">[@spring.message "createLoanAccount.enterAccountInfo.accountDetail.header" /]</span>
        </td>
     [@security.authorize access="isFullyAuthenticated() and hasRole('ROLE_CAN_DEFINE_HIDDEN_MANDATORY_FIELDS')"]
        <td>
            <span id="changeOrder">[@spring.message "createLoanAccount.enterAccountInfo.changeFieldsOrder" /]</span>    
        </td>
     [/@security.authorize]
    </tr>
    <tr>
        <td colspan="2">
            <table id="accountDetailsInner">
        
[/#if]

    [#if loanProductReferenceData.glimApplicable || (loanProductReferenceData.group && loanProductReferenceData.groupLoanWithMembersEnabled)]
    <script type="text/javascript">
    	function calculateTotalLoanAmount() {
    		$(document).ready(function () {
    		    var decimalPlaces = ${loanAccountFormBean.digitsAfterDecimalForMonetaryAmounts};
				var total = 0;
				$('.amountfield').each(function(index) {
					var fieldamount = parseFloat($(this).val());
					if (isNaN(fieldamount)) {
						fieldamount = 0;
					}
					$('.clientbox').each(function(subindex) {
						if (this.checked && index==subindex) {
							total = total + fieldamount;
						};
					});	
  				});
  				
				$('#glimsumloanamount').val(total.toFixed(decimalPlaces));
    		});
    	}
    	
    	function calculateFeeAmounts() {
    	    calculateIndividualDefaultFeeAmounts();
    	    calculateIndividualAdditionalFeeAmounts();
    	}
    	
    	function calculateIndividualDefaultFeeAmounts() {
    	   $(document).ready(function () {
    	       $('[id^=defaultFeeAmountRow]').each(function (defaultFeeIndex) {
    	           var isRateBasedFee = $("#hiddenDefaultFeeRateBased" + defaultFeeIndex).val();
    	           calculateIndividualFeeAmounts("default", defaultFeeIndex, isRateBasedFee);
    	       });
    	   });
    	}
    	
    	function calculateIndividualAdditionalFeeAmounts() {
            $(document).ready(function () {
                $("[id^=selectedFeeId]").each(function (additionalFeeIndex) {
                    var selectedValue = $(this).val();
                    if (selectedValue != null && selectedValue != "") {
                        var isRateBasedFee = $("#hiddenFeeRateBased" + selectedValue).val();
                        calculateIndividualFeeAmounts("selected", additionalFeeIndex, isRateBasedFee);
                    }
                }); 
            });
        }
        
        function calculateIndividualFeeAmounts(feeType, feeIndex, isRateBasedFee) {
            var totalGroupLoanAmount = $("#glimsumloanamount").val();
            var decimalPlaces = ${loanAccountFormBean.digitsAfterDecimalForMonetaryAmounts};
            var groupFeeAmount = $("#" + feeType + "FeeAmount\\[" + feeIndex + "\\]").val();
            var amountLeft = groupFeeAmount;
            var biggestClientAmountIndex = 0;
            var biggestClientAmount = -1;
            $('.clientbox').each(function(clientIndex) {
                var clientRow = $("[id^=" + feeType + "FeeIndividualAmountsRow][id$=\\[" + feeIndex + "\\]\\[" + clientIndex + "\\]]");
                if (this.checked) {
                    var clientLoanAmount = $("#clientAmount\\[" + clientIndex + "\\]").val();
                    if (clientLoanAmount > biggestClientAmount) {
                        biggestClientAmount = clientLoanAmount;
                        biggestClientAmountIndex = clientIndex;
                    }
                    var factor = clientLoanAmount / totalGroupLoanAmount;
                    if (isRateBasedFee == "false") {
                        var clientFeeAmount = (groupFeeAmount*factor).toFixed(decimalPlaces);
                        $(clientRow).children('input').val(clientFeeAmount);
                        $(clientRow).children('input').attr('readonly', false);
                        amountLeft -= clientFeeAmount;
                        $("#" + feeType + "FeeAmount\\[" + feeIndex + "\\]").attr('readonly', true);
                    } else {
                        $(clientRow).children('input').val(groupFeeAmount);
                        $(clientRow).children('input').attr('readonly', true);
                        $("#" + feeType + "FeeAmount\\[" + feeIndex + "\\]").attr('readonly', false);
                    }
                    $(clientRow).show();
                } else {
                    $(clientRow).hide();
                };
            });
            if (amountLeft != 0 && isRateBasedFee == "false") {
                var biggestClientInput = $("#" + feeType + "FeeIndividualAmounts\\[" + feeIndex + "\\]\\[" + biggestClientAmountIndex + "\\]");
                var biggestClientValue = biggestClientInput.val();
                var adjustedAmount = parseFloat(biggestClientValue) + parseFloat(amountLeft);
                adjustedAmount = adjustedAmount.toFixed(decimalPlaces);
                biggestClientInput.val(adjustedAmount);
            }
        }
        
        function updateFeeAmounts(feeType) {
            var decimalPlaces = ${loanAccountFormBean.digitsAfterDecimalForMonetaryAmounts};
            $('[id^=' + feeType + 'FeeAmountRow]').each(function(index) {
                if ($('#' + feeType + 'FeeAmount\\[' + index + '\\]').attr('readonly')) {
                    var groupAmount = 0.0;
                    $('input[id^=' + feeType + 'FeeIndividualAmounts\\[' + index + '\\]]').each(function() {
                        if ($(this).val() != undefined && $(this).val() != "") {
                            groupAmount += parseFloat($(this).val());
                        }
                    });
                    $('#' + feeType + 'FeeAmount\\[' + index + '\\]').val(groupAmount.toFixed(decimalPlaces));
                } else {
                    var groupAmount = $('#' + feeType + 'FeeAmount\\[' + index + '\\]').val();
                    $('input[id^=' + feeType + 'FeeIndividualAmounts\\[' + index + '\\]]').each(function() {
                        if ($(this).is(':visible')) {
                            $(this).val(groupAmount);
                        }
                    });
                }
            });
        }
    
    	$(document).ready(function () {
			$('#selectAll').change(function() {
				 if (this.checked)
				 {
				    $('.clientbox').attr("checked",true);
				 } else {
				 	$('.clientbox').attr("checked",false);
				 }
				 calculateTotalLoanAmount();
				 calculateFeeAmounts();
			});
			
			$('.clientbox').click(function() {
				calculateTotalLoanAmount();
				calculateFeeAmounts();
			});
			
			$('.amountfield').change(function() {
				calculateTotalLoanAmount();
				calculateFeeAmounts();
			});
			
            $('input[id^=defaultFee]').change(function() {
                updateFeeAmounts("default");
            });

            $('input[id^=selectedFee]').change(function() {
                updateFeeAmounts("selected");
            });
    	});
    </script>
    <table border="1">
    	<tr>
    		<td><input type="checkbox" name="selectAll" id="selectAll" /></td>
    		<td>[@spring.message "selectProduct.accountOwnerName" /]</td>
    		<td>&nbsp;</td>
    		<td><span class="mandatory">*</span>[@spring.message "createLoanAccount.amount"/]</td>
    		<td>&nbsp;</td>
    		<td>[#if loanAccountFormBean.purposeOfLoanMandatory]<span class="mandatory">*</span>[/#if][@spring.message "createLoanAccount.purposeOfLoan" /]</td>
    	</tr>
    	[#assign index = 0]
    	[#list loanProductReferenceData.clientDetails as clientdata]
    	<tr>
    		<td>
    			[@spring.formCheckbox "loanAccountFormBean.clientSelectForGroup[${index}]" "class='clientbox'"/]
    		</td>
    		<td>
    			<div><span class="standout">Client:</span><span id="GLIMLoanAccounts.clientName.${index}">${clientdata.clientName}</span></div>
    			<div><span class="standout">Client Id</span>:<span id="GLIMLoanAccounts.clientId.${index}">${clientdata.clientId}</span></div>
    			[@spring.formHiddenInput "loanAccountFormBean.clientGlobalId[${index}]" /]
    		</td>
    		<td>&nbsp;</td>
    		<td>[@form.input path="loanAccountFormBean.clientAmount[${index}]"  id="clientAmount[${index}]" attributes="class='amountfield separatedNumber' "/]</td>
    		<td>&nbsp;</td>
    		<td>[@form.singleSelectWithPrompt path="loanAccountFormBean.clientLoanPurposeId[${index}]" options=loanProductReferenceData.purposeOfLoanOptions selectPrompt="selectPrompt" attributes="class=trigger noAutoSelect" /]</td>
    	</tr>
    	[#assign index = index + 1]
    	[/#list]
    	<tr>
    		<td>&nbsp;</td>
    		<td>&nbsp;</td>
    		<td>Total amount:</td>
    		<td>[@form.input path="loanAccountFormBean.amount"  id="glimsumloanamount" attributes="readonly=readonly"/]</td>
    		<td><span id="createloan.allowedamounttext">([@spring.message "createLoanAccount.allowedAmount"/] ${loanProductReferenceData.minLoanAmount?string.number} - ${loanProductReferenceData.maxLoanAmount?string.number})</span></td>
    		<td>&nbsp;</td>
    	</tr>
    </table>
    <table id="accountDetails">
    <tr>
        <td>
        <span class="standout">[@spring.message "createLoanAccount.enterAccountInfo.accountDetail.header" /]</span>
        </td>
     [@security.authorize access="isFullyAuthenticated() and hasRole('ROLE_CAN_DEFINE_HIDDEN_MANDATORY_FIELDS')"]
        <td>
            <span id="changeOrder">[@spring.message "createLoanAccount.enterAccountInfo.changeFieldsOrder" /]</span>    
        </td>
     [/@security.authorize]
    </tr>
    <tr>
        <td colspan="2">
            <table id="accountDetailsInner">
    [/#if]
    
    [#list loanAccountFormBean.loanInformationOrder as informationOrder]
        [#switch informationOrder.name]
        [#case "amount"]
            [#if loanProductReferenceData.glimApplicable || (loanProductReferenceData.group && loanProductReferenceData.groupLoanWithMembersEnabled)]
               [#else]
             <tr data-order-id="${informationOrder.id}">
        <td>
        [@form.label "amount" true ][@spring.message "createLoanAccount.amount"/][/@form.label]
        [@form.input path="loanAccountFormBean.amount"  id="loancreationdetails.input.sumLoanAmount" attributes="class=separatedNumber" /]
        <span id="createloan.allowedamounttext">([@spring.message "createLoanAccount.allowedAmount"/] ${loanProductReferenceData.minLoanAmount?string.number} - ${loanProductReferenceData.maxLoanAmount?string.number})</span>
        </td>
        <td>
            <span class="changeOrderArrows">
                <img class="moveUp" src="pages/framework/images/smallarrowtop.gif" />&nbsp;
                <img class="moveDown" src="pages/framework/images/smallarrowdown.gif" />
            </span>
        </td>
    </tr>
    [/#if]
        [#break]
        [#case "interestRate"]
             <tr data-order-id="${informationOrder.id}">
        <td>
        [@form.label "interestRate" true ][@spring.message "createLoanAccount.interestRate"/][/@form.label]
        [@form.input path="loanAccountFormBean.interestRate" id="loancreationdetails.input.interestRate" attributes="class=separatedNumber" /]
        <span>([@spring.message "createLoanAccount.allowedInterestRate"/] ${loanProductReferenceData.minInterestRate?string.number} - ${loanProductReferenceData.maxInterestRate?string.number} %)</span>
    </td>
    <td>
            <span class="changeOrderArrows">
                <img class="moveUp" src="pages/framework/images/smallarrowtop.gif" />&nbsp;
                <img class="moveDown" src="pages/framework/images/smallarrowdown.gif" />
            </span>
        </td>
    </tr>
         [#break]
         [#case "numberOfInstallments"]
           <tr data-order-id="${informationOrder.id}">
        <td>
        [@form.label "numberOfInstallments" true ][@spring.message "createLoanAccount.numberOfInstallments"/][/@form.label]
        [@form.input path="loanAccountFormBean.numberOfInstallments" id="loancreationdetails.input.numberOfInstallments" /]
        <span>([@spring.message "createLoanAccount.allowedNumberOfInstallments"/] ${loanProductReferenceData.minNumberOfInstallments?string.number} - ${loanProductReferenceData.maxNumberOfInstallments?string.number})</span>
    </td>
    <td>
    <span class="changeOrderArrows">
            <img class="moveUp" src="pages/framework/images/smallarrowtop.gif" />&nbsp;
            <img class="moveDown" src="pages/framework/images/smallarrowdown.gif" />
        </span>
    </td>
    </tr>
         [#break]
         [#case "disbursalDate"]
         <tr data-order-id="${informationOrder.id}">
        <td>
        [@form.label "disbursaldatedd" tfrue ][@spring.message "createLoanAccount.disbursalDate"/][/@form.label]
        [@form.input path="loanAccountFormBean.disbursementDateDD" id="disbursementDateDD" attributes="size=1 maxlength=2" /]<span>[@spring.message "datefield.dd"/]</span>
        [@form.input path="loanAccountFormBean.disbursementDateMM" id="disbursementDateMM" attributes="size=1 maxlength=2" /]<span>[@spring.message "datefield.mm"/]</span>
        [@form.input path="loanAccountFormBean.disbursementDateYY" id="disbursementDateYY" attributes="size=3 maxlength=4" /]<span>[@spring.message "datefield.yyyy"/]</span>
    </td>
    <td>
            <span class="changeOrderArrows">
                <img class="moveUp" src="pages/framework/images/smallarrowtop.gif" />&nbsp;
                <img class="moveDown" src="pages/framework/images/smallarrowdown.gif" />
            </span>
        </td>
    </tr>
         [#break]
         [#case "modeOfPayment"]
            [#if customerSearchFormBean.redoLoanAccount]
     <tr data-order-id="${informationOrder.id}">
        <td>
        [@form.label "disbursalpaymenttype" true ][@spring.message "reviewInstallments.mode_of_payment"/][/@form.label]
        [@form.singleSelectWithPrompt "loanAccountFormBean.disbursalPaymentTypeId", loanProductReferenceData.disbursalPaymentTypes /]
    </td>
    <td>
            <span class="changeOrderArrows">
                <img class="moveUp" src="pages/framework/images/smallarrowtop.gif" />&nbsp;
                <img class="moveDown" src="pages/framework/images/smallarrowdown.gif" />
            </span>
        </td>
    </tr>
    [/#if]
         [#break]
         [#case "repaymentDay"]
         [#if loanProductReferenceData.repaymentIndependentOfMeetingEnabled]
         <tr data-order-id="${informationOrder.id}">
        <td>
            [#if loanProductReferenceData.loanOfferingMeetingDetail.meetingDetailsDto.recurrenceTypeId == 1]
            
                  [@form.label "weekly.repaymentFrequency" true ][@spring.message "createLoanAccount.repaymentDay"/][/@form.label]
                <input type="radio" id="loancreationdetails.input.frequencyWeeks" name="repaymentFrequency" checked=checked />
                <span>[@spring.message "manageLoanProducts.defineLoanProduct.week(s)"/]</span>
                <input type="radio" id="loancreationdetails.input.frequencyMonths" name="repaymentFrequency" disabled=disabled/>
                <span>[@spring.message "manageLoanProducts.defineLoanProduct.month(s)"/]</span>
                <input type="radio" id="loancreationdetails.input.frequencyDays" name="repaymentFrequency" disabled=disabled/>
                <span>[@spring.message "manageLoanProducts.defineLoanProduct.day(s)"/]</span>
                <div id="week" id="weekDIV" style="margin-left: 306px; margin-bottom: 5px; margin-top: 5px; border: 1px solid grey; padding-left: 4px;">
                    <div style="margin-top: 2px; margin-bottom: 2px;">[@spring.message "manageLoanProducts.defineLoanProduct.ifweeks,specifythefollowing" /]</div>
                    [@spring.message "manageLoanProducts.defineLoanProduct.recurevery" /]
                    [@form.input path="loanAccountFormBean.repaymentRecursEvery" id="loancreationdetails.input.weekFrequency" attributes="size=3 maxlength=2"/]
                    <span id="weekLabelMessage">[@spring.message "manageLoanProducts.defineLoanProduct.week(s)" /]</span>
                    [@form.singleSelectWithPrompt path="loanAccountFormBean.repaymentDayOfWeek" options=loanProductReferenceData.daysOfTheWeekOptions id="weekDay" /]
                </div>
        
            [/#if]
            [#if loanProductReferenceData.loanOfferingMeetingDetail.meetingDetailsDto.recurrenceTypeId == 2]
            
            [@form.label "weekly.repaymentFrequency" true ][@spring.message "createLoanAccount.repaymentDay"/][/@form.label]
                <input type="radio" id="loancreationdetails.input.frequencyWeeks" name="repaymentFrequency" checked=checked />
                <span>[@spring.message "manageLoanProducts.defineLoanProduct.week(s)"/]</span>
                <input type="radio" id="loancreationdetails.input.frequencyMonths" name="repaymentFrequency" disabled=disabled/>
                <span>[@spring.message "manageLoanProducts.defineLoanProduct.month(s)"/]</span>
                <input type="radio" id="loancreationdetails.input.frequencyDays" name="repaymentFrequency" disabled=disabled/>
                <span>[@spring.message "manageLoanProducts.defineLoanProduct.day(s)"/]</span>
                <div id="montlycontainer" style="margin-left: 306px; margin-bottom: 5px; margin-top: 5px; border: 1px solid grey; padding-left: 4px;">
                    <div id="monthlyoption1">
                        <div style="margin-top: 2px; margin-bottom: 2px;">[@spring.message "manageLoanProducts.defineLoanProduct.ifmonths,specifythefollowing" /]</div>
                        [@spring.bind "loanAccountFormBean.montlyOption" /]
                        <input type="radio" id="${spring.status.expression}0" name="montlyOption" value="dayOfMonth"
                            [#if spring.stringStatusValue == "dayOfMonth"]
                                checked="checked"
                            [/#if]
                        />
                        <span>[@spring.message "manageLoanProducts.defineLoanProduct.dayOfMonthLabel" /]</span>
                        [@form.input path="loanAccountFormBean.repaymentDayOfMonth" id="loancreationdetails.input.dayOfMonth" attributes="size=3 maxlength=2"/]
                        <span>[@spring.message "manageLoanProducts.defineLoanProduct.ofEvery" /]</span>
                        [@form.input path="loanAccountFormBean.repaymentRecursEvery" id="recursEvery" attributes="size=3 maxlength=2 disabled=disabled"/]
                        <span id="monthLabelMessage">[@spring.message "manageLoanProducts.defineLoanProduct.month(s)" /]</span>
                    </div>
                    <div id="monthlyoption2">
                        [@spring.bind "loanAccountFormBean.montlyOption" /]
                        <input type="radio" id="${spring.status.expression}1" name="montlyOption" value="weekOfMonth"
                            [#if spring.stringStatusValue == "weekOfMonth"]
                                checked="checked"
                            [/#if]
                        />
                        <span>[@spring.message "manageLoanProducts.defineLoanProduct.weekOfMonthLabel" /]</span>
                        [@form.singleSelectWithPrompt path="loanAccountFormBean.repaymentWeekOfMonth" options=loanProductReferenceData.weeksOfTheMonthOptions selectPrompt="selectPrompt" id="monthRank" /]
                        [@form.singleSelectWithPrompt path="loanAccountFormBean.repaymentDayOfWeek" options=loanProductReferenceData.daysOfTheWeekOptions selectPrompt="selectPrompt" id="monthWeek"/]
                        <span>[@spring.message "manageLoanProducts.defineLoanProduct.ofEvery" /]</span>
                        [@form.input path="loanAccountFormBean.repaymentRecursEvery" id="recursEvery" attributes="size=3 maxlength=2 disabled=disabled"/]
                        <span id="monthLabelMessage">[@spring.message "manageLoanProducts.defineLoanProduct.month(s)" /]</span>
                    </div>
                </div>
      
            [/#if]
            [#if loanProductReferenceData.loanOfferingMeetingDetail.meetingDetailsDto.recurrenceTypeId == 3]
        
            [@form.label "weekly.repaymentFrequency" true ][@spring.message "createLoanAccount.repaymentDay"/][/@form.label]
                <input type="radio" id="loancreationdetails.input.frequencyWeeks" name="repaymentFrequency" disabled=disabled />
                <span>[@spring.message "manageLoanProducts.defineLoanProduct.week(s)"/]</span>
                <input type="radio" id="loancreationdetails.input.frequencyMonths" name="repaymentFrequency" disabled=disabled/>
                <span>[@spring.message "manageLoanProducts.defineLoanProduct.month(s)"/]</span>
                <input type="radio" id="loancreationdetails.input.frequencyDays" name="repaymentFrequency" checked=checked/>
                <span>[@spring.message "manageLoanProducts.defineLoanProduct.day(s)"/]</span>
                <div id="day" id="dayDIV" style="margin-left: 306px; margin-bottom: 5px; margin-top: 5px; border: 1px solid grey; padding-left: 4px;">
                    <div style="margin-top: 2px; margin-bottom: 2px;">[@spring.message "manageLoanProducts.defineLoanProduct.ifdays,specifythefollowing" /]</div>
                    [@spring.message "manageLoanProducts.defineLoanProduct.recurevery" /]
                    [@form.input path="loanAccountFormBean.repaymentRecursEvery" id="loancreationdetails.input.dayFrequency" attributes="size=3 maxlength=2"/]
                    <span id="dayLabelMessage">[@spring.message "manageLoanProducts.defineLoanProduct.day(s)" /]</span>
                </div>
            
            [/#if]
            </td>
            <td>
            <span class="changeOrderArrows">
                <img class="moveUp" src="pages/framework/images/smallarrowtop.gif" />&nbsp;
                <img class="moveDown" src="pages/framework/images/smallarrowdown.gif" />
            </span>
        </td>
        </tr>
    [/#if]
         [#break]
         [#case "gracePeriod"]
         <tr data-order-id="${informationOrder.id}">
   <td>
        [@form.label "graceduration" true ][@spring.message "createLoanAccount.graceDuration"/][/@form.label]
        [#if loanAccountFormBean.graceDuration?? && loanAccountFormBean.graceDuration == 0]
            [@form.input path="loanAccountFormBean.graceDuration" id="loancreationdetails.input.gracePeriod" attributes="disabled"/]
        [#else]
            [@form.input path="loanAccountFormBean.graceDuration" id="loancreationdetails.input.gracePeriod" /]
        [/#if]
        <span>[@spring.message "createLoanAccount.allowedGraceInInstallments"/]</span>
   </td>
   <td>
            <span class="changeOrderArrows">
                <img class="moveUp" src="pages/framework/images/smallarrowtop.gif" />&nbsp;
                <img class="moveDown" src="pages/framework/images/smallarrowdown.gif" />
            </span>
        </td>
   </tr>
         [#break]
         [#case "sourceOfFunds"]
            <tr data-order-id="${informationOrder.id}">
   <td>
        [@form.label "fundId" loanAccountFormBean.sourceOfFundsMandatory][@spring.message "createLoanAccount.sourceOfFund" /][/@form.label]
        [@form.singleSelectWithPrompt path="loanAccountFormBean.fundId" options=loanProductReferenceData.fundOptions selectPrompt="selectPrompt" attributes="class='noAutoSelect'" /]
   </td>
   <td>
            <span class="changeOrderArrows">
                <img class="moveUp" src="pages/framework/images/smallarrowtop.gif" />&nbsp;
                <img class="moveDown" src="pages/framework/images/smallarrowdown.gif" />
            </span>
        </td>
   </tr>
         [#break]
         [#case "purposeOfLoan"]
            [#if loanProductReferenceData.glimApplicable]
    [#else]
   <tr data-order-id="${informationOrder.id}">
   <td>
        [@form.label "loanPurposeId" loanAccountFormBean.purposeOfLoanMandatory][@spring.message "createLoanAccount.purposeOfLoan" /][/@form.label]
        [@form.singleSelectWithPrompt path="loanAccountFormBean.loanPurposeId" options=loanProductReferenceData.purposeOfLoanOptions selectPrompt="selectPrompt" attributes="class='noAutoSelect'"/]
    </td>
    <td>
            <span class="changeOrderArrows">
                <img class="moveUp" src="pages/framework/images/smallarrowtop.gif" />&nbsp;
                <img class="moveDown" src="pages/framework/images/smallarrowdown.gif" />
            </span>
        </td>
    </tr>
    [/#if]
         [#break]
         [#case "collateralType"]
          [#if loanAccountFormBean.collateralTypeAndNotesHidden]
    [#else]
    <tr data-order-id="${informationOrder.id}">
   <td>
        [@form.label "collateralTypeId" false][@spring.message "createLoanAccount.collateralType" /][/@form.label]
        [@form.singleSelectWithPrompt path="loanAccountFormBean.collateralTypeId" options=loanProductReferenceData.collateralOptions selectPrompt="selectPrompt" attributes="class='noAutoSelect'" /]
   </td>
   <td>
            <span class="changeOrderArrows">
                <img class="moveUp" src="pages/framework/images/smallarrowtop.gif" />&nbsp;
                <img class="moveDown" src="pages/framework/images/smallarrowdown.gif" />
            </span>
        </td>
   </tr>
       [/#if]
         [#break]
         [#case "collateralNotes"]
          [#if loanAccountFormBean.collateralTypeAndNotesHidden]
    [#else]
      <tr data-order-id="${informationOrder.id}">
   <td>
        [@form.label "collateralNotes" false][@spring.message "createLoanAccount.collateralNotes" /][/@form.label]
        [@spring.bind "loanAccountFormBean.collateralNotes" /]
        <textarea name="${spring.status.expression}" rows="4" cols="50" maxlength="200">${spring.status.value?if_exists}</textarea>
   </td>
   <td>
            <span class="changeOrderArrows">
                <img class="moveUp" src="pages/framework/images/smallarrowtop.gif" />&nbsp;
                <img class="moveDown" src="pages/framework/images/smallarrowdown.gif" />
            </span>
        </td>
   </tr>
       [/#if]
    
         [#break]
         [#case "externalId"]
    [#if loanAccountFormBean.externalIdHidden]
    [#else]
    <tr data-order-id="${informationOrder.id}">
   <td>
        [@form.label "externalId" loanAccountFormBean.externalIdMandatory][@spring.message "createLoanAccount.externalId"/][/@form.label]
        [@form.input path="loanAccountFormBean.externalId" id="externalId" /]
    </td>
    <td>
            <span class="changeOrderArrows">
                <img class="moveUp" src="pages/framework/images/smallarrowtop.gif" />&nbsp;
                <img class="moveDown" src="pages/framework/images/smallarrowdown.gif" />
            </span>
        </td>
    </tr>
    [/#if]
         [#break]
        [/#switch]
    [/#list]  
          </table>
        </td>
    </tr>
    </table>
    <p><span class="standout">[@spring.message "createLoanAccount.enterAccountInfo.defaultfees.header" /]</span></p>

	<div class="default-fees">
		[#assign index = 0]
    	[#list loanProductReferenceData.defaultFees as defaultFee]
            <input type="hidden" id="hiddenDefaultFeeRateBased${index}" value="${defaultFee.rateBasedFee?string}" />
            <div class="row" id="defaultFeeAmountRow[${index}]">
		        [@form.label "defaultFeeAmountOrRate[${index}]" false ]${defaultFee.name}:[/@form.label]
		        
                [#if loanProductReferenceData.group && loanProductReferenceData.groupLoanWithMembersEnabled]
                   [#if defaultFee.rateBasedFee]
                        [@form.input path="loanAccountFormBean.defaultFeeAmountOrRate[${index}]" id="defaultFeeAmount[${index}]" attributes="class=separatedNumber" /]
                    [#else]
                        [@form.input path="loanAccountFormBean.defaultFeeAmountOrRate[${index}]" id="defaultFeeAmount[${index}]" attributes="class=separatedNumber; readonly='readonly'"/]
                    [/#if]
                [#else]
                    [#if defaultFee.feeFrequency.oneTime]
                        [@form.input path="loanAccountFormBean.defaultFeeAmountOrRate[${index}]" id="defaultFeeAmountOrRate[${index}]" attributes="class=separatedNumber" /]
                    [#else]
                        [@form.input path="loanAccountFormBean.defaultFeeAmountOrRate[${index}]" id="defaultFeeAmountOrRate[${index}]" attributes="disabled='disabled'"/]
                    [/#if]
                [/#if]
		        
		        <span style="margin-left: 4px;">
		        [#if defaultFee.feeFrequency.oneTime]
		        	[@spring.message "createLoanAccount.periodicity"/] ${defaultFee.feeFrequencyType}
		        [#else]
		        	[@spring.message "createLoanAccount.periodicity"/] ${defaultFee.feeFrequency.recurAfterPeriod}
		        	[#if defaultFee.feeFrequency.weekly]
		        		<span>[@spring.message "createLoanAccount.weeks"/]</span>
		       		[#else]
		        		<span>[@spring.message "createLoanAccount.months"/]</span>
		       		[/#if]
		        [/#if]
		        
		       	[#if defaultFee.rateBasedFee]
		        	<span style="position:relative; left: -119px; top: 15px">[@spring.message "createLoanAccount.formula"/] ${defaultFee.feeFormula.name}</span>
		       	[/#if]
		       	</span>
		       	<div style="position:relative; left: 655px; top: -25px; height: 2px;">[@spring.formCheckbox "loanAccountFormBean.defaultFeeSelected[${index}]"/]Check to remove</div>
		       	[@spring.formHiddenInput "loanAccountFormBean.defaultFeeId[${index}]" /]
		    </div>
            [#if loanProductReferenceData.group && loanProductReferenceData.groupLoanWithMembersEnabled]
                [#assign clientIdx = 0]
                [#list loanProductReferenceData.clientDetails as clientdata]
                    <div class="row" id="defaultFeeIndividualAmountsRow[${index}][${clientIdx}]">
                        [@form.label "defaultFeeIndividualAmounts[${index}][${clientIdx}]" false ]${clientdata.clientName}:[/@form.label]
                        [#if defaultFee.rateBasedFee]
                            [@form.input path="loanAccountFormBean.defaultFeeIndividualAmounts[${index}][${clientIdx}]" 
                                         id="defaultFeeIndividualAmounts[${index}][${clientIdx}]" attributes="class=separatedNumber; readonly='readonly'" /]
                        [#else]
                            [@form.input path="loanAccountFormBean.defaultFeeIndividualAmounts[${index}][${clientIdx}]" 
                                         id="defaultFeeIndividualAmounts[${index}][${clientIdx}]" attributes="class=separatedNumber" /]
                        [/#if]
                    </div>
                    [#assign clientIdx = clientIdx + 1]
                [/#list]
                <div class="clear">
                <br />
            [/#if]
            [#assign index = index + 1]
	    [/#list]
	</div>  
	<div class="clear"/>  
    <br />
    <p><span class="standout">[@spring.message "createLoanAccount.enterAccountInfo.defaultpenalties.header" /]</span></p>

    <div class="default-penalties">
        [#assign idx = 0]
        [#list loanProductReferenceData.defaultPenalties as defaultPenalty]
            <div class="row">
                [@form.label "defaultPenaltyAmountOrRate[${idx}]" false ]${defaultPenalty.penaltyName}:[/@form.label]
                
                [#--
                [#if defaultPenalty.penaltyFrequency.oneTime]
                    [@form.input path="loanAccountFormBean.defaultPenaltyAmountOrRate[${idx}]" id="defaultPenaltyAmountOrRate[${idx}]" attributes="class=separatedNumber" /]
                [#else]
                    [@form.input path="loanAccountFormBean.defaultPenaltyAmountOrRate[${idx}]" id="defaultPenaltyAmountOrRate[${idx}]" attributes="disabled='disabled'"/]
                [/#if]
                --]
                
                [@form.input path="loanAccountFormBean.defaultPenaltyAmountOrRate[${idx}]" id="defaultPenaltyAmountOrRate[${idx}]" attributes="disabled='disabled'"/]
                
                <span style="margin-left: 20px;">
                    [@spring.message "createLoanAccount.periodicity"/] ${defaultPenalty.penaltyFrequency.name}
                
                [#if defaultPenalty.rateBasedPenalty]
                    <span style="position:relative; left: -116px; top: 15px">[@spring.message "createLoanAccount.formula"/] ${defaultPenalty.penaltyFormula.name}</span>
                [/#if]
                </span>
                [#--
                <div style="position:relative; left: 650px; top: -25px; height: 2px;">[@spring.formCheckbox "loanAccountFormBean.defaultPenaltySelected[${idx}]"/]Check to remove</div>
                [@spring.formHiddenInput "loanAccountFormBean.defaultPenaltyId[${idx}]" /]
                --]
            </div>
            [#assign idx = idx + 1]
        [/#list]
    </div>  
    <div class="clear"/>  
    <br />
    <p><div class="standout">[@spring.message "createLoanAccount.enterAccountInfo.additionalfees.header" /]</div></p>
    
    <div class="additional-fees">
        [#assign feeIdx = 0]
        [#list loanAccountFormBean.selectedFeeId as selectedFee]
            <div class="row">
                [@form.label "selectedFeeId${feeIdx}" false][@spring.message "createLoanAccount.feeType" /][/@form.label]
                [@form.singleSelectWithPrompt path="loanAccountFormBean.selectedFeeId[${feeIdx}]" 
                                              options=loanProductReferenceData.additionalFeeOptions 
                                              selectPrompt="selectPrompt" id="selectedFeeId${feeIdx}" attributes="class='noAutoSelect'" /]
                [#if !((loanProductReferenceData.group && loanProductReferenceData.groupLoanWithMembersEnabled))]
                    <span style="margin-left: 10px;">[@spring.message "createLoanAccount.feeAmount" /]</span>
                    [@form.input path="loanAccountFormBean.selectedFeeAmount[${feeIdx}]" 
                                 id="selectedFeeAmount[${feeIdx}]" attributes="style='margin-left: 20px;' class='separatedNumber'"/]
                [/#if]
            </div>
            [#if loanProductReferenceData.group && loanProductReferenceData.groupLoanWithMembersEnabled]
                [#assign clientIdx = 0]
                [#assign isRateBasedFee = false]
                [#if selectedFee??]
                    [#list loanProductReferenceData.additionalFees as additionalFee]
                        [#if additionalFee.id == selectedFee?string && additionalFee.rateBasedFee]
                            [#assign isRateBasedFee = true]
                        [/#if]
                    [/#list]
                [/#if]
                [#list loanProductReferenceData.clientDetails as clientdata]
                    <div class="row" id="selectedFeeIndividualAmountsRow[${feeIdx}][${clientIdx}]">
                        [@form.label "selectedFeeIndividualAmounts[${feeIdx}][${clientIdx}]" false ]${clientdata.clientName}:[/@form.label]
                        [#if isRateBasedFee]
                            [@form.input path="loanAccountFormBean.selectedFeeIndividualAmounts[${feeIdx}][${clientIdx}]" 
                                     id="selectedFeeIndividualAmounts[${feeIdx}][${clientIdx}]" 
                                     attributes="class='separatedNumber'; readonly='readonly'"/]
                        [#else]
                            [@form.input path="loanAccountFormBean.selectedFeeIndividualAmounts[${feeIdx}][${clientIdx}]" 
                                     id="selectedFeeIndividualAmounts[${feeIdx}][${clientIdx}]" 
                                     attributes="class='separatedNumber'"/]
                        [/#if]
                    </div>
                    [#assign clientIdx = clientIdx + 1]
                [/#list]
                <div class="row" id="selectedFeeAmountRow[${feeIdx}]">
                    [@form.label "selectedFeeAmount[${feeIdx}]" false][@spring.message "accounts.amountGroupLoan" /]:[/@form.label]
                    [#if isRateBasedFee]
                        [@form.input path="loanAccountFormBean.selectedFeeAmount[${feeIdx}]" 
                                     id="selectedFeeAmount[${feeIdx}]" attributes="class='separatedNumber'" /]
                    [#else]
                        [@form.input path="loanAccountFormBean.selectedFeeAmount[${feeIdx}]" 
                                     id="selectedFeeAmount[${feeIdx}]" attributes="class='separatedNumber'; readonly='readonly'" /]
                    [/#if]
                </div>
                <div class="clear">
                <br />
            [/#if]
            [#assign feeIdx = feeIdx + 1]
        [/#list]
	</div>
	<div class="clear"/>  
	<br/>

    <p><div class="standout" id="attachements">[@spring.message "client.Attachements" /] ( [@spring.message "upload.maxUploadSize" /] 2 MB )</div></p>
    <div class="attachements">
        <div class="row">
        [@form.label "selecttedFileLabel" false][@spring.message "upload.file" /][/@form.label]
        [@spring.bind "loanAccountFormBean.selectedFile" /]
        [@form.input path="loanAccountFormBean.selectedFile" fieldType="file" id="selectedFile"  /]
        </div>
        <div class="row">
            [@form.label "selecttedFileDescriptionLabel" false][@spring.message "Description" /][/@form.label]
            [@form.input path="loanAccountFormBean.selectedFileDescription" id="selectedFileDescription" attributes="maxlength='60' size='40'" /]
        </div>
        <div class="row">
            [@form.simpleButton label="upload.addFile" id="addFileButton" webflowEvent="newFileSelected" attributes="style='margin-left: 305px;'"/]
            <div class="clear"/>  
            <br/>
        </div>
        <ol id="filesToUpload" style='margin-left: 305px;'>
            [#list loanAccountFormBean.filesMetadata as fileMetadata]
                <li id="${fileMetadata.name}">
                    <b>${fileMetadata.name}</b><br/>
                    ${fileMetadata.description}<br/>
                    <input type="button" class="delete-file-button" value="X" />
                    <br/><br/>
                </li>
            [/#list]
        </ol>
    </div>
    </fieldset>
    
    <div class="row webflow-controls">
        [@form.submitButton label="widget.form.buttonLabel.continue" id="loancreationdetails.button.continue" webflowEvent="detailsEntered" /]
        [@form.cancelButton label="widget.form.buttonLabel.cancel" webflowEvent="cancel" /]
    </div>
    [#list loanProductReferenceData.additionalFees as additionalFee]
    	<input type="hidden" id="hiddenFeeAmount${additionalFee.id}" value="${additionalFee.amountOrRate?string.number}" />
    	<input type="hidden" id="hiddenFeeRateBased${additionalFee.id}" value="${additionalFee.rateBasedFee?string}" />
    [/#list]
        <input type="hidden" id="flowExecutionUrl" value="${flowExecutionUrl}" />
</form>

<script type="text/javascript">
$(document).ready(function() {
    $('.selectnewproduct').change(function(e) {
        $(this).closest('form').submit();
    });
    
    $('input[id^=selectedFeeAmount]').each(function(index){
    	if ( $(this).val() === "" || !$(this).val() || !$('#selectedFeeId'+index).attr('selectedIndex') ){
			$(this).attr('readonly', true);
			$(this).val("");
			$('#selectedFeeAmountRow\\[' + index + '\\]').hide();
    	}
    });
    
    $('[id*=FeeIndividualAmountsRow]').each(function(index){
        if ( $(this).children('input').val() === "" || !$(this).children('input').val() ){
            $(this).hide();
        }
    });
    
    $('[id^=selectedFeeId]').change(function(e) {
        var index = $('[id^=selectedFeeId]').index($(this));
        var selectedValue = $(this).val();
        if (selectedValue == null || selectedValue == "") {
            $('#selectedFeeAmount\\[' + index + '\\]').val("");
            $('#selectedFeeAmountRow\\[' + index + '\\]').hide();
            $('[id^=selectedFeeIndividualAmountsRow\\[' + index + '\\]]').hide();
            $('input[id^=selectedFeeIndividualAmounts\\[' + index + '\\]]').val("");
            $('#selectedFeeAmount\\[' + index + '\\]').attr('readonly', true);
        } else {
            var hiddenField = '#hiddenFeeAmount' + selectedValue;
            var hiddenValue = $(hiddenField).val();
            $('#selectedFeeAmount\\[' + index + '\\]').val(hiddenValue);
            $('#selectedFeeAmount\\[' + index + '\\]').attr('readonly', false);
            if ($('[id^=selectedFeeIndividualAmountsRow]').length != 0) {
                $('#selectedFeeAmountRow\\[' + index + '\\]').show();
                var isRateBasedFee = $('#hiddenFeeRateBased' + selectedValue).val();
                if (isRateBasedFee == "true") {
                   $('#selectedFeeAmount\\[' + index + '\\]').attr('readonly', false);
                } else {
                   $('#selectedFeeAmount\\[' + index + '\\]').attr('readonly', true);
                }
                var decimalPlaces = ${loanAccountFormBean.digitsAfterDecimalForMonetaryAmounts};
                var totalGroupLoanAmount = $('#glimsumloanamount').val();
                var amountLeft = hiddenValue;
                var biggestClientAmountIndex = 0;
                var biggestClientAmount = -1;
                $('.clientbox').each(function (subindex) {
                    var selectedFeeIndividualAmountRow = $('#selectedFeeIndividualAmountsRow\\[' + index + "\\]\\[" + subindex + "\\]");
                    if (this.checked && isRateBasedFee == "false") {
                        $(selectedFeeIndividualAmountRow).show();
                        var clientLoanAmount = $('#clientAmount\\[' + subindex + '\\]').val();
                        if (clientLoanAmount > biggestClientAmount) {
                            biggestClientAmount = clientLoanAmount;
                            biggestClientAmountIndex = subindex;
                        }
                        var factor = clientLoanAmount / totalGroupLoanAmount;
                        var feeAmount = (hiddenValue * factor).toFixed(decimalPlaces);
                        $(selectedFeeIndividualAmountRow).children('input').val(feeAmount);
                        $(selectedFeeIndividualAmountRow).children('input').attr('readonly', false);
                        amountLeft -= feeAmount;
                    } else if (this.checked && isRateBasedFee == 'true') {
                        $(selectedFeeIndividualAmountRow).show();
                        $(selectedFeeIndividualAmountRow).children('input').attr('readonly', true);
                        $(selectedFeeIndividualAmountRow).children('input').val(hiddenValue);
                    }
                });
                if (amountLeft != 0 && isRateBasedFee == "false") {
                    var biggestClientInput = $('#selectedFeeIndividualAmounts\\[' + index + '\\]\\[' + biggestClientAmountIndex + '\\]');
                    var biggestClientValue = biggestClientInput.val();
                    var adjustedAmount = parseFloat(biggestClientValue) + parseFloat(amountLeft);
                    adjustedAmount = adjustedAmount.toFixed(decimalPlaces);
                    biggestClientInput.val(adjustedAmount);
                }
            }
        }
    });
    
    $('#addFileButton').click(function(){
       var file = document.getElementById("selectedFile");
       var description = document.getElementById("selectedFileDescription");
       if (file.value != null && file.value != "" && !document.getElementById(file.value)) {
           $(this).closest('form').attr("action", $('#flowExecutionUrl').val() + "&_eventId=newFileSelected#attachements");
           $(this).closest('form').submit();
       }
    });
    
    $('.delete-file-button').click(function(){
        var fileName = $(this).closest('li').attr('id');
        $(this).closest('form').attr("action", $('#flowExecutionUrl').val() + "&_eventId=fileDeleted&fileToDelete=" + fileName + "#attachements");
        $(this).closest('form').submit();
    });
    
});
</script>

[/@layout.webflow]