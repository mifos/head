package org.mifos.application.checklist.business;

import java.util.List;

import org.mifos.application.accounts.business.AccountStateEntity;
import org.mifos.application.checklist.exceptions.CheckListException;
import org.mifos.application.productdefinition.business.ProductTypeEntity;

public class AccountCheckListBO extends CheckListBO {

	private ProductTypeEntity productTypeEntity;

	private AccountStateEntity accountStateEntity;

	public AccountCheckListBO() {
	}

	public AccountCheckListBO(ProductTypeEntity productTypeEntity,
			AccountStateEntity accountStateEntity, String name,
			Short checkListStatus, List<String> details, Short prefferedLocale,
			Short userId) throws CheckListException {
		super(name, checkListStatus, details, prefferedLocale, userId);
		this.productTypeEntity = productTypeEntity;
		this.accountStateEntity = accountStateEntity;
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
}
