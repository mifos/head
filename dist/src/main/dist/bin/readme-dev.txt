The mifos.sh & mifos.bat files which are intentionally commited under source control here
were originally built by the Application Assembler Maven Plugin <http://mojo.codehaus.org/appassembler/appassembler-maven-plugin/>.
as configured (but now commented out) in the dist/pom.xml

They were then copied from target/appassembler/bin to here (src/main/appassembler),
and then minor changes made: CLASSPATH was hard-coded to use "%BASEDIR%"[\/]mifos.war
instead of "$REPO"/mifos-1.11(-SNAPSHOT).war or "$REPO"/* (with BASEDIR instead of
REPO just because that's how it always was in the ZIP).


This as done because it wasn't possible to get the classpath working as we needed it it:

We cannot use the <useAsterikClassPath>true</useAsterikClassPath>,
because Java6 "class path wildcards" only get *.jar from lib/* and ignore *.war. 
We must use lib/mifos.war, not lib/mifos-1.11[-SNAPSHOT].war
because in the dist ZIP the WAR's filename, even if in lib, should be the one ready for external Jetty deployment, 
and also because otherwise the Launch4j EXE wouldn't find it (or if we baked the version number into the EXE, it would break too easily when people would rename the WAR)  
