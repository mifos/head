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

package org.mifos.ui.core.controller;

import java.util.ArrayList;
import java.util.List;

import org.mifos.dto.domain.AdminDocumentDto;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/viewAdminDocs")
public class AdminDocumentController {

    @RequestMapping(method = RequestMethod.GET)
    @ModelAttribute("listofadministrativedocuments")
    public List<AdminDocumentDto> showAllDocuments() {
        return createStub();
    }

    private List<AdminDocumentDto> createStub(){
        List<AdminDocumentDto> docs = new ArrayList<AdminDocumentDto>();

        Integer[] docid = {1, 2, 3, 4};
        Boolean[] isactive = {true, false, true, false};
        String[] name = {"doc 1","doc 2","doc 3","doc 4"};
        String[] identifier   = {"id 1","id 2","id 3","id 4"};

        for (int i = 0; i < name.length; i++)
        {
            AdminDocumentDto adminDoc = new AdminDocumentDto(docid[i], name[i], identifier[i], isactive[i]);
            docs.add( i, adminDoc);
        }
        return docs;
    }
}