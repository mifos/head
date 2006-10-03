package org.mifos.application.accounts.business;

import java.util.HashSet;
import java.util.Set;

import org.mifos.application.accounts.util.helpers.AccountState;
import org.mifos.application.master.business.StateEntity;
import org.mifos.application.productdefinition.business.ProductTypeEntity;

public class AccountStateEntity extends StateEntity {

	private ProductTypeEntity prdType;

	private Set<AccountStateFlagEntity> flagSet;

	private Short optional;

	protected AccountStateEntity() {
	}

	public AccountStateEntity(AccountState accountState) {
		super(accountState.getValue());
		this.flagSet = new HashSet<AccountStateFlagEntity>();
	}

	public ProductTypeEntity getPrdType() {
		return prdType;
	}

	public Set<AccountStateFlagEntity> getFlagSet() {
		return flagSet;
	}

	public void setFlagSet(Set<AccountStateFlagEntity> flagSet) {
		this.flagSet = flagSet;
	}

	public Short getOptional() {
		return optional;
	}

	public void setOptional(Short optional) {
		this.optional = optional;
	}
}
