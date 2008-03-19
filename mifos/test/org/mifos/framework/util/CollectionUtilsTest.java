package org.mifos.framework.util;

import java.util.List;

import junit.framework.TestCase;

public class CollectionUtilsTest extends TestCase {

	public void testAsListReturnsOneElementPassed() {
		List<Integer> list = CollectionUtils.asList(Integer.valueOf(0));
		assertEquals(1, list.size());
		assertEquals(Integer.valueOf(0), list.get(0));
	}

	public void testAsListReturnsListFormedOfMultipleElements()
			throws Exception {
		List<Integer> list = CollectionUtils.asList(Integer.valueOf(0), Integer
				.valueOf(1), Integer.valueOf(2));
		assertEquals(3, list.size());
		assertEquals(Integer.valueOf(0), list.get(0));
		assertEquals(Integer.valueOf(1), list.get(1));
		assertEquals(Integer.valueOf(2), list.get(2));
	}
}
