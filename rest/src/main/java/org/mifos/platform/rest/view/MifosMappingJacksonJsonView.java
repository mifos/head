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

package org.mifos.platform.rest.view;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.springframework.web.servlet.view.json.MappingJacksonJsonView;

public class MifosMappingJacksonJsonView extends MappingJacksonJsonView {

    public MifosMappingJacksonJsonView() {
        super();
        ObjectMapper om = new ObjectMapper();
        om.configure(SerializationConfig.Feature.SORT_PROPERTIES_ALPHABETICALLY, true);
        om.configure(DeserializationConfig.Feature.USE_BIG_DECIMAL_FOR_FLOATS, true);
        om.configure(JsonGenerator.Feature.ESCAPE_NON_ASCII, false);
        om.configure(JsonParser.Feature.ALLOW_NUMERIC_LEADING_ZEROS, true);
        super.setObjectMapper(om);
    }



}
