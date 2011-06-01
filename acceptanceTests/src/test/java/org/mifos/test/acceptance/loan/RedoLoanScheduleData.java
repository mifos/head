package org.mifos.test.acceptance.loan;

@SuppressWarnings("PMD")
public class RedoLoanScheduleData {
    public static final String[][] VARIABLE_LOAN_SCHEDULE_ONE = {{"Installments paid", "", "", "", "", ""},
            {"1", "12-Oct-2010", "12-Oct-2010", "202.3", "0.7", "0.0", "203.0"},
            {"2", "19-Oct-2010", "19-Oct-2010", "199.3", "3.7", "0.0", "203.0"},
            {"Future Installments", "", "", "", "", ""}, //future installment
            {"3", "26-Oct-2010", "-", "200.2", "2.8", "0.0", "203.0"},
            {"4", "02-Nov-2010", "-", "201.2", "1.8", "0.0", "203.0"},
            {"5", "09-Nov-2010", "-", "196.9", "0.9", "0.0", "197.8"}};
    public static final String[][] VARIABLE_LOAN_RUNNING_BALANCE_ONE = {{"797.7", "9.2", "0.0", "806.8"},
            {"598.3", "5.5", "0.0", "603.8"}};

    public static final String[][] VARIABLE_LOAN_PAYMENT_2 = {{"19-Oct-2010", "609"}, {"", ""}, {"", ""}, {"", ""}, {"", ""}};
    public static final String[][] VARIABLE_LOAN_SCHEDULE_2 = {{"Installments paid", "", "", "", "", ""},
            {"1", "12-Oct-2010", "19-Oct-2010", "202.3", "0.7", "0.0", "203.0"},
            {"2", "19-Oct-2010", "19-Oct-2010", "199.3", "3.7", "0.0", "203.0"},
            {"3", "26-Oct-2010", "19-Oct-2010", "200.2", "2.8", "0.0", "203.0"},
            {"Future Installments", "", "", "", "", ""}, //future installment
            {"4", "02-Nov-2010", "-", "201.2", "1.8", "0.0", "203.0"},
            {"5", "09-Nov-2010", "-", "196.9", "0.9", "0.0", "197.8"}};
    public static final String[][] VARIABLE_LOAN_RUNNING_BALANCE_2 = {{"797.7", "9.2", "0.0", "806.8"},
            {"598.3", "5.5", "0.0", "603.8"},
            {"398.1", "2.7", "0.0", "400.8"}};

    public static final String[][] DECLINING_PRINCIPAL_LATE_PAYMENT_1 = {{"19/10/10", "410.0"}, {"22/10/10", "0"}, {"29/10/10", "0"}, {"05/11/10", "0"}, {"12/11/10", "0"}};
    public static final String[][] DECLINING_PRINCIPAL_LATE_SCHEDULE_1 = {{"Installments paid", "", "", "", "", ""},
            {"1", "12-Oct-2010", "19-Oct-2010", "202.3", "0.7", "100.0", "303.0"},
            {"2", "19-Oct-2010", "19-Oct-2010", "2.4", "4.6", "100.0", "107.0"},
            {"Installments due", "", "", "", "", ""},
            {"2", "19-Oct-2010", "-", "196.9", "0.0", "0.0", "196.9"},
            {"Future Installments", "", "", "", "", ""}, //future installment
            {"3", "26-Oct-2010", "-", "200.2", "3.5", "100.0", "303.8"},
            {"4", "02-Nov-2010", "-", "201.2", "1.8", "100.0", "303.0"},
            {"5", "09-Nov-2010", "-", "196.9", "0.9", "100.0", "297.8"}};
    public static final String[][] DECLINING_PRINCIPAL_LATE_BALANCE_1 = {{"797.7", "10.9", "400.0", "1208.5"},
            {"795.3", "6.3", "300.0", "1101.5"}};

    public static final String[][] DECLINING_PRINCIPAL_EARLY_PAYMENT_1 = {{"19-Oct-2010", "303"}, {"25-Oct-2010", "503.9"}, {"", ""}, {"", ""}, {"", ""}};
    public static final String[][] DECLINING_PRINCIPAL_EARLY_SCHEDULE_1 = {{"Installments paid", "", "", "", "", ""},
            {"1", "12-Oct-2010", "19-Oct-2010", "202.3", "0.7", "100.0", "303.0"},
            {"2", "19-Oct-2010", "25-Oct-2010", "199.3", "4.6", "100.0", "303.9"},
            {"3", "26-Oct-2010", "25-Oct-2010", "196.8", "3.2", "0.0", "200.0"},
            {"Future Installments", "", "", "", "", ""}, //future installment
            {"3", "26-Oct-2010", "-", "3.4", "0.3", "100.0", "103.7"},
            {"4", "02-Nov-2010", "-", "201.2", "1.8", "100.0", "303.0"},
            {"5", "09-Nov-2010", "-", "196.9", "0.9", "100.0", "297.8"}};
    public static final String[][] DECLINING_PRINCIPAL_EARLY_BALANCE_1 = {{"797.7", "10.8", "400.0", "1208.4"},
            {"598.3", "6.1", "300.0", "904.5"},  //4.4 -> 6.2
            {"401.5", "3.0", "300.0", "704.5"}}; //2.2 -> 3.0

    public static final String[][] DECLINING_PRINCIPAL_ENTIRE_PAYMENT_1 = {{"12-Oct-2010", "303"}, {"19-Oct-2010", "303"}, {"26-Oct-2010", "303"}, {"02-Nov-2010", "303"}, {"12-Nov-2010", "297.8"}};
    public static final String[][] DECLINING_PRINCIPAL_ENTIRE_SCHEDULE_1 = {{"Installments paid", "", "", "", "", ""},
            {"1", "12-Oct-2010", "12-Oct-2010", "202.3", "0.7", "100.0", "303.0"},
            {"2", "19-Oct-2010", "19-Oct-2010", "199.3", "3.7", "100.0", "303.0"},
            {"3", "26-Oct-2010", "26-Oct-2010", "200.2", "2.8", "100.0", "303.0"},
            {"4", "02-Nov-2010", "02-Nov-2010", "201.2", "1.8", "100.0", "303.0"},
            {"5", "09-Nov-2010", "12-Nov-2010", "196.5", "1.3", "100.0", "297.8"}};
    public static final String[][] DECLINING_PRINCIPAL_ENTIRE_BALANCE_1 = {{"797.7", "9.6", "400.0", "1207.2"},
            {"598.3", "5.9", "300.0", "904.2"},  //4.4 -> 6.2
            {"398.1", "3.1", "200.0", "601.2"},  //4.4 -> 6.2
            {"196.9", "1.3", "100.0", "298.2"},  //4.4 -> 6.2
            {"0.4", "0.0", "0.0", "0.4"}}; //2.2 -> 3.0

}
