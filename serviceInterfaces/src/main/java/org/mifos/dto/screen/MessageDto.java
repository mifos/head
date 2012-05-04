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
package org.mifos.dto.screen;

import java.io.Serializable;
import java.util.Arrays;

public class MessageDto implements Serializable {
    private static final long serialVersionUID = 8649079782681109659L;

    private final String msgKey;
    private final Object[] msgArgs;

    public MessageDto(final String msgKey) {
        this(msgKey, null);
    }

    public MessageDto(final String msgKey, final Object[] msgArgs) {
        this.msgKey = msgKey;
        this.msgArgs = (msgArgs == null) ? new Object[0] : msgArgs;
    }

    public String getMsgKey() {
        return msgKey;
    }

    public Object[] getMsgArgs() {
        return Arrays.copyOf(msgArgs, msgArgs.length);
    }
}
