package org.mifos.application.accounts.savings.util.helpers;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.meeting.util.resources.MeetingConstants;
import org.mifos.framework.util.helpers.TestObjectFactory;

import junit.framework.TestCase;

public class TestSavingsHelper extends TestCase{
	SavingsHelper helper = new SavingsHelper(); 
	
	public void testCalculateDays()throws Exception{
			SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
			Date fromDate = df.parse("01/01/2006");
			Date toDate = df.parse("08/01/2006");
			int days = helper.calculateDays(fromDate, toDate);
			assertEquals("7",String.valueOf(days));
			
			//check for month feb
			fromDate = df.parse("25/02/2006");
			toDate = df.parse("05/03/2006");
			days = helper.calculateDays(fromDate, toDate);
			assertEquals("8",String.valueOf(days));
			
			//check for month feb in leap year
			fromDate = df.parse("25/02/2004");
			toDate = df.parse("05/03/2004");
			days = helper.calculateDays(fromDate, toDate);
			assertEquals("9",String.valueOf(days));
			
			fromDate = df.parse("25/01/2006");
			toDate = df.parse("05/02/2006");
			days = helper.calculateDays(fromDate, toDate);
			assertEquals("11",String.valueOf(days));
			
			//long differnce in days
			fromDate = df.parse("05/01/2006");
			toDate = df.parse("06/07/2006");
			days = helper.calculateDays(fromDate, toDate);
			assertEquals("182",String.valueOf(days));
	}
	
	public void testGetNextInterestCalculationDateEveryMonth()throws Exception{
			SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
			Date date;
			Date resultDate;
			Date accountActivationDate = df.parse("05/04/2006");
			MeetingBO meeting = TestObjectFactory.getMeeting(MeetingConstants.MONTHLY,"1",MeetingConstants.INTEREST_CALC_FREQ);
			
			resultDate=helper.getNextScheduleDate(accountActivationDate,null, meeting);
			date = df.parse("01/05/2006");
			assertEquals(date,resultDate);
			
			resultDate=helper.getNextScheduleDate(accountActivationDate,date, meeting);
			date = df.parse("01/06/2006");
			assertEquals(date,resultDate);
			
			resultDate=helper.getNextScheduleDate(accountActivationDate,date, meeting);
			date = df.parse("01/07/2006");
			assertEquals(date,resultDate);
			
			resultDate=helper.getNextScheduleDate(accountActivationDate,date, meeting);
			date = df.parse("01/08/2006");
			assertEquals(date,resultDate);
			
			date = df.parse("01/11/2006");
			resultDate=helper.getNextScheduleDate(accountActivationDate,date, meeting);
			
			date = df.parse("01/12/2006");
			assertEquals(date,resultDate);
			
			resultDate=helper.getNextScheduleDate(accountActivationDate,date, meeting);
			date = df.parse("01/01/2007");
			assertEquals(date,resultDate);
			
			resultDate=helper.getNextScheduleDate(accountActivationDate,date, meeting);
			date = df.parse("01/02/2007");
			assertEquals(date,resultDate);
	}
	
	
	public void testGetNextInterestCalculationDateThreeMonths()throws Exception{
		SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
		Date date;
		Date resultDate;
		Date accountActivationDate = df.parse("25/01/2006");
		MeetingBO meeting = TestObjectFactory.getMeeting(MeetingConstants.MONTHLY,"3",MeetingConstants.INTEREST_CALC_FREQ);
		
		resultDate=helper.getNextScheduleDate(accountActivationDate,null, meeting);
		date = df.parse("01/04/2006");
		assertEquals(date,resultDate);
		
		resultDate=helper.getNextScheduleDate(accountActivationDate,date, meeting);
		date = df.parse("01/07/2006");
		assertEquals(date,resultDate);
		
		resultDate=helper.getNextScheduleDate(accountActivationDate,date, meeting);
		date = df.parse("01/10/2006");
		assertEquals(date,resultDate);
		
		resultDate=helper.getNextScheduleDate(accountActivationDate,date, meeting);
		date = df.parse("01/01/2007");
		assertEquals(date,resultDate);
		
		resultDate=helper.getNextScheduleDate(accountActivationDate,date, meeting);
		date = df.parse("01/04/2007");
		assertEquals(date,resultDate);
		
		resultDate=helper.getNextScheduleDate(accountActivationDate,date, meeting);
		date = df.parse("01/07/2007");
		assertEquals(date,resultDate);
	}
	
