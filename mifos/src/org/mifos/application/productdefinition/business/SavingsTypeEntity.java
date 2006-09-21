package org.mifos.application.productdefinition.business;

import org.mifos.application.master.business.MasterDataEntity;
import org.mifos.application.productdefinition.util.helpers.SavingsType;

public class SavingsTypeEntity extends MasterDataEntity {

	public SavingsTypeEntity(SavingsType savingsType) {
		super(savingsType.getValue());
	}

	protected SavingsTypeEntity() {
		super();
	}

}
