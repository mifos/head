package org.mifos.application.holiday.util.helpers;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.mifos.application.accounts.business.service.AccountBusinessService;
import org.mifos.application.accounts.loan.business.LoanBO;
import org.mifos.application.accounts.loan.business.LoanScheduleEntity;
import org.mifos.application.accounts.loan.business.service.LoanBusinessService;
import org.mifos.application.accounts.savings.business.SavingsBO;
import org.mifos.application.accounts.savings.business.SavingsScheduleEntity;
import org.mifos.application.holiday.business.HolidayBO;
import org.mifos.application.holiday.business.service.HolidayBusinessService;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.meeting.exceptions.MeetingException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.config.FiscalCalendarRules;

public class HolidayUtils {
//	public static long inHolidayTotalDuration = 0;
//	public static long isWorkingDayTotalDuration = 0;
//	public static long adjust4onedate = 0;
//	
//	public static long isWorkingDayDuration = 0;
//	public static long inHolidayDuration = 0;
//	
//	public static File file;
//	public static FileWriter fileWriter;
//	public static BufferedWriter bufferedWriter;
	
	
	//Holiday Checking helpers methods	
	public static boolean isWorkingDay(Calendar day) throws RuntimeException{
		return FiscalCalendarRules.isWorkingDay(day);
		
	}

	public static HolidayBO inHoliday(Calendar pday) throws RuntimeException{	
		//long startTime = System.currentTimeMillis();
		
		Calendar day = Calendar.getInstance();
		day.setTimeInMillis(0);
		day.set(pday.get(Calendar.YEAR), pday.get(Calendar.MONTH), pday.get(Calendar.DAY_OF_MONTH));

		HolidayBusinessService holidayService = new HolidayBusinessService();
		try {			
			List<HolidayBO> holidayList = 
				holidayService.getHolidays(
						Calendar.getInstance().get(Calendar.YEAR), 1);			
			holidayList.addAll(holidayService.getHolidays(Calendar.getInstance().get(Calendar.YEAR)+1, 1));
			for (HolidayBO holidayEntity : holidayList) {				
				if((DateUtils.getDateWithoutTimeStamp(day.getTimeInMillis())
						.compareTo(DateUtils.getDateWithoutTimeStamp(holidayEntity.getHolidayFromDate().getTime())) >= 0 ) &&
						( (DateUtils.getDateWithoutTimeStamp(day.getTimeInMillis())
								.compareTo(DateUtils.getDateWithoutTimeStamp(holidayEntity.getHolidayThruDate().getTime())) <= 0 ) ||				    
								( holidayEntity.getHolidayThruDate() == null && DateUtils.getDateWithoutTimeStamp(day.getTimeInMillis())
										.compareTo(DateUtils.getDateWithoutTimeStamp(holidayEntity.getHolidayThruDate().getTime())) == 0 ) ) ){
					
					//long endTime = System.currentTimeMillis();
					//long runTime = endTime - startTime;
					//inHolidayDuration += runTime;
					return holidayEntity;
				}
			}
		}
		catch (ServiceException e) {
			throw new RuntimeException(e);
		}
		
		//long endTime = System.currentTimeMillis();;
		//long runTime = endTime - startTime;
		//inHolidayDuration += runTime;
		return null;
	}

	public static Calendar adjustDate(Calendar day, MeetingBO meeting) throws MeetingException {
		Calendar adjustedDate = day;
		if(!HolidayUtils.isWorkingDay(adjustedDate)) {
			adjustedDate.add(Calendar.DATE, 1);
			adjustedDate = adjustDate(adjustedDate, meeting);
		} else {
			HolidayBO holiday = HolidayUtils.inHoliday(adjustedDate);
			if(holiday == null || holiday.getRepaymentRuleId().equals(RepaymentRuleTypes.SAME_DAY.getValue())) {
				return adjustedDate;
			} else if(holiday.getRepaymentRuleId().equals(RepaymentRuleTypes.NEXT_WORKING_DAY.getValue())) {
				adjustedDate.add(Calendar.DATE, 1);
				adjustedDate = adjustDate(adjustedDate, meeting);
			} else if(holiday.getRepaymentRuleId().equals(RepaymentRuleTypes.NEXT_MEETING_OR_REPAYMENT.getValue())) {
				adjustedDate = DateUtils.getCalendar( meeting.getNextScheduleDateAfterRecurrenceWithoutAdjustment(adjustedDate.getTime()) );
				adjustedDate = adjustDate(adjustedDate, meeting);
			}
		}
		return adjustedDate;
	}
	
