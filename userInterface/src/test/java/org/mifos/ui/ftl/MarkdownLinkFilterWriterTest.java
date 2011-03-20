/*
 * Copyright (c) 2005-2010 Grameen Foundation USA
 *  All rights reserved.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 *  implied. See the License for the specific language governing
 *  permissions and limitations under the License.
 *
 *  See also http://www.apache.org/licenses/LICENSE-2.0.html for an
 *  explanation of the license and how it is applied.
 */
package org.mifos.ui.ftl;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.Writer;

@RunWith(MockitoJUnitRunner.class)
public class MarkdownLinkFilterWriterTest {
    private String link;
    @Mock
    private Writer out;
    private MarkdownLinkFilterWriter markdownLinkFilterWriter;

    @Before
    public void setUp() {
        link = "createQuestion.ftl";
        markdownLinkFilterWriter = new MarkdownLinkFilterWriter(out, link);
    }

    @Test
    public void testMarkupEnglish() throws Exception {
        char[] characterBuffer = "You can also [define a new question](%s)".toCharArray();
        markdownLinkFilterWriter.write(characterBuffer, 0, 40);
        Mockito.verify(out).write("You can also <a href=\"createQuestion.ftl\">define a new question</a>\n");
    }

    @Test
    public void testMarkupFrench() throws Exception {
        char[] characterBuffer = "Vous pouvez aussi [créer une nouvelle question](%s)".toCharArray();
        new MarkdownLinkFilterWriter(out, link).write(characterBuffer, 0, 51);
        Mockito.verify(out).write("Vous pouvez aussi <a href=\"createQuestion.ftl\">créer une nouvelle question</a>\n");
    }

    @Test
    public void testMarkupChinese() throws Exception {
        char[] characterBuffer = "你也可以[定义新问题](%s)".toCharArray();
        new MarkdownLinkFilterWriter(out, link).write(characterBuffer, 0, 15);
        Mockito.verify(out).write("你也可以<a href=\"createQuestion.ftl\">定义新问题</a>\n");
    }
}
