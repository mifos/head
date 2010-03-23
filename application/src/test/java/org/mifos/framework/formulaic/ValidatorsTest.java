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

package org.mifos.framework.formulaic;

import java.math.BigInteger;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.joda.time.DateMidnight;
import org.testng.annotations.Test;

@Test(groups={"unit", "fastTestsSuite"},  dependsOnGroups={"productMixTestSuite"})
public class ValidatorsTest extends TestCase {

    private void checkException(Validator val, Object input, String error) {
        try {
            val.validate(input);
            Assert.fail();
        } catch (ValidationError e) {
           Assert.assertTrue(e.getMsg().endsWith(error));
        }

    }

    public void testDateComponentValidator() throws Exception {
        Validator validator = new DateComponentValidator();
        Map<String, String> inputMap = new HashMap<String, String>();
        Date expectedDate = new DateMidnight(2000, 05, 20).toDate();
        inputMap.put("DD", "20");
        inputMap.put("MM", "5");
        inputMap.put("YY", "2000");
       Assert.assertEquals(expectedDate, validator.validate(inputMap));

        inputMap.put("MM", "20");
        checkException(validator, inputMap, ErrorType.DATE_FORMAT.toString());

        Schema schema = new Schema();
        schema.setMapValidator("mydate", validator);
        inputMap = new HashMap<String, String>();
        inputMap.put("mydate_DD", "20");
        inputMap.put("mydate_MM", "5");
        inputMap.put("mydate_YY", "2000");
       Assert.assertEquals(expectedDate, schema.validate(inputMap).get("mydate"));

    }

    public void testDateValidator() throws Exception {
        Validator validator = new DateValidator();
        Date expectedDate = new DateMidnight(2005, 04, 03).toDate();
       Assert.assertEquals(expectedDate, validator.validate("3/4/2005"));
        checkException(validator, "5/13/2000", ErrorType.DATE_FORMAT.toString());
    }

    public void testSchemaCompoundFunctionality() {
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("simple", "a");
        data.put("map_one", "b");
        data.put("map_two", "c");
       Assert.assertEquals("a", Schema.parseField("simple", Schema.FieldType.SIMPLE, data));
        Assert.assertNull(Schema.parseField("notafield", Schema.FieldType.SIMPLE, data));
        Map<String, Object> expectedResults = new HashMap<String, Object>();
        expectedResults.put("one", "b");
        expectedResults.put("two", "c");
       Assert.assertEquals(expectedResults, Schema.parseField("map", Schema.FieldType.MAP, data));
    }

    private enum TestEnum {
        VALUE_ONE,
    }

    public void testEnumValidator() throws Exception {
        Validator val = new EnumValidator(TestEnum.class);
       Assert.assertEquals(TestEnum.VALUE_ONE, val.validate("VALUE_ONE"));
       Assert.assertEquals(TestEnum.VALUE_ONE, val.validate("value_one"));
        checkException(val, null, ErrorType.MISSING.toString());
        checkException(val, "bad value", ErrorType.INVALID_ENUM.toString());
    }

    public void testSwitchValidator() throws Exception {
        SwitchValidator val = new SwitchValidator("switch_field");
        Schema schemaA = new Schema();
        schemaA.setSimpleValidator("age", new IntValidator());
        val.addCase("a", schemaA);
        Schema schemaB = new Schema();
        schemaB.setSimpleValidator("name", new MaxLengthValidator(5));
        val.addCase("b", schemaB);

        Map<String, String> data = new HashMap<String, String>();
        data.put("switch_field", "a");
        data.put("age", "2");
        Map<String, Object> results = val.validate(data);
       Assert.assertEquals(2, results.get("age"));

        data.put("name", "bob");
        data.put("switch_field", "b");
        results = val.validate(data);
       Assert.assertEquals("bob", results.get("name"));

        try {
            data.put("switch_field", "c");
            val.validate(data);
            Assert.fail();
        } catch (ValidationError e) {
           Assert.assertEquals(val.getKey(ErrorType.MISSING_EXPECTED_VALUE.toString()), e.getMsg());
        }

        val.setDefaultCase(schemaB);
        val.validate(data);
    }

