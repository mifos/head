package org.mifos.framework.util.helpers;

public enum PersistenceServiceName {
	Savings("org.mifos.application.accounts.savings.persistence.service.SavingsPersistenceService"), 
	Customer("org.mifos.application.customer.persistence.service.CustomerPersistenceService"), 
	MasterDataService("org.mifos.application.master.persistence.service.MasterPersistenceService"), 
	CollectionSheet("org.mifos.application.collectionsheet.persistence.service.CollectionSheetPersistenceService"), 
	BulkEntryPersistanceService("org.mifos.application.bulkentry.persistance.service.BulkEntryPersistanceService"),
	SavingsProduct("org.mifos.application.productdefinition.persistence.service.SavingsPrdPersistenceService"),
	LoansProduct("org.mifos.application.productdefinition.persistence.service.LoansPrdPersistenceService"),
	Account("org.mifos.application.accounts.persistence.service.AccountPersistanceService"),
	Personnel("org.mifos.application.personnel.persistence.service.PersonnelPersistenceService"),
	Loan("org.mifos.application.accounts.loan.persistance.service.LoanPersistenceService"),
	Reports("org.mifos.application.reports.persistence.service.ReportsPersistenceService"),
	Fees("org.mifos.application.fees.persistence.service.FeePersistenceService"),
	Office("org.mifos.application.office.persistence.service.OfficePersistenceService"),
	Configuration("org.mifos.framework.components.configuration.persistence.service.ConfigurationPersistenceService");

	String name;

	PersistenceServiceName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
}
