package org.mifos.platform.rest.controller;

public class RESTAPIHelper {

    public static class ErrorMessage {
        public static final String INVALID_AMOUNT = "please specify correct amount";
        public static final String NON_NEGATIVE_AMOUNT = "amount must be grater than 0";
        public static final String NOT_ACTIVE_ACCOUNT = "account is not in active state.";
        public static final String INVALID_NOTE = "note is not specified";
        public static final String INVALID_FEE_ID = "invalid fee Id";
        public static final String INVALID_DATE_STRING = "string is not valid date";
        public static final String FUTURE_DATE = "Date can not be a future date.";
        public static final String INVALID_PAYMENT_TYPE_ID = "invalid payment type Id";
        public static final String INVALID_PRODUCT_ID = "invalid product Id";
        public static final String INVALID_GLOABAL_CUSTOMER_NUM = "invalid global customer number";
    }

}
