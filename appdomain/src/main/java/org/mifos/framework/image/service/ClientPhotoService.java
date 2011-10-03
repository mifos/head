package org.mifos.framework.image.service;

import java.io.InputStream;

import org.mifos.framework.image.domain.ClientPhoto;

public interface ClientPhotoService {

    boolean create(Long clientId, InputStream in);

    ClientPhoto read(Long clientId);

    boolean update(Long clientId, InputStream in);

    boolean delete(Long clientId);

    byte[] getData(String path);

}
