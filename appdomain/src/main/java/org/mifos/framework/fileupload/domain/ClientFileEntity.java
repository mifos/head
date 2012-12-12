package org.mifos.framework.fileupload.domain;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.mifos.framework.business.AbstractEntity;
import org.mifos.framework.fileupload.domain.FileInfoEntity;

@Entity
@Table(name = "client_file")
public class ClientFileEntity extends AbstractEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    @Column(name = "file_id")
    private Long fileId;

    @Column(name = "client_id")
    private Integer clientId;

    @OneToOne(fetch = FetchType.EAGER, targetEntity = FileInfoEntity.class, cascade = CascadeType.ALL)
    private FileInfoEntity fileInfo;

    public Long getFileId() {
        return fileId;
    }

    public void setFileId(Long fileId) {
        this.fileId = fileId;
    }

    public Integer getClientId() {
        return clientId;
    }

    public void setClientId(Integer clientId) {
        this.clientId = clientId;
    }

    public FileInfoEntity getFileInfo() {
        return fileInfo;
    }

    public void setFileInfo(FileInfoEntity fileInfo) {
        this.fileInfo = fileInfo;
    }

}
