package org.mifos.framework;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.StringReader;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import junit.framework.Assert;
import junitx.extensions.EqualsHashCodeTestCase;

import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class TestUtils {

	/**
	 * Also see {@link TestObjectFactory#getUserContext()} which should be
	 * slower (it involves several database accesses).
	 */
	public static UserContext makeUser(int role) {
		UserContext user = new UserContext();
		user.setId(new Short("1"));
		user.setLocaleId(new Short("1"));
		Set<Short> set = new HashSet<Short>();
		set.add((short) role);
		user.setRoles(set);
		user.setLevelId(Short.valueOf("2"));
		user.setName("mifos");
		user.setPereferedLocale(new Locale("en", "US"));
		user.setBranchId(new Short("1"));
		user.setBranchGlobalNum("0001");
		return user;
	}

	public static void assertWellFormedFragment(String xml) 
	throws DocumentException {
		SAXReader reader = new SAXReader();
	    reader.read(new StringReader("<root>" + xml + "</root>"));
	}

    /**
     * Here is our equals/hashCode testing framework.  Is there really
     * not just one to download?  This wheel gets reinvented so often.
     * The one in {@link EqualsHashCodeTestCase} is seriously broken -
     * it often gets confused about which equals method it is testing
     * (e.g. the one from Object or the one under test) and similar
     * problems.
     */
    
    public static void assertAllEqual(Object[] objects) {
        /**
         * The point of checking each pair is to make sure that equals is
         * transitive per the contract of {@link Object#equals(java.lang.Object)}.
         */
        for (int i = 0; i < objects.length; i++) {
            Assert.assertFalse(objects[i].equals(null));
            for (int j = 0; j < objects.length; j++) {
                assertIsEqual(objects[i], objects[j]);
            }
        }
    }

    /**
     * The reason this method should only be called from 
     * {@link #assertAllEqual(Object[])} is that the 
     * latter checks for reflexive and null.
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
    }

    public static void assertReflexiveAndNull(Object object) {
    	Assert.assertTrue(object.equals(object));
    	Assert.assertFalse(object.equals(null));
    }
    
	public static void assertCanSerialize(Object object) throws IOException {
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		ObjectOutputStream objectOutputStream = 
			new ObjectOutputStream(byteArrayOutputStream);
		objectOutputStream.writeObject(object);
		objectOutputStream.close();
		Assert.assertTrue(byteArrayOutputStream.toByteArray().length > 0);
	}

	public static void showMemory() {
		System.out.println("free: " + 
				Runtime.getRuntime().freeMemory()/ 1000000.0 +
				" MB"
				);
		System.out.println("max: " + 
				Runtime.getRuntime().maxMemory()/ 1000000.0 +
				" MB"
				);
		System.out.println("total: " + 
				Runtime.getRuntime().totalMemory()/ 1000000.0 +
				" MB"
				);
		System.out.println();
	}	
    

}
