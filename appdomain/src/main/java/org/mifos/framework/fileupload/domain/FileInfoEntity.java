package org.mifos.framework.fileupload.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.mifos.framework.business.AbstractEntity;

@Entity
@Table(name = "file_info")
public class FileInfoEntity extends AbstractEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    @Column(name = "file_info_id")
    private Long fileInfoId;

    @Column(name = "name")
    private String name;
    
    @Column (name = "content_type")
    private String contentType;

    @Column(name = "size")
    private Integer size;

    @Column(name = "description")
    private String description;

    @Column(name = "upload_date")
    private Date uploadDate;

    public Long getFileInfoId() {
        return fileInfoId;
    }

    public void setFileInfoId(Long fileInfoId) {
        this.fileInfoId = fileInfoId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getUploadDate() {
        return uploadDate;
    }

    public void setUploadDate(Date uploadDate) {
        this.uploadDate = uploadDate;
    }

}
