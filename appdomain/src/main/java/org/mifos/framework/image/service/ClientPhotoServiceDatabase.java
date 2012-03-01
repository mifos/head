/*
 * Copyright (c) 2005-2011 Grameen Foundation USA
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 *
 * See also http://www.apache.org/licenses/LICENSE-2.0.html for an
 * explanation of the license and how it is applied
 */
package org.mifos.framework.image.service;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;
import java.sql.Blob;
import java.sql.SQLException;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import org.mifos.accounts.savings.persistence.GenericDao;
import org.mifos.customers.client.business.CustomerPictureEntity;
import org.mifos.customers.persistence.CustomerDao;
import org.mifos.framework.hibernate.helper.HibernateTransactionHelper;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.image.domain.ClientPhoto;
import org.mifos.framework.image.domain.ImageInfo;

import com.mchange.v1.io.InputStreamUtils;

public class ClientPhotoServiceDatabase implements ClientPhotoService {

    private static final String DEFAULT_CONTENT_TYPE = "image/png";
    private static final String NO_PHOTO_PNG = "/org/mifos/image/nopicture.png";
    private static final String SYSERROR_PNG = "/org/mifos/image/syserror.png";
    
    private static final Logger LOG = LoggerFactory.getLogger(ClientPhotoServiceDatabase.class);
    
    @Autowired
    private GenericDao genericDao;
    @Autowired 
    private HibernateTransactionHelper hibernateTransactionHelper;

    public void setGenericDao(GenericDao genericDao) {
        this.genericDao = genericDao;
    }
  
    public void setHibernateTransactionHelper(HibernateTransactionHelper hibernateTransactionHelper) {
        this.hibernateTransactionHelper = hibernateTransactionHelper;
    }

    @Override
    public boolean create(Long clientId, InputStream in) {       
        boolean transactionStarted = false;
        
        if (in == null) {
            return false;
        }       
        try {
            //create blob
            final Blob blob = this.createBlob(in);
            //create picture            
            final ImageInfo imageInfo = new ImageInfo();
            imageInfo.setContentType(this.determineContentType(in));
            imageInfo.setLength(blob.length());
            
            CustomerPictureEntity picture = new CustomerPictureEntity(blob);
            imageInfo.setPicture(picture);
            
            final ClientPhoto clientPhoto = new ClientPhoto();
            clientPhoto.setClientId(clientId);
            clientPhoto.setImageInfo(imageInfo);
            //save
            hibernateTransactionHelper.startTransaction();
            transactionStarted = true;
            this.genericDao.createOrUpdate(clientPhoto);
            hibernateTransactionHelper.commitTransaction();
            
        } catch (Exception ex) {
            if (transactionStarted) {
                hibernateTransactionHelper.rollbackTransaction();
            }
            LOG.error("Unable to create picture", ex);
            return false;
        }
        
        return true;
    }

    @Override
    public ClientPhoto read(Long clientId) {
        return (ClientPhoto) this.genericDao.getSession().createQuery("from ClientPhoto cp where cp.clientId=" + clientId)
                .uniqueResult();
    }

    @Override
    public boolean update(Long clientId, InputStream in) {     
        boolean transactionStarted = false;
        
        if (in == null) {
            return false;
        }
        
        try {
            final ClientPhoto clientPhoto = this.read(clientId);
            if (clientPhoto == null) {
                return this.create(clientId, in); //create new picture
            } else {
                //get picture
                final Blob blob = this.createBlob(in);
                final String contentType = this.determineContentType(in);
                //update data
                final ImageInfo imageInfo = clientPhoto.getImageInfo();
                imageInfo.setContentType(contentType);
                imageInfo.setLength(blob.length());
                imageInfo.getCustomerPictureEntity().setPicture(blob);
                
                this.hibernateTransactionHelper.startTransaction();
                transactionStarted = true;
                this.genericDao.createOrUpdate(clientPhoto);
                this.hibernateTransactionHelper.commitTransaction();
            }
        } catch (Exception ex) {
            if (transactionStarted) {
                hibernateTransactionHelper.rollbackTransaction();
            }
            LOG.error("Unable to update picture", ex);
            return false;
        }
        
        return true;
    }

    @Override
    public boolean delete(Long clientId) {
        boolean transactionStarted = false;
        try { 
            ClientPhoto clientPhoto = read(clientId);
            if (clientPhoto == null) {
                return false;
            } else {
                this.hibernateTransactionHelper.startTransaction();
                transactionStarted = true;
                this.genericDao.delete(clientPhoto.getImageInfo().getCustomerPictureEntity());
                this.genericDao.delete(clientPhoto);
                this.hibernateTransactionHelper.commitTransaction();
            }
        } catch (Exception ex) {
            if (transactionStarted) {
                hibernateTransactionHelper.rollbackTransaction();
            }
            LOG.error("Unable to delete picture", ex);
            return false;
        }
        return true;
    }

    @Override
    public byte[] getData(ClientPhoto clientPhoto) {
        
        byte[] output;
        
        if (clientPhoto == null || clientPhoto.getImageInfo() == null) {
            output = getStaticImage(NO_PHOTO_PNG);
        } else {
            final Blob picture = clientPhoto.getImageInfo().getCustomerPictureEntity().getPicture();
            if (picture == null)  {
                output = getStaticImage(NO_PHOTO_PNG);
            } else {
                try {
                    output = picture.getBytes((long)1, (int)picture.length());
                } catch (SQLException ex) {
                    output = getStaticImage(SYSERROR_PNG);
                }
            }
        }
        
        return output;
    }
    
    private String determineContentType(InputStream in) throws IOException {
        String contentType = URLConnection.guessContentTypeFromStream(in);
        if (contentType == null) {
            contentType = DEFAULT_CONTENT_TYPE;
        }
        return contentType;
    }
    
    private Blob createBlob(final InputStream in) throws IOException {
        final byte[] pictureBytes = IOUtils.toByteArray(in);
        return  StaticHibernateUtil.getSessionTL().getLobHelper().createBlob(pictureBytes);
    }
    
    private byte[] getStaticImage(final String path) {
        byte[] data;
        try {
            data = InputStreamUtils.getBytes(ClientPhotoServiceFileSystem.class.getResourceAsStream(path));
        } catch (IOException ex) {
            data = "Error loading file !!!".getBytes();
            LOG.error(path + "can't be loaded !!!", ex);
        }
        return data;
        
    }
}
