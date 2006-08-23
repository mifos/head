package org.mifos.application.customer.business;

import org.mifos.application.master.business.MasterDataEntity;

public class PositionEntity extends MasterDataEntity{
	
	/*
	 * Adding a default constructor is hibernate's requirement and should not be
	 * used to create a valid Object.
	 */
	protected PositionEntity(){}

	public PositionEntity(Short id, Short localeId) {
		super(id, localeId);
	}

	public PositionEntity(Short id) {
		super(id);
	}	
	
}
