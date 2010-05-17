package org.mifos.accounts.fees.servicefacade;

import org.mifos.customers.office.business.OfficeBO;
import org.mifos.customers.office.persistence.OfficeDao;

public interface OfficeDaoExtension extends OfficeDao, GenericDao<OfficeBO, Short> {

}
