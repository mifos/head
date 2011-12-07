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
package org.mifos.rest.approval.aop;

import org.aopalliance.intercept.MethodInvocation;
import org.mifos.rest.approval.aop.MethodInvocationHandler;

public class StubMethodInvocationHandler implements MethodInvocationHandler {

    public static final String EXECUTED = "executed";
    public static final String NOT_EXECUTED = "notExecuted";
    public static final String HI = "HI";

    private String executed = NOT_EXECUTED;

    private int noOfExecution = 0;

    @Override
    public Object process(MethodInvocation invocation) throws Throwable {
        executed = EXECUTED;
        noOfExecution+= 1;
        invocation.getArguments()[0] = HI;
        return invocation.proceed();
    }

    public String getExecuted() {
        return executed;
    }

    public void setExecuted(String executed) {
        this.executed = executed;
    }

    public int getNoOfExecution() {
        return noOfExecution;
    }

    public void setNoOfExecution(int noOfExecution) {
        this.noOfExecution = noOfExecution;
    }
}
