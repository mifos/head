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
@Table(name = "loan_file")
public class LoanFileEntity extends AbstractEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    @Column(name = "file_id")
    private Long fileId;

    @Column(name = "loan_id")
    private Integer loanId;

    @OneToOne(fetch = FetchType.EAGER, targetEntity = FileInfoEntity.class, cascade = CascadeType.ALL)
    private FileInfoEntity fileInfo;

    public Long getFileId() {
        return fileId;
    }

    public void setFileId(Long fileId) {
        this.fileId = fileId;
    }

    public Integer getLoanId() {
        return loanId;
    }

    public void setLoanId(Integer loanId) {
        this.loanId = loanId;
    }

    public FileInfoEntity getFileInfo() {
        return fileInfo;
    }

    public void setFileInfo(FileInfoEntity fileInfo) {
        this.fileInfo = fileInfo;
    }

}
