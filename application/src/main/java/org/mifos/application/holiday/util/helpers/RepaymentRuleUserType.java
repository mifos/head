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

package org.mifos.application.holiday.util.helpers;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import org.hibernate.HibernateException;
import org.hibernate.usertype.UserType;

/**
 * We are NOT using {@link org.hibernate.type.EnumType} because it persist based on ORDINAL (order of enums) values.
 * We want to store value(id) associated with the enum.
 */
public class RepaymentRuleUserType implements UserType {
    private int sqlType = Types.SMALLINT;

    public int[] sqlTypes() {
        return new int[] { sqlType };
    }

    public Class<RepaymentRuleTypes> returnedClass() {
        return RepaymentRuleTypes.class;
    }

    public boolean equals(Object x, Object y) throws HibernateException {
        return x == y;
    }

    public int hashCode(Object x) throws HibernateException {
        return x == null ? 0 : x.hashCode();
    }

    public Object nullSafeGet(ResultSet rs, String[] names, @SuppressWarnings("unused") Object owner) throws HibernateException, SQLException {
        Object object = rs.getObject(names[0]);
        if (rs.wasNull()) {
            return null;
        }
        if (object instanceof Number) {
            short value = ((Number) object).shortValue();
            for (RepaymentRuleTypes repaymentRuleTypes : RepaymentRuleTypes.values()) {
                if (value == repaymentRuleTypes.getValue()) {
                    return repaymentRuleTypes;
                }
            }
        }
        return null;
    }

    public void nullSafeSet(PreparedStatement st, Object obj, int index) throws HibernateException, SQLException {
        if (obj == null) {
            st.setNull(index, sqlType);
        } else {
            st.setObject(index, ((RepaymentRuleTypes) obj).getValue(), sqlType);
        }
    }

    public Object deepCopy(Object value) throws HibernateException {
        return value;
    }

    public boolean isMutable() {
        return false;
    }

    public Serializable disassemble(Object value) throws HibernateException {
        return (Serializable) value;
    }

    public Object assemble(Serializable cached, @SuppressWarnings("unused") Object owner) throws HibernateException {
        return cached;
    }

    public Object replace(Object original, @SuppressWarnings("unused") Object target,
            @SuppressWarnings("unused") Object owner) throws HibernateException {
        return original;
    }
}
