package org.mifos.application.holiday.util.helpers;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.mifos.application.holiday.business.HolidayBO;
import org.mifos.application.holiday.business.HolidayPK;
import org.mifos.application.holiday.exceptions.HolidayException;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.meeting.business.WeekDaysEntity;
import org.mifos.application.meeting.exceptions.MeetingException;
import org.mifos.application.meeting.persistence.MeetingPersistence;
import org.mifos.application.meeting.util.helpers.MeetingType;
import org.mifos.application.meeting.util.helpers.RankType;
import org.mifos.application.meeting.util.helpers.WeekDay;
import org.mifos.framework.MifosTestCase;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class TestHolidayUtils extends MifosTestCase {

	private MeetingBO meeting;
	private Short recurAfter = Short.valueOf("1"); // recur After January
	private Date startDate;
	
	private String[] inputDates    = 
			{"21/08/2007",                         "21/08/2007",
			 "21/08/2007", 						   "26/08/2007",
			 "23/08/2007"}, 
	                 outputDates   = 
	        {"21/08/2007",                         "25/08/2007",
	         "27/08/2007", 						   "29/08/2007",
	         "08/09/2007"};
	private String[] holidyRanges  = 
	        {"20/08/2007 - 22/08/2007, 22/08/2007 - 24/08/2007", // [Holiday - Holiday] [Same Day - Same Day] case
             "20/08/2007 - 22/08/2007, 22/08/2007 - 24/08/2007", // [Holiday - Holiday] [Next Working Day - Next Working Day] case
             "20/08/2007 - 22/08/2007, 22/08/2007 - 25/08/2007",  // [Holiday - Holiday - Non-Working Day] [Next Working Day - Next Working Day] case
             "27/08/2007 - 28/08/2007",  // [Non-Working Day - Holiday] [Next Working Day - Next Working Day] case
             "20/08/2007 - 25/08/2007, 27/08/2007 - 01/09/2007" // [Holiday - Non-Working Day - Holiday] [Next Meeting - Next Meeting] case
			} ;
	private String[] rpaymentRules = 
	        {"1,1",                                "3,3",
			 "3,3",                                "3,3", 
			 "2,2"};
	private String[] meetings      = 
	        {"Monthly-18/08/2007", "Monthly-18/08/2007", 
			 "Monthly-18/08/2007", "Monthly-18/08/2007",
			 "Weekly-18/08/2007"};
	               
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		startDate = getDate("18/08/2007");
		meeting = createWeeklyMeeting(WeekDay.THURSDAY, recurAfter, startDate);
		
		/////////////////////
		List<WeekDaysEntity> weekDays = new MeetingPersistence().getWorkingDays(Short.valueOf("1"));
		for(WeekDaysEntity weekDay: weekDays){
			if(weekDay.getId().equals(Short.valueOf("1")) &&
					weekDay.isWorkingDay())
				weekDay.setWorkDay(Short.valueOf("0"));
			    weekDay.save();
		}
		/////////////////////
//		System.out.println("#\n#\n#\n#\nUPDATING values in Mysql database table!\n#\n#\n#\n#");
//		
//	    Connection con = null;
//	    String url = "jdbc:mysql://localhost:3306/";
//	    String db = "test";
//	    String driver = "com.mysql.jdbc.Driver";
//	    try{
//	      Class.forName(driver);
//	      con = DriverManager.getConnection(url+db,"root","mysql");
//	      try{
//	        Statement st = con.createStatement();
//	        int val = st.executeUpdate("UPDATE WEEK_DAYS_MASTER SET WORKING_DAY = 0 WHERE WEEK_DAYS_MASTER_ID = 1;");
//	        System.out.println("1 row affected");
//	        st.close();
//	        con.close();
//	      }
//	      catch (SQLException s){
//	        System.out.println("SQL statement is not executed!");
//	      }
//	    }
//	    catch (Exception e){
//	      e.printStackTrace();
//	    }
		/////////////////////
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		HibernateUtil.closeSession();
	}

	public void testNonWorkingDay() throws Exception {
		Date testDate = getDate("19/08/2007");
				
		Calendar adjustedCalendar = HolidayUtils.adjustDate(DateUtils.getCalendar(testDate), meeting);
		
		assertEquals(DateUtils.getDateWithoutTimeStamp(adjustedCalendar.getTimeInMillis()).getTime(), 
				DateUtils.getDateWithoutTimeStamp(getDate("20/08/2007").getTime()).getTime());

	}
	
	public void testSameDay() throws Exception {
				
		Date holidayStartDate = getDate("20/08/2007");
		Date holidayEndDate = getDate("22/08/2007");
		
		Date testDate = getDate("21/08/2007");
		
		// Create Holiday
		HolidayPK holidayPK = new HolidayPK((short)1, holidayStartDate);
		HolidayBO holidayEntity = new HolidayBO(holidayPK, holidayEndDate, "Same Day Holiday",
				(short) 1, (short) 1, "Same Day");// the last string has no effect.
		
		holidayEntity.save();
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		
		holidayEntity = (HolidayBO) TestObjectFactory.getObject(
				HolidayBO.class, holidayEntity.getHolidayPK());

		// assert that the holiday is created
		assertEquals("Same Day Holiday", holidayEntity.getHolidayName());		
		
		Calendar adjustedCalendar = HolidayUtils.adjustDate(DateUtils.getCalendar(testDate), meeting);
		
		assertEquals(DateUtils.getDateWithoutTimeStamp(adjustedCalendar.getTimeInMillis()).getTime(), 
				DateUtils.getDateWithoutTimeStamp(testDate.getTime()).getTime());

		// Clean up the Holiday that was created
		TestObjectFactory.cleanUp(holidayEntity);
	}
	
	public void testNexteMeetingOrRepayment() throws Exception {
		
		Date holidayStartDate = getDate("20/08/2007");
		Date holidayEndDate = getDate("22/08/2007");
		
		Date testDate = getDate("21/08/2007");
		
		// Create Holiday
		HolidayPK holidayPK = new HolidayPK((short)1, holidayStartDate);
		HolidayBO holidayEntity = new HolidayBO(holidayPK, holidayEndDate, "Next Meeting Or Repayment Holiday",
				(short) 1, (short) 2, "Next MeetingOrRepayment");// the last string has no effect.
		
		holidayEntity.save();
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		
		holidayEntity = (HolidayBO) TestObjectFactory.getObject(
				HolidayBO.class, holidayEntity.getHolidayPK());

		// assert that the holiday is created
		assertEquals("Next Meeting Or Repayment Holiday", holidayEntity.getHolidayName());		
		
		Calendar adjustedCalendar = HolidayUtils.adjustDate(DateUtils.getCalendar(testDate), meeting);
		
		assertEquals(DateUtils.getDateWithoutTimeStamp(adjustedCalendar.getTimeInMillis()).getTime(), 
				DateUtils.getDateWithoutTimeStamp(getDate("25/08/2007").getTime()).getTime());

		// Clean up the Holiday that was created
		TestObjectFactory.cleanUp(holidayEntity);
	}
	
	public void testNextWorkingDay() throws Exception {
				
		Date holidayStartDate = getDate("20/08/2007");
		Date holidayEndDate = getDate("22/08/2007");
		
		Date testDate = getDate("21/08/2007");
		
		HolidayBO holidayEntity = createHoliday(holidayStartDate, holidayEndDate, Short.valueOf("3"), 
												"Next Working Day Holiday");
		
		holidayEntity = (HolidayBO) TestObjectFactory.getObject(
				HolidayBO.class, holidayEntity.getHolidayPK());

		// assert that the holiday is created
		assertEquals("Next Working Day Holiday", holidayEntity.getHolidayName());		
		
		Calendar adjustedCalendar = HolidayUtils.adjustDate(DateUtils.getCalendar(testDate), meeting);
		
		assertEquals(DateUtils.getDateWithoutTimeStamp(adjustedCalendar.getTimeInMillis()).getTime(), 
				DateUtils.getDateWithoutTimeStamp(getDate("23/08/2007").getTime()).getTime());

		// Clean up the Holiday that was created
		TestObjectFactory.cleanUp(holidayEntity);
	}

    public void testHolidayCombinations() throws Exception {
		
    	for(int i=0; i<inputDates.length; i++) {
    		
    		Date inputDate = getDate(inputDates[i]);
    		Date outputDate = getDate(outputDates[i]);
    		
    		HolidayBO[] holidays = new HolidayBO[3]; 
    		
    		holidays = createHolidayCollection(holidyRanges[i],	rpaymentRules[i]);    		
    		    		
    		String[] meetingData = meetings[i].split("-");
    		startDate = getDate(meetingData[1]);
    		
    		if(meetingData[0].equalsIgnoreCase("Weekly")) {
    			meeting = createWeeklyMeeting(WeekDay.THURSDAY, recurAfter, startDate);
    		} else if(meetingData[0].equalsIgnoreCase("Monthly")) {
    			meeting = createMonthlyMeetingOnWeekDay(WeekDay.THURSDAY, RankType.FIRST, recurAfter, startDate);
    		}
    		/////////////////////////////////////////////////////////
    		
    		Calendar adjustedCalendar = HolidayUtils.adjustDate(DateUtils.getCalendar(inputDate), meeting);
    		
    		assertEquals(DateUtils.getDateWithoutTimeStamp(adjustedCalendar.getTimeInMillis()).getTime(), 
    				DateUtils.getDateWithoutTimeStamp(outputDate.getTime()).getTime());
    		
    		// Clean up the Holiday that was created
    		for(int j=0; j<holidays.length; j++)
    			TestObjectFactory.cleanUp(holidays[j]);
    	}
	}
	
    /*
    public void testRescheduleLoanRepaymentDates() throws Exception{
    	
    	Date holidayStartDate = getDate("21/03/2007"), 
    	     holidayEndDate   = getDate("31/03/2007");
    	
    	HolidayBO holiday = createHoliday(holidayStartDate, holidayEndDate, Short.valueOf("3"), 
									"testRescheduleLoanRepaymentDates");
    	
    	holiday = (HolidayBO) TestObjectFactory.getObject(
							HolidayBO.class, holiday.getHolidayPK());

		// assert that the holiday is created
		assertEquals("testRescheduleLoanRepaymentDates", holiday.getHolidayName());
		///////////////////////////////////////////////////////////////
		// Create the Schedule to be used in testing
		new TestLoanBO().testDisbursalLoanRegeneteRepaymentSchedule();
		/////////////////////////////////////////////////////////////
		HolidayUtils.rescheduleLoanRepaymentDates(holiday);
		
		// Clean up the Holiday that was created
		TestObjectFactory.cleanUp(holiday);
    }
    */
    
	private HolidayBO[] createHolidayCollection(String holidayDateList, String ruleList) throws HolidayException, ParseException {
		
		Date holidayStartDate, holidayEndDate;
		String[] holidayRanges = holidayDateList.split(",");
		HolidayBO[] holidays = new HolidayBO[holidayRanges.length];
		String[] rules = ruleList.split(",");
		
		for(int i=0; i<holidayRanges.length; i++ ) {
			String[] holidayStartEnd = holidayRanges[i].split("-");
			
			holidayStartDate = getDate(holidayStartEnd[0]);
			holidayEndDate = getDate(holidayStartEnd[1]);
			
			
			holidays[i] = createHoliday(holidayStartDate, holidayEndDate, Short.valueOf(rules[i]), 
									"testHolidayCombination_1");
		
			holidays[i] = (HolidayBO) TestObjectFactory.getObject(
							HolidayBO.class, holidays[i].getHolidayPK());

			// assert that the holiday is created
			assertEquals("testHolidayCombination_1", holidays[i].getHolidayName());			
		}
		
		return holidays;
	}

	private HolidayBO createHoliday(Date holidayStartDate, Date holidayEndDate, 
									Short repaymentRule, String holidayName) throws HolidayException {
		// Create Holiday
		HolidayPK holidayPK = new HolidayPK((short)1, holidayStartDate);
		HolidayBO holidayEntity = new HolidayBO(holidayPK, holidayEndDate, holidayName,
				(short) 1, repaymentRule, "repaymentRule");// the last string has no effect.
		
		holidayEntity.save();
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		return holidayEntity;
	}

	private MeetingBO createWeeklyMeeting(WeekDay weekDay, Short recurAfer, Date startDate) throws MeetingException{
		return new MeetingBO(weekDay, recurAfer, startDate, MeetingType.CUSTOMER_MEETING, "MeetingPlace");
	}
	private MeetingBO createMonthlyMeetingOnWeekDay(WeekDay weekDay, RankType rank, Short recurAfer, Date startDate) throws MeetingException{
		return new MeetingBO(weekDay, rank, recurAfer, startDate, MeetingType.CUSTOMER_MEETING, "MeetingPlace");
	}
}
