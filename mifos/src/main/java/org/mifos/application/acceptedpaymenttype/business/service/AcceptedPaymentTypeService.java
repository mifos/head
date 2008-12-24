package org.mifos.application.acceptedpaymenttype.business.service;

import java.util.List;

import org.mifos.application.acceptedpaymenttype.persistence.AcceptedPaymentTypePersistence;
import org.mifos.application.master.business.PaymentTypeEntity;
import org.mifos.application.util.helpers.TrxnTypes;

public class AcceptedPaymentTypeService {

	public static List<PaymentTypeEntity> getAcceptedPaymentTypes(Short localeId) throws Exception {
		return new AcceptedPaymentTypePersistence().getAcceptedPaymentTypesForATransaction(
				localeId,
				TrxnTypes.loan_disbursement.getValue());
	}

}
