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
package org.mifos.reports.pentaho;

import java.util.Arrays;
import java.util.List;

public class PentahoReport {

    private String contentType;
    private byte[] content;
    private String name;
    private String fileExtension;
    private List<PentahoValidationError> errors;

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public byte[] getContent() {
        return Arrays.copyOf(content, content.length);
    }

    public void setContent(byte[] content) {
        this.content = Arrays.copyOf(content, content.length);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFileExtension() {
        return fileExtension;
    }

    public void setFileExtension(String fileExtension) {
        this.fileExtension = fileExtension;
    }

    public int getContentSize() {
        return (content == null) ? 0 : content.length;
    }

    public String getFilename() {
        return name + fileExtension;
    }

    public List<PentahoValidationError> getErrors() {
        return errors;
    }

    public void setErrors(List<PentahoValidationError> errors) {
        this.errors = errors;
    }

    public boolean isInError() {
        return !(errors == null || errors.isEmpty());
    }
}
