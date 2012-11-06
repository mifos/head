$(document).ready(function() {
	const SAVINGS_ACC_ROW_ID = '#applypayment\\.row\\.savingsForTransfer';
	const PAYMENT_TYPE_SELECT_ID = '#applypayment\\.input\\.paymentType';
    const ACCOUNT_FOR_TRANSFER_SELECT_ID = '#applypayment\\.input\\.accountForTransfer';
	var transfer_id = $('#transfer\\.id').attr('title');
    var previous = '\.' + $(ACCOUNT_FOR_TRANSFER_SELECT_ID).val();
	
	if ($(PAYMENT_TYPE_SELECT_ID).val() != transfer_id) {
		$(SAVINGS_ACC_ROW_ID).hide();
	}

	$(PAYMENT_TYPE_SELECT_ID).change(function() {
		var val = $(this).val();
		if (val == transfer_id) {
			$(SAVINGS_ACC_ROW_ID).show();
            if (previous != '\.') {
                $(previous).show();
            }
		} else {
			$(SAVINGS_ACC_ROW_ID).hide();
            if (previous != '\.') {
                $(previous).hide();
            }
		}
	});
	
    $(ACCOUNT_FOR_TRANSFER_SELECT_ID).change(function() {
        var val = $(this).val();
        if (val != '') {
            $('\.' + val).show();
        }
        if (previous != '\.') {
            $(previous).hide();
        }
        previous = '\.' + val;
    });
});

