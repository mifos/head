package org.mifos.test.acceptance.loan;

@SuppressWarnings("PMD")
public class RedoLoanScheduleData {
    public static final String[][] VARIABLE_LOAN_PAYMENT_ONE = {{"12-Oct-2010","","","",""},
                {"100","","","",""}};
    public static final String[][] VARIABLE_LOAN_SCHEDULE_ONE = {{"Installments paid", "", "", "", "", ""},
                {"1", "12-Oct-2010", "12-Oct-2010", "0.0", "0.0", "100.0", "100.0"},
                {"Installments due", "", "", "", "", ""},
                {"1", "12-Oct-2010", "-", "0.0", "0.0", "100.0", "100.0"},
                {"2", "19-Oct-2010", "-", "249.5", "4.2", "0.0", "353.6"},
                {"3", "26-Oct-2010", "-", "249.5", "4.2", "0.0", "353.6"},
                {"4", "02-Nov-2010", "-", "250.7", "2.3", "0.0", "353.0"},
                {"5", "09-Nov-2010", "-", "252.1", "1.9", "0.0", "354.0"}};
    public static final String[][] VARIABLE_LOAN_RUNNING_BALANCE_ONE = {{"","","",""}};
}
