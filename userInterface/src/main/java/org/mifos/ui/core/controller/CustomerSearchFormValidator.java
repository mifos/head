package org.mifos.ui.core.controller;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

public class CustomerSearchFormValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return CustomerSearchFormBean.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        
        CustomerSearchFormBean customerSearch = (CustomerSearchFormBean) target;
        
        if ( customerSearch.getSearchString().trim().isEmpty() ){
            errors.reject("errors.namemandatory");
        }   
    }

}
