package org.mifos.ui.core.controller;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

/**
 * User: morzechowski@soldevelo.com
 * Date: 2010-10-18
 */
public class ProductMixFormValidator implements Validator {


    @Override
    public boolean supports(Class<?> clazz) {
        return ProductMixFormBean.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {

        ProductMixFormBean formBean = (ProductMixFormBean) target;

        if (formBean.getProductTypeId().trim().isEmpty()) {
            errors.reject("productmix.producttype.required", "Please specify product type.");
        }

        if (formBean.getProductId().trim().isEmpty()) {
            errors.reject("productmix.productinstance.required", "Please specify product instance name.");
        }

    }
}
