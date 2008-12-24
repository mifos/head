package org.mifos.application.productdefinition.business;

import org.mifos.application.master.business.MasterDataEntity;
import org.mifos.application.productdefinition.util.helpers.ApplicableTo;

/**
 * Also see {@link ApplicableTo}.
 */
public class PrdApplicableMasterEntity extends MasterDataEntity {

	public PrdApplicableMasterEntity() {
		super();
	}

	public PrdApplicableMasterEntity(ApplicableTo prdApplicableMaster) {
		super(prdApplicableMaster.getValue());
	}

	public ApplicableTo asEnum() {
		return ApplicableTo.fromInt(getId());
	}

}
