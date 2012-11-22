function fun_cancel(){
	goBackToLoanAccountDetails.submit();
}

function fun_back(){
	goBackToAdjustmentDetails.submit();
}

function fn_submit(){
	if(document.getElementsByName("method")[0].value=='loadAdjustment' && document.getElementsByName("typeOfGroupLoan")[0].value==='parentAcc'){
		applyAdjustmentActionForm.method.value="divide";
		applyAdjustmentActionForm.action="applyAdjustment.do?method=divide";
		func_disableSubmitBtn("submit_btn");
		return true;	
	} else if(document.getElementsByName("method")[0].value=='loadAdjustment' || document.getElementsByName("method")[0].value=='divide'){

/*		if(!document.getElementsByName("adjustcheckbox")[0].checked || trim(document.getElementsByName("adjustmentNote")[0].value)==''){
		
			return false;
		}*/
		applyAdjustmentActionForm.method.value="previewAdjustment";
		applyAdjustmentActionForm.action="applyAdjustment.do?method=previewAdjustment";
		func_disableSubmitBtn("submit_btn");
		return true;
	}else{

		applyAdjustmentActionForm.method.value="applyAdjustment";
		applyAdjustmentActionForm.action="applyAdjustment.do?method=applyAdjustment";
		func_disableSubmitBtn("submit_btn");
		return true;
	}
}

function trim(str) {
	return str.replace(/^\s*|\s*$/g,"");
} 

$(document).ready(function() {
	if ($("#applyadjustment\\.input\\.revertLastPayment").is(":checked")) {
		disablePaymentDetails();
	}
	
	$("#applyadjustment\\.input\\.revertLastPayment").click(function() {
		if ($(this).is(":checked")) {
			disablePaymentDetails();
		} 
		else {
			enablePaymentDetails();
		}
	});
});

function disablePaymentDetails() {
	$("#applyAdjustment\\.input\\.amount").attr("disabled", "disabled");
	$("#applyAdjustment\\.input\\.paymentType").attr("disabled", "disabled");
	$("#transactionDateDD").attr("disabled", "disabled");
	$("#transactionDateMM").attr("disabled", "disabled");
	$("#transactionDateYY").attr("disabled", "disabled");
	
}

function enablePaymentDetails() {
	$("#applyAdjustment\\.input\\.amount").removeAttr("disabled");
	$("#applyAdjustment\\.input\\.paymentType").removeAttr("disabled");
	$("#transactionDateDD").removeAttr("disabled", "disabled");
	$("#transactionDateMM").removeAttr("disabled", "disabled");
	$("#transactionDateYY").removeAttr("disabled", "disabled");
}
	