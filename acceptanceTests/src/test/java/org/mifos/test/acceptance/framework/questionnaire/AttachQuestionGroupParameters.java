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

package org.mifos.test.acceptance.framework.questionnaire;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AttachQuestionGroupParameters {

    private String target;
    private String questionGroupName;

    private final Map<String, String> textResponses = new HashMap<String, String>();
    private final Map<String, List<String>> checkResponses = new HashMap<String, List<String>>();
    private final List<String> errorList = new ArrayList<String>();

    public String getTarget() {
        return this.target;
    }
    public void setTarget(String target) {
        this.target = target;
    }
    public String getQuestionGroupName() {
        return this.questionGroupName;
    }
    public void setQuestionGroupName(String questionGroupName) {
        this.questionGroupName = questionGroupName;
    }
    public Map<String, String> getTextResponses() {
        return this.textResponses;
    }
    public Map<String, List<String>> getCheckResponses() {
        return this.checkResponses;
    }

    public void addTextResponse(String question, String response) {
        this.textResponses.put(question, response);
    }

    public void addCheckResponse(String question, String response) {
        List<String> answers = this.checkResponses.get(question);
        if(answers == null) {
            answers = new ArrayList<String>();
        }
        answers.add(response);
        this.checkResponses.put(question, answers);
    }

    public void addError(String error) {
        this.errorList.add(error);
    }

    public String[] getErrors() {
        return this.errorList.toArray(new String[0]);
    }

}
