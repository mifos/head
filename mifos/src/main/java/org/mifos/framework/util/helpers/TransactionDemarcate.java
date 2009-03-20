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
 
package org.mifos.framework.util.helpers;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.mifos.framework.struts.action.BaseAction;

/**
 * This interface is used to annotate methods in the struts action class for transaction boundries
 * specifying when a new transaction is begining so that based on that a token can be generated and saved
 * in the session or removed from the session.
 * 1.saveToken - It saves a new token in the session.
 * 2.validateAndResetToken - It validates the token and then resets the token.
 * 3.joinToken - It checks if the token is present it does nothing else it 
 * saves a new token in the session.
 * 
 * @see BaseAction
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface TransactionDemarcate {
	public boolean saveToken() default false;
	public boolean validateAndResetToken() default false;
	public boolean joinToken() default false;
	public boolean conditionToken() default false;
	
}
