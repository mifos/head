#!/bin/bash
#This script repackages mifos into a Cloud Foundry ready .war by removing servlet 3.0 structure
#and lowering the number of open file descriptors by unpacking Birt jars

print_usage() 
{
	echo "Prepares mifos war for Cloud Foundry integration"
	echo "Options:"
	echo "-i infile=mifos.war		Input mifos .war file"
	echo "-o outfile=mifos-CF.war		Output .war file"
	echo "-h				Displays this message" 	
}


WARFILE="mifos.war"

TEMP_DIR=/tmp/"$$-mifos-cf"
BASE_DIR=`pwd`

OUTFILE=""

while getopts "i:o:h" opt; do
	case $opt in
	i)
		WARFILE=$OPTARG
	;;
	o)
		OUTFILE=$OPTARG
	;;
	h)
		print_usage
		exit 0
	;;
	\?)
		print_usage
		exit 1
	;;
	esac
done

if [ ! -e $WARFILE ]
then
	echo "$WARFILE not  found"
	print_usage	
	exit 1
fi

#default outfile name (infile-CF.war)
if [ -z $OUTFILE ]
then
	OUTFILE=${WARFILE%.*}-CF.war
fi

#Create tmp dir and copy war there
mkdir $TEMP_DIR
mkdir $TEMP_DIR/output
cp $WARFILE $TEMP_DIR/output
cd $TEMP_DIR/output

#unpack war
echo "Unpacking war"
jar xf $WARFILE
rm $WARFILE

################# Webapp ###################
#move webapp to a separate directory and unpack
echo "Unpacking webapp"
mkdir $TEMP_DIR/webapp
cd $TEMP_DIR/webapp
mv $TEMP_DIR/output/WEB-INF/lib/mifos-webapp*.jar .
jar xf mifos-webapp*.jar
rm mifos-webapp*.jar

