package org.mifos.application.reports.business.service;

import static org.mifos.framework.util.TransformerUtils.TRANSFORM_STRING_TO_SHORT;

import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.mifos.application.master.business.MifosCurrency;
import org.mifos.framework.business.service.ConfigService;
import org.mifos.framework.components.configuration.business.Configuration;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.util.helpers.DateUtils;
import org.springframework.core.io.Resource;

public class BranchCashConfirmationConfigService extends ConfigService {

	private static final String PRODUCT_OFFERING_DISBURSEMENT_IDS = "product.offering.disbursement.ids";
	private static final String CENTER_RECOVERY_PRODUCT_OFFERING_IDS = "product.offering.recovery.ids";
	private static final String CENTER_ISSUE_PRODUCT_OFFERING_IDS = "product.offering.issue.ids";

	public BranchCashConfirmationConfigService(Resource resource) {
		super(resource);
	}

	public Date getActionDate() {
		return DateUtils.currentDate();
	}

	public List<Short> getProductOfferingsForRecoveries()
			throws ServiceException {
		return getProductIds(CENTER_RECOVERY_PRODUCT_OFFERING_IDS);
	}

	public List<Short> getProductOfferingsForIssues() throws ServiceException {
		return getProductIds(CENTER_ISSUE_PRODUCT_OFFERING_IDS);
	}

	public List<Short> getProductOfferingsForDisbursements()
			throws ServiceException {
		return getProductIds(PRODUCT_OFFERING_DISBURSEMENT_IDS);
	}

	public MifosCurrency getCurrency() {
		return Configuration.getInstance().getSystemConfig().getCurrency();
	}

	private List<Short> getProductIds(String productIdKey)
			throws ServiceException {
		return (List<Short>) CollectionUtils.collect(
				getPropertyValues(productIdKey), TRANSFORM_STRING_TO_SHORT);
	}
}
