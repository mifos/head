/**
 * 
 */
package org.mifos.application.customer.center.business;

import java.util.List;

import org.mifos.application.customer.business.CustomFieldView;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.exceptions.CustomerException;
import org.mifos.application.customer.persistence.CustomerPersistence;
import org.mifos.application.customer.util.helpers.CustomerConstants;
import org.mifos.application.customer.util.helpers.CustomerLevel;
import org.mifos.application.customer.util.helpers.CustomerStatus;
import org.mifos.application.fees.business.FeeView;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.personnel.business.PersonnelBO;
import org.mifos.framework.business.util.Address;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.security.util.UserContext;

/**
 * @author sumeethaec
 * 
 */
public class CenterBO extends CustomerBO {

	protected CenterBO() {
		super();
	}

	// TODO: removed searchId from parameter and generate internally
	public CenterBO(UserContext userContext, String displayName, Address address,
			List<CustomFieldView> customFields, List<FeeView> fees,
			PersonnelBO formedBy, Short office, MeetingBO meeting,
			PersonnelBO personnel) throws CustomerException {
		super(userContext, displayName, CustomerLevel.CENTER, CustomerStatus.CENTER_ACTIVE,
				address, customFields, fees, formedBy, office, null, meeting,
				personnel);
		this.validateFields(meeting, personnel);
		int count;
		try{
			count = new CustomerPersistence().getCustomerCountForOffice(CustomerLevel.CENTER,office);
		}catch(PersistenceException pe){
			throw new CustomerException(pe);
		}
		this.setSearchId("1." + ++count);
		this.setCustomerActivationDate(this.getCreatedDate());
	}

	public boolean isCustomerActive() {
		if (getCustomerStatus().getId().equals(
				CustomerConstants.CENTER_ACTIVE_STATE))
			return true;
		return false;
	}

	private void validateFields(MeetingBO meeting, PersonnelBO personnel)
			throws CustomerException {
		validateMeeting(meeting);
		validateLO(personnel);
	}
}