#move content
mv *.properties $TEMP_DIR/output
mv sql $TEMP_DIR/output
mv org $TEMP_DIR/output/WEB-INF/classes
mv META-INF/resources/pages $TEMP_DIR/output
mv META-INF/resources/WEB-INF/* $TEMP_DIR/output/WEB-INF
#make web-fragment.xml the new web.xml
mv META-INF/web-fragment.xml $TEMP_DIR/output/WEB-INF/web.xml

#clean up webapp dir
cd $TEMP_DIR
rm -r webapp

################## Reports ##################
#move reports to a separate directory and unpack
echo "Unpacking reports"
mkdir $TEMP_DIR/reports
cd $TEMP_DIR/reports
mv $TEMP_DIR/output/WEB-INF/lib/mifos-reporting*.jar .
jar xf mifos-reporting*.jar
rm mifos-reporting*.jar

#move content
cp -r org $TEMP_DIR/output/WEB-INF/classes
mv birt/* $TEMP_DIR/output
mv pentaho $TEMP_DIR/output/WEB-INF/classes
mv META-INF/resources/webcontent $TEMP_DIR/output
cp -r META-INF/resources/pages $TEMP_DIR/output
cp -r META-INF/resources/WEB-INF/* $TEMP_DIR/output/WEB-INF
mv META-INF/web-fragment.xml $TEMP_DIR/output/WEB-INF/reports-web-fragment.xml
#clean up reports
cd $TEMP_DIR
rm -r $TEMP_DIR/reports

#loop through other jars and search for pages
echo "Unpacking pages from other jars"
cd $TEMP_DIR/output/WEB-INF/lib
for jarfile in mifos-*.jar
do
	#move to temp dir
	mkdir $TEMP_DIR/jar
	cp $jarfile $TEMP_DIR/jar
	cd $TEMP_DIR/jar
	#unpack	
	jar xf $jarfile
	rm $jarfile
	#copy pages
	if [ -e META-INF/resources/pages ]
	then
		cp -r META-INF/resources/pages $TEMP_DIR/output
		echo "Found pages in $jarfile"
	fi
	if [ -e META-INF/resources/js ]
	then
		cp -r META-INF/resources/js $TEMP_DIR/output
		echo "Found js in $jarfile"
	fi
	#clean up
	cd $TEMP_DIR/output/WEB-INF/lib
	rm -r $TEMP_DIR/jar	
done

#unpack birt jars in order to limit open file descriptors
echo "Unpacking Birt jars"
cd $TEMP_DIR/output/WEB-INF/platform/plugins
for file in `ls -d *.jar`
do
 	dirname=${file%.*}
	mkdir $dirname
	mv $file $dirname   
	
	cd $dirname
	jar -xf "$file"
	rm "$file"
	cd ..
done

#modify web.xml
echo "Creating web.xml"
cd $TEMP_DIR/output/WEB-INF/

MATCH="xsi:.*"
sed -i "s/$MATCH//g" web.xml
MATCH='version="3\.0">'
sed -i "s/$MATCH//g" web.xml
MATCH='<\/web-fragment>'
sed -i "s/$MATCH//g" web.xml
NEW_ROOT='<web-app version="2\.5" xmlns="http:\/\/java\.sun\.com\/xml\/ns\/javaee" xmlns:xsi="http:\/\/www\.w3\.org\/2001\/XMLSchema-instance" xsi:schemaLocation="http:\/\/java\.sun\.com\/xml\/ns\/javaee http:\/\/java\.sun\.com\/xml\/ns\/javaee\/web-app_2_5\.xsd">'
OLD_ROOT='<web-fragment.*'
sed -i "s/$OLD_ROOT/$NEW_ROOT/g" web.xml


#insert reporting web-fragment into web.xml
MATCH="<?xml.*"
sed -i "s/$MATCH//g" reports-web-fragment.xml
MATCH="<name.*"
sed -i "s/$MATCH//g" reports-web-fragment.xml
MATCH="<description.*"
sed -i "s/$MATCH//g" reports-web-fragment.xml
MATCH="xsi:.*"
sed -i "s/$MATCH//g" reports-web-fragment.xml
MATCH='version="3\.0">'
sed -i "s/$MATCH//g" reports-web-fragment.xml
MATCH='<web-fragment.*'
sed -i "s/$MATCH//g" reports-web-fragment.xml
MATCH='<\/web-fragment>'
sed -i "s/$MATCH//g" reports-web-fragment.xml
MATCH='<param-value>\${mifos\.birt.*'
NEW_VAL='<param-value><\/param-value>'
sed -i "s/$MATCH/$NEW_VAL/g" reports-web-fragment.xml

cat reports-web-fragment.xml >> web.xml
echo '</web-app>' >> web.xml

rm reports-web-fragment.xml

#change image storage type
#move appdomain .jar to a separate dir and unpack
mkdir $TEMP_DIR/appdomain
cd $TEMP_DIR/appdomain
mv $TEMP_DIR/output/WEB-INF/lib/mifos-appdomain*.jar .

#change properties
echo "Changing properties in appdomain"
#unpack and switch properties contents
jar xf mifos-appdomain*jar 
APPDOMAIN_JAR_NAME=`ls -d mifos-appdomain*jar` 
rm mifos-appdomain*jar
#change property value
MATCH='GeneralConfig.ImageStorageType=filesystem'
NEW_VAL='GeneralConfig.ImageStorageType=database'
sed -i "s/$MATCH/$NEW_VAL/g" org/mifos/config/resources/applicationConfiguration.default.properties
#build jar
jar cf $APPDOMAIN_JAR_NAME *
mv $APPDOMAIN_JAR_NAME $TEMP_DIR/output/WEB-INF/lib
#clean up
cd $TEMP_DIR
rm -r $TEMP_DIR/appdomain

#build war
echo "Building war"
cd $TEMP_DIR/output
jar cf $OUTFILE *
mv $OUTFILE $BASE_DIR

#clean up
cd $BASE_DIR
rm -r $TEMP_DIR

echo "Done. Cloud Foundry ready war is $OUTFILE. Deploy as a JavaWeb Application."
