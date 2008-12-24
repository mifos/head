package org.mifos.application.office.business;

import org.mifos.framework.business.PersistentObject;
import org.mifos.framework.business.util.Address;

public class OfficeAddressEntity extends PersistentObject {

	private final Short officeAdressId;

	private final OfficeBO office;

	private Address address;

	public OfficeAddressEntity(OfficeBO office, Address address) {
		super();

		this.office = office;
		this.address = address;
		this.officeAdressId = null;
	}

	protected OfficeAddressEntity() {
		super();
		officeAdressId = null;
		office = null;
		address = new Address();
	}

	public Address getAddress() {
		return address;
	}

	public void setAddress(Address address) {
		this.address = address;
	}
}
