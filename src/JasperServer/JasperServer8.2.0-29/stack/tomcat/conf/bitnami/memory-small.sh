# Bitnami memory configuration for Tomcat
#
# Note: This will be modified on server size changes

export JAVA_OPTS="-Xms256M -Xmx768M ${JAVA_OPTS}"
