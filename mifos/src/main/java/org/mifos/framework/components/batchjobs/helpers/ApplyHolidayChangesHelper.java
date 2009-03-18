package org.mifos.framework.components.batchjobs.helpers;

import java.util.ArrayList;
import java.util.List;

import org.mifos.application.holiday.business.HolidayBO;
import org.mifos.application.holiday.persistence.HolidayPersistence;
import org.mifos.application.holiday.util.helpers.HolidayUtils;
import org.mifos.application.util.helpers.YesNoFlag;
import org.mifos.framework.components.batchjobs.MifosTask;
import org.mifos.framework.components.batchjobs.SchedulerConstants;
import org.mifos.framework.components.batchjobs.TaskHelper;
import org.mifos.framework.components.batchjobs.exceptions.BatchJobException;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;

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
					StaticHibernateUtil.commitTransaction();
				} catch (Exception e) {
					StaticHibernateUtil.rollbackTransaction();
					errorList.add(holiday.toString());
				} finally {
					StaticHibernateUtil.closeSession();
				}
			}
		if (errorList.size() > 0)
			throw new BatchJobException(SchedulerConstants.FAILURE, errorList);
	}
	
	private void handleHolidayApplication(HolidayBO holiday) throws Exception 
	{
		HolidayUtils.rescheduleLoanRepaymentDates(holiday);
		
StaticHibernateUtil.commitTransaction();
		
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

