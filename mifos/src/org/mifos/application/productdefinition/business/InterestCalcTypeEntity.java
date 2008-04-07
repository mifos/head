package org.mifos.application.productdefinition.business;

import org.mifos.application.master.business.MasterDataEntity;
import org.mifos.application.productdefinition.util.helpers.InterestCalcType;

public class InterestCalcTypeEntity extends MasterDataEntity {

	public InterestCalcTypeEntity(InterestCalcType interestCalcType) {
		super(interestCalcType.getValue());

	}

	protected InterestCalcTypeEntity() {
		super();
	}

}
