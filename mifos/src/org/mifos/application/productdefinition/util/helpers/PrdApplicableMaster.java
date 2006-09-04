package org.mifos.application.productdefinition.util.helpers;

import org.mifos.framework.exceptions.PropertyNotFoundException;

public enum PrdApplicableMaster {
	CLIENTS((short) 1), GROUPS((short) 2),CENTERS((short)3),ALLCUSTOMERS((short)4);

	Short value;

	PrdApplicableMaster(Short value) {
		this.value = value;
	}

	public Short getValue() {
		return value;
	}

	public static PrdApplicableMaster getPrdApplicableMaster(Short value)
			throws PropertyNotFoundException {
		for (PrdApplicableMaster prdApplicableMaster : PrdApplicableMaster
				.values())
			if (prdApplicableMaster.getValue().equals(value))
				return prdApplicableMaster;
		throw new PropertyNotFoundException("PrdApplicableMaster");
	}
}
