package org.mifos.application.personnel.util.helpers;

import org.mifos.framework.exceptions.PropertyNotFoundException;

public enum PersonnelStatus {
	ACTIVE(Short.valueOf("1")), INACTIVE(Short.valueOf("2"));

	Short value;

	PersonnelStatus(Short value) {
		this.value = value;
	}

	public Short getValue() {
		return value;
	}
	public static PersonnelStatus getPersonnelStatus(Short id)
	throws PropertyNotFoundException {
		for (PersonnelStatus status : PersonnelStatus.values()) {
			if (status.value.equals(id))
				return status;
		}
		// TODO: give proper message
		throw new PropertyNotFoundException("PersonnelStatus");
	}

}
