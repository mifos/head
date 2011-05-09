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

package org.mifos.platform.validations;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Errors implements Serializable {
    
    private final List<ErrorEntry> errorEntries;

    public Errors() {
        errorEntries = new ArrayList<ErrorEntry>();
    }

    public void addError(String fieldName, String errorCode) {
        errorEntries.add(new ErrorEntry(errorCode, fieldName));
    }

    public void addError(String errorCode, String[] args) {
        ErrorEntry errorEntry = new ErrorEntry(errorCode, "");
        errorEntry.setArgs(Arrays.asList(args));
        errorEntries.add(errorEntry);
    }

    public boolean hasErrors() {
        return !errorEntries.isEmpty();
    }

    public List<ErrorEntry> getErrorEntries() {
        return errorEntries;
    }

    public ErrorEntry getFieldError(String fieldName) {
        ErrorEntry result = null;
        for (ErrorEntry errorEntry : errorEntries) {
            if (errorEntry.isSameField(fieldName)) {
                result = errorEntry;
                break;
            }
        }
        return result;
    }

    public void addErrors(List<ErrorEntry> errorEntries) {
        this.errorEntries.addAll(errorEntries);
    }

    public void addErrors(Errors otherErrors) {
        for (ErrorEntry errorEntry : otherErrors.getErrorEntries()) {
            this.errorEntries.add(errorEntry);
        }
    }

    public boolean hasErrorEntryWithCode(String errorCode) {
        boolean result = false;
        if (hasErrors()) {
            for (ErrorEntry errorEntry : errorEntries) {
                if (errorCode.equals(errorEntry.getErrorCode())) {
                    result = true;
                    break;
                }
            }
        }
        return result;
    }
}
