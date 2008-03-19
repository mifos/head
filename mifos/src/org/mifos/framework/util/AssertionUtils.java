package org.mifos.framework.util;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

import java.util.Collection;


public class AssertionUtils  {
	public static<T> void assertSameCollections(Collection<T> expected, Collection<T> actual) {
		assertEquals(expected.size(), actual.size());
		assertTrue(actual.containsAll(expected));
	}
	
	public static<T> void assertNotEmpty(Collection<T> collection) {
		assertNotNull(collection);
		assertFalse(collection.isEmpty());
	}
}
