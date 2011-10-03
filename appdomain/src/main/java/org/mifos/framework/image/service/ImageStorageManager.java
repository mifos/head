package org.mifos.framework.image.service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.RandomUtils;
import org.mifos.config.business.MifosConfigurationManager;
import org.mifos.framework.image.domain.ImageInfo;
import org.mifos.framework.util.ConfigurationLocator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mchange.v1.io.InputStreamUtils;

public class ImageStorageManager {

    public static final Logger LOG = LoggerFactory.getLogger(ImageStorageManager.class);

    public static String location;

    public static final String BASE_DIR = "/imageStore/";

    public static final String DEFAULT_CONTENT_TYPE = "image/png";

    public static final String NO_PHOTO_PNG = "/org/mifos/image/nopicture.png";

    public static final String SYSERROR_PNG = "/org/mifos/image/syserror.png";

    public static final String STORAGE_CONFIG_KEY = "GeneralConfig.ImageStorageDirectory";

    public ImageInfo createImage(InputStream in, String name) throws IOException {
        if (in == null) {
            return null;
        }
        String filePath = generateRandomSubDir() + name;
        String contentType;
        contentType = URLConnection.guessContentTypeFromStream(in);
        if (contentType == null) {
            contentType = DEFAULT_CONTENT_TYPE;
        }

        byte[] data = InputStreamUtils.getBytes(in);
        if (data.length == 0) {
            return null;
        }

        File file = new File(getStorageLocation() + filePath);
        FileUtils.touch(file);
        FileUtils.writeByteArrayToFile(file, data);

        ImageInfo imInfo = new ImageInfo();
        imInfo.setContentType(contentType);
        imInfo.setLength(Long.valueOf(data.length));
        imInfo.setPath(filePath);
        return imInfo;
    }

    public ImageInfo updateImage(InputStream in, ImageInfo imInfo) throws IOException {
        if (in == null) {
            return null;
        }
        String filePath = imInfo.getPath();
        String contentType;
        contentType = URLConnection.guessContentTypeFromStream(in);
        if (contentType == null) {
            contentType = DEFAULT_CONTENT_TYPE;
        }

        byte[] data = InputStreamUtils.getBytes(in);
        if (data.length == 0) {
            return null;
        }

        File file = new File(getStorageLocation() + filePath);
        FileUtils.touch(file);
        FileUtils.writeByteArrayToFile(file, data);

        imInfo.setContentType(contentType);
        imInfo.setLength(Long.valueOf(data.length));
        return imInfo;
    }

    public byte[] getData(String path) {
        byte[] data;
        try {
            if (path == null) {
                data = InputStreamUtils.getBytes(ClientPhotoServiceFileSystem.class.getResourceAsStream(NO_PHOTO_PNG));
            } else {
                File file = new File(getStorageLocation() + path);
                data = FileUtils.readFileToByteArray(file);
            }
        } catch (IOException e) {
            LOG.error("Image storage moved !!!" + path, e);
            try {
                data = InputStreamUtils.getBytes(ClientPhotoServiceFileSystem.class.getResourceAsStream(SYSERROR_PNG));
            } catch (IOException e1) {
                data = "image storage moved !!!".getBytes();
                LOG.error("syserror.png can't be loaded !!!" + path, e);
            }
        }
        return data;
    }

    public boolean delete(String path) {
        return FileUtils.deleteQuietly(new File(getStorageLocation() + path));
    }

    public static void initStorage() {
        new ImageStorageManager().getStorageLocation();
    }

    private String generateRandomSubDir() {
        int deep = RandomUtils.nextInt(4) + 1;
        String subPath = "";
        for (int i = 0; i < deep; i++) {
            subPath += RandomStringUtils.randomAlphabetic(1) + "/";
        }
        return subPath;
    }

    private synchronized String getStorageLocation() {
        if (location == null) {
            String pathConfig = MifosConfigurationManager.getInstance().getString(STORAGE_CONFIG_KEY);
            if (StringUtils.isBlank(pathConfig) || pathConfig.equals(ConfigurationLocator.LOCATOR_ENVIRONMENT_PROPERTY_NAME)) {
                ConfigurationLocator cl = new ConfigurationLocator();
                location = cl.getConfigurationDirectory() + BASE_DIR;
            } else {
                location = pathConfig + BASE_DIR;
            }
            checkStoragePermission();
        }
        return location;
    }

    private void checkStoragePermission() {
        try {
            File file = new File(location + "/permissionCheck");
            FileUtils.touch(file);
            FileUtils.writeStringToFile(file, "This is test");
            FileUtils.deleteQuietly(file);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

}
