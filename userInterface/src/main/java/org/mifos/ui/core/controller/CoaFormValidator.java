package org.mifos.ui.core.controller;

import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

public class CoaFormValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return CoaFormBean.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        CoaFormBean formBean = (CoaFormBean) target;
        checkIfEmpty(formBean.getCoaName(), "coa.emptyCoaName", errors);
        checkIfEmpty(formBean.getGlCode(), "coa.emptyGlCode", errors);
    }

    private void checkIfEmpty(String value, String errorCode, Errors errors) {
        if (value == null ||  !StringUtils.hasText(value)) {
            errors.reject(errorCode);
        }
    }
}
