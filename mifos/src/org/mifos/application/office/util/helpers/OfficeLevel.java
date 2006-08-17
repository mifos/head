package org.mifos.application.office.util.helpers;

import org.mifos.framework.exceptions.PropertyNotFoundException;

public enum OfficeLevel {

	HEADOFFICE(Short.valueOf("1")), REGIONALOFFICE(Short.valueOf("2")), SUBREGIONALOFFICE(
			Short.valueOf("3")), AREAOFFICE(Short.valueOf("4")), BRANCHOFFICE(
			Short.valueOf("5"));
	Short value;

	OfficeLevel(Short value) {
		this.value = value;
	}

	public Short getValue() {
		return value;
	}

	public static OfficeLevel getOfficeLevel(Short id)
			throws PropertyNotFoundException {
		for (OfficeLevel level : OfficeLevel.values())
			if (level.value.equals(id))
				return level;
		throw new PropertyNotFoundException("CustomerLevel");
	}
}
