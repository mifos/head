package org.mifos.dto.screen;

import java.io.Serializable;
import java.util.Date;

public class UploadedFileDto implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private final Long uploadedFileId;
    private final String name;
    private final String contentType;
    private final Integer size;
    private final String description;
    private final Date uploadDate;
    
    public UploadedFileDto(Long uploadedFileId, String name, String contentType, Integer size, String description, Date uploadDate) {
        this.uploadedFileId = uploadedFileId;
        this.name = name;
        this.contentType = contentType;
        this.size = size;
        this.description = description;
        this.uploadDate = (Date)uploadDate.clone();
    }
    
    public UploadedFileDto(String name, String contentType, Integer size, String description) {
        this.uploadedFileId = 0L;
        this.name = name;
        this.contentType = contentType;
        this.size = size;
        this.description = description;
        this.uploadDate = new Date();
    }

    public Long getUploadedFileId() {
        return uploadedFileId;
    }

    public String getName() {
        return name;
    }
    
    public String getContentType() {
        return contentType;
    }

    public Integer getSize() {
        return size;
    }

    public String getDescription() {
        return description;
    }

    public Date getUploadDate() {
        return (Date)uploadDate.clone();
    }

}
