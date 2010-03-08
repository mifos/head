/*
 * Copyright (c) 2005-2009 Grameen Foundation USA
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 *
 * See also http://www.apache.org/licenses/LICENSE-2.0.html for an
 * explanation of the license and how it is applied.
 */

package org.mifos.framework.struts.tags;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.testng.annotations.Test;

@Test(groups={"unit", "fastTestsSuite"},  dependsOnGroups={"productMixTestSuite"})
public class XmlBuilderTest extends TestCase {

    private XmlBuilder out;

    @Override
    public void setUp() {
        out = new XmlBuilder();
    }

    public void testBasics() {
        out.startTag("html");
        out.endTag("html");
       Assert.assertEquals("<html></html>", out.getOutput());
    }

    public void testText() {
        out.startTag("html");
        out.text("don't\"<&>");
        out.endTag("html");
       Assert.assertEquals("<html>don't\"&lt;&amp;&gt;</html>", out.getOutput());
    }

    public void testAttribute() {
        out.startTag("html", "characters", "<&>\"don't");
        out.endTag("html");
       Assert.assertEquals("<html characters=\"&lt;&amp;&gt;&quot;don't\"></html>", out.getOutput());
    }

    public void testMultipleAttributes() {
        out.startTag("form", new String[] { "action", "launch", "method", "nuke" });
        out.endTag("form");
       Assert.assertEquals("<form action=\"launch\" method=\"nuke\"></form>", out.getOutput());
    }

    public void testMultipleAttributesSingleTag() {
        out.singleTag("img", new String[] { "src", "foo.png", "alt", "Wombat Porn" });
       Assert.assertEquals("<img src=\"foo.png\" alt=\"Wombat Porn\" />", out.getOutput());
    }

    public void testSingleTag() {
        out.singleTag("br");
       Assert.assertEquals("<br />", out.getOutput());
    }

    public void testSingleTagWithAttribute() {
        out.singleTag("input", "name", "phoneNumber");
       Assert.assertEquals("<input name=\"phoneNumber\" />", out.getOutput());
    }

    public void testUnclosed() {
        out.startTag("html");
        try {
            out.getOutput();
            Assert.fail();
        } catch (XmlBuilderException e) {
           Assert.assertEquals("unclosed element html", e.getMessage());
        }
    }

    public void testMismatched() {
        out.startTag("p");
        try {
            out.endTag("body");
            Assert.fail();
        } catch (XmlBuilderException e) {
           Assert.assertEquals("end tag body does not match start tag p", e.getMessage());
        }
    }

    public void testLegalNameCharacters() {
        /*
         * Leading colon is legal according to XML 1.0 spec, but the namespace
         * spec does not appear to allow it.
         */
        out.startTag(":foo");
        out.startTag("_bar");
        out.singleTag("x:y-z_w.7e\u0300");
        out.endTag("_bar");
        out.endTag(":foo");
       Assert.assertEquals("<:foo><_bar><x:y-z_w.7e\u0300 /></_bar></:foo>", out.getOutput());
    }

    public void testBadCharacterInName() throws Exception {
        /*
         * Of course, this is just a small subset of the characters which are
         * illegal accoding to the XML spec. But these ones seem like the ones
         * most likely to create a security hole or other really strange stuff.
         *
         * As for the rest, the rules are complicated and differ between XML 1.0
         * and 1.1, so maybe this is one of those things that isn't a big enough
         * problem in practice to worry about enforcing.
         */
        checkBadCharacter("Bad character < in start tag a<b", "a<b");
        checkBadCharacter("Bad character & in start tag this&that", "this&that");
        checkBadCharacter("Bad character > in start tag x>y", "x>y");
        checkBadCharacter("Bad character ' in start tag don't", "don't");
        checkBadCharacter("Bad character \" in start tag x\"", "x\"");
    }

    private void checkBadCharacter(String expectedMessage, String tag) {
        try {
            out.startTag(tag);
            Assert.fail();
        } catch (XmlBuilderException e) {
           Assert.assertEquals(expectedMessage, e.getMessage());
        }
    }

    public void testBadCharacterInSingleTag() throws Exception {
        try {
            out.singleTag("<html/>");
            Assert.fail();
        } catch (XmlBuilderException e) {
           Assert.assertEquals("Bad character < in start tag <html/>", e.getMessage());
        }
    }

    public void testNamespaces() {
        /*
         * As of now, the code is not at all namespace-aware. Of course, it
         * needs to be namespace-tolerant (right now, that means treating
         * namespaced attribute and tag names just like any others).
         */
        out.startTag("html", "xmlns", "default/namespace");
        out.startTag("body", "xmlns:x", "some/other/namespace");
        out.singleTag("x:image", "x:width", "5");
        out.endTag("body");
        out.endTag("html");

       Assert.assertEquals("<html xmlns=\"default/namespace\">" + "<body xmlns:x=\"some/other/namespace\">"
                + "<x:image x:width=\"5\" />" + "</body>" + "</html>", out.getOutput());
    }

    public void testNewline() throws Exception {
        out.singleTag("html");
        out.newline();
       Assert.assertEquals("<html />\n", out.getOutput());
    }

    public void testIndent() throws Exception {
        out.startTag("html");
        out.newline();
        out.indent(4);
        out.singleTag("br");
        out.newline();
        out.endTag("html");
        out.newline();
       Assert.assertEquals("<html>\n" + "    <br />\n" + "</html>\n", out.getOutput());
    }

    public void testFragment() throws Exception {
        /*
         * Test the ability to generate part of an XML document: that is,
         * something which would be well-formed if a root start tag and end tag
         * were wrapped around it.
         */
        out.text("Hi");
        out.startTag("p");
        out.text("a paragraph");
        out.endTag("p");
       Assert.assertEquals("Hi<p>a paragraph</p>", out.getOutput());
    }

    public void testCompose() throws Exception {
        /*
         * Test the ability to compose XML in little pieces and build it up into
         * a larger document.
         */
        XmlBuilder paragraph = new XmlBuilder();
        paragraph.startTag("p");
        paragraph.text("Hello, world");
        paragraph.endTag("p");

        out.text("intro");
        out.append(paragraph);
       Assert.assertEquals("intro<p>Hello, world</p>", out.getOutput());
    }

    public void testComment() throws Exception {
        out.comment("text");
       Assert.assertEquals("<!--text-->", out.getOutput());
    }

    public void testCommentWithHyphens() throws Exception {
        out.comment("--");
       Assert.assertEquals("<!--__-->", out.getOutput());
    }

    public void testCommentWithHyphensEnds() throws Exception {
        out.comment("- foo -");
       Assert.assertEquals("<!--_ foo _-->", out.getOutput());
    }

}
