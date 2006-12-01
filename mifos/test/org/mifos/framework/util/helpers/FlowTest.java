package org.mifos.framework.util.helpers;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;

import junit.framework.TestCase;

public class FlowTest extends TestCase {
	class NonSerializableClass {
		int data;
	}
	public void testIsSerializable() throws Exception {

		Flow flow = new Flow();
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(out);
		oos.writeObject(flow);
		oos.close();
		assertTrue(out.toByteArray().length > 0);
	}	
	
}
