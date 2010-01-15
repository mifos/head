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

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.engine.SessionImplementor;
import org.hibernate.type.Type;
import org.hibernate.usertype.CompositeUserType;
import org.mifos.application.master.business.MifosCurrency;
import org.mifos.config.AccountingRules;
import org.mifos.framework.components.configuration.business.Configuration;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;

/**
 * This class denotes a composite user type that has been created for the Money
 * class. This has been done so that the Money object can be persisted by
 * Hibernate
 */
public class MoneyCompositeUserType implements CompositeUserType {

    public MoneyCompositeUserType() {
        super();

    }

    /**
     * Properties that the Money class is composed off. These are the properties
     * that will be persisted
     */
    public String[] getPropertyNames() {
        return new String[] { "currency", "amount" };
    }

    public Type[] getPropertyTypes() {
        return new Type[] { Hibernate.SHORT, Hibernate.BIG_DECIMAL };
    }

    public Object getPropertyValue(Object component, int property) throws HibernateException {
        Money money = (Money) component;
        if (property == 0)
            return money.getCurrency();
        else
            return money.getAmount();
    }

    public void setPropertyValue(Object component, int property, Object value) throws HibernateException {

    }

    public Class returnedClass() {
        return Money.class;
    }

    public boolean equals(Object money1, Object money2) throws HibernateException {
        if (money1 == money2)
            return true;
        if (money1 == null || money2 == null)
            return false;
        return money1.equals(money2);
    }

    public int hashCode(Object arg0) throws HibernateException {
        return 0;
    }

    public Object nullSafeGet(ResultSet resultSet, String[] names, SessionImplementor session, Object owner)
            throws HibernateException, SQLException {
        MifosCurrency currency = null;
        if (resultSet == null)
            return null;
        Short currencyId = resultSet.getShort(names[0]);
        MifosCurrency configCurrency = Configuration.getInstance().getSystemConfig().getCurrency();
        // If currency id retrieved has a value of 0 or is null then the default
        // currency is retrieved. This
        // has been done so that there is a compatibility with M1 code
        if (currencyId == null || currencyId.shortValue() == 0 || currencyId.equals(configCurrency.getCurrencyId())) {
            currency = configCurrency;
        } else {
            currency = AccountingRules.getCurrencyByCurrencyId(currencyId);
        }

        BigDecimal value = resultSet.getBigDecimal(names[1]);
        return value == null ? new Money(currency, BigDecimal.valueOf(0)) : new Money(currency, value);
    }

    public void nullSafeSet(PreparedStatement statement, Object value, int index, SessionImplementor session)
            throws HibernateException, SQLException {
        if (value == null) {
            statement.setNull(index, Types.NUMERIC);
            statement.setNull(index + 1, Types.VARCHAR);
        } else {
            Money amount = (Money) value;
            Short currencyId = amount.getCurrency().getCurrencyId();
            statement.setShort(index, currencyId);
            statement.setBigDecimal(index + 1, amount.getAmount());
        }
    }

    public Object deepCopy(Object value) throws HibernateException {
        return value;
    }

    public boolean isMutable() {
        return false;
    }

    public Serializable disassemble(Object value, SessionImplementor session) throws HibernateException {

        return (Serializable) value;
    }

    public Object assemble(Serializable cached, SessionImplementor session, Object owner) throws HibernateException {

        return cached;
    }

    public Object replace(Object value, Object arg1, SessionImplementor session, Object arg3) throws HibernateException {

        return null;
    }

}
