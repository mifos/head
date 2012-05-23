var RepayLoan = {};

RepayLoan.renderAmount = function (selectedOption) {
    if(selectedOption == "true") {
        $("#totalRepaymentAmount").hide();
        $("#waivedRepaymentAmount").show();
        $("#waiverInterestWarning").attr('style','visibility:visible;');
        $("input[name=amount]").attr('value',$("input[name=waivedAmount]").val())
        $("input[name=waiverInterest]").attr('value','true');
    }else{
        $("#totalRepaymentAmount").show();
        $("#waivedRepaymentAmount").hide();
        $("#waiverInterestWarning").attr('style','visibility:hidden;');
        $("input[name=amount]").attr('value',$("input[name=repaymentAmount]").val())
        $("input[name=waiverInterest]").attr('value','false');
    }
}

$(document).ready(function(){

    $("input[name=waiverInterestChckBox]").bind("change", function(){
        RepayLoan.renderAmount($('input:checkbox[name=waiverInterestChckBox]:checked').val());
    });

    RepayLoan.renderAmount($('input:checkbox[name=waiverInterestChckBox]:checked').val());
    fundTransfer();
});

function fundTransfer() {
	const SAVINGS_ACC_ROW_ID = '#repayLoan\\.row\\.savingsForTransfer';
	const PAYMENT_TYPE_SELECT_ID = '#RepayLoan\\.input\\.modeOfRepayment';
	var transfer_id = $('input[name=transferPaymentTypeId]').val();

	if ($(PAYMENT_TYPE_SELECT_ID).val() != transfer_id) {
		$(SAVINGS_ACC_ROW_ID).hide();
	}

	$(PAYMENT_TYPE_SELECT_ID).change(function() {
		var val = $(this).val();
		if (val == transfer_id) {
			$(SAVINGS_ACC_ROW_ID).show();
		} else {
			$(SAVINGS_ACC_ROW_ID).hide();
		}
	});
}