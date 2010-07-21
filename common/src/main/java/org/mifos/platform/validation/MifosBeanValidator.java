/*
 * Copyright (c) 2005-2010 Grameen Foundation USA
 *  All rights reserved.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 *  implied. See the License for the specific language governing
 *  permissions and limitations under the License.
 *
 *  See also http://www.apache.org/licenses/LICENSE-2.0.html for an
 *  explanation of the license and how it is applied.
 */
package org.mifos.platform.validation;

import org.springframework.binding.message.MessageBuilder;
import org.springframework.binding.message.MessageContext;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import javax.validation.ConstraintViolation;
import java.util.Set;


public class MifosBeanValidator {
    
    private MifosLocalValidatorFactoryBean targetValidator;

    public void setTargetValidator(LocalValidatorFactoryBean validator) {
        targetValidator = new MifosLocalValidatorFactoryBean(validator);
    }

    public Errors checkConstraints(Object target, Class<?>...groups) {
        return targetValidator.checkConstraints(target, groups);
    }

    @SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops")
    public void validate(Object target,  MessageContext context, Class<?>...groups)  {
        Errors errors = checkConstraints(target, groups);
        if (errors.hasErrors()) {
            for (FieldError fieldError : errors.getFieldErrors()) {
                context.addMessage(
                        new MessageBuilder().error().source(fieldError.getField()).codes(
                                fieldError.getCodes()).defaultText(fieldError.getDefaultMessage()).build()
                );
            }
        }
    }



    private static class MifosLocalValidatorFactoryBean extends LocalValidatorFactoryBean {
        //@see http://jira.springframework.org/browse/SPR-7062 
        private final LocalValidatorFactoryBean beanValidator;

        @SuppressWarnings("PMD.CallSuperInConstructor")
        public MifosLocalValidatorFactoryBean(final LocalValidatorFactoryBean validator) {
           this.beanValidator = validator;
        }

        public Errors checkConstraints(Object target, Class<?>...groups) {
           BeanPropertyBindingResult errors = new BeanPropertyBindingResult(target, target.getClass().getSimpleName());
           Set<ConstraintViolation<Object>> result = this.beanValidator.validate(target, groups);
		   for (ConstraintViolation<Object> violation : result) {
			  String field = violation.getPropertyPath().toString();
			  FieldError fieldError = errors.getFieldError(field);
			  if (fieldError == null || !fieldError.isBindingFailure()) {
				errors.rejectValue(field,
						violation.getConstraintDescriptor().getAnnotation().annotationType().getSimpleName(),
						getArgumentsForConstraint(errors.getObjectName(), field, violation.getConstraintDescriptor()),
						violation.getMessage());
			  }
		   }
           return errors;
        }
    }
}
