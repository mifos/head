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

package org.mifos.framework;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.StringReader;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import junit.framework.Assert;

import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.mifos.application.master.business.MifosCurrency;
import org.mifos.application.personnel.util.helpers.PersonnelConstants;
import org.mifos.application.personnel.util.helpers.PersonnelLevel;
import org.mifos.application.rolesandpermission.util.helpers.RolesAndPermissionConstants;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.spring.SpringUtil;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class TestUtils {

    /*
     * Supplied in a few tests, but not actually in master data. The test might
     * set up the role, or more likely the test doesn't need it to exist in the
     * database.
     */
    public static final int DUMMY_ROLE = 2;

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
        user.setLocaleId(TestObjectFactory.TEST_LOCALE);

        user.setMfiLocale(ukLocale());
        user.setMfiLocaleId(TestObjectFactory.TEST_LOCALE);
        return user;
    }

    /**
     * Also see {@link TestObjectFactory#getUserContext()} which should be
     * slower (it involves several database accesses).
     */
    public static UserContext makeUser(int role) {
        return makeUser(role, sampleLocale());
    }

    public static UserContext makeUser(int role, Locale locale) {
        UserContext user = new UserContext();
        user.setId(PersonnelConstants.SYSTEM_USER);
        user.setLocaleId(TestObjectFactory.TEST_LOCALE);
        Set<Short> set = new HashSet<Short>();
        set.add((short) role);
        user.setRoles(set);
        user.setLevel(PersonnelLevel.NON_LOAN_OFFICER);
        user.setName("mifos");
        user.setPreferredLocale(locale);
        user.setBranchId(TestObjectFactory.HEAD_OFFICE);
        user.setBranchGlobalNum("0001");
        return user;
    }

    public static Locale sampleLocale() {
        // return new Locale("en", "US");
        return new Locale("en", "GB");
    }

    /**
     * Corresponds to the locale one gets from
     * {@link TestObjectFactory#getUserContext()}.
     */
    public static Locale ukLocale() {
        return new Locale("EN", "GB");
    }

    public static final MifosCurrency RUPEE = new MifosCurrency((short) 2, "RUPEE", 1.0f, (short) 1, "INR");

    public static final MifosCurrency EURO = new MifosCurrency((short) 3, "EURO", 0.5f, (short) 1, "EUR");

    public static void assertWellFormedFragment(String xml) throws DocumentException {
        assertWellFormedDocument("<root>" + xml + "</root>");
    }

    public static void assertWellFormedDocument(String xmlDocument) throws DocumentException {
        SAXReader reader = new SAXReader();
        reader.read(new StringReader(xmlDocument));
    }

    /*
     * Here is our equals/hashCode testing framework. Is there really not just
     * one to download? This wheel gets reinvented so often. The one in
     * junitx.extensions.EqualsHashCodeTestCase is seriously broken - it often
     * gets confused about which equals method it is testing (e.g. the one from
     * Object or the one under test) and similar problems.
     */

    /**
     * Verify equals contract. A single call to this method will generally
     * suffice to test equals and hashCode. Just make sure to pass in enough
     * examples of equal and not-equal objects to cover each of the cases in
     * your equals implementation. Generally there should be an instance of a
     * subclass somewhere in the data you pass. The null case is always checked
     * and should not be passed in either the equalArray or noEqualArray.
     * 
     * @param equalArray
     *            - an array of class T containing at least 2 elements which are
     *            all equal to one another (eg. new Foo[] {new Foo(5), new
     *            Foo(5)})
     * @param notEqualArray
     *            - an array of class T containing at least 1 element all of
     *            which are not equal to the equalArray[0] parameter (eg. new
     *            Foo[] {Foo(4)} )
     */
    public static <T> void verifyBasicEqualsContract(T[] equalArray, T[] notEqualArray) {
        if (equalArray.length < 2) {
            Assert.fail("equalArray requires at least 2 elements (but only had " + equalArray.length + ")");
        }
        if (notEqualArray.length < 1) {
            Assert.fail("notEqualArray requires at least 1 element");
        }
        T equalObject = equalArray[0];

        // verify equals contract for equal objects of the same class
        assertAllEqual(equalArray);

        // verify inequality of an objects of the same class
        for (T notEqual : notEqualArray) {
            assertIsNotEqual(equalObject, notEqual);
        }

        // verify inequality of an unrelated class
        assertIsNotEqual(equalObject, new Object());
    }

    public static void assertAllEqual(Object[] objects) {
        /**
         * The point of checking each pair is to make sure that equals is
         * transitive per the contract of
         * {@link Object#equals(java.lang.Object)}.
         */
        for (int i = 0; i < objects.length; i++) {
            Assert.assertNotNull("You don't need to pass null; null is checked for you", objects[i]);
            Assert.assertFalse(objects[i].equals(null));
            for (int j = 0; j < objects.length; j++) {
                assertIsEqual(objects[i], objects[j]);
            }
        }
    }

    /**
     * The reason this method should only be called from
     * {@link #assertAllEqual(Object[])} is that the latter checks for reflexive
     * and null.
     */
    private static void assertIsEqual(Object one, Object two) {
        Assert.assertTrue(one.equals(two));
        Assert.assertTrue(two.equals(one));
        Assert.assertEquals(one.hashCode(), two.hashCode());
    }

    public static void assertIsNotEqual(Object one, Object two) {
        assertReflexiveAndNull(one);
        assertReflexiveAndNull(two);
        Assert.assertFalse(one.equals(two));
        Assert.assertFalse(two.equals(one));

        /*
         * The hashCodes may or may not be equal, but they shouldn't throw an
         * exception.
         */
        one.hashCode();
        two.hashCode();
    }

    public static void assertReflexiveAndNull(Object object) {
        Assert.assertNotNull("You don't need to pass null; null is checked for you", object);
        Assert.assertTrue(object.equals(object));
        Assert.assertFalse(object.equals(null));
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

    /**
     * This method initializes the Spring framework context.
     */
    public static void initializeSpring() {
        SpringUtil.initializeSpring();
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
    
    public static MifosCurrency getCurrency() {
        // TODO: will be replaced by a better way to get currency for integration tests
        // NOTE: TestObjectFactory.getCurrency also exists
        return Money.getDefaultCurrency();
    }

}
