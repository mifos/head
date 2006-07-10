package org.mifos.framework.components.cronjobs.helpers;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.hibernate.HibernateException;
import org.mifos.application.accounts.business.AccountBO;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.persistence.service.CustomerPersistenceService;
import org.mifos.application.util.helpers.YesNoFlag;
import org.mifos.framework.business.service.ServiceFactory;
import org.mifos.framework.components.cronjobs.TaskHelper;
import org.mifos.framework.components.scheduler.SchedulerException;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.util.helpers.PersistenceServiceName;

public class RegenerateScheduleHelper extends TaskHelper {

	List<Integer> accountList;
	
	public void execute(long timeInMills){
		try{
			accountList=new ArrayList<Integer>();
			CustomerPersistenceService customerPersistenceService=(CustomerPersistenceService) ServiceFactory.getInstance().getPersistenceService(
				PersistenceServiceName.Customer);
			List<Integer> customerIds = customerPersistenceService.getCustomersWithUpdatedMeetings();
			if(customerIds!=null && !customerIds.isEmpty())
				for(Integer customerId :  customerIds){
					try{
						handleChangeInMeetingSchedule(customerId);
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
	
	private void handleChangeInMeetingSchedule(Integer customerId) throws Exception{
		CustomerBO customer = (CustomerBO)HibernateUtil.getSessionTL().get(CustomerBO.class,customerId);
		customer.getCustomerMeeting().setUpdatedFlag(YesNoFlag.NO.getValue());
		Set<AccountBO> accounts=customer.getAccounts();
		if(accounts!=null && !accounts.isEmpty())
			for(AccountBO account : accounts){
				if(!accountList.contains(account.getAccountId())){
					account.handleChangeInMeetingSchedule();
					accountList.add(account.getAccountId());
				}
			}
		CustomerPersistenceService customerPersistenceService=(CustomerPersistenceService) ServiceFactory.getInstance().getPersistenceService(PersistenceServiceName.Customer);
		List<Integer> customerIds=customerPersistenceService.getChildrenForParent(customer.getSearchId(),customer.getOffice().getOfficeId());
		if(customerIds!=null && !customerIds.isEmpty()){
			for(Integer childCustomerId : customerIds){
				handleChangeInMeetingSchedule(childCustomerId);
			}
		}
	}

}