	public void testGetNextInterestPostingDateWithRecurOnEveryMonth()throws Exception{
		SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
		Date date;
		Date resultDate;
		Date accountActivationDate = df.parse("05/07/2006");
		MeetingBO meeting = TestObjectFactory.getMeeting(MeetingConstants.MONTHLY,"1",MeetingConstants.INTEREST_POST_FREQ);
		
		resultDate=helper.getNextScheduleDate(accountActivationDate,null, meeting);
		date = df.parse("31/07/2006");
		assertEquals(date,resultDate);

		resultDate=helper.getNextScheduleDate(accountActivationDate,date, meeting);
		date = df.parse("31/08/2006");
		assertEquals(date,resultDate);
		
		resultDate=helper.getNextScheduleDate(accountActivationDate,date, meeting);
		date = df.parse("30/09/2006");
		assertEquals(date,resultDate);

		resultDate=helper.getNextScheduleDate(accountActivationDate,date, meeting);
		date = df.parse("31/10/2006");
		assertEquals(date,resultDate);
		
		resultDate=helper.getNextScheduleDate(accountActivationDate,date, meeting);
		date = df.parse("30/11/2006");
		assertEquals(date,resultDate);

		resultDate=helper.getNextScheduleDate(accountActivationDate,date, meeting);
		date = df.parse("31/12/2006");
		assertEquals(date,resultDate);

		resultDate=helper.getNextScheduleDate(accountActivationDate,date, meeting);
		date = df.parse("31/01/2007");
		assertEquals(date,resultDate);
		
		resultDate=helper.getNextScheduleDate(accountActivationDate,date, meeting);
		date = df.parse("28/02/2007");
		assertEquals(date,resultDate);
	}
	
	public void testGetNextInterestPostingDateWithRecurOnEveryFourMonths()throws Exception{
		SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
		Date date;
		Date resultDate;
		Date accountActivationDate = df.parse("25/12/2007");
		MeetingBO meeting = TestObjectFactory.getMeeting(MeetingConstants.MONTHLY,"4",MeetingConstants.INTEREST_POST_FREQ);
		
		resultDate=helper.getNextScheduleDate(accountActivationDate,null, meeting);
		date = df.parse("31/01/2008");
		assertEquals(date,resultDate);
		
		resultDate=helper.getNextScheduleDate(accountActivationDate,date, meeting);
		date = df.parse("31/05/2008");
		assertEquals(date,resultDate);
	
		resultDate=helper.getNextScheduleDate(accountActivationDate,date, meeting);
		date = df.parse("30/09/2008");
		assertEquals(date,resultDate);
		
		resultDate=helper.getNextScheduleDate(accountActivationDate,date, meeting);
		date = df.parse("31/01/2009");
		assertEquals(date,resultDate);
	}
	
	public void testPrevIntCalcDateOnEveryMonth()throws Exception{
		SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
		Date date;
		Date resultDate;
		//Date accountActivationDate = df.parse("05/04/2006");
		MeetingBO meeting = TestObjectFactory.getMeeting(MeetingConstants.MONTHLY,"1",MeetingConstants.INTEREST_CALC_FREQ);
		
		resultDate=helper.getPrevScheduleDate(df.parse("01/04/2006"), df.parse("01/07/2006"), meeting);
		date = df.parse("01/06/2006");
		assertEquals(date,resultDate);
		
		resultDate=helper.getPrevScheduleDate(df.parse("01/04/2006"), df.parse("01/01/2007"), meeting);
		date = df.parse("01/12/2006");
		assertEquals(date,resultDate);
	}
	
	public void testPrevIntCalcDateOnEveryThreeMonths()throws Exception{
		SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
		Date date;
		Date resultDate;
		//Date accountActivationDate = df.parse("25/01/2006");
		MeetingBO meeting = TestObjectFactory.getMeeting(MeetingConstants.MONTHLY,"3",MeetingConstants.INTEREST_CALC_FREQ);
		
		resultDate=helper.getPrevScheduleDate(df.parse("01/04/2006"), df.parse("01/07/2006"), meeting);
		date = df.parse("01/04/2006");
		assertEquals(date,resultDate);
		
		resultDate=helper.getPrevScheduleDate(df.parse("01/04/2006"), df.parse("01/04/2006"), meeting);
		assertNull(resultDate);
	}

	public void testPrevInterestPostingDateWithRecurOnEveryMonth()throws Exception{
		SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
		Date date;
		Date resultDate;
		//	Date accountActivationDate = df.parse("05/07/2006");
		MeetingBO meeting = TestObjectFactory.getMeeting(MeetingConstants.MONTHLY,"1",MeetingConstants.INTEREST_POST_FREQ);

		resultDate=helper.getPrevScheduleDate(df.parse("01/07/2006"),df.parse("31/01/2007"), meeting);
		date = df.parse("31/12/2006");
		assertEquals(date,resultDate);
		
		resultDate=helper.getPrevScheduleDate(df.parse("01/07/2006"),df.parse("28/02/2007"), meeting);
		date = df.parse("31/01/2007");
		assertEquals(date,resultDate);
	}
	
	
	public void testPrevInterestPostingDateWithRecurOnEveryFourMonths()throws Exception{
		SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
		Date date;
		Date resultDate;
		//Date accountActivationDate = df.parse("25/12/2007");
		MeetingBO meeting = TestObjectFactory.getMeeting(MeetingConstants.MONTHLY,"4",MeetingConstants.INTEREST_POST_FREQ);
	
		resultDate=helper.getPrevScheduleDate(df.parse("01/07/2006"),df.parse("31/05/2008"), meeting);
		date = df.parse("31/01/2008");
		assertEquals(date,resultDate);
		
		resultDate=helper.getPrevScheduleDate(df.parse("01/07/2006"),df.parse("31/01/2009"), meeting);
		date = df.parse("30/09/2008");
		assertEquals(date,resultDate);
	}
}
