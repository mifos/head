package org.mifos.framework.util.helpers;

import org.mifos.application.admin.business.service.AdminBusinessService;
import org.mifos.application.configuration.business.service.ConfigurationBusinessService;
import org.mifos.application.customer.business.service.CustomerBusinessService;
import org.mifos.application.customer.center.business.service.CenterBusinessService;
import org.mifos.application.customer.client.business.service.ClientBusinessService;
import org.mifos.application.customer.group.business.service.GroupBusinessService;
import org.mifos.application.fees.business.service.FeeBusinessService;
import org.mifos.application.master.business.service.MasterDataService;
import org.mifos.application.office.business.service.OfficeBusinessService;
import org.mifos.application.personnel.business.service.PersonnelBusinessService;
import org.mifos.application.productdefinition.business.service.LoanPrdBusinessService;
import org.mifos.application.reports.business.service.ReportsBusinessService;
import org.mifos.application.rolesandpermission.business.service.RolesPermissionsBusinessService;
import org.mifos.framework.business.service.ServiceFactory;
import org.mifos.framework.components.audit.business.service.AuditBusinessService;

/**
 * This class is deprecated.
 * 
 * Instead of calling {@link ServiceFactory#getBusinessService(BusinessServiceName)},
 * just call the constructor of the business service directly.
 * for example, new ConfigurationBusinessService();
 */
public enum BusinessServiceName {
	Customer(CustomerBusinessService.class),
	MasterDataService(MasterDataService.class),
	ReportsService(ReportsBusinessService.class),
	FeesService(FeeBusinessService.class),
	Personnel(PersonnelBusinessService.class),
	Center(CenterBusinessService.class),
	Client(ClientBusinessService.class),
	Group(GroupBusinessService.class),
	Office(OfficeBusinessService.class),
	LoanProduct(LoanPrdBusinessService.class),
	RolesPermissions(RolesPermissionsBusinessService.class),
	Admin(AdminBusinessService.class),
	AuditLog(AuditBusinessService.class),
	Configuration(ConfigurationBusinessService.class),
	;

	private String name;

	private BusinessServiceName(String name) {
		this.name = name;
	}

	private BusinessServiceName(Class service) {
		this(service.getName());
	}

	public String getName() {
		return name;
	}

}
