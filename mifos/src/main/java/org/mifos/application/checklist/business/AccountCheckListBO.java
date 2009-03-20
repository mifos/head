/*
 * Copyright (c) 2005-2009 Grameen Foundation USA
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 *
 * See also http://www.apache.org/licenses/LICENSE-2.0.html for an
 * explanation of the license and how it is applied.
 */
 
package org.mifos.application.checklist.business;

import java.util.List;

import org.mifos.application.accounts.business.AccountStateEntity;
import org.mifos.application.checklist.exceptions.CheckListException;
import org.mifos.application.checklist.persistence.CheckListPersistence;
import org.mifos.application.checklist.util.helpers.CheckListType;
import org.mifos.application.productdefinition.business.ProductTypeEntity;
import org.mifos.framework.exceptions.PersistenceException;

public class AccountCheckListBO extends CheckListBO {

	private ProductTypeEntity productTypeEntity;

	private AccountStateEntity accountStateEntity;

	protected AccountCheckListBO() {
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

	@Override
	public CheckListType getCheckListType() {
		return CheckListType.ACCOUNT_CHECKLIST;
	}

	public void update(ProductTypeEntity productTypeEntity,
			AccountStateEntity accountStateEntity, String name,
			Short checkListStatus, List<String> details, Short prefferedLocale,
			Short userId) throws CheckListException {
		super.update(name, checkListStatus, details, prefferedLocale, userId);
		if (!this.productTypeEntity.getProductTypeID().equals(
				productTypeEntity.getProductTypeID())
				|| !this.accountStateEntity.getId().equals(
						accountStateEntity.getId()))
			validateCheckListState(productTypeEntity.getProductTypeID(),
					accountStateEntity.getId(), false);
		this.productTypeEntity = productTypeEntity;
		this.accountStateEntity = accountStateEntity;
		try {
			new CheckListPersistence().createOrUpdate(this);
		} catch (PersistenceException e) {
			throw new CheckListException(e);
		}
	}

}
