package org.mifos.clientportfolio.loan.service;

public class MonthlyOnWeekOfMonthSchedule implements RecurringSchedule {

    private final Integer recursEvery;
    private final Integer weekOfMonth;
    private final Integer dayOfWeek;

    public MonthlyOnWeekOfMonthSchedule(Integer recursEvery, Integer weekOfMonth, Integer dayOfWeek) {
        this.recursEvery = recursEvery;
        this.weekOfMonth = weekOfMonth;
        this.dayOfWeek = dayOfWeek;
    }

    @Override
    public boolean isWeekly() {
        return false;
    }

    @Override
    public boolean isMonthly() {
        return true;
    }

    @Override
    public boolean isMonthlyOnDayOfMonth() {
        return false;
    }

    @Override
    public boolean isMonthlyOnWeekAndDayOfMonth() {
        return true;
    }

    @Override
    public Integer getEvery() {
        return this.recursEvery;
    }

    @Override
    public Integer getDay() {
        return this.dayOfWeek;
    }

    @Override
    public Integer getWeek() {
        return this.weekOfMonth;
    }

	@Override
	public boolean isDaily() {
		return false;
	}
}