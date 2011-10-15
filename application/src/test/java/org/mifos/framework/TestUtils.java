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

package org.mifos.framework;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.StringReader;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import junit.framework.Assert;

import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;
import org.joda.time.DateMidnight;
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.LocalDate;
import org.mifos.accounts.business.AccountActionDateEntity;
import org.mifos.application.master.business.MifosCurrency;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.meeting.util.helpers.WeekDay;
import org.mifos.config.Localization;
import org.mifos.customers.business.CustomerBO;
import org.mifos.customers.business.CustomerScheduleEntity;
import org.mifos.customers.personnel.util.helpers.PersonnelConstants;
import org.mifos.customers.personnel.util.helpers.PersonnelLevel;
import org.mifos.framework.util.helpers.Money;
import org.mifos.schedule.ScheduledEvent;
import org.mifos.schedule.ScheduledEventFactory;
import org.mifos.security.rolesandpermission.util.helpers.RolesAndPermissionConstants;
import org.mifos.security.util.UserContext;
import org.springframework.util.ReflectionUtils;

public class TestUtils {

    /*
     * Supplied in a few tests, but not actually in master data. The test might
     * set up the role, or more likely the test doesn't need it to exist in the
     * database.
     */
    public static final int DUMMY_ROLE = 2;
    public static final int TEST_ROLE = 3;
    private static final Short TEST_LOCALE = 1;
    private static final Short HEAD_OFFICE = 1;

    public static UserContext makeUser() {
        return makeUser(RolesAndPermissionConstants.ADMIN_ROLE);
    }

    /**
     * This is a closer simulation to real life than {@link #makeUser()} in
     * terms of the locales. It probably could replace it, although I haven't
     * tried to see whether that breaks anything.
     */
    public static UserContext makeUserWithLocales() {
        UserContext user = makeUser(RolesAndPermissionConstants.ADMIN_ROLE, ukLocale());
        user.setLocaleId(TEST_LOCALE);

        user.setMfiLocale(ukLocale());
        user.setMfiLocaleId(TEST_LOCALE);
        return user;
    }

    /**
     * Also see TestObjectFactory#getUserContext() which should be
     * slower (it involves several database accesses).
     */
    public static UserContext makeUser(int role) {
        return makeUser(role, sampleLocale());
    }

    public static UserContext makeUser(int role, Locale locale) {
        UserContext user = new UserContext();
        user.setPreferredLocale(Locale.UK);
        user.setLocaleId(Localization.ENGLISH_LOCALE_ID);
        user.setId(PersonnelConstants.SYSTEM_USER);
        user.setLocaleId(TEST_LOCALE);
        Set<Short> set = new HashSet<Short>();
        set.add((short) role);
        user.setRoles(set);
        user.setLevel(PersonnelLevel.NON_LOAN_OFFICER);
        user.setName("mifos");
        user.setPreferredLocale(locale);
        user.setBranchId(HEAD_OFFICE);
        user.setBranchGlobalNum("0001");
        return user;
    }

    public static Locale sampleLocale() {
        // return new Locale("en", "US");
        return new Locale("en", "GB");
    }

    /**
     * Corresponds to the locale one gets from
     * TestObjectFactory#getUserContext()
     */
    public static Locale ukLocale() {
        return new Locale("EN", "GB");
    }

    public static final MifosCurrency RUPEE = new MifosCurrency((short) 2, "RUPEE", BigDecimal.valueOf(1.0), "INR");

    public static final MifosCurrency EURO = new MifosCurrency((short) 3, "EURO", BigDecimal.valueOf(0.5), "EUR");

    public static void assertWellFormedFragment(String xml) throws DocumentException {
        assertWellFormedDocument("<root>" + xml + "</root>");
    }

    public static void assertWellFormedDocument(String xmlDocument) throws DocumentException {
        SAXReader reader = new SAXReader();
        reader.read(new StringReader(xmlDocument));
    }

    public static Money createMoney(String amount) {
        return new Money(RUPEE, amount);
    }

    public static Money createMoney(Double amount) {
        return new Money(RUPEE, BigDecimal.valueOf(amount));
    }

    public static Money createMoney(int amount) {
        // TODO Auto-generated method stub
        return new Money(RUPEE, BigDecimal.valueOf(amount));
    }

    public static Money createMoney() {
        return new Money(RUPEE);
    }

    /* end equals testing methods */

