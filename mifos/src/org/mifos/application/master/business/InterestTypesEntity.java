package org.mifos.application.master.business;

import org.mifos.application.productdefinition.business.ProductTypeEntity;
import org.mifos.application.productdefinition.util.helpers.InterestTypeConstants;

public class InterestTypesEntity extends MasterDataEntity {
	private java.lang.String descripton;
	private ProductTypeEntity productType;
	
	protected InterestTypesEntity() {
		super();
	}
	
	public InterestTypesEntity(InterestTypeConstants interestType) {
		super(interestType.getValue());
	}

	public java.lang.String getDescripton() {
		return descripton;
	}

	public void setDescripton(java.lang.String descripton) {
		this.descripton = descripton;
	}

	public ProductTypeEntity getProductType() {
		return productType;
	}

	public void setProductType(ProductTypeEntity productType) {
		this.productType = productType;
	}
	
	
}
