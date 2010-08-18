/*
 * Copyright (c) 2005-2010 Grameen Foundation USA
 *  All rights reserved.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 *  implied. See the License for the specific language governing
 *  permissions and limitations under the License.
 *
 *  See also http://www.apache.org/licenses/LICENSE-2.0.html for an
 *  explanation of the license and how it is applied.
 */

package org.mifos.platform.questionnaire.domain.ppi;

import org.mifos.framework.exceptions.SystemException;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static org.mifos.platform.questionnaire.QuestionnaireConstants.*; //NOPMD
import static org.mifos.platform.util.CollectionUtils.isNotEmpty;

public final class PPISurveyLocatorImpl implements PPISurveyLocator, ResourceLoaderAware {
    private ResourceLoader resourceLoader;
    private final String ppiXmlFolder;

    public PPISurveyLocatorImpl(String ppiXmlFolder) {
        this.ppiXmlFolder = ppiXmlFolder;
    }

    @Override
    public List<String> getAllPPISurveyFiles() {
        try {
            Resource resource = this.resourceLoader.getResource(ppiXmlFolder);
            return getPPISurveyFiles(resource.getFile());
        } catch (IOException e) {
            throw new SystemException(FETCH_PPI_XMLS_FAILED, e);
        }
    }

    @Override
    public String getPPIUploadFileForCountry(String country) {
        try {
            String fileName = getPPIXmlFileName(country);
            Resource resource = this.resourceLoader.getResource(ppiXmlFolder);
            return getPPIFilePath(fileName, resource.getFile());
        } catch (IOException e) {
            throw new SystemException(FETCH_PPI_COUNTRY_XML_FAILED, e);
        }
    }

    @SuppressWarnings("PMD.NullAssignment")
    private String getPPIFilePath(String fileName, File ppiFolder) {
        List<File> ppiFiles = new ArrayList<File>();
        if (ppiFolder.isDirectory()) {
            ppiFiles.addAll(asList(ppiFolder.listFiles(new PPICompleteFileNameFilter(fileName))));
        }
        return isNotEmpty(ppiFiles)? ppiFiles.get(0).getAbsolutePath(): null;
    }

    private List<String> getPPISurveyFiles(File ppiFolder) {
        List<String> ppiFiles = new ArrayList<String>();
        if (ppiFolder.isDirectory()) {
            ppiFiles.addAll(asList(ppiFolder.list(new PPIPartialFileNameFilter())));
        }
        return ppiFiles;
    }

    private String getPPIXmlFileName(String country) {
        return String.format("%s%s%s", PPI_SURVEY_FILE_PREFIX, country, PPI_SURVEY_FILE_EXT);
    }

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    private static class PPIPartialFileNameFilter implements FilenameFilter {
        @Override
        public boolean accept(File file, String fileName) {
            return fileName.startsWith(PPI_SURVEY_FILE_PREFIX) && fileName.endsWith(PPI_SURVEY_FILE_EXT);
        }
    }

    private static class PPICompleteFileNameFilter implements FilenameFilter {
        private final String ppiFileName;

        public PPICompleteFileNameFilter(String ppiFileName) {
            this.ppiFileName = ppiFileName;
        }

        @Override
        public boolean accept(File file, String fileName) {
            return org.apache.commons.lang.StringUtils.equalsIgnoreCase(fileName, ppiFileName);
        }
    }
}