    public void testIntValidator() throws Exception {
        Validator val = new IntValidator();
       Assert.assertEquals(42, val.validate("42"));
       Assert.assertEquals(42, val.validate("042"));
        checkException(val, "notanumber", ErrorType.INVALID_INT.toString());
        // check error when given wrong type
        checkException(val, new LinkedList(), ErrorType.WRONG_TYPE.toString());
        checkException(val, " 042", ErrorType.INVALID_INT.toString());
    }

    public void testIsInstanceValidator() throws Exception {
        Validator val = new IsInstanceValidator(Number.class);
       Assert.assertEquals(42, val.validate(42));
       Assert.assertEquals(new BigInteger("42"), val.validate(new BigInteger("42")));
        checkException(val, "a string", ErrorType.WRONG_TYPE.toString());
    }

    public void testIdentityValidator() throws Exception {
        Validator val = new IdentityValidator();
        String input = "a string";
       Assert.assertEquals(input, val.validate(input));
       Assert.assertEquals(null, val.validate(null));
    }

    public void testNullValidator() throws Exception {
        Validator val = new NullValidator(new IntValidator());
       Assert.assertEquals(null, val.validate(null));
       Assert.assertEquals(42, val.validate("42"));
        checkException(val, "a string", ErrorType.INVALID_INT.toString());
    }

    public void testMaxLengthValidator() throws Exception {
        Validator val = new MaxLengthValidator(4);
       Assert.assertEquals("abc", val.validate("abc"));
       Assert.assertEquals("abcd", val.validate("abcd"));
        checkException(val, "toolong", ErrorType.STRING_TOO_LONG.toString());
    }

    public void testSchema() throws Exception {
        Schema schema = new Schema();
        BaseValidator intValidator = new IntValidator();
        schema.setSimpleValidator("fieldOne", intValidator);
        BaseValidator maxLengthValidator = new MaxLengthValidator(4);
        schema.setSimpleValidator("fieldTwo", maxLengthValidator);
        Map<String, String> input = new HashMap<String, String>();
        input.put("fieldOne", "42");
        input.put("fieldTwo", "abcd");
        Map<String, Object> results = schema.validate(input);
       Assert.assertTrue(42 == (Integer) results.get("fieldOne"));
       Assert.assertEquals("abcd", (String) results.get("fieldTwo"));

        input.put("fieldOne", null);
        input.put("fieldTwo", "abcde");
        try {
            schema.validate(input);
            Assert.fail();
        } catch (SchemaValidationError e) {
           Assert.assertEquals(2, e.size());
           Assert.assertEquals(maxLengthValidator.getKey(ErrorType.STRING_TOO_LONG.toString()), e.getFieldMsg("fieldTwo"));
           Assert.assertEquals(intValidator.getKey(ErrorType.MISSING.toString()), e.getFieldMsg("fieldOne"));
        }
        // now make field one optional
        schema.setSimpleValidator("fieldOne", new NullValidator(new IntValidator()));
        try {
            schema.validate(input);
            Assert.fail();
        } catch (SchemaValidationError e) {
           Assert.assertEquals(1, e.size());
           Assert.assertEquals(maxLengthValidator.getKey(ErrorType.STRING_TOO_LONG.toString()), e.getFieldMsg("fieldTwo"));
        }
    }

    // the itemsource version of OneOfValidator is intended to
    // be used with inner classes (perhaps anonymous) like this..
    // so that instance methods to access hibernate persistence
    // will work
    private class TestItemSource implements OneOfValidatorSource {
        public Collection getItems() {
            LinkedList<String> items = new LinkedList<String>();
            items.add("one");
            items.add("two");
            return items;
        }
    }

    public void testOneOfValidator() throws Exception {
        LinkedList<String> items = new LinkedList<String>();
        items.add("one");
        items.add("two");
        Validator val = new OneOfValidator(items);
       Assert.assertEquals("one", val.validate("one"));
        checkException(val, null, ErrorType.MISSING.toString());
        checkException(val, "three", ErrorType.NOT_A_CHOICE.toString());

        val = new OneOfValidator(new TestItemSource());
       Assert.assertEquals("one", val.validate("one"));
        checkException(val, null, ErrorType.MISSING.toString());
        checkException(val, "three", ErrorType.NOT_A_CHOICE.toString());
    }
}
