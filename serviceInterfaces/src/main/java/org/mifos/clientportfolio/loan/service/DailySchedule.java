package org.mifos.clientportfolio.loan.service;

public class DailySchedule implements RecurringSchedule {
	
	private final Integer recursEvery;
	
	public DailySchedule(Integer recursEvery) {
        this.recursEvery = recursEvery;
    }
	
	@Override
	public boolean isWeekly() {
		return false;
	}

	@Override
	public boolean isMonthly() {
		return false;
	}

	@Override
	public boolean isMonthlyOnDayOfMonth() {
		return false;
	}

	@Override
	public boolean isMonthlyOnWeekAndDayOfMonth() {
		return false;
	}

	@Override
	public Integer getEvery() {
		return this.recursEvery;
	}

	@Override
	public Integer getDay() {
		return null;
	}

	@Override
	public Integer getWeek() {
		return null;
	}

	@Override
	public boolean isDaily() {
		return true;
	}

}
