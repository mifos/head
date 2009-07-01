/*
 * Copyright (c) 2005-2009 Grameen Foundation USA
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

package org.mifos.framework.components.customTableTag;

import org.mifos.framework.exceptions.TableTagParseException;

public class LinkDetails {

    private String action = null;
    private ActionParam[] actionParam = null;

    public void setAction(String action) {
        this.action = action;
    }

    public String getAction() {
        return action;
    }

    public void setActionParam(ActionParam[] actionParam) {
        this.actionParam = actionParam;
    }

    public ActionParam[] getActionParam() {
        return actionParam;
    }

    public void generateLink(StringBuilder tableInfo, Object obj) throws TableTagParseException {
        tableInfo.append(" href=\"" + getAction() + "?");
        ActionParam[] actionParam = getActionParam();
        for (int i = 0; i < actionParam.length; i++) {
            actionParam[i].generateParameter(tableInfo, obj);
            if (i + 1 != actionParam.length)
                tableInfo.append("&");
        }
        tableInfo.append("\"");
    }

}
