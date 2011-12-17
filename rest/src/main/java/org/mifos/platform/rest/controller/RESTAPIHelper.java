package org.mifos.platform.rest.controller;

public class RESTAPIHelper {

    public static class ErrorMessage {
        public static final String INVALID_AMOUNT = "please specify correct amount";
        public static final String NON_NEGATIVE_AMOUNT = "amount must be grater than 0";
        public static final String NOT_ACTIVE_ACCOUNT = "account is not in active state.";
        public static final String INVALID_NOTE = "note is not specified";
    }

}
