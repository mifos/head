package org.mifos.application.personnel.util.helpers;

import org.mifos.framework.exceptions.PropertyNotFoundException;

public enum PersonnelLevel {

	LOAN_OFFICER(Short.valueOf("1")), NON_LOAN_OFFICER(Short.valueOf("2"));

	Short value;

	PersonnelLevel(Short value) {
		this.value = value;
	}

	public Short getValue() {
		return value;
	}

	public static PersonnelLevel getPersonnelLevel(Short id)
			throws PropertyNotFoundException {
		for (PersonnelLevel level : PersonnelLevel.values()) {
			if (level.value.equals(id))
				return level;
		}
		throw new PropertyNotFoundException(PersonnelConstants.ERROR_NO_LEVEL);
	}
}
