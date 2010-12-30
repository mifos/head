package org.mifos.test.acceptance.loan;

@SuppressWarnings("PMD")
public class OriginalScheduleData {

    public static final String[][] FLAT_LOAN_SCHEDULE = {{"Installment", "DueDate", "Principal", "Interest", "Fee", "Total"},
            {"1","12-Oct-2011","200.2","3.8","100.0","304.0"},
            {"2","18-Oct-2011","200.2","3.8","100.0","304.0"},
            {"3","25-Oct-2011","200.2","3.8","100.0","304.0"},
            {"4","01-Nov-2011","200.2","3.8","100.0","304.0"},
            {"5","08-Nov-2011","199.2","4.8","100.0","304.0"}};

    public static final String[][] VARIABLE_LOAN_LATE_DISBURSAL_SCHEDULE = {};
    public static final String[][] VARIABLE_LOAN_EARLY_DISBURSAL_SCHEDULE = {};
    public static final String[][] DEC_BAL_INT_RECALC_LOAN_EARLY_DISBURSAL_SCHEDULE = {};
}
