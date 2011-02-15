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

package org.mifos.platform.questionnaire.domain;

public enum QuestionState {
    INACTIVE(0), ACTIVE(1), INACTIVE_NOT_EDITABLE(2), ACTIVE_NOT_EDITABLE(3);

    private int value;

    private QuestionState(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    @SuppressWarnings("PMD.AvoidThrowingRawExceptionTypes")
    public static QuestionState fromInt(int state) {
        for (QuestionState candidate : QuestionState.values()) {
            if (state == candidate.getValue()) {
                return candidate;
            }
        }
        throw new RuntimeException("no question state " + state);
    }

    public static QuestionState getQuestionStateEnum(boolean active, boolean editable) {
        return active ?
                editable ? ACTIVE : ACTIVE_NOT_EDITABLE :
                editable ? INACTIVE : INACTIVE_NOT_EDITABLE;
    }
}
