package org.mifos.framework.util.helpers;

public enum BusinessServiceName {
	Savings("org.mifos.application.accounts.savings.business.service.SavingsBusinessService"),
	Customer("org.mifos.application.customer.business.service.CustomerBusinessService"),
	MasterDataService("org.mifos.application.master.business.service.MasterDataService"),
	BulkEntryService("org.mifos.application.bulkentry.business.service.BulkEntryBusinessService"),
	Accounts("org.mifos.application.accounts.business.service.AccountBusinessService"),
	SavingsProduct("org.mifos.application.productdefinition.business.service.SavingsPrdBusinessService"),
	Financial("org.mifos.application.accounts.financial.business.service.FinancialBusinessService"),
	Loan("org.mifos.application.accounts.loan.business.service.LoanBusinessService"),
	ReportsService("org.mifos.application.reports.business.service.ReportsBusinessService"),
	FeesService("org.mifos.application.fees.business.service.FeeBusinessService"),
	Personnel("org.mifos.application.personnel.business.service.PersonnelBusinessService"),
	Center("org.mifos.application.customer.center.business.service.CenterBusinessService"),
	Client("org.mifos.application.customer.client.business.service.ClientBusinessService"),
	Group("org.mifos.application.customer.group.business.service.GroupBusinessService"),
	Office("org.mifos.application.office.business.service.OfficeBusinessService"),
	LoanProduct("org.mifos.application.productdefinition.business.service.LoanPrdBusinessService"),
	OfficeHierarchy("org.mifos.application.office.business.service.OfficeHierarchyBusinessService"),
	Meeting("org.mifos.application.meeting.business.service.MeetingBusinessService"),
	RolesPermissions("org.mifos.application.rolesandpermission.business.service.RolesPermissionsBusinessService"),
	Admin("org.mifos.application.admin.business.service.AdminBusinessService"),
	fund("org.mifos.application.fund.business.service.FundBusinessService"),
	AuditLog("org.mifos.framework.components.audit.business.service.AuditBusinessService"),
	holiday("org.mifos.application.holiday.business.service.HolidayBusinessService");

	String name;

	BusinessServiceName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

}
