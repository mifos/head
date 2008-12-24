package org.mifos.framework.util.helpers;

import junit.framework.TestCase;

import org.mifos.framework.TestUtils;

public class FlowManagerTest extends TestCase {
	public void testIsSerializable() throws Exception {
		FlowManager flowManager = new FlowManager();
		TestUtils.assertCanSerialize(flowManager);
	}	
}
