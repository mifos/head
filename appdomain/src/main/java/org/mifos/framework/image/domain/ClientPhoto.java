package org.mifos.framework.image.domain;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;

import org.mifos.framework.business.AbstractEntity;

@Entity
public class ClientPhoto extends AbstractEntity {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    private Long photoId;

    private Long clientId;

    @OneToOne(fetch=FetchType.EAGER,
              targetEntity=ImageInfo.class,
              cascade=CascadeType.ALL)
    private ImageInfo imageInfo;

    public Long getPhotoId() {
        return photoId;
    }

    public void setPhotoId(Long photoId) {
        this.photoId = photoId;
    }

    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    public ImageInfo getImageInfo() {
        return imageInfo;
    }

    public void setImageInfo(ImageInfo imageInfo) {
        this.imageInfo = imageInfo;
    }
}
