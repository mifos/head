package org.mifos.ui.core.controller;

import java.io.Serializable;

import org.springframework.binding.message.MessageBuilder;
import org.springframework.binding.message.MessageContext;
import org.springframework.binding.validation.ValidationContext;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

public class ImportSavingsFormBean implements Serializable {

	private static final long serialVersionUID = 3742567723775898111L;
	private CommonsMultipartFile file;

    public CommonsMultipartFile getFile() {
        return file;
    }

    public void setFile(CommonsMultipartFile file) {
        this.file = file;
    }

    public void validateSelectFileStep(ValidationContext context) {
        MessageContext messageContext = context.getMessageContext();
        if (file == null || file.getSize() <= 0) {
            messageContext.addMessage(new MessageBuilder().error()
                    .source("file").code("errors.importexport.mandatory_file")
                    .build());
        }
    }	
}
