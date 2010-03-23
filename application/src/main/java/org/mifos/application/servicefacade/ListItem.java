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
package org.mifos.application.servicefacade;

import java.io.Serializable;

/**
 * I am a DTO for items within a list e.g. a dropdown list.
 */
public class ListItem<T> implements Serializable {

    private final T id;
    private final String displayValue;

    public ListItem(T id, String displayValue) {
        this.id = id;
        this.displayValue = displayValue;
    }

    public T getId() {
        return this.id;
    }

    public String getDisplayValue() {
        return this.displayValue;
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean equals(Object obj) {
        ListItem<T> item = (ListItem<T>) obj;

        return (item.id.equals(this.id) && item.displayValue.equals(this.displayValue));
    }

    @Override
    public int hashCode() {
        return id.hashCode() * displayValue.hashCode();
    }

    @Override
    public String toString() {
        return "Item: id: " + this.id + " displayValue: " + this.displayValue;
    }
}
