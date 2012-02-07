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
 * explanation of the license and how it is applied.
 */

package org.mifos.test.acceptance.util;

import org.mifos.framework.util.ConfigurationLocator;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

public class FileUtil {
    private static String configPath = new ConfigurationLocator().getConfigurationDirectory();

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    public static String moveConfigFileToTemp(String fileToMove) throws Exception {
        File oldFile = new File(configPath + fileToMove);
        File temp = File.createTempFile(oldFile.getName(), ".tmp");
        String tempFilePath = null;

        if (oldFile.exists()) {
            copyFile(oldFile,temp);
            oldFile.delete();
            tempFilePath = temp.getAbsolutePath();
        }

        return tempFilePath;
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    public static void moveConfigFileFromTemp(String fileToMove, String tempFileName) throws Exception {
        if (tempFileName != null) {
            File temp = new File(tempFileName);
            File newFile = new File(configPath + fileToMove);

            copyFile(temp,newFile);

            temp.delete();
        }
    }
	
    public static void copyFile(File sourceFile, File destFile) throws IOException {
        FileChannel source = null;
        FileChannel destination = null;
        try {
            source = new FileInputStream(sourceFile).getChannel();
            destination = new FileOutputStream(destFile).getChannel();
            destination.transferFrom(source, 0, source.size());
        } finally {
            if (source != null) {
                source.close();
            }
            if (destination != null) {
                destination.close();
            }
        }
    }
}
