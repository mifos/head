package org.mifos.framework.fileupload.service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.mifos.accounts.savings.persistence.GenericDao;
import org.mifos.application.admin.servicefacade.ViewOrganizationSettingsServiceFacade;
import org.mifos.customers.persistence.CustomerDao;
import org.mifos.dto.screen.UploadedFileDto;
import org.mifos.framework.fileupload.domain.ClientFileEntity;
import org.mifos.framework.fileupload.domain.FileInfoEntity;
import org.mifos.framework.hibernate.helper.HibernateTransactionHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class ClientFileServiceFileSystem implements ClientFileService {
    private static final Logger logger = LoggerFactory.getLogger(ClientFileServiceFileSystem.class);

    @Autowired
    private HibernateTransactionHelper hibernateTransactionHelper;

    @Autowired
    private GenericDao genericDao;

    @Autowired
    private CustomerDao customerDao;

    @Autowired
    private ViewOrganizationSettingsServiceFacade viewOrganizationSettingsServiceFacade;

    public boolean create(Integer clientId, InputStream in, String contentType, String name, String description) {
        try {
            String storageDir = viewOrganizationSettingsServiceFacade.getClientStorageDirectory();
            String fileDir = storageDir + File.separator + clientId.toString();
            File file = new File(fileDir + File.separator + name);
            if (file.exists()) {
                return update(clientId, in, contentType, name, description);
            }
            FileInfoEntity fileInfo = FileStorageManager.createFile(in, fileDir, contentType, name, description);
            hibernateTransactionHelper.startTransaction();
            ClientFileEntity clientFile = new ClientFileEntity();
            clientFile.setClientId(clientId);
            clientFile.setFileInfo(fileInfo);
            genericDao.getSession().save(clientFile);
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
        ClientFileEntity clientFileEntity = customerDao.getUploadedFile(fileId);
        return new UploadedFileDto(clientFileEntity.getFileId(), clientFileEntity.getFileInfo().getName(),
                clientFileEntity.getFileInfo().getContentType(), clientFileEntity.getFileInfo().getSize(),
                clientFileEntity.getFileInfo().getDescription(), clientFileEntity.getFileInfo().getUploadDate());
    }

    public List<UploadedFileDto> readAll(Integer clientId) {
        List<ClientFileEntity> clientFileEntities = customerDao.getClientAllUploadedFiles(clientId);
        List<UploadedFileDto> uploadedFiles = new ArrayList<UploadedFileDto>();
        for (ClientFileEntity entity : clientFileEntities) {
            FileInfoEntity fileInfo = entity.getFileInfo();
            Long uploadFileId = fileInfo.getFileInfoId();
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

    public boolean update(Integer clientId, InputStream in, String contentType, String name, String description) {
        ClientFileEntity clientFile = customerDao.getClientUploadedFileByName(clientId, name);

        if (clientFile == null) {
            return create(clientId, in, contentType, name, description);
        }

        try {
            String storageDir = viewOrganizationSettingsServiceFacade.getClientStorageDirectory();
            String fileDir = storageDir + File.separator + clientId.toString();
            FileInfoEntity updateFileInfo = FileStorageManager.updateFile(in, fileDir, clientFile.getFileInfo(),
                    contentType, name, description);
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

    public boolean delete(Integer clientId, Long fileId) {
        ClientFileEntity clientFile = customerDao.getUploadedFile(fileId);
        FileInfoEntity fileInfo = clientFile.getFileInfo();
        hibernateTransactionHelper.startTransaction();
        genericDao.getSession().delete(clientFile);
        hibernateTransactionHelper.commitTransaction();
        return FileStorageManager.delete("/clients/" + clientId.toString() + "/" + fileInfo.getName());
    }

    public byte[] getData(UploadedFileDto uploadedFileDto) {
        ClientFileEntity clientFile = customerDao.getUploadedFile(uploadedFileDto.getUploadedFileId());
        if (clientFile == null || clientFile.getFileInfo() == null) {
            return new byte[0];
        } else {
            final String path = "/clients/" + clientFile.getClientId().toString() + "/"
                    + clientFile.getFileInfo().getName();
            return FileStorageManager.getData(path);
        }
    }

    public boolean checkIfFileExists(Integer clientId, String fileName) {
        String storageDir = viewOrganizationSettingsServiceFacade.getClientStorageDirectory();
        String fileDir = storageDir + File.separator + clientId.toString();
        File file = new File(fileDir + File.separator + fileName);
        if (file.exists()) {
            return true;
        } else {
            return false;
        }
    }
}
