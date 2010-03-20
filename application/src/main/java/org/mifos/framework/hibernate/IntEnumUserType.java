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

package org.mifos.framework.hibernate;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import org.hibernate.HibernateException;
import org.hibernate.usertype.UserType;

/**
 * For more information check out http://www.hibernate.org/312.html
 *
 * Generic class to map a JDK 1.5 enum to a SMALL INT column in the DB. Beware
 * that the enum.ordinal() relates to the ORDERING of the enum values, so, if
 * your change it later on, all your DB values will return an incorrect value!
 *
 * @author Benoit Xhenseval
 * @version 1
 */
public class IntEnumUserType<E extends Enum<E>> implements UserType {
    private Class<E> clazz = null;
    private E[] theEnumValues;

    /**
     * Contrary to the example mapping to a VARCHAR, this would
     *
     * @param c
     *            the class of the enum.
     * @param e
     *            The values of enum (by invoking .values()).
     */
    protected IntEnumUserType(Class<E> c, E[] e) {
        this.clazz = c;
        this.theEnumValues = e;
    }

    private static final int[] SQL_TYPES = { Types.SMALLINT };

    /**
     * simple mapping to a SMALLINT.
     */
    public int[] sqlTypes() {
        return SQL_TYPES;
    }

    public Class returnedClass() {
        return clazz;
    }

    /**
     * From the SMALLINT in the DB, get the enum. Because there is no
     * Enum.valueOf(class,int) method, we have to iterate through the given
     * enum.values() in order to find the correct "int".
     */
    public Object nullSafeGet(ResultSet resultSet, String[] names, Object owner) throws HibernateException,
            SQLException {
        final int val = resultSet.getShort(names[0]);
        E result = null;
        if (!resultSet.wasNull()) {
            try {
                for (int i = 0; i < theEnumValues.length && result == null; i++) {
                    if (theEnumValues[i].ordinal() == val) {
                        result = theEnumValues[i];
                    }
                }
            } catch (SecurityException e) {
                result = null;
            } catch (IllegalArgumentException e) {
                result = null;
            }
        }
        return result;
    }

    /**
     * set the SMALLINT in the DB based on enum.ordinal() value, BEWARE this
     * could change.
     */
    public void nullSafeSet(PreparedStatement preparedStatement, Object value, int index) throws HibernateException,
            SQLException {
        if (null == value) {
            preparedStatement.setNull(index, Types.SMALLINT);
        } else {
            preparedStatement.setInt(index, ((Enum) value).ordinal());
        }
    }

    public Object deepCopy(Object value) throws HibernateException {
        return value;
    }

    public boolean isMutable() {
        return false;
    }

    public Object assemble(Serializable cached, Object owner) throws HibernateException {
        return cached;
    }

    public Serializable disassemble(Object value) throws HibernateException {
        return (Serializable) value;
    }

    public Object replace(Object original, Object target, Object owner) throws HibernateException {
        return original;
    }

    public int hashCode(Object x) throws HibernateException {
        return x.hashCode();
    }

    public boolean equals(Object x, Object y) throws HibernateException {
        if (x == y) {
            return true;
        }
        if (null == x || null == y) {
            return false;
        }
        return x.equals(y);
    }
}
