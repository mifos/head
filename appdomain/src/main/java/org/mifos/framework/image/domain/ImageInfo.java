package org.mifos.framework.image.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.mifos.framework.business.AbstractEntity;

@Entity
public class ImageInfo extends AbstractEntity {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    private Long imageId;

    private String contentType;

    private String path;

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

}
