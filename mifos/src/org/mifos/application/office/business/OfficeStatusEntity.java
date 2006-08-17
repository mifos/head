
package org.mifos.application.office.business;

import org.mifos.application.master.business.MasterDataEntity;
import org.mifos.application.office.util.helpers.OfficeStatus;

public class OfficeStatusEntity extends MasterDataEntity {
	
	protected OfficeStatusEntity(){
		super();
	}

	public OfficeStatusEntity(OfficeStatus status){
		super(status.getValue());
	}
}
