touch port.properties 
echo $SERVLET_PORT > port.properties
adb remount
adb push port.properties /system/port.properties
rm port.properties
