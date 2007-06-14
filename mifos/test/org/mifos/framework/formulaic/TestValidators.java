package org.mifos.framework.formulaic;

import java.math.BigInteger;
import java.text.DateFormat;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import junit.framework.TestCase;

public class TestValidators extends TestCase {
	
	private void checkException(Validator val, Object input, String error) {
		try {
			val.validate(input);
			fail();
		}
		catch (ValidationError e) {
			assertEquals(error, e.getMsg());
		}
		
	}
	
	public void testSwitchValidator() throws Exception {
		SwitchValidator val = new SwitchValidator("switch_field");
		Schema schemaA = new Schema();
		schemaA.setValidator("age", new IntValidator());
		val.addCase("a", schemaA);
		Schema schemaB = new Schema();
		schemaB.setValidator("name", new MaxLengthValidator(5));
		val.addCase("b", schemaB);
		
		Map<String, String> data = new HashMap<String, String>();
		data.put("switch_field", "a");
		data.put("age", "2");
		Map<String, Object> results = val.validate(data);
		assertEquals(2, results.get("age"));
		
		data.put("name", "bob");
		data.put("switch_field", "b");
		results = val.validate(data);
		assertEquals("bob", results.get("name"));
		
		try {
			data.put("switch_field", "c");
			val.validate(data);
			fail();
		}
		catch (ValidationError e) {
			assertEquals(SwitchValidator.MISSING_EXPECTED_VALUE_ERROR, e.getMsg());
		}
		
		val.setDefaultCase(schemaB);
		val.validate(data);
	}
	
	public void testIntValidator() throws Exception {
		Validator val = new IntValidator();
		assertEquals(42, val.validate("42"));
		assertEquals(42, val.validate("042"));
		checkException(val, "notanumber", IntValidator.PARSE_ERROR);
		// check error when given wrong type
		checkException(val, new LinkedList(), IntValidator.WRONG_TYPE_ERROR);
		checkException(val, " 042", IntValidator.PARSE_ERROR);	
	}
	
	public void testIsInstanceValidator() throws Exception {
		Validator val = new IsInstanceValidator(Number.class);
		assertEquals(42, val.validate(42));
		assertEquals(new BigInteger("42"), val.validate(new BigInteger("42")));
		checkException(val, "a string", IsInstanceValidator.WRONG_TYPE_ERROR);
	}
	
	public void testIdentityValidator() throws Exception {
		Validator val = new IdentityValidator();
		String input = "a string";
		assertEquals(input, val.validate(input));
		assertEquals(null, val.validate(null));
	}
	
	public void testNullValidator() throws Exception {
		Validator val = new NullValidator(new IntValidator());
		assertEquals(null, val.validate(null));
		assertEquals(42, val.validate("42"));
		checkException(val, "a string", IntValidator.PARSE_ERROR);
	}
	
	public void testMaxLengthValidator() throws Exception {
		Validator val = new MaxLengthValidator(4);
		assertEquals("abc", val.validate("abc"));
		assertEquals("abcd", val.validate("abcd"));
		checkException(val, "toolong", MaxLengthValidator.TOO_LONG_ERROR);
	}
	
	public void testSchema() throws Exception {
		Schema schema = new Schema();
		schema.setValidator("fieldOne", new IntValidator());
		schema.setValidator("fieldTwo", new MaxLengthValidator(4));
		Map<String, String> input = new HashMap<String, String>();
		input.put("fieldOne", "42");
		input.put("fieldTwo", "abcd");
		Map<String, Object> results = schema.validate(input);
		assertTrue(42 == (Integer) results.get("fieldOne"));
		assertEquals("abcd", (String) results.get("fieldTwo"));
		
		input.put("fieldOne", null);
		input.put("fieldTwo", "abcde");
		try {
			schema.validate(input);
			fail();
		}
		catch (SchemaValidationError e) {
			assertEquals(2, e.size());
			assertEquals(MaxLengthValidator.TOO_LONG_ERROR, e.getFieldMsg("fieldTwo"));
			assertEquals(BaseValidator.MISSING_ERROR, e.getFieldMsg("fieldOne"));
		}
		// now make field one optional
		schema.setValidator("fieldOne", new NullValidator(new IntValidator()));
		try {
			schema.validate(input);
			fail();
		}
		catch(SchemaValidationError e) {
			assertEquals(1, e.size());
			assertEquals(MaxLengthValidator.TOO_LONG_ERROR, e.getFieldMsg("fieldTwo"));	
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
		assertEquals("one", val.validate("one"));
		checkException(val, null, OneOfValidator.MISSING_ERROR);
		checkException(val, "three", OneOfValidator.NOT_A_CHOICE_ERROR);
		
		val = new OneOfValidator(new TestItemSource());
		assertEquals("one", val.validate("one"));
		checkException(val, null, OneOfValidator.MISSING_ERROR);
		checkException(val, "three", OneOfValidator.NOT_A_CHOICE_ERROR);
	}
}
