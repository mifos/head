package org.mifos.application.personnel.business;

import org.mifos.application.master.business.MasterDataEntity;
import org.mifos.application.personnel.util.helpers.PersonnelStatus;

public class PersonnelStatusEntity extends MasterDataEntity {
		
	protected PersonnelStatusEntity(){
		
	}
	public PersonnelStatusEntity(PersonnelStatus personnelStatus){
		super(personnelStatus.getValue());
	}
}
