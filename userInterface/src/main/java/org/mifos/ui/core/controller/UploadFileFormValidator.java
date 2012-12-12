package org.mifos.ui.core.controller;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

public class UploadFileFormValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return UploadedFileFormBean.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        UploadedFileFormBean formBean = (UploadedFileFormBean) target;
        if (formBean.getFile() == null || formBean.getFile().getSize() == 0) {
            errors.reject("errors.importexport.mandatory_file");
        }
    }
    
}
