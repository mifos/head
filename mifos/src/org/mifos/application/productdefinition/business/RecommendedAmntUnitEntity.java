package org.mifos.application.productdefinition.business;

import org.mifos.application.master.business.MasterDataEntity;
import org.mifos.application.productdefinition.util.helpers.RecommendedAmountUnit;

public class RecommendedAmntUnitEntity extends MasterDataEntity {

	public RecommendedAmntUnitEntity(RecommendedAmountUnit recommendedAmountUnit) {
		super(recommendedAmountUnit.getValue());
	}

	protected RecommendedAmntUnitEntity() {
		super();
	}
}
