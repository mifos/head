package org.mifos.accounts.fees.persistence;

import org.mifos.customers.office.business.OfficeBO;
import org.mifos.customers.office.persistence.OfficeDao;
import org.mifos.platform.persistence.GenericDao;

public interface OfficeDaoExtension extends OfficeDao, GenericDao<OfficeBO, Short> {

}
