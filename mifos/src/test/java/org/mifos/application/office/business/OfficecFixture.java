package org.mifos.application.office.business;

public class OfficecFixture {

	public static OfficeBO createOffice(String searchId) {
		OfficeBO office = new OfficeBO();
		office.setSearchId(searchId);
		return office;
	}

	public static OfficeBO createOffice(Short officeId) {
		return new OfficeBO(officeId, "Some Office", null, null);
	}
}
