package org.mifos.application.office.business;

import org.mifos.application.master.business.MasterDataEntity;
import org.mifos.application.office.exceptions.OfficeException;
import org.mifos.application.office.util.helpers.OfficeStatus;
import org.mifos.framework.exceptions.PropertyNotFoundException;

public class OfficeStatusEntity extends MasterDataEntity {

	public OfficeStatusEntity(OfficeStatus status) {
		super(status.getValue());
	}

	protected OfficeStatusEntity() {
		super();
	}

	public OfficeStatus getStatus() throws OfficeException {
		try {
			OfficeStatus.getOfficeStatus(this.getId());
		} catch (PropertyNotFoundException e) {
			throw new OfficeException();
		}
		return null;
	}
}
