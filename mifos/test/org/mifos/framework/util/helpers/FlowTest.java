package org.mifos.framework.util.helpers;

import junit.framework.TestCase;
import org.mifos.framework.TestUtils;

public class FlowTest extends TestCase {
	public void testIsSerializable() throws Exception {
		Flow flow = new Flow();
		TestUtils.assertCanSerialize(flow);
	}
}
