package org.mifos.application.productdefinition.business;

import org.mifos.application.master.business.MasterDataEntity;
import org.mifos.application.productdefinition.util.helpers.PrdCategoryStatus;

public class PrdCategoryStatusEntity extends MasterDataEntity {

	PrdCategoryStatusEntity() {
		super();
	}

	PrdCategoryStatusEntity(PrdCategoryStatus prdCategoryStatus) {
		super(prdCategoryStatus.getValue());
	}
}
