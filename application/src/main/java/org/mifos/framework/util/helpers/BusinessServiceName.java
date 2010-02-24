/*
 * Copyright (c) 2005-2009 Grameen Foundation USA
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 *
 * See also http://www.apache.org/licenses/LICENSE-2.0.html for an
 * explanation of the license and how it is applied.
 */

package org.mifos.framework.util.helpers;

import org.mifos.application.admin.business.service.AdminBusinessService;
import org.mifos.application.configuration.business.service.ConfigurationBusinessService;
import org.mifos.customers.business.service.CustomerBusinessService;
import org.mifos.customers.center.business.service.CenterBusinessService;
import org.mifos.customers.client.business.service.ClientBusinessService;
import org.mifos.customers.group.business.service.GroupBusinessService;
import org.mifos.accounts.fees.business.service.FeeBusinessService;
import org.mifos.application.master.business.service.MasterDataService;
import org.mifos.customers.office.business.service.OfficeBusinessService;
import org.mifos.customers.personnel.business.service.PersonnelBusinessService;
import org.mifos.accounts.productdefinition.business.service.LoanPrdBusinessService;
import org.mifos.accounts.productsmix.business.service.ProductMixBusinessService;
import org.mifos.application.reports.business.service.ReportsBusinessService;
import org.mifos.security.rolesandpermission.business.service.RolesPermissionsBusinessService;
import org.mifos.framework.components.audit.business.service.AuditBusinessService;

/**
 * This class is @deprecated.
 * 
 * Instead of calling
 * </b>ServiceFactory#getBusinessService(BusinessServiceName)</b>, just call the
 * constructor of the business service directly. for example, new
 * ConfigurationBusinessService();
 */
@Deprecated
public enum BusinessServiceName {
    Customer(CustomerBusinessService.class), MasterDataService(MasterDataService.class), ReportsService(
            ReportsBusinessService.class), FeesService(FeeBusinessService.class), Personnel(
            PersonnelBusinessService.class), Center(CenterBusinessService.class), Client(ClientBusinessService.class), Group(
            GroupBusinessService.class), Office(OfficeBusinessService.class), LoanProduct(LoanPrdBusinessService.class), RolesPermissions(
            RolesPermissionsBusinessService.class), Admin(AdminBusinessService.class), AuditLog(
            AuditBusinessService.class), Configuration(ConfigurationBusinessService.class), PrdMix(
            ProductMixBusinessService.class), ;

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
