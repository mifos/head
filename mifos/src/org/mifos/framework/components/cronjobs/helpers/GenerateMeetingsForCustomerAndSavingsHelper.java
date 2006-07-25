package org.mifos.framework.components.cronjobs.helpers;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.mifos.application.accounts.business.AccountActionDateEntity;
import org.mifos.application.accounts.business.AccountBO;
import org.mifos.application.accounts.business.AccountFeesActionDetailEntity;
import org.mifos.application.accounts.business.AccountFeesEntity;
import org.mifos.application.accounts.business.CustomerAccountBO;
import org.mifos.application.accounts.loan.util.helpers.LoanConstants;
import org.mifos.application.accounts.persistence.service.AccountPersistanceService;
import org.mifos.application.accounts.savings.business.SavingsBO;
import org.mifos.application.accounts.savings.util.helpers.SavingsHelper;
import org.mifos.application.accounts.util.helpers.AccountConstants;
import org.mifos.application.accounts.util.valueobjects.AccountActionDate;
import org.mifos.application.accounts.util.valueobjects.AccountFees;
import org.mifos.application.accounts.util.valueobjects.AccountFeesActionDetail;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.util.helpers.CustomerConstants;
import org.mifos.application.fees.util.helpers.FeeFrequencyType;
import org.mifos.application.fees.util.valueobjects.Fees;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.meeting.util.valueobjects.Meeting;
import org.mifos.application.productdefinition.struts.action.PaymentTypesAction;
import org.mifos.application.productdefinition.util.helpers.ProductDefinitionConstants;
import org.mifos.application.productdefinition.util.valueobjects.PaymentType;
import org.mifos.framework.components.cronjobs.TaskHelper;
import org.mifos.framework.components.repaymentschedule.RepaymentSchedule;
import org.mifos.framework.components.repaymentschedule.RepaymentScheduleConstansts;
import org.mifos.framework.components.repaymentschedule.RepaymentScheduleException;
import org.mifos.framework.components.repaymentschedule.RepaymentScheduleFactory;
import org.mifos.framework.components.repaymentschedule.RepaymentScheduleHelper;
import org.mifos.framework.components.repaymentschedule.RepaymentScheduleIfc;
import org.mifos.framework.components.repaymentschedule.RepaymentScheduleInputsIfc;
import org.mifos.framework.components.scheduler.SchedulerException;
import org.mifos.framework.components.scheduler.SchedulerIntf;
import org.mifos.framework.components.scheduler.helpers.SchedulerHelper;
import org.mifos.framework.exceptions.HibernateProcessException;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.util.helpers.DateUtils;

public class GenerateMeetingsForCustomerAndSavingsHelper extends TaskHelper {

	@Override
	public void execute(long timeInMillis) {
		try {
			if( !isYearEnd(timeInMillis)) return ;
			AccountPersistanceService accountPersistanceService = new AccountPersistanceService();
			List<AccountBO> customerAccounts = accountPersistanceService
					.getActiveCustomerAndSavingsAccounts();
			for (AccountBO accountBO : customerAccounts) {
				HibernateUtil.startTransaction();
				if (accountBO instanceof CustomerAccountBO)
					 ((CustomerAccountBO)accountBO).generateMeetingsForNextYear();
				else if (accountBO instanceof SavingsBO)
					((SavingsBO)accountBO).generateMeetingsForNextYear();

				HibernateUtil.commitTransaction();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private boolean isYearEnd( long timeInMillis){
		
		Calendar currentDate =Calendar.getInstance();
		currentDate.setTimeInMillis(timeInMillis);
		Calendar lastDayOfyear = Calendar.getInstance();
		lastDayOfyear.setTime(DateUtils.getLastDayOfYear());
		
		 return new GregorianCalendar(
				 currentDate.get(Calendar.YEAR),currentDate.get(Calendar.MONTH),
				 currentDate.get(Calendar.DATE),0,0,0).compareTo(
			   	new GregorianCalendar(
			   	lastDayOfyear.get(Calendar.YEAR),lastDayOfyear.get(Calendar.MONTH),
				lastDayOfyear.get(Calendar.DATE),0,0,0))
				==0 ?true:false;
	}
}
