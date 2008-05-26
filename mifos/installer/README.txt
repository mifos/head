This file contains the izpack installer directory structure for Mifos.
----------------------------------------------------------------------

There is one folder named "installer" which should be copied in /svn/mifos/trunk/mifos
An independent build file "build-installer.xml" is provided with targets to build the 
Mifos installer.

Structure of the installer directory
------------------------------------

The installer directory contains the following sub-directories
i) antcalls - This directory contains the ant files used by the installer during the installation.
ii) izpack  - This directory contains all the izpack libraries which it uses to build the installer.
iii) lang-packs - This directory contains the files localized to two languages, English and French.
                  These files contain the localized strings used on the installer panels.
iv)lib  - The lib contains the jars used during the installation.
v) resources - This directory contains the resources used in the installation. Currently it contains
               hibernate.properties with the tags which are replaced during installation and the mifos
               logo displayed on the installer.
vi) specs - This directory contains the Mifos specific specification files used by the installer.
vii) src - This directory contains the source for the definition of DatabaseUserInputPanel.
viii) tomcat - This directory contains the zipped version of tomcat used by mifos.
               In case when the version changes please download the latest version of tomcat as zip 
               and copy it in the installer/tomcat directory.
               

The two build files install.xml and install-tomcatbundle.xml are the mifos installer definition files used by izpack.
install.xml builds the mifos installer with the mifos war and the database installer.
install-TomcatBundle.xml builds the complete mifos installer with tomcat bundled.

Mifos installer build file.
---------------------------

The build-installer.xml build file is used to build the mifos installer. This file imports the mifos build file.
The targets are :

i) clean-installer - deletes the mifos installer jars created in mifos/dist.
ii) build-installer - builds the mifos installers, mifos-installer-v$svn-version.jar and mifos-installer-v$svn-version-tomcat.6.0.16.jar
                      This target expects that the mifos war is already built in mifos/dist.
                      This target depends on clean-installer, svn-revision.
iii) clean-build-installer - builds the mifos installers, mifos-installer-v$svn-version.jar and mifos-installer-v$svn-version-tomcat.6.0.16.jar
                             This target depends on dist target in mifos build file.
                             This target depends on clean-installer, svn-revision.
                             
Integrating the installer in mifos build file.
----------------------------------------------

When integrating the installer with mifos build just copy the izpack properties and first two targets in the mifos build file.

        <property name="IzPack_dir" value="${basedir}/installer/izpack"/>
        <property name="IzPack_lib_dir" value="${basedir}/installer/izpack/lib"/>

        <target name="clean-installer" description="Cleans the old nstallers jars">
		<delete>
			<fileset dir="${dist.dir}" includes="**/*.jar"/>
		</delete>
        </target>

        <target name="build-installer" description="Mifos Application Installer V1.1" depends="clean-installer, svn-revision">
                <taskdef name="IzPack" classpath="${IzPack_lib_dir}/compiler.jar" classname="com.izforge.izpack.ant.IzPackTask" />
                <IzPack input="installer/install.xml" output="${basedir}/dist/mifos-installer-v${svn.version}.jar" installerType="standard" basedir="." IzPackDir="${IzPack_dir}"/>
                <IzPack input="installer/install-TomcatBundle.xml" output="${basedir}/dist/mifos-installer-v${svn.version}-tomcat.6.0.16.jar" installerType="standard" basedir="." IzPackDir="${IzPack_dir}"/>
        </target>

