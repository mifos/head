/*
 * Copyright (c) 2005-2009 Grameen Foundation USA
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

package org.mifos.reports.business;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.apache.struts.upload.FormFile;

public class MockFormFile implements FormFile {

    private String fileName;

    public MockFormFile(String name) {
        setFileName(name);
    }

    public void destroy() {

    }

    public String getContentType() {
        throw new RuntimeException("not implemented");
    }

    public byte[] getFileData() throws FileNotFoundException, IOException {
        throw new RuntimeException("not implemented");
    }

    public String getFileName() {
        return fileName;
    }

    public int getFileSize() {
        throw new RuntimeException("not implemented");
    }

    public InputStream getInputStream() throws FileNotFoundException, IOException {
        return new ByteArrayInputStream(new byte[0]);
    }

    public void setContentType(String arg0) {
        throw new RuntimeException("not implemented");
    }

    public void setFileName(String name) {
        this.fileName = name;
    }

    public void setFileSize(int arg0) {
        throw new RuntimeException("not implemented");
    }

}
