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
package org.mifos.ui.core.controller;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;

import org.mifos.application.importexport.servicefacade.ImportClientsServiceFacade;
import org.mifos.core.MifosRuntimeException;
import org.mifos.dto.domain.ParsedClientsDto;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

@Controller
public class ImportClientsController {

    private final ImportClientsServiceFacade importClientsServiceFacade;

    @Autowired
    public ImportClientsController(ImportClientsServiceFacade importClientsServiceFacade) {
        this.importClientsServiceFacade = importClientsServiceFacade;
    }

    public ParsedClientsDto parseFile(ImportClientsFormBean importClientsFormBean) {
        ParsedClientsDto result = null;
        CommonsMultipartFile file = importClientsFormBean.getFile();
        InputStream is = null;
        if (file == null) {
            throw new MifosRuntimeException("File cannot be null");
        }

        try {
            is = file.getInputStream();
            result = importClientsServiceFacade.parseImportClients(is);
        } catch (IOException ex) {
            result = importClientsServiceFacade.createDtoFromSingleError(ex.getMessage());
        } finally {
            closeStream(is);
            importClientsFormBean.setFile(null);
        }

        return result;
    }

    public ParsedClientsDto save(ParsedClientsDto parsedClientsDto) {
        importClientsServiceFacade.save(parsedClientsDto);
        return parsedClientsDto;
    }

    private void closeStream(Closeable stream) {
        if (stream != null) {
            try {
                stream.close();
            } catch (IOException ex) {
                throw new MifosRuntimeException(ex);
            }
        }
    }
}
