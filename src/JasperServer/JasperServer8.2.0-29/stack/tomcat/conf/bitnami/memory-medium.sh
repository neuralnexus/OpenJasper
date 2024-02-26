# Bitnami memory configuration for Tomcat
#
# Note: This will be modified on server size changes

export JAVA_OPTS="-Xms1G -Xmx2G ${JAVA_OPTS}"
