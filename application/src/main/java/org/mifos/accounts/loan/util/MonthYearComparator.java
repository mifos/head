package org.mifos.accounts.loan.util;

import java.util.Calendar;
import java.util.Date;

public class MonthYearComparator{

    public static boolean firstLessOrEqualSecond(Date firstDate, Date secondDate) {
        Calendar dueDate = Calendar.getInstance();
        dueDate.setTime(firstDate);

        Calendar cashflow = Calendar.getInstance();
        cashflow.setTime(secondDate);

        dueDate.set(Calendar.DAY_OF_MONTH, 1);

        return dueDate.compareTo(cashflow) <= 0;
    }

}
