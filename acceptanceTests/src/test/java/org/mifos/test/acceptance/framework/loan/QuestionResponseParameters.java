/*
 * Copyright (c) 2005-2011 Grameen Foundation USA
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

package org.mifos.test.acceptance.framework.loan;

import java.util.LinkedHashMap;
import java.util.Map;

public class QuestionResponseParameters {
    private final Map<String, String> textResponses = new LinkedHashMap<String, String>();
    private final Map<String, String> singleSelectResponses = new LinkedHashMap<String, String>();
    private final Map<String, Map<String, String>> smartSelectResponses = new LinkedHashMap<String, Map<String, String>>();

    public void addTextAnswer(String questionInputId, String answer) {
        textResponses.put(questionInputId, answer);
    }

    public void addSingleSelectAnswer(String questionInputId, String answer) {
        singleSelectResponses.put(questionInputId, answer);
    }

    public void addSmartSelectAnswer(String questionInputId, Map<String, String> answers) {
        smartSelectResponses.put(questionInputId, answers);
    }

    public Map<String, String> getTextResponses() {
        return textResponses;
    }

    public Map<String, String> getSingleSelectResponses() {
        return singleSelectResponses;
    }

    public Map<String, Map<String, String>> getSmartSelectResponses() {
        return smartSelectResponses;
    }
}
