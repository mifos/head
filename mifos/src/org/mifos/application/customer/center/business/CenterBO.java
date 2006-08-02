/**
 * 
 */
package org.mifos.application.customer.center.business;

import java.util.List;

import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.business.CustomFieldView;
import org.mifos.application.customer.exceptions.CustomerException;
import org.mifos.application.customer.util.helpers.CustomerConstants;
import org.mifos.application.customer.util.helpers.CustomerLevel;
import org.mifos.application.customer.util.helpers.CustomerStatus;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.office.business.OfficeBO;
import org.mifos.application.personnel.business.PersonnelBO;
import org.mifos.framework.business.util.Address;
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
	public CenterBO(UserContext userContext, String displayName,
			CustomerStatus customerStatus, Address address,
			List<CustomFieldView> customFields, PersonnelBO formedBy,
			OfficeBO office, MeetingBO meeting, PersonnelBO personnel,
			String searchId) throws CustomerException {
		super(userContext, displayName, CustomerLevel.CENTER, customerStatus,
				address, customFields, formedBy, office, null, meeting,
				personnel);
		this.setSearchId(searchId);
		this.setCustomerActivationDate(this.getCreatedDate());
	}

	public boolean isCustomerActive() {
		if (getCustomerStatus().getId().equals(
				CustomerConstants.CENTER_ACTIVE_STATE))
			return true;
		return false;
	}
}
