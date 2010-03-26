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

package org.mifos;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.codehaus.jackson.map.ObjectMapper;
import org.mifos.config.Localization;
import org.mifos.customers.center.business.service.CenterDetailsServiceFacade;
import org.mifos.customers.center.business.service.CenterInformationDto;
import org.mifos.customers.client.business.ClientBO;
import org.mifos.customers.client.persistence.ClientPersistence;
import org.mifos.security.util.UserContext;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

public class BackendBridgeController extends AbstractController {

    // FIXME: autowire, if possible
    private CenterDetailsServiceFacade centerDetailsServiceFacade;

    public CenterDetailsServiceFacade getCenterDetailsServiceFacade() {
        return this.centerDetailsServiceFacade;
    }

    public void setCenterDetailsServiceFacade(CenterDetailsServiceFacade centerDetailsServiceFacade) {
        this.centerDetailsServiceFacade = centerDetailsServiceFacade;
    }

    private ObjectMapper jsonObjectMapper;

    public ObjectMapper getJsonObjectMapper() {
        return this.jsonObjectMapper;
    }

    public void setJsonObjectMapper(ObjectMapper jsonObjectMapper) {
        this.jsonObjectMapper = jsonObjectMapper;
    }

    @Override
    protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        response.setContentType("application/json");
        String pathParts[] = request.getPathInfo().split("/");
        // String restApiVersion = pathParts[1];
        String objectToFetch = pathParts[2];

        if (objectToFetch.equals("clients")) {
            // fetch list of clients
            String hardcodedSearchId = "1";
            short hardcodedOffice = 4;
            List<ClientBO> clientObjects = new ClientPersistence().getActiveClientsUnderParent(hardcodedSearchId,
                    hardcodedOffice);
            List<Map<String, Object>> clients = new ArrayList<Map<String, Object>>();
            for (ClientBO client : clientObjects) {
                Map<String, Object> clientMap = new HashMap<String, Object>();
                clientMap.put("id", client.getCustomerId());
                clientMap.put("firstName", client.getFirstName());
                clientMap.put("lastName", client.getLastName());
                clients.add(clientMap);
            }
            //response.getWriter().write(JSONArray.fromObject(clients).toString());
            jsonObjectMapper.writeValue(response.getWriter(), clients);
        } else if (objectToFetch.equals("center")) {
            String idOfObjectToFetch = pathParts[3];
            UserContext user = new UserContext();
            user.setPreferredLocale(Localization.getInstance().getMainLocale());
            user.setLocaleId((short) 1);
            CenterInformationDto center = centerDetailsServiceFacade.getCenterInformationDto(idOfObjectToFetch,
                    user);
            //response.getWriter().write(JSONObject.fromObject(center).toString());
            //ObjectMapper mapper = new ObjectMapper();
            jsonObjectMapper.writeValue(response.getWriter(), center);
        } else {
            // fetch a particular client
            String idOfObjectToFetch = pathParts[3];
            ClientBO client = new ClientPersistence().getClient(new Integer(idOfObjectToFetch));
            Map<String, Object> model = new HashMap<String, Object>();
            model.put("id", client.getCustomerId());
            model.put("firstName", client.getFirstName());
            model.put("lastName", client.getLastName());
            //response.getWriter().write(JSONObject.fromObject(model).toString());
            jsonObjectMapper.writeValue(response.getWriter(), model);
        }

        return null; // indicate we've already handled response
    }
}
