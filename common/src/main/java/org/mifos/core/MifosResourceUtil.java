/*
 * Copyright (c) 2005-2010 Grameen Foundation USA
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

package org.mifos.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

import org.springframework.core.io.ClassPathResource;
import org.springframework.util.ResourceUtils;

/**
 * Load resources from the classpath.
 *
 */

public class MifosResourceUtil {

    public static File getFile(String fileNameWithLocation) {
        File file;
        try {
            file = ResourceUtils.getFile(fileNameWithLocation);
        } catch (FileNotFoundException e) {
            throw new MifosRuntimeException("file not found :" + fileNameWithLocation, e);
        }
        return file;
    }

    private static File getFile(String location, String fileName) {
        return getFile(location + fileName);
    }

    public static Reader getSQLFileAsReader(String fileName) throws FileNotFoundException {
        return new FileReader(getFile("/sql/", fileName));
    }

    public static InputStream getSQLFileAsStream(String fileName) throws FileNotFoundException {
        return new FileInputStream(getFile("/sql/", fileName));
    }

    public static File getClassPathResource(String file) throws IOException {
        return new ClassPathResource(file).getFile();
    }

}
