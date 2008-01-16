package org.mifos.application.accounts.business;

import java.util.HashSet;
import java.util.Set;

import org.mifos.application.accounts.util.helpers.AccountState;
import org.mifos.application.master.MessageLookup;
import org.mifos.application.master.business.StateEntity;
import org.mifos.application.productdefinition.business.ProductTypeEntity;
import org.mifos.config.LocalizedTextLookup;

/**
 * Should be replaced by {@link AccountState}
 */
public class AccountStateEntity extends StateEntity implements LocalizedTextLookup {

	private ProductTypeEntity prdType;

	private Set<AccountStateFlagEntity> flagSet;

	private Short optional;

	private String description;
	
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

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
	
	public String getPropertiesKey() {
		return getLookUpValue().getLookUpEntity().getEntityType() + "." + getLookUpValue().getLookUpName();
	}	

	
}
