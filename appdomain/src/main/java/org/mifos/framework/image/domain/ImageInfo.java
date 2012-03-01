package org.mifos.framework.image.domain;

import java.sql.Blob;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.OneToOne;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.mifos.customers.client.business.CustomerPictureEntity;
import org.mifos.framework.business.AbstractEntity;

@Entity
public class ImageInfo extends AbstractEntity {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    private Long imageId;

    private String contentType;

    /*
     * Depending on the configuration, a picture is either saved as a file in the filesystem or a blob in the database
     */
    private String path; //represents a picture in the filesystem

    @OneToOne(fetch=FetchType.LAZY,
            targetEntity=CustomerPictureEntity.class,
            cascade=CascadeType.ALL)
    private CustomerPictureEntity customerPictureEntity; //represents a picture in the database
    
    private Long length;

    public Long getImageId() {
        return imageId;
    }

    public void setImageId(Long imageId) {
        this.imageId = imageId;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Long getLength() {
        return length;
    }

    public void setLength(Long length) {
        this.length = length;
    }

    public CustomerPictureEntity getCustomerPictureEntity() {
        return customerPictureEntity;
    }

    public void setPicture(CustomerPictureEntity customerPictureEntity) {
        this.customerPictureEntity = customerPictureEntity;
    }

}
