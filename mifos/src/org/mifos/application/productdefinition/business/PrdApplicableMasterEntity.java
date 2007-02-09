package org.mifos.application.productdefinition.business;

import org.mifos.application.master.business.MasterDataEntity;
import org.mifos.application.productdefinition.util.helpers.PrdApplicableMaster;

public class PrdApplicableMasterEntity extends MasterDataEntity {

	public PrdApplicableMasterEntity() {
		super();
	}

	public PrdApplicableMasterEntity(PrdApplicableMaster prdApplicableMaster) {
		super(prdApplicableMaster.getValue());
	}

	public PrdApplicableMaster asEnum() {
		return PrdApplicableMaster.fromInt(getId());
	}

}
