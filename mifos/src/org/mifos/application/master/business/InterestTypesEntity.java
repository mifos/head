package org.mifos.application.master.business;

import org.mifos.application.productdefinition.business.ProductTypeEntity;

public class InterestTypesEntity extends MasterDataEntity {
	private java.lang.String descripton;
	private ProductTypeEntity productType;
	
	public InterestTypesEntity() {
		super();
	}
	
	public InterestTypesEntity(Short id) {
		super(id);
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
