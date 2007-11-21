/**
 * ProperlyJUnit4AdaptedTest.java
 * 
 * Copyright (c) 2005-2007 Grameen Foundation USA
 * 1029 Vermont Avenue, NW, Suite 400, Washington DC 20005
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
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * See also http://www.apache.org/licenses/LICENSE-2.0.html for an
 * explanation of the license and how it is applied.
 */
package org.mifos.framework;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import static org.junit.Assert.assertTrue;

import junit.framework.JUnit4TestAdapter;

/**
 * Try to programmatically find a particular problem with JUnit 4 tests. If they
 * are adapted to JUnit 3, the class name passed to the JUnit4TestAdapter
 * constructor must match the class name of the test case. The resulting problem
 * is that the wrong test case may be run, and other test cases may never be
 * exercised. This test tries to quickly scan unit tests for this coding error.
 * 
 * <p>
 * This is by no means a very robust unit test, but it may help prevent this
 * particular fatfinger coding error that is otherwise a silent failure. A
 * better test, for instance, might use reflection or examine bytecode to
 * confirm that the code is actually broken, rather than the current "poor-man's
 * parsing" of the java source using regular expressions.
 * 
 * <h2> Things that could break this test </h2>
 * <ul>
 * <li>more than one class per .java source code file</li>
 * <li>slight change in formatting/spacing/newlines breaks regular expression
 * in unexpected way</li>
 * <li>unit tests appear elsewhere than the "test" directory</li>
 * </ul>
 * 
 * But that's ok. This isn't meant to be perfect, just to possibly help out and
 * hopefully not get in the way.
 */
public class ProperlyAdaptedJUnit4Test {
	private static final String RE_ADAPTER_CONSTRUCTOR_CALL = ".+new\\s+JUnit4TestAdapter.+";

	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter(ProperlyAdaptedJUnit4Test.class);
	}

	@Test
	public void properlyAdapted() throws IOException {
		// recursively find .java files in test dir
		List<String> javaTestSourceFiles = getJavaTestSourceFiles();
		assertTrue("no java source files were found", javaTestSourceFiles
				.size() > 0);

		// look for only those files that call the JUnit4TestAdapter constructor
		// from a
		// "suite()" method
		List<String> adaptedTests = filterNonAdaptedTests(javaTestSourceFiles);
		assertTrue("no adapted tests were found. This test class can now be"
				+ " deleted!", adaptedTests.size() > 0);

		// ensure that class calls the JUnit4TestAdapter constructor
		// appropriately
		for (String adaptedTest : adaptedTests) {
			String errmsg = adaptedTest
					+ " is not properly coded as a JUnit 4 adapted test."
					+ " Please ensure that the call to the JUnit4TestAdapter"
					+ " constructor in the suite() method passes in the"
					+ " correct class as the first argument.";
			assertTrue(errmsg, isProperlyAdapted(adaptedTest));
		}
	}

	private static List<String> getJavaTestSourceFiles() throws IOException {
		List<String> files = new ArrayList<String>();
		findJavaSourceFiles("test", files);
		return files;
	}

	private static void findJavaSourceFiles(String dir, List<String> foundSoFar)
			throws IOException {
		File d = new File(dir);
		assertTrue("dir was actually passed in", d.isDirectory());
		for (File f : d.listFiles()) {
			// files beginning with a period or an underscore are generally
			// considered "hidden"
			if (f.isFile() && f.getName().matches("^(\\.|_)"))
				continue;

			// some letters with a ".java" extension... looks like a java
			// source code file
			if (f.isFile() && f.getName().matches("^(?i)\\w+\\.java$"))
				foundSoFar.add(f.getPath());

			else if (f.isDirectory())
				findJavaSourceFiles(f.getPath(), foundSoFar);
		}
	}

	private static List<String> filterNonAdaptedTests(
			List<String> javaTestSourceFiles) throws IOException {
		List<String> adapted = new ArrayList<String>();
		for (String j : javaTestSourceFiles) {
			if (grepFor(j, RE_ADAPTER_CONSTRUCTOR_CALL))
				adapted.add(j);
		}
		return adapted;
	}

	private static boolean grepFor(String fileName, String regex)
			throws IOException {
		BufferedReader in = new BufferedReader(new FileReader(fileName));
		while (true) {
			String line = in.readLine();
			if (null == line) {
				in.close();
				return false;
			}
			if (line.matches(regex)) {
				in.close();
				return true;
			}
		}
	}

	private boolean isProperlyAdapted(String adaptedTest) throws IOException {
		BufferedReader in = new BufferedReader(new FileReader(adaptedTest));
		String fileName = new File(adaptedTest).getName();
		String noExt = fileName.split("\\.")[0];
		String thisFilesClassNameRe = ".+" + noExt + ".+";
		while (true) {
			String line = in.readLine();
			if (null == line) {
				in.close();
				return true;
			}
			if (line.matches(RE_ADAPTER_CONSTRUCTOR_CALL)
					&& !line.matches(thisFilesClassNameRe)) {
				System.out.println(line);
				in.close();
				return false;
			}
		}
	}
}
