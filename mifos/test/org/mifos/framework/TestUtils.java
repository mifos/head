package org.mifos.framework;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.StringReader;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import junit.framework.Assert;

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
		assertWellFormedDocument("<root>" + xml + "</root>");
	}

	public static void assertWellFormedDocument(String xmlDocument) 
	throws DocumentException {
		SAXReader reader = new SAXReader();
		reader.read(new StringReader(xmlDocument));
	}

    /**
     * Here is our equals/hashCode testing framework.  Is there really
     * not just one to download?  This wheel gets reinvented so often.
     * The one in junitx.extensions.EqualsHashCodeTestCase is seriously broken -
     * it often gets confused about which equals method it is testing
     * (e.g. the one from Object or the one under test) and similar
     * problems.
     * 
     * Ideas for improvement:
     * - some kind of simplified interface which makes it more clear
     *   what to pass in (like the way gsbase.sourceforge.net's equals
     *   tester is hardcoded at 4 objects, each of which has a particular
     *   meaning).  Might make it easier to call, but does it discourage
     *   people from writing all the tests they need to?  Or encourage
     *   them to pass in dummy arguments?
     * - checkEquals(Object[]... objects)
     *   for example checkEquals(new Object[] { a1, a2, a3 },
     *      new Object[] { b1, b2 },
     *      new Object[] { c1, c2, c3});
     *   where the a's are to be equals to each other, the b's are
     *   to be equals to each other, etc.  But no a is to be equals
     *   to one of the b's or c's, and so on.
     *   This does a bit more thorough job of checking transitivity than
     *   {@link #assertIsNotEqual(Object, Object)}, but is it harder to
     *   understand/read?
     * - hardcoded check that nothing is equals to an unrelated class
     *   (say, an object of type String)?
     */
    
	/**
	 * verifyBasicEqualsContract verifies that an array of objects
	 * of a given class which should be equal to one another meet the 
	 * requirements of the equals contract and that an array of objects 
	 * that are not equal to the first element in the array of equal 
	 * objects all evaluate to false when compared to that element using
	 * the equals method. This method is not intented to cover the full 
	 * range of equals testing but rather to be a single easy to use method 
	 * that ensures a minimum set of requirements are met.  
	 * 
	 * @param equalArray - an array of class T containing at least 2 elements
	 * which are all equal to one another (eg. new Foo[] {new Foo(5), new Foo(5)})
	 * @param notEqual - an array of class T containing at least 1 element all of which
	 * are not equal to the equalArray[0] parameter (eg. new Foo[] {Foo(4)} )
	 */
	public static <T> void verifyBasicEqualsContract(T[] equalArray, T[] notEqualArray) {
		if (equalArray.length < 2) {
			Assert.fail("equalArray requires at least 2 elements (but only had "
							+ equalArray.length + ")");
		}
		if (notEqualArray.length < 1) {
			Assert.fail("notEqualArray requires at least 1 element");
		}
		T equalObject = equalArray[0];
		// verify equals contract for equal objects of the same class
		assertAllEqual(equalArray);
		// verify inequality of an objects of the same class
		for (T notEqual : notEqualArray) {
			assertIsNotEqual(equalObject, notEqual);
		}
		// verify inequality of an unrelated class
		assertIsNotEqual(equalObject, new Object());
		
	}
		
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
