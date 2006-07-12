package org.mifos.framework.components.cronjobs.helpers;

import java.util.List;
import java.util.Set;

import org.mifos.application.accounts.business.AccountBO;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.group.util.helpers.GroupConstants;
import org.mifos.application.customer.persistence.service.CustomerPersistenceService;
import org.mifos.application.customer.util.helpers.CustomerConstants;
import org.mifos.application.util.helpers.YesNoFlag;
import org.mifos.framework.business.service.ServiceFactory;
import org.mifos.framework.components.cronjobs.TaskHelper;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.util.helpers.PersistenceServiceName;

public class PortfolioAtRiskHelper extends TaskHelper {
	
	@Override
	public void execute(long timeInMillis) {
		try{
			CustomerPersistenceService customerPersistenceService=(CustomerPersistenceService) ServiceFactory.getInstance().getPersistenceService(PersistenceServiceName.Customer);
			List<Integer> customerIds = customerPersistenceService.getCustomers(CustomerConstants.GROUP_LEVEL_ID);
			if(customerIds!=null && !customerIds.isEmpty())
				for(Integer customerId :  customerIds){
					try{
						CustomerBO customer = (CustomerBO)HibernateUtil.getSessionTL().get(CustomerBO.class,customerId);
						customer.generatePortfolioAtRisk();
						HibernateUtil.commitTransaction();
					}			
					catch(Exception e){
						HibernateUtil.rollbackTransaction();
						e.printStackTrace();
					}finally{
						HibernateUtil.closeSession();
					}
				}
		}catch(Exception e){
			e.printStackTrace();
		}

	}
	
	@Override
	public boolean isTaskAllowedToRun() {
		return true;
	}

}
