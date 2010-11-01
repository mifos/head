Please do not remove this otherwise empty directory.

This src\main\webapp\WEB-INF\classes is needed so that Eclipse can compile into it.
Normally this is not needed, but because the .classpath of MIFOS is a little special
and needs to overlay web-content from two projects, and deletes the Output folder
for each source folder on a full rebuild, this is required.

http://mifosforge.jira.com/browse/MIFOS-3925 has some further background.

This TXT serves to explain that, and ensure that SCM tools consider this empty directory.

Best,
Michael Vorburger
