package org.mifos.ui.core.controller;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

/**
 * User: morzechowski@soldevelo.com
 * Date: 2010-10-22
 */
public class LatenessDormancyFormValidator implements Validator {
    @Override
    public boolean supports(Class<?> clazz) {
        return LatenessDormancyFormBean.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {

        LatenessDormancyFormBean formBean = (LatenessDormancyFormBean) target;

        if (null == formBean.getLatenessDays() || formBean.getLatenessDays().intValue() < 0 ) {
            errors.reject("manageProducts.editLatenessDormancy.specifyValidLateness");
        }

        if (null == formBean.getDormancyDays() || formBean.getDormancyDays().intValue() < 0) {
            errors.reject("manageProducts.editLatenessDormancy.specifyValidDormancy");
        }

    }
}
