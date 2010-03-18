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

package org.mifos.framework.util;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;

import org.junit.Test;
import org.mifos.framework.util.UnicodeUtil.UnicodeInputStream;

/**
 * Tests detecting and decoding unicode files. May require changes if default
 * system file encoding is not UTF-8.
 */
public class UnicodeUtilTest {
    static final String EXPECTED_CONTENT = "Hello world";
    static final String SAMPLE_UTF_16LE_TXT = "/sample_UTF-16LE.txt";
    static final int ONE_KIBIBYTE = 1024;
    static final String DEFAULT_ENCODING = System.getProperty("file.encoding");

    @Test
    public void fileIsExpectedLength() throws IOException, URISyntaxException {
        String filename = this.getClass().getResource(SAMPLE_UTF_16LE_TXT).toURI().getPath();
        FileInputStream in = new FileInputStream(filename);
        byte data[] = new byte[ONE_KIBIBYTE];
        int expectedFilesize = 26;
        assertThat("in.read() correct size", in.read(data), is(expectedFilesize));
        in.close();
        assertThat("not yet decodeable", new String(data).trim(), not(EXPECTED_CONTENT));
    }

    @Test
    public void canDetectUtf16le() throws IOException, URISyntaxException {
        String filename = this.getClass().getResource(SAMPLE_UTF_16LE_TXT).toURI().getPath();
        FileInputStream in = new FileInputStream(filename);
        UnicodeInputStream uis = new UnicodeInputStream(in, DEFAULT_ENCODING);
        String detectedEncoding = uis.getEncoding();
        in.close();
        uis.close();
        assertThat(detectedEncoding, is("UTF-16LE"));
    }

    @Test
    public void canDecodeUtf16le() throws IOException, URISyntaxException {
        String filename = this.getClass().getResource(SAMPLE_UTF_16LE_TXT).toURI().getPath();
        FileInputStream in = new FileInputStream(filename);
        byte data[] = new byte[ONE_KIBIBYTE];
        in.read(data);
        in.close();
        byte converted[] = UnicodeUtil.convert(data, "US-ASCII");
        assertThat(new String(converted).trim(), is(EXPECTED_CONTENT));
    }

    @Test
    public void canReadAndDecodeLineByLine() throws IOException, URISyntaxException {
        String filename = this.getClass().getResource(SAMPLE_UTF_16LE_TXT).toURI().getPath();
        BufferedReader reader = UnicodeUtil.getUnicodeAwareBufferedReader(filename);
        String read = reader.readLine();
        reader.close();
        assertThat(read.trim(), is(EXPECTED_CONTENT));
    }

    @Test
    public void canDetectUtf8() throws IOException, URISyntaxException {
        String filename = this.getClass().getResource("/sample_UTF-8.txt").toURI().getPath();
        FileInputStream in = new FileInputStream(filename);
        byte data[] = new byte[ONE_KIBIBYTE];
        int expectedFilesize = 12;
        assertThat("correct size", in.read(data), is(expectedFilesize));
        in.close();

        UnicodeInputStream uic = new UnicodeInputStream(new FileInputStream(filename), DEFAULT_ENCODING);
        assertThat("guessed encoding", uic.getEncoding(), is(DEFAULT_ENCODING));
        assertThat("read unicode", uic.read(data), is(expectedFilesize));
        uic.close();
        assertThat("correct contents", new String(data).trim(), is(EXPECTED_CONTENT));
    }
}
