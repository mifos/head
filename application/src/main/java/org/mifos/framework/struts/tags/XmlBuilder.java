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

import java.util.Stack;

/**
 * Do we really need our own XML/HTML generation class?
 */
public class XmlBuilder {

    private StringBuilder out = new StringBuilder();
    private Stack openElements = new Stack();

    public void startTag(String tag, String... attributes) {
        out.append("<");
        tagName(tag);

        attributes(attributes);

        out.append(">");

        openElements.push(tag);
    }

    private void tagName(String tag) {
        rejectCharacter(tag, '<');
        rejectCharacter(tag, '&');
        rejectCharacter(tag, '>');
        rejectCharacter(tag, '\'');
        rejectCharacter(tag, '"');
        out.append(tag);
    }

    private void rejectCharacter(String tag, char character) {
        if (tag.indexOf(character) != -1) {
            throw new XmlBuilderException("Bad character " + character + " in start tag " + tag);
        }
    }

    private void attribute(String attributeName, String attributeValue) {
        if (attributeName != null) { // useful for dealing with optional
                                     // attributes
            out.append(" ");
            out.append(attributeName);
            out.append("=\"");
            out.append(attributeValue.replaceAll("&", "&amp;").replaceAll("<", "&lt;").replaceAll(">", "&gt;")
                    .replaceAll("\"", "&quot;"));
            out.append("\"");
        }
    }

    private void attributes(String[] attributes) {
        for (int i = 0; i < attributes.length; i += 2) {
            attribute(attributes[i], attributes[i + 1]);
        }
    }

    public void endTag(String tag) {
        Object startTag = openElements.pop();
        if (!tag.equals(startTag)) {
            throw new XmlBuilderException("end tag " + tag + " does not match start tag " + startTag);
        }

        out.append("</");
        out.append(tag);
        out.append(">");
    }

    public void singleTag(String tag, String... attributes) {
        out.append("<");
        tagName(tag);
        attributes(attributes);
        out.append(" />");
    }

    public String getOutput() {
        if (!openElements.isEmpty()) {
            throw new XmlBuilderException("unclosed element " + openElements.peek());
        }
        return out.toString();
    }

    /**
     * Whether it is better style to have toString give you the output, or make
     * people call {@link #getOutput()} is an interesting debate, but we provide
     * toString simply for ease of converting code which had been using
     * StringBuilder.
     */
    @Override
    public String toString() {
        return getOutput();
    }

    public void text(String text) {
        if (text != null) {
            out.append(text.replaceAll("&", "&amp;").replaceAll("<", "&lt;").replaceAll(">", "&gt;"));
        }
    }

    public void newline() {
        out.append('\n');
    }

    public void nonBreakingSpace() {
        // note: using the usual "&nbsp;" won't pass unit tests as valid XHTML
        // so the equivalent "\\u00a0" is being used instead
        out.append("\u00a0");
    }

    public void indent(int spaces) {
        for (int i = 0; i < spaces; ++i) {
            out.append(' ');
        }
    }

    /**
     * Insert the XML corresponding to the argument. The argument is an
     * XmlBuilder rather than a string to encourage programmers to treat XML and
     * one thing, and strings (for example, data from a database, or anything
     * else which needs to be quoted) as another.
     */
    public void append(XmlBuilder builder) {
        out.append(builder.getOutput());
    }

    public void comment(String string) {
        out.append("<!--");
        String text = string.replaceAll("--", "__");
        if (text.startsWith("-")) {
            text = "_" + text.substring(1);
        }
        if (text.endsWith("-")) {
            text = text.substring(0, text.length() - 1) + "_";
        }
        out.append(text);
        out.append("-->");
    }

}
