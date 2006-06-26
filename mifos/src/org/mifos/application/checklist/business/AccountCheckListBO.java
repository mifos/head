package org.mifos.application.checklist.business;

import org.mifos.application.accounts.business.AccountStateEntity;
import org.mifos.application.productdefinition.business.ProductTypeEntity;
import org.mifos.framework.struts.plugin.helper.EntityMasterConstants;


public class AccountCheckListBO extends CheckListBO{
	
	private static final long serialVersionUID=198547694L;	
	
	private ProductTypeEntity productTypeEntity;	
	private AccountStateEntity accountStateEntity;	
	
	
	public AccountCheckListBO(){		
	}	

	public ProductTypeEntity getProductTypeEntity() {
		return productTypeEntity;
	}
	
	public void setProductTypeEntity(ProductTypeEntity productTypeEntity) {
		this.productTypeEntity = productTypeEntity;
	}

	public AccountStateEntity getAccountStateEntity() {
		return accountStateEntity;
	}

	public void setAccountStateEntity(AccountStateEntity accountStateEntity) {
		this.accountStateEntity = accountStateEntity;
	}

	@Override
	public Short getEntityID() {
		return EntityMasterConstants.Checklist;
	}
	
}
