# Bitnami memory configuration for Tomcat
#
# Note: This will be modified on server size changes

export JAVA_OPTS="-Xms2G -Xmx4G ${JAVA_OPTS}"
