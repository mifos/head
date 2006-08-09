/**
 * 
 */
package org.mifos.application.accounts.business;

import java.util.HashSet;
import java.util.Set;

import org.mifos.application.accounts.util.helpers.AccountState;
import org.mifos.application.master.business.StateEntity;
import org.mifos.application.productdefinition.util.valueobjects.ProductType;

/**
 * @author rohitr
 *
 */
public class AccountStateEntity extends StateEntity {

	private static final long serialVersionUID = 13465L;

	/** The value of the prdTypeId property. */
	private ProductType prdType;

	private Set<AccountStateFlagEntity> flagSet;
	
	private Short optional;
	
	public AccountStateEntity() {
	}

	public AccountStateEntity(AccountState accountState) {
		super(accountState.getValue());
		this.flagSet = new HashSet<AccountStateFlagEntity>();
	}
	
	public AccountStateEntity(Short accountStateId) {
		super(accountStateId);
		this.flagSet = new HashSet<AccountStateFlagEntity>();
	}

	public ProductType getPrdType() {
		return prdType;
	}

	private void setPrdType(ProductType prdType) {
		this.prdType = prdType;
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
