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

package org.mifos.framework.components.logger;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Level;

public class TestLogger extends MifosLogger {

    private List<NonKey> nonKeyLogs = new ArrayList<NonKey>();

    public TestLogger() {
    }

    @Override
    public String getOfficeID() {
        return "test-office";
    }

    @Override
    public String getUserID() {
        return "test-user";
    }

    public int nonKeyCount() {
        return nonKeyLogs.size();
    }

    public Level nonKeyLevel(int index) {
        return nonKeyLogs.get(index).level;
    }

    public String nonKeyMessage(int index) {
        return nonKeyLogs.get(index).message;
    }

    @Override
    protected void logNonKey(Level level, String message, Throwable exception) {
        nonKeyLogs.add(new NonKey(level, message, exception));
    }

    @Override
    protected void logKey(Level level, String key, Object[] args1, Throwable exception) {
    }

    static class NonKey {
        final Level level;
        final String message;
        final Throwable exception;

        public NonKey(Level level, String message, Throwable exception) {
            this.level = level;
            this.message = message;
            this.exception = exception;
        }
    }

}
