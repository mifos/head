package org.mifos.clientportfolio.loan.service;

public interface RecurringSchedule {

    boolean isWeekly();
    
    boolean isMonthly();
    
    boolean isMonthlyOnDayOfMonth();
    
    boolean isMonthlyOnWeekAndDayOfMonth();
    
    Integer getEvery();
    
    Integer getDay();
    
    Integer getWeek();
    
    boolean isDaily();
}
