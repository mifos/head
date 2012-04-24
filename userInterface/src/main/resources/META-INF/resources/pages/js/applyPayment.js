$(document).ready(function() {
	const SAVINGS_ACC_ROW_ID = '#applypayment\\.row\\.savingsForTransfer';
	const PAYMENT_TYPE_SELECT_ID = '#applypayment\\.input\\.paymentType';
	var transfer_id = $('#transfer\\.id').attr('title');
	
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
});

