package org.mifos.framework.image.service;

import java.io.IOException;
import java.io.InputStream;
import org.mifos.accounts.savings.persistence.GenericDao;
import org.mifos.customers.client.util.helpers.ClientConstants;
import org.mifos.framework.hibernate.helper.HibernateTransactionHelper;
import org.mifos.framework.image.domain.ClientPhoto;
import org.mifos.framework.image.domain.ImageInfo;
import org.mifos.service.BusinessRuleException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class ClientPhotoServiceFileSystem implements ClientPhotoService {

    public static final Logger LOG = LoggerFactory.getLogger(ClientPhotoServiceFileSystem.class);


    @Autowired
    private HibernateTransactionHelper hibernateTransactionHelper;

    @Autowired
    private GenericDao genericDao;

    // TODO use spring configuration
    private ImageStorageManager imageStorageManager = new ImageStorageManager();

    @Override
    public boolean create(Long clientId, InputStream in) {
        try {
            ImageInfo imInfo = imageStorageManager.createImage(in, clientId.toString());
            if(imInfo == null) {
                return false;
            }
            hibernateTransactionHelper.startTransaction();
            ClientPhoto clientPhoto = new ClientPhoto();
            clientPhoto.setClientId(clientId);
            clientPhoto.setImageInfo(imInfo);
            genericDao.getSession().save(clientPhoto);
            hibernateTransactionHelper.commitTransaction();

        } catch (IOException e) {
            LOG.error("Unable to persist", e);
            return false;
        } catch (Exception e) {
            hibernateTransactionHelper.rollbackTransaction();
            throw new BusinessRuleException(ClientConstants.INVALID_PHOTO, new Object[] { e.getMessage() }, e);
        }
        return true;
    }

    @Override
    public ClientPhoto read(Long clientId) {
        return (ClientPhoto) genericDao.getSession().createQuery("from ClientPhoto cp where cp.clientId=" + clientId)
                .uniqueResult();
    }

    @Override
    public boolean update(Long clientId, InputStream in) {
        if (in == null) {
            return false;
        }

        ClientPhoto clientPhoto = read(clientId);

        if (clientPhoto == null) {
            return create(clientId, in);
        }

        try {
            ImageInfo updateImageInfo = imageStorageManager.updateImage(in, clientPhoto.getImageInfo());
            if(updateImageInfo == null) {
                return false;
            }
            hibernateTransactionHelper.startTransaction();
            genericDao.getSession().save(updateImageInfo);
            hibernateTransactionHelper.commitTransaction();

        } catch (IOException e) {
            LOG.error("Unable to persist", e);
            return false;
        } catch (Exception e) {
            throw new BusinessRuleException(ClientConstants.INVALID_PHOTO, new Object[] { e.getMessage() }, e);
        }
        return true;
    }

    @Override
    public boolean delete(Long clientId) {
        ClientPhoto clientPhoto = read(clientId);
        if (clientPhoto == null) {
            return false;
        }
        ImageInfo imageInfo = clientPhoto.getImageInfo();
        hibernateTransactionHelper.startTransaction();
        genericDao.getSession().delete(clientPhoto);
        hibernateTransactionHelper.commitTransaction();
        return imageStorageManager.delete(imageInfo.getPath());
    }

    @Override
    public byte[] getData(String path) {
        return imageStorageManager.getData(path);
    }
}
