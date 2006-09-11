package org.mifos.framework.struts.tags;

import junit.framework.TestCase;

public class MifosTagUtilsTest extends TestCase {
	
	public void testFilter() throws Exception {
		/** This one seems dubious: most callers will pass null
		    directly to {@link StringBuilder#append} which will
		    append the string "null".
		*/
		assertEquals(null, MifosTagUtils.xmlEscape(null));

		assertEquals("", MifosTagUtils.xmlEscape(""));
		assertEquals("a", MifosTagUtils.xmlEscape("a"));
		assertEquals("&lt;", MifosTagUtils.xmlEscape("<"));
		assertEquals("x &gt; &quot;y&quot; &amp;&amp; z &lt; &#39;a&#39;", 
			MifosTagUtils.xmlEscape("x > \"y\" && z < 'a'"));
	}

}
