package org.mifos.ui.core.controller;

import org.springframework.binding.message.MessageBuilder;
import org.springframework.binding.message.MessageContext;
import org.springframework.binding.validation.ValidationContext;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

public class ImportLoansFormBean implements java.io.Serializable {

    private static final long serialVersionUID = -4844232841626666993L;
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
