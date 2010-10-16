package org.mifos.platform.validation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.binding.message.MessageContext;

public class ScreenObject implements java.io.Serializable {

    private static final long serialVersionUID = -98563977268032788L;

    private transient MifosBeanValidator validator;

    @Autowired
    public void setValidator(MifosBeanValidator validator) {
        this.validator = validator;
    }

    public void validateConstraints(MessageContext context, Class<?>...groups) {
        if (validator != null) {
            validator.validate(this, context, groups);
        }
    }
}