package org.mifos.ui.core.controller;

import java.io.Serializable;

import org.springframework.web.multipart.commons.CommonsMultipartFile;

public class UploadedFileFormBean implements Serializable {
    
    private static final long serialVersionUID = 6766656607918159631L;
    
    private CommonsMultipartFile file;
    private String description;
    
    public CommonsMultipartFile getFile() {
        return file;
    }

    public void setFile(CommonsMultipartFile file) {
        this.file = file;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
