#!/bin/bash
# $Id: workbench.sh 17453 2010-05-21 22:21:54Z swood $
# Launch Mondrian Schema Workbench on Linux, UNIX or Cygwin

# Platform specific path-separator.

# first look in directory of the script for lib, then 
# look up one folder if lib does not exist

MONDRIAN_HOME=`cd \`dirname $0\`; pwd`
if test ! -d "$MONDRIAN_HOME/lib"; then
  MONDRIAN_HOME="`cd \`dirname $0\`/..; pwd`"
fi
case `uname` in
Windows_NT|CYGWIN*)
    export PS=";"
    export MONDRIAN_HOME=`cygpath -m $MONDRIAN_HOME`
    ;;
*)
    export PS=":"
    ;;
esac

CP="${MONDRIAN_HOME}/lib/commons-collections.jar"
CP="${CP}${PS}${MONDRIAN_HOME}/lib/commons-pool.jar"
CP="${CP}${PS}${MONDRIAN_HOME}/lib/commons-dbcp.jar"
CP="${CP}${PS}${MONDRIAN_HOME}/lib/commons-io.jar"
CP="${CP}${PS}${MONDRIAN_HOME}/lib/commons-lang.jar"
CP="${CP}${PS}${MONDRIAN_HOME}/lib/commons-math.jar"
CP="${CP}${PS}${MONDRIAN_HOME}/lib/commons-vfs.jar"
CP="${CP}${PS}${MONDRIAN_HOME}/lib/commons-logging.jar"
CP="${CP}${PS}${MONDRIAN_HOME}/lib/eigenbase-properties.jar"
CP="${CP}${PS}${MONDRIAN_HOME}/lib/eigenbase-resgen.jar"
CP="${CP}${PS}${MONDRIAN_HOME}/lib/eigenbase-xom.jar"
CP="${CP}${PS}${MONDRIAN_HOME}/lib/javacup.jar"
CP="${CP}${PS}${MONDRIAN_HOME}/lib/log4j.jar"
CP="${CP}${PS}${MONDRIAN_HOME}/lib/mondrian.jar"
CP="${CP}${PS}${MONDRIAN_HOME}/lib/olap4j.jar"
CP="${CP}${PS}${MONDRIAN_HOME}/lib/jlfgr.jar"
CP="${CP}${PS}${MONDRIAN_HOME}/lib/asm-commons.jar"
CP="${CP}${PS}${MONDRIAN_HOME}/lib/asm.jar"
CP="${CP}${PS}${MONDRIAN_HOME}/lib/xercesImpl.jar"
CP="${CP}${PS}${MONDRIAN_HOME}/lib/branding-3.8.0.jar"

# Workbench GUI code and resources
CP="${CP}${PS}${MONDRIAN_HOME}/lib/workbench.jar"

# local directory is ~/.schemaWorkbench

if test ! -d ${HOME}/.schemaWorkbench; then
    mkdir ${HOME}/.schemaWorkbench
fi

# copy mondrian.properties and log4j.xml if necessary
if test ! -e ${HOME}/.schemaWorkbench/mondrian.properties; then
    cp mondrian.properties ${HOME}/.schemaWorkbench/mondrian.properties
fi

if test ! -e ${HOME}/.schemaWorkbench/log4j.xml; then
    cp log4j.xml ${HOME}/.schemaWorkbench/log4j.xml
fi

CP="${CP}${PS}${HOME}/.schemaWorkbench"


# or
# set the log4j.properties system property 
# "-Dlog4j.properties=path to <.properties or .xml file>"
# in the java command below to adjust workbench logging

# add all needed JDBC drivers to the classpath
for i in `find ${MONDRIAN_HOME}/drivers -name '*.jar' 2> /dev/null`; do
    CP="${CP}${PS}${i}"
done

# add all needed plugins to the classpath
for i in `ls ${MONDRIAN_HOME}/plugins/*.jar 2> /dev/null`; do
    CP="${CP}${PS}${i}"
done

#echo $CP

JAVA_FLAGS="-Xms100m -Xmx500m"
#JAVA_FLAGS="-verbose $JAVA_FLAGS"

exec java $JAVA_FLAGS -cp "$CP" mondrian.gui.Workbench

# End workbench.sh
