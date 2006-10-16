package org.mifos.application.office.util.helpers;

import org.mifos.application.office.util.resources.OfficeConstants;
import org.mifos.framework.exceptions.PropertyNotFoundException;

public enum OfficeStatus {
	ACTIVE(Short.valueOf("1")), INACTIVE(Short.valueOf("2"));
	Short value;

	OfficeStatus(Short value) {
		this.value = value;
	}

	public Short getValue() {
		return value;
	}
	public static OfficeStatus getOfficeStatus(Short id) throws PropertyNotFoundException{
		for (OfficeStatus status : OfficeStatus.values()) {
			if ( status.value.equals(id))
				return status;
		}
		throw new PropertyNotFoundException(OfficeConstants.ERROR_STATUS);
	}
}
