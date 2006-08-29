package org.mifos.framework.util.valueobjects;

import java.io.Serializable;

/**
	Value Objects were used in the M1 code. These value objects held state
	with business handler classes having the respective business logic. In
	the M2 style code we are not using these value objects and business
	handlers. Instead the business objects hold both the state and
	behaviour. We will remove all these un-used classes after the migration.
 */
public abstract class ValueObject implements Serializable, ReturnType {

	public String getResultName() {
		return null;
	}

}
