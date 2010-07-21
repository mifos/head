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

package org.mifos.platform.questionnaire.service;

import java.io.Serializable;

public class EventSource implements Serializable{
    private String source;
    private String event;
    private String description;
    private static final long serialVersionUID = 285579716279021785L;

    public EventSource() {
        //FIXEME: why does spring crib for not having a default constructor
        //for this class, when this class is not hooked onto spring?
        //TODO: Investigate later
        super();
    }

    public EventSource(String event, String source, String description) {
        super();
        this.event = event;
        this.source = source;
        this.description = description;
    }

    public String getSource() {
        return source;
    }

    public String getEvent() {
        return event;
    }

    public String getDescription() {
        return description;
    }
}
