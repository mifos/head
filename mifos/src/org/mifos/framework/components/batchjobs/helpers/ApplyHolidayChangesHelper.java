package org.mifos.framework.components.batchjobs.helpers;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.mifos.framework.components.batchjobs.MifosTask;
import org.mifos.framework.components.batchjobs.SchedulerConstants;
import org.mifos.framework.components.batchjobs.TaskHelper;
import org.mifos.framework.components.batchjobs.exceptions.BatchJobException;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.application.accounts.business.AccountBO;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.persistence.CustomerPersistence;
import org.mifos.application.holiday.*;
import org.mifos.application.holiday.business.HolidayBO;
import org.mifos.application.holiday.persistence.HolidayPersistence;
import org.mifos.application.holiday.util.helpers.HolidayUtils;
import org.mifos.application.util.helpers.YesNoFlag;

public class ApplyHolidayChangesHelper extends TaskHelper {
	
	List<HolidayBO> unappliedHolidays;
	
	public ApplyHolidayChangesHelper(MifosTask mifosTask) {
		super(mifosTask);
	}

	@Override
	public void execute(long timeInMillis) throws BatchJobException {
		List<String> errorList = new ArrayList<String>();
		
		unappliedHolidays = new ArrayList<HolidayBO>();
		try {
			unappliedHolidays = new HolidayPersistence().getUnAppliedHolidays();
		} catch (Exception e) {
			throw new BatchJobException(e);
		}
		if (unappliedHolidays != null && !unappliedHolidays.isEmpty())
			for (HolidayBO holiday : unappliedHolidays) {
				try {
					handleHolidayApplication(holiday);
					HibernateUtil.commitTransaction();
					System.out.println("\n\nA holiday has been adjusted\n\n");
				} catch (Exception e) {
					HibernateUtil.rollbackTransaction();
					errorList.add(holiday.toString());
				} finally {
					HibernateUtil.closeSession();
				}
			}
		if (errorList.size() > 0)
			throw new BatchJobException(SchedulerConstants.FAILURE, errorList);
	}
	
	private void handleHolidayApplication(HolidayBO holiday) throws Exception 
	{
		HolidayUtils.rescheduleLoanRepaymentDates(holiday);
		
HibernateUtil.commitTransaction();
		
		HolidayUtils.rescheduleSavingDates(holiday);
		
		//Change flag for this holiday in the databese
		holiday.setHolidayChangesAppliedFlag(YesNoFlag.YES.getValue());
		new HolidayPersistence().createOrUpdate(holiday);
	}
	
	@Override
	public boolean isTaskAllowedToRun() {
		return true;
	}	
}

