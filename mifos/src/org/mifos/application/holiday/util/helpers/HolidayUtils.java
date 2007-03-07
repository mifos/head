package org.mifos.application.holiday.util.helpers;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.mifos.application.accounts.business.AccountActionDateEntity;
import org.mifos.application.accounts.business.AccountBO;
import org.mifos.application.accounts.business.service.AccountBusinessService;
import org.mifos.application.accounts.loan.business.LoanBO;
import org.mifos.application.accounts.loan.business.LoanScheduleEntity;
import org.mifos.application.accounts.loan.business.service.LoanBusinessService;
import org.mifos.application.accounts.savings.business.SavingsBO;
import org.mifos.application.accounts.savings.business.SavingsScheduleEntity;
import org.mifos.application.accounts.savings.business.service.SavingsBusinessService;
import org.mifos.application.holiday.business.HolidayBO;
import org.mifos.application.holiday.business.service.HolidayBusinessService;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.meeting.business.WeekDaysEntity;
import org.mifos.application.meeting.business.service.MeetingBusinessService;
import org.mifos.application.meeting.exceptions.MeetingException;
import org.mifos.application.meeting.util.helpers.WeekDay;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.util.helpers.DateUtils;

public class HolidayUtils {

//	 Holiday Checking helpers methods
	//static boolean isWorkingDay(WeekDaysEntity day) {
	public static boolean isWorkingDay(Calendar day) throws RuntimeException{
		//		Days of the week
	    //final String[] DAYS_OF_WEEK =
	    //   { "Sunday","Monday","Tuesday","Wednesday","Thursday","Friday","Saturday" };
		MeetingBusinessService meetingService = new MeetingBusinessService();
		List<WeekDaysEntity> weekDaysList = null;
		
		try {
			weekDaysList = meetingService.getWorkingDays((short)1);
		}
		catch (ServiceException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			throw new RuntimeException(e);
		}		
		
		int dayOfWeek = day.get(Calendar.DAY_OF_WEEK);
		
		for (WeekDaysEntity weekDaysEntity : weekDaysList) {		
			
			if ( WeekDay.getWeekDay(Short.valueOf(dayOfWeek+"")).name().equalsIgnoreCase(weekDaysEntity.getName()) &&
			     weekDaysEntity.isWorkingDay()) {			
				return true;
			}
		}
		
		return false;
	}

	public static HolidayBO inHoliday(Calendar pday) throws RuntimeException{	
		
		Calendar day = Calendar.getInstance();
		day.setTimeInMillis(0);
		day.set(pday.get(Calendar.YEAR), pday.get(Calendar.MONTH), pday.get(Calendar.DAY_OF_MONTH));
		
		HolidayBusinessService holidayService =  new HolidayBusinessService();
		List<HolidayBO> holidayList = null;
		try {			
			holidayList = holidayService.getHolidays(Calendar.getInstance().get(Calendar.YEAR), 1);			
			holidayList.addAll(holidayService.getHolidays(Calendar.getInstance().get(Calendar.YEAR)+1, 1));
			for (HolidayBO holidayEntity : holidayList) {				
				if((DateUtils.getDateWithoutTimeStamp(day.getTimeInMillis())
						.compareTo(DateUtils.getDateWithoutTimeStamp(holidayEntity.getHolidayFromDate().getTime())) >= 0 ) &&
					( (DateUtils.getDateWithoutTimeStamp(day.getTimeInMillis())
							.compareTo(DateUtils.getDateWithoutTimeStamp(holidayEntity.getHolidayThruDate().getTime())) <= 0 ) ||				    
					( holidayEntity.getHolidayThruDate() == null && DateUtils.getDateWithoutTimeStamp(day.getTimeInMillis())
							.compareTo(DateUtils.getDateWithoutTimeStamp(holidayEntity.getHolidayThruDate().getTime())) == 0 ) ) ){
					
					return holidayEntity;
				}
			}
			
		}
		catch (ServiceException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			throw new RuntimeException(e);
		}
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
				
				LoanBO loan = new LoanBusinessService().getAccount(loanScheduleEntity.getAccount().getAccountId());
				MeetingBO loadedMeeting = loan.getLoanMeeting();
				
				Date adjustedDate = HolidayUtils.adjustDate(DateUtils.getCalendar(loanScheduleEntity.getActionDate()), 
						loadedMeeting).getTime();
				
				loanScheduleEntity.setActionDate(new java.sql.Date(adjustedDate.getTime()));
			}
		}
		catch (ServiceException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			throw new RuntimeException(e);
		}
		catch (MeetingException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	public static void rescheduleSavingDates(HolidayBO holiday) throws RuntimeException{
		try {
			List<SavingsScheduleEntity> savingSchedulsList = new HolidayBusinessService()
																.getAllSavingSchedule(holiday);
			for (SavingsScheduleEntity savingScheduleEntity : savingSchedulsList) {
				
				SavingsBO saving = (SavingsBO) new AccountBusinessService()
								.getAccount(savingScheduleEntity.getAccount().getAccountId());
				
				//new SavingsBusinessService().getAccount(savingScheduleEntity.getAccount().getAccountId());
				MeetingBO loadedMeeting = saving.getCustomer().getCustomerMeeting().getMeeting();
				//TimePerForInstcalc();//LoanMeeting();
				
				Date adjustedDate = HolidayUtils.adjustDate(DateUtils.getCalendar(savingScheduleEntity.getActionDate()), 
						loadedMeeting).getTime();
				
				savingScheduleEntity.setActionDate(new java.sql.Date(adjustedDate.getTime()));
			}
		}
		catch (ServiceException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			throw new RuntimeException(e);
		}
		catch (MeetingException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

}
