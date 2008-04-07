package org.mifos.application.customer.client.business;

public enum NameType {
	
	CLIENT(3),
	SPOUSE(2), 
	
	/** This value is supplied in test data where {@link #CLIENT} would
	    seem to make more sense.  I do not yet have evidence that any 
	    of the tests which supply this actually need this rather than CLIENT.
	    Is this just a test mistake?  Or is there
	    a real meaning for 1?  */
	MAYBE_CLIENT(1);

	private final short value;

	private NameType(int value) {
		this.value = (short) value;
	}
	
	public short getValue() {
		return value;
	}

}
