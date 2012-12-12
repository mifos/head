package org.mifos.framework.fileupload.service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.FileUtils;
import org.mifos.framework.fileupload.domain.FileInfoEntity;
import org.mifos.framework.util.ConfigurationLocator;
import org.mifos.framework.util.helpers.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mchange.v1.io.InputStreamUtils;

public class FileStorageManager {
    private static final Logger logger = LoggerFactory.getLogger(FileStorageManager.class);
    private static final String BASE_DIR = "/uploads/";
    public static String location;

    public static FileInfoEntity createFile(InputStream in, String fileDir, String contentType, String name,
            String description) throws IOException {
        byte[] data = InputStreamUtils.getBytes(in);

        File file = new File(fileDir + File.separator + name);
        FileUtils.touch(file);
        FileUtils.writeByteArrayToFile(file, data);

        FileInfoEntity fileInfo = new FileInfoEntity();
        fileInfo.setName(name);
        fileInfo.setContentType(contentType);
        fileInfo.setSize(data.length);
        fileInfo.setDescription(description);
        fileInfo.setUploadDate(DateUtils.getCurrentJavaDateTime());
        return fileInfo;
    }

    public static FileInfoEntity updateFile(InputStream in, String fileDir, FileInfoEntity fileInfo,
            String contentType, String name, String description) throws IOException {
        String filePath = fileDir + File.separator + fileInfo.getName();
        byte[] data = InputStreamUtils.getBytes(in);

        File file = new File(filePath);
        FileUtils.touch(file);
        FileUtils.writeByteArrayToFile(file, data);

        fileInfo.setContentType(contentType);
        fileInfo.setName(name);
        fileInfo.setDescription(description);
        fileInfo.setSize(data.length);
        fileInfo.setUploadDate(DateUtils.getCurrentJavaDateTime());
        return fileInfo;
    }

    public static byte[] getData(String path) {
        byte[] data = null;
        try {
            File file = new File(getStorageLocation() + path);
            data = FileUtils.readFileToByteArray(file);
        } catch (IOException e) {
            logger.error("Error reading file " + path, e);
        }
        return data;
    }

    public static boolean delete(String path) {
        return FileUtils.deleteQuietly(new File(getStorageLocation() + path));
    }

    public static void initStorage() {
        getStorageLocation();
    }

    public static synchronized String getStorageLocation() {
        if (location == null) {
            ConfigurationLocator cl = new ConfigurationLocator();
            location = cl.getConfigurationDirectory() + BASE_DIR;
            checkStoragePermission();
        }
        return location;
    }

    private static void checkStoragePermission() {
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
