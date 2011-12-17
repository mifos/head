package org.mifos.platform.rest.controller.validation;

public class ParamValidationException extends Exception {
    public ParamValidationException(String msg) {
        super(msg);
    }

    private static final long serialVersionUID = 1L;
}
