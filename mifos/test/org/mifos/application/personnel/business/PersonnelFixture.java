package org.mifos.application.personnel.business;

import org.mifos.application.office.business.OfficeBO;
import org.mifos.application.office.business.OfficecFixture;

public class PersonnelFixture {

	public static PersonnelBO createPersonnel(String officeSearchId) {
		OfficeBO office = OfficecFixture.createOffice(officeSearchId);
		PersonnelBO personnel = new PersonnelBO();
		personnel.setOffice(office);
		return personnel;
	}

}
