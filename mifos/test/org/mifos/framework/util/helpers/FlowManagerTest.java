package org.mifos.framework.util.helpers;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import junit.framework.TestCase;

public class FlowManagerTest extends TestCase {
	public void testIsSerializable() throws Exception {

		FlowManager flowManager = new FlowManager();
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(out);
		oos.writeObject(flowManager);
		oos.close();
		assertTrue(out.toByteArray().length > 0);
	}	
}
