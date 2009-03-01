package org.mifos.framework.util.helpers;

import java.util.Locale;

import org.mifos.framework.MifosTestCase;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.SystemException;

public class BundleKeyTest extends MifosTestCase {

	public BundleKeyTest() throws SystemException, ApplicationException {
        super();
    }

    private BundleKey bundleKey = null;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		Locale locale = new Locale("EN");
		bundleKey = new BundleKey(locale, "Key");
	}

	public void testHashCode() {
		assertEquals(905023, bundleKey.hashCode());
	}

	public void testEqualsObject() {
		assertTrue(bundleKey.equals(bundleKey));
		assertFalse(bundleKey.equals(null));
		Locale locale = new Locale("EN");
		assertFalse(bundleKey.equals(new BundleKey(locale, "wrongKey")));
		locale = new Locale("SP");
		assertFalse(bundleKey.equals(new BundleKey(locale, "Key")));
	}

}