    public static void assertCanSerialize(Object object) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
        objectOutputStream.writeObject(object);
        objectOutputStream.close();
        Assert.assertTrue(byteArrayOutputStream.toByteArray().length > 0);
    }

    public static void showMemory() {
        System.out.println("free: " + Runtime.getRuntime().freeMemory() / 1000000.0 + " MB");
        System.out.println("max: " + Runtime.getRuntime().maxMemory() / 1000000.0 + " MB");
        System.out.println("total: " + Runtime.getRuntime().totalMemory() / 1000000.0 + " MB");
        System.out.println();
    }

    public static Date generateNearestMondayOnOrAfterToday() {
        // start from today's date and then add enough days to make it a Monday
        DateTime dateTime = new DateTime();
        Date startDate = null;
        DateTime dateTimeMonday = null;
        int dayOfWeek = dateTime.getDayOfWeek();

        if (dayOfWeek == DateTimeConstants.MONDAY) {
            startDate = dateTime.toDate();
            dateTimeMonday = dateTime;
        } else {
            dateTimeMonday = dateTime.plusDays(7 - (dayOfWeek - DateTimeConstants.MONDAY));
            startDate = dateTimeMonday.toDate();
        }
        return startDate;
    }

    /**
     * x != notx <br/><br/>
     * x = y = z (but different objects)
     *
     * @param x
     * @param notx
     * @param y
     * @param z
     */
    public static void assertEqualsAndHashContract(Object x, Object notx, Object y, Object z) {
        assertEqual_ToSelf(x);
        assertPassIncompatibleType_isFalse(x);
        assertNullReference_isFalse(x);
        assertEquals_isReflexive_isSymmetric(x, y);
        assertEquals_isTransitive(x, y, z);
        assertEquals_isConsistent(x, y, notx);
        assertHashcode_isConsistent(x);
        assertHashcode_twoEqualsObjects_produceSameNumber(x, y);
        assertHashcode_twoUnEqualObjects_produceDifferentNumber(x, notx);
    }

    /**
     * A class is equal to itself.
     */
    private static void assertEqual_ToSelf(Object x) {

        Assert.assertTrue("Class equal to itself.", x.equals(x));
    }

    /**
     * x.equals(WrongType) must return false;
     */
    private static void assertPassIncompatibleType_isFalse(Object x) {
        Assert.assertFalse("Passing incompatible object to equals should return false", x.equals("string"));
    }

    /**
     * x.equals(null) must return false;
     */
    private static void assertNullReference_isFalse(Object x) {
        Assert.assertFalse("Passing null to equals should return false", x.equals(null));
    }

    /**
     * 1. x, x.equals(x) must return true. 2. x and y, x.equals(y) must return true if and only if y.equals(x) returns
     * true.
     */
    private static void assertEquals_isReflexive_isSymmetric(Object x, Object y) {

        Assert.assertTrue("Reflexive test fail x,y", x.equals(y));
        Assert.assertTrue("Symmetric test fail y", y.equals(x));

    }

    /**
     * 1. x.equals(y) returns true 2. y.equals(z) returns true 3. x.equals(z) must return true
     */
    private static void assertEquals_isTransitive(Object x, Object y, Object z) {

        Assert.assertTrue("Transitive test fails x,y", x.equals(y));
        Assert.assertTrue("Transitive test fails y,z", y.equals(z));
        Assert.assertTrue("Transitive test fails x,z", x.equals(z));
    }

    /**
     * Repeated calls to equals consistently return true or false.
     */
    private static void assertEquals_isConsistent(Object x, Object y, Object notx) {

        Assert.assertTrue("Consistent test fail x,y", x.equals(y));
        Assert.assertTrue("Consistent test fail x,y", x.equals(y));
        Assert.assertTrue("Consistent test fail x,y", x.equals(y));
        Assert.assertFalse("Consistent test fail x,notx", notx.equals(x));
        Assert.assertFalse("Consistent test fail x,notx", notx.equals(x));
        Assert.assertFalse("Consistent test fail x,notx", notx.equals(x));

    }

    /**
     * Repeated calls to hashcode should consistently return the same integer.
     */
    private static void assertHashcode_isConsistent(Object x) {

        int initial_hashcode = x.hashCode();

        Assert.assertEquals("Consistent hashcode test fails", initial_hashcode, x.hashCode());
        Assert.assertEquals("Consistent hashcode test fails", initial_hashcode, x.hashCode());
    }

    /**
     * Objects that are equal using the equals method should return the same integer.
     */
    private static void assertHashcode_twoEqualsObjects_produceSameNumber(Object x, Object y) {

        int xhashcode = x.hashCode();
        int yhashcode = y.hashCode();

        Assert.assertEquals("Equal object, return equal hashcode test fails", xhashcode, yhashcode);
    }

    /**
     * A more optimal implementation of hashcode ensures that if the objects are unequal different integers are
     * produced.
     */
    private static void assertHashcode_twoUnEqualObjects_produceDifferentNumber(Object x, Object notx) {

        int xhashcode = x.hashCode();
        int yhashcode = notx.hashCode();

        Assert.assertTrue("Equal object, return unequal hashcode test fails", !(xhashcode == yhashcode));
    }

    public static DateTime nearestDateMatchingPeriodStartingOn(DateTime inclusiveOf, MeetingBO period) {
        ScheduledEvent scheduledEvent = ScheduledEventFactory.createScheduledEventFrom(period);
        return scheduledEvent.nearestMatchingDateBeginningAt(inclusiveOf);
    }

    public static void assertThatAllCustomerSchedulesOccuringBeforeOrOnCurrentInstallmentPeriodRemainUnchanged(CustomerBO customer, WeekDay expectedDayOfWeek) {
        Set<AccountActionDateEntity> customerSchedules = customer.getCustomerAccount().getAccountActionDates();
        for (AccountActionDateEntity accountActionDateEntity : customerSchedules) {
            CustomerScheduleEntity customerSchedule = (CustomerScheduleEntity) accountActionDateEntity;

            LocalDate scheduledDate = new LocalDate(customerSchedule.getActionDate());
            DateTime endOfCurrentInstallmentPeriod = TestUtils.nearestDateMatchingPeriodStartingOn(new DateMidnight().toDateTime(), customer.getCustomerMeetingValue());
            LocalDate endOfCurrentInstallmentPeriodLocalDate = new LocalDate(endOfCurrentInstallmentPeriod);

            if (scheduledDate.isBefore(endOfCurrentInstallmentPeriodLocalDate)) {
                assertThat(scheduledDate.dayOfWeek().get(), is(WeekDay.getJodaDayOfWeekThatMatchesMifosWeekDay(expectedDayOfWeek.getValue())));
            }
        }
    }

    public static void assertThatAllCustomerSchedulesOccuringAfterCurrentInstallmentPeriodFallOnDayOfWeek(CustomerBO customer, WeekDay expectedDayOfWeek) {
        Set<AccountActionDateEntity> customerSchedules = customer.getCustomerAccount().getAccountActionDates();
        for (AccountActionDateEntity accountActionDateEntity : customerSchedules) {
            CustomerScheduleEntity customerSchedule = (CustomerScheduleEntity) accountActionDateEntity;

            LocalDate scheduledDate = new LocalDate(customerSchedule.getActionDate());
            DateTime endOfCurrentInstallmentPeriod = TestUtils.nearestDateMatchingPeriodStartingOn(new DateMidnight().toDateTime(), customer.getCustomerMeetingValue());

            if (scheduledDate.isAfter(new LocalDate(endOfCurrentInstallmentPeriod))) {
                assertThat(scheduledDate.dayOfWeek().get(), is(WeekDay.getJodaDayOfWeekThatMatchesMifosWeekDay(expectedDayOfWeek.getValue())));
            }
        }
    }

    public static void dereferenceObjects(final Object testCase) {
        ReflectionUtils.doWithFields(testCase.getClass(), new ReflectionUtils.FieldCallback() {
            @SuppressWarnings("cast")
            @Override
            public void doWith(Field field) throws IllegalArgumentException, IllegalAccessException {
                field.setAccessible(true);
                try {
                    if (Modifier.isStatic(field.getModifiers()) || field.getType().isPrimitive()) {
                        return;
                    }
                    if (field.get(testCase) instanceof Object) {
                        field.set(testCase, null);
                    }
                }
                finally {
                    field.setAccessible(false);
                }
            }
        }, new ReflectionUtils.FieldFilter() {
            @Override
            public boolean matches(Field field) {
                return field.getDeclaringClass().equals(testCase.getClass());
            }
        });
    }

    public static Date getDate(int date, int month, int year) {
        DateMidnight dateMidnight = new DateMidnight(year, month, date);
        return dateMidnight.toDate();
    }

    public static DateTime getDateTime(int date, int month, int year) {
        DateMidnight dateMidnight = new DateMidnight(year, month, date);
        return dateMidnight.toDateTime();
    }

    public static java.sql.Date getSqlDate(int date, int month, int year) {
        return new java.sql.Date(getDate(date, month, year).getTime());
    }
}