	public static void rescheduleLoanRepaymentDates(HolidayBO holiday) throws RuntimeException{
		try {
			List<LoanScheduleEntity> loanSchedulsList = new HolidayBusinessService().getAllLoanSchedule(holiday);
			for (LoanScheduleEntity loanScheduleEntity : loanSchedulsList) {
				//long startTime = System.currentTimeMillis();
				
				LoanBO loan = new LoanBusinessService().getAccount(loanScheduleEntity.getAccount().getAccountId());
				MeetingBO loadedMeeting = loan.getLoanMeeting();			
				 
				//isWorkingDayDuration = 0;
				//inHolidayDuration = 0;				
				//long adjustDateStartTime = System.currentTimeMillis();
				
				Date adjustedDate = HolidayUtils.adjustDate(DateUtils.getCalendar(loanScheduleEntity.getActionDate()), 
						loadedMeeting).getTime();
				
				//long adjustDateStopTime = System.currentTimeMillis();
				//long adjustDateRunTime = adjustDateStopTime - adjustDateStartTime;
								
				//logTiming(isWorkingDayDuration, 0, 0);
				//logTiming(inHolidayDuration, 1, 0);
				//logTiming(adjustDateRunTime, 2, 0);
				
				loanScheduleEntity.setActionDate(new java.sql.Date(adjustedDate.getTime()));
				
				//long endTime = System.currentTimeMillis();
				//long runTime = endTime - startTime;
				//logTiming(runTime, 3, 0);
			}
		}
		catch (ServiceException e) {
			throw new RuntimeException(e);
		}
		catch (MeetingException e) {
			throw new RuntimeException(e);
		}
	}

	public static void rescheduleSavingDates(HolidayBO holiday) throws RuntimeException{
		try {
			List<SavingsScheduleEntity> savingSchedulsList = new HolidayBusinessService()
																.getAllSavingSchedule(holiday);
			for (SavingsScheduleEntity savingScheduleEntity : savingSchedulsList) {
				//long startTime = System.currentTimeMillis();
				
				SavingsBO saving = (SavingsBO) new AccountBusinessService()
								.getAccount(savingScheduleEntity.getAccount().getAccountId());
				
				//new SavingsBusinessService().getAccount(savingScheduleEntity.getAccount().getAccountId());
				MeetingBO loadedMeeting = saving.getCustomer().getCustomerMeeting().getMeeting();
				
				//isWorkingDayDuration = 0;
				//inHolidayDuration = 0;				
				//long adjustDateStartTime = System.currentTimeMillis();
				
				Date adjustedDate = HolidayUtils.adjustDate(DateUtils.getCalendar(savingScheduleEntity.getActionDate()), 
						loadedMeeting).getTime();
				
				//long adjustDateStopTime = System.currentTimeMillis();
				//long adjustDateRunTime = adjustDateStopTime - adjustDateStartTime;
				
				//logTiming(isWorkingDayDuration, 0, 1);
				//logTiming(inHolidayDuration, 1, 1);
				//logTiming(adjustDateRunTime, 2, 1);	
				
				savingScheduleEntity.setActionDate(new java.sql.Date(adjustedDate.getTime()));
				
				//long endTime = System.currentTimeMillis();
				//long runTime = endTime - startTime;
				//logTiming(runTime, 3, 1);
			}
		}
		catch (ServiceException e) {
			throw new RuntimeException(e);
		}
		catch (MeetingException e) {
			throw new RuntimeException(e);
		}
	}
	
	
	/*
	public static void logTiming(long ms,int offset,int witchOne)
	{
		try
		{
			if(file == null){file = new File("~/code.csv");}
			if(fileWriter == null){fileWriter = new FileWriter(file,true);}
			if(bufferedWriter == null){bufferedWriter = new BufferedWriter(fileWriter);}
			switch(offset)
			{
			case 0:
				bufferedWriter.write(String.valueOf(witchOne) + ",");
			case 1:
			case 2:
				bufferedWriter.write(String.valueOf(ms) + ",");
				break;
			case 3:
				bufferedWriter.write(String.valueOf(ms) + "\n");
			}
			bufferedWriter.flush();
		}
		catch(Exception ex)
		{ex.printStackTrace();}
	}
	*/
}