package org.mifos.clientportfolio.loan.service;

public interface RecurringSchedule {

    boolean isDaily();
    
    boolean isWeekly();
    
    boolean isMonthly();
    
    boolean isMonthlyOnDayOfMonth();
    
    boolean isMonthlyOnWeekAndDayOfMonth();
    
    Integer getEvery();
    
    Integer getDay();
    
    Integer getWeek();
}
