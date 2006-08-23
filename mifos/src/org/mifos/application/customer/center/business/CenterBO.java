/**
 * 
 */
package org.mifos.application.customer.center.business;

import java.util.Date;
import java.util.List;

import org.mifos.application.configuration.business.MifosConfiguration;
import org.mifos.application.configuration.exceptions.ConfigurationException;
import org.mifos.application.configuration.util.helpers.ConfigurationConstants;
import org.mifos.application.customer.business.CustomFieldView;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.center.exception.StateChangeException;
import org.mifos.application.customer.center.persistence.CenterPersistence;
import org.mifos.application.customer.client.util.helpers.ClientConstants;
import org.mifos.application.customer.exceptions.CustomerException;
import org.mifos.application.customer.exceptions.CustomerStateChangeException;
import org.mifos.application.customer.persistence.CustomerPersistence;
import org.mifos.application.customer.util.helpers.CustomerConstants;
import org.mifos.application.customer.util.helpers.CustomerLevel;
import org.mifos.application.customer.util.helpers.CustomerStatus;
import org.mifos.application.fees.business.FeeView;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.framework.business.util.Address;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.security.util.UserContext;

/**
 * @author sumeethaec
 * 
 */
public class CenterBO extends CustomerBO {

	protected CenterBO() {
		super();
	}

	public CenterBO(UserContext userContext, String displayName,
			Address address, List<CustomFieldView> customFields,
			List<FeeView> fees, String externalId, Date mfiJoiningDate,
			Short office, MeetingBO meeting, Short loanOfficerId)
			throws CustomerException {
		super(userContext, displayName, CustomerLevel.CENTER,
				CustomerStatus.CENTER_ACTIVE, externalId, mfiJoiningDate,
				address, customFields, fees, null, office, null, meeting,
				loanOfficerId);
		this.validateFields(displayName, meeting, loanOfficerId);
		int count;
		try {
			count = new CustomerPersistence().getCustomerCountForOffice(
					CustomerLevel.CENTER, office);
		} catch (PersistenceException pe) {
			throw new CustomerException(pe);
		}
		this.setSearchId("1." + ++count);
		this.setCustomerActivationDate(this.getCreatedDate());
	}

	@Override
	public boolean isActive() {
		return getCustomerStatus().getId().equals(CustomerStatus.CENTER_ACTIVE.getValue());
	}

	private void validateFields(String displayName, MeetingBO meeting,
			Short personnel) throws CustomerException {
		if (new CenterPersistence().isCenterExists(displayName)) {
			Object[] values = new Object[1];
			values[0] = displayName;
			throw new CustomerException(
					CustomerConstants.ERRORS_DUPLICATE_CUSTOMER, values);
		}
		validateMeeting(meeting);
		validateLO(personnel);
	}

	@Override
	protected void validateStatusChange(Short newStatusId)
			throws CustomerException {
		if (newStatusId.equals(CustomerStatus.CENTER_INACTIVE.getValue())) {
			if (getActiveAndApprovedLoanAccounts(new Date()).size() > 0
					|| getActiveSavingsAccounts().size() > 0) {
				throw new CustomerException(
						CustomerConstants.CUSTOMER_HAS_ACTIVE_ACCOUNTS_EXCEPTION);
			}
			try {
				if (getChildren(CustomerLevel.GROUP.getValue()).size() > 0) {
					throw new CustomerException(
							CustomerConstants.ERROR_STATE_CHANGE_EXCEPTION,
							new Object[] { MifosConfiguration.getInstance()
									.getLabel(
											ConfigurationConstants.GROUP,
											this.getUserContext()
													.getPereferedLocale()) });
				}
			} catch (PersistenceException pe) {
				throw new CustomerException(pe);
			}  catch (ConfigurationException ce) {
				throw new CustomerException(ce);
			}
		} else if (newStatusId.equals(CustomerStatus.CENTER_ACTIVE.getValue())) {
			if (getPersonnel() == null
					|| getPersonnel().getPersonnelId() == null) {
				throw new CustomerException(
						ClientConstants.CLIENT_LOANOFFICER_NOT_ASSIGNED);
			}
		}

	}
	//TODO
	@Override
	protected boolean checkNewStatusIsFirstTimeActive(Short oldStatus,
			Short newStatusId) {
		return false;
	}
}
