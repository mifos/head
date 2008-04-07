package org.mifos.application.personnel.business;

import org.mifos.application.office.business.OfficeBO;
import org.mifos.application.office.business.OfficecFixture;
import org.mifos.application.personnel.util.helpers.PersonnelLevel;

public class PersonnelFixture {

	public static PersonnelBO createPersonnel(String officeSearchId) {
		OfficeBO office = OfficecFixture.createOffice(officeSearchId);
		PersonnelBO personnel = new PersonnelBO();
		personnel.setOffice(office);
		return personnel;
	}

	public static PersonnelBO createPersonnel(Short personnelId) {
		PersonnelBO personnel = new PersonnelBO(personnelId, null, null, null);
		personnel.setOffice(OfficecFixture.createOffice(Short.valueOf("1")));
		return personnel;
	}

	public static PersonnelBO createLoanOfficer(Short personnelId) {
		PersonnelBO personnel = createPersonnel(personnelId);
		personnel
				.setLevel(new PersonnelLevelEntity(PersonnelLevel.LOAN_OFFICER));
		return personnel;
	}

	public static PersonnelBO createNonLoanOfficer(Short personnelId) {
		PersonnelBO personnel = createPersonnel(personnelId);
		personnel.setLevel(new PersonnelLevelEntity(
				PersonnelLevel.NON_LOAN_OFFICER));
		return personnel;
	}
}
