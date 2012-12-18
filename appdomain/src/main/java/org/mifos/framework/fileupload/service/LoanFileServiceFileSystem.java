package org.mifos.framework.fileupload.service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.mifos.accounts.loan.persistance.LoanDao;
import org.mifos.accounts.savings.persistence.GenericDao;
import org.mifos.application.admin.servicefacade.ViewOrganizationSettingsServiceFacade;
import org.mifos.dto.screen.UploadedFileDto;
import org.mifos.framework.fileupload.domain.FileInfoEntity;
import org.mifos.framework.fileupload.domain.LoanFileEntity;
import org.mifos.framework.hibernate.helper.HibernateTransactionHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class LoanFileServiceFileSystem implements LoanFileService {
    private static final Logger logger = LoggerFactory.getLogger(LoanFileServiceFileSystem.class);

    @Autowired
    private HibernateTransactionHelper hibernateTransactionHelper;

    @Autowired
    private GenericDao genericDao;

    @Autowired
    private LoanDao loanDao;

    @Autowired
    private ViewOrganizationSettingsServiceFacade viewOrganizationSettingsServiceFacade;

    public boolean create(Integer accountId, InputStream in, UploadedFileDto uploadedFileDto) {
        try {
            String storageDir = viewOrganizationSettingsServiceFacade.getLoanStorageDirectory();
            String fileDir = storageDir + File.separator + accountId.toString();
            File file = new File(fileDir + File.separator + uploadedFileDto.getName());
            if (file.exists()) {
                return update(accountId, in, uploadedFileDto);
            }
            FileInfoEntity fileInfo = FileStorageManager.createFile(in, fileDir, uploadedFileDto);
            hibernateTransactionHelper.startTransaction();
            LoanFileEntity loanFile = new LoanFileEntity();
            loanFile.setLoanId(accountId);
            loanFile.setFileInfo(fileInfo);
            genericDao.getSession().save(loanFile);
            hibernateTransactionHelper.commitTransaction();
        } catch (IOException e) {
            logger.error("Unable to persist", e);
            return false;
        } catch (Exception e) {
            hibernateTransactionHelper.rollbackTransaction();
        }
        return true;
    }

    public UploadedFileDto read(Long fileId) {
        LoanFileEntity loanFileEntity = loanDao.getUploadedFile(fileId);
        return new UploadedFileDto(loanFileEntity.getFileId(), loanFileEntity.getFileInfo().getName(),
                loanFileEntity.getFileInfo().getContentType(), loanFileEntity.getFileInfo().getSize(),
                loanFileEntity.getFileInfo().getDescription(), loanFileEntity.getFileInfo().getUploadDate());
    }

    public List<UploadedFileDto> readAll(Integer accountId) {
        List<LoanFileEntity> loanFileEntities = loanDao.getLoanAllUploadedFiles(accountId);
        List<UploadedFileDto> uploadedFiles = new ArrayList<UploadedFileDto>();
        for (LoanFileEntity entity : loanFileEntities) {
            FileInfoEntity fileInfo = entity.getFileInfo();
            Long uploadFileId = entity.getFileId();
            String fileName = fileInfo.getName();
            String contentType = fileInfo.getContentType();
            Integer fileSize = fileInfo.getSize();
            String description = fileInfo.getDescription();
            java.util.Date uploadDate = fileInfo.getUploadDate();
            uploadedFiles.add(new UploadedFileDto(uploadFileId, fileName, contentType, fileSize, description,
                    uploadDate));
        }
        return uploadedFiles;
    }

    public boolean update(Integer accountId, InputStream in, UploadedFileDto uploadedFileDto) {
        LoanFileEntity loanFile = loanDao.getLoanUploadedFileByName(accountId, uploadedFileDto.getName());

        if (loanFile == null) {
            return create(accountId, in, uploadedFileDto);
        }

        try {
            String storageDir = viewOrganizationSettingsServiceFacade.getLoanStorageDirectory();
            String fileDir = storageDir + File.separator + accountId.toString();
            FileInfoEntity updateFileInfo = FileStorageManager.updateFile(in, fileDir, loanFile.getFileInfo(),
                    uploadedFileDto);
            if (updateFileInfo == null) {
                return false;
            }
            hibernateTransactionHelper.startTransaction();
            genericDao.getSession().save(updateFileInfo);
            hibernateTransactionHelper.commitTransaction();
        } catch (IOException e) {
            logger.error("Unable to persist", e);
            return false;
        } catch (Exception e) {
            hibernateTransactionHelper.rollbackTransaction();
        }
        return true;
    }

    public boolean delete(Integer accountId, Long fileId) {
        LoanFileEntity loanFile = loanDao.getUploadedFile(fileId);
        FileInfoEntity fileInfo = loanFile.getFileInfo();
        hibernateTransactionHelper.startTransaction();
        genericDao.getSession().delete(loanFile);
        hibernateTransactionHelper.commitTransaction();
        return FileStorageManager.delete(viewOrganizationSettingsServiceFacade.getLoanStorageDirectory()
                + File.separator + accountId.toString() + "/" + fileInfo.getName());
    }

    public byte[] getData(UploadedFileDto uploadedFileDto) {
        LoanFileEntity loanFile = loanDao.getUploadedFile(uploadedFileDto.getUploadedFileId());
        if (loanFile == null || loanFile.getFileInfo() == null) {
            return new byte[0];
        } else {
            final String path = viewOrganizationSettingsServiceFacade.getLoanStorageDirectory() + File.separator
                    + loanFile.getLoanId().toString() + File.separator + loanFile.getFileInfo().getName();
            return FileStorageManager.getData(path);
        }
    }

    public boolean checkIfFileExists(Integer accountId, String fileName) {
        String storageDir = viewOrganizationSettingsServiceFacade.getLoanStorageDirectory();
        String fileDir = storageDir + File.separator + accountId.toString();
        File file = new File(fileDir + File.separator + fileName);
        if (file.exists()) {
            return true;
        } else {
            return false;
        }
    }
}
