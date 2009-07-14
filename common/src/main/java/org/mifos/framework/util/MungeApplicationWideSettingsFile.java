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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

// TODO: allow user to specify end-of-line character?
// like, "system" for system default, "dos", "unix", etc.

/**
 * 1st arg: input file 2nd arg: output file
 */
public class MungeApplicationWideSettingsFile {
    public static void main(String[] args) throws FileNotFoundException, IOException {
        commentUncommentedAndNonBlankLines(args[0], args[1]);
    }

    @edu.umd.cs.findbugs.annotations.SuppressWarnings(value="OS_OPEN_STREAM_EXCEPTION_PATH", justification="reader and writer appear to be closed properly")
    public static void commentUncommentedAndNonBlankLines(String inputFilename, String outputFilename)
            throws FileNotFoundException, IOException {
        BufferedReader input = null;
        BufferedWriter output = null;
        try {
            input = new BufferedReader(new FileReader(inputFilename));
            output = new BufferedWriter(new FileWriter(outputFilename));
            String line = input.readLine();
            while (line != null) {
                if (line.matches("^\\s*(#).*") || line.matches("^\\s*$")) {
                    output.write(line + "\n");
                 // don't write anything if the line begins with "!" (this skips the warning for the default file)
                } else if (!line.matches("^\\s*(!).*")){
                    output.write("#" + line + "\n");
                }
                line = input.readLine();
            }
        } finally {
            if (input != null) {
                input.close();
            }
            if (output != null) {
                output.close();
            }
        }
    }
}
