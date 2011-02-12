/*
 * Copyright Grameen Foundation USA
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

package org.mifos.ui.ftl;

import java.io.IOException;
import java.io.Writer;

import com.petebevin.markdown.MarkdownProcessor;

/**
 * A {@link Writer} that transforms the plain-text Markdown character stream to HTML and forwards it to another
 * {@link Writer}. Note that the marked-down text is further edited: the paragraph element (automatically and always
 * added by Markdown) is removed. The intended use of this class is within a directive which creates HTML links from
 * i18n messages, and it may be undesirable to also wrap a message in a paragraph element.
 */
public class MarkdownLinkFilterWriter extends Writer {

    private final Writer out;
    private final String dest;

    MarkdownLinkFilterWriter(Writer out, String dest) {
        super();
        this.out = out;
        this.dest = dest;
    }

    @Override
    public void write(char[] cbuf, int off, int len) throws IOException {
        StringBuffer slurped = new StringBuffer();
        for (int i = 0; i < len; i++) {
            slurped.append(cbuf[i + off]);
        }
        String replaced = String.format(slurped.toString(), dest);
        String markedDown = new MarkdownProcessor().markdown(replaced);
        markedDown = markedDown.replace("<p>", "");
        markedDown = markedDown.replace("</p>", "");
        out.write(markedDown);
    }

    @Override
    public void flush() throws IOException {
        out.flush();
    }

    @Override
    public void close() throws IOException {
        out.close();
    }
}
