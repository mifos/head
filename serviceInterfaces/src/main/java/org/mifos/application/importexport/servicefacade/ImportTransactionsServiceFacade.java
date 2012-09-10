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

package org.mifos.application.importexport.servicefacade;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

import org.mifos.application.servicefacade.ListItem;
import org.mifos.dto.domain.AccountTrxDto;
import org.mifos.dto.domain.ParseResultDto;
import org.mifos.dto.screen.ImportedFileDto;

public interface ImportTransactionsServiceFacade {

    void saveImportedFileName(String importTransactionsFileName, String importPluginClassname,List<AccountTrxDto> idsToUndoImport);

    boolean isAlreadyImported(String importTransactionsFileName);

    List<ListItem<String>> retrieveImportPlugins();

    ParseResultDto parseImportTransactions(String importPluginClassname, InputStream inputStream);

    ParseResultDto confirmImport(String importPluginClassname, String tempFileName);
    
    void undoFullImport(String importTransactionsFileName);
    
    List<ImportedFileDto> getImportedFiles();
    
    Map<String, Map<String, String>> getUndoImportDateToValidate(String fileName);
}