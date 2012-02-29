package org.mifos.framework.image.service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.junit.Assert;
import org.junit.Test;
import org.mifos.framework.MifosIntegrationTestCase;
import org.mifos.framework.image.domain.ClientPhoto;
import org.mifos.framework.image.domain.ImageInfo;
import org.springframework.beans.factory.annotation.Autowired;

public class ClientPhotoServiceIntegrationTest extends MifosIntegrationTestCase {

    @Autowired
    ClientPhotoService clientPhotoService;

    @Test
    public void testCRUD() {
        String data = "test string";
        InputStream in = new ByteArrayInputStream(data.getBytes());
        Long clientId = 2342L;

        clientPhotoService.create(clientId, in);

        ClientPhoto cp = clientPhotoService.read(2342L);

        Assert.assertEquals(clientId, cp.getClientId());

        ImageInfo imageInfo = cp.getImageInfo();
        String path = imageInfo.getPath();
        Assert.assertNotNull(path);
        Assert.assertNotNull(imageInfo.getContentType());
        Assert.assertEquals(data.length(), imageInfo.getLength().intValue());

        Assert.assertEquals(data, new String(clientPhotoService.getData(cp)));


        String otherData = "other test string";
        in = new ByteArrayInputStream(otherData.getBytes());
        clientPhotoService.update(clientId, in);

        Assert.assertEquals(path, imageInfo.getPath());
        Assert.assertNotNull(imageInfo.getContentType());
        Assert.assertEquals(otherData.length(), imageInfo.getLength().intValue());

        Assert.assertEquals(otherData, new String(clientPhotoService.getData(cp)));

        Assert.assertTrue(clientPhotoService.delete(clientId));
    }

}
