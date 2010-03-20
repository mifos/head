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

package org.mifos.framework.components.batchjobs.exceptions;

import java.util.ArrayList;
import java.util.List;

import org.mifos.framework.exceptions.ApplicationException;

public class BatchJobException extends ApplicationException {
    private static final long serialVersionUID = 1L;

    protected final String key;

    protected final List<String> values;

    public BatchJobException(Throwable cause) {
        super(cause);
        this.key = null;
        values = new ArrayList<String>();
    }

    public BatchJobException(String key, List<String> values) {
        super(key);
        this.key = key;
        this.values = values;
    }

    @Override
    public String getKey() {
        if (null == key) {
            return "exception.framework.BatchJobException";
        } else {
            return this.key;
        }
    }

    public String getErrorMessage() {
        String errorMessage = null;
        if (values.size() > 0) {
            StringBuilder builder = new StringBuilder();
            for (String string : values) {
                builder.append(string);
                builder.append(",");
            }
            errorMessage = builder.substring(0, builder.length() - 1);
        } else {
            errorMessage = this.getMessage();
        }
        if (errorMessage.length() > 500) {
            return errorMessage.substring(0, 499);
        }
        return errorMessage;
    }
}
