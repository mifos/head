package org.mifos.framework.persistence;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;

/**
 * This is a little test jig that can read the files in the sql
 * directory, with no need to copy them to the build directory.
 */
class FileReadingPersistence extends DatabaseVersionPersistence {
	FileReadingPersistence(Connection connection) {
		super(connection);
	}

	@Override
	URL lookup(String name) {
		try {
			String filename = "sql/" + name;
			if (new File(filename).exists()) {
				return new URL("file:" + filename);
			}
			else {
				return null;
			}
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
	}
}