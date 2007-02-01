package org.mifos.application.holiday.util.helpers;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.mifos.application.accounts.loan.business.LoanBO;
import org.mifos.application.accounts.loan.business.LoanScheduleEntity;
import org.mifos.application.accounts.loan.business.service.LoanBusinessService;
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

	public static boolean isWorkingDay(Calendar day) {
		MeetingBusinessService meetingService = new MeetingBusinessService();
		List<WeekDaysEntity> weekDaysList = null;
		
		try {
			weekDaysList = meetingService.getWorkingDays((short)1);
		}
		catch (ServiceException e) {
			e.printStackTrace();
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

	public static HolidayBO inHoliday(Calendar pday) {	
		
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
			e.printStackTrace();
		}
		return null;
	}

	public static Calendar adjustDate(Calendar day, MeetingBO meeting) throws MeetingException {//throws Exception{
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
	
	public static void rescheduleLoanRepaymentDates(HolidayBO holiday) {
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
			e.printStackTrace();
		}
		catch (MeetingException e) {
			e.printStackTrace();
		}
	}

}
