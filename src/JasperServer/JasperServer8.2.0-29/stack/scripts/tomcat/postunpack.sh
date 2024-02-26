#!/bin/bash
# Copyright VMware, Inc.
# SPDX-License-Identifier: APACHE-2.0

# shellcheck disable=SC1091

set -o errexit
set -o nounset
set -o pipefail
# set -o xtrace # Uncomment this line for debugging purposes

# Load libraries
. /opt/bitnami/scripts/libtomcat.sh
. /opt/bitnami/scripts/libfs.sh

# Load Tomcat environment variables
. /opt/bitnami/scripts/tomcat-env.sh

# Ensure 'tomcat' user exists when running as 'root'
ensure_user_exists "$TOMCAT_DAEMON_USER" --group "$TOMCAT_DAEMON_GROUP" --system
# By default, the upstream Tomcat tarball includes very specific permissions on its files
# For simplicity purposes, since Bitnami Tomcat is considered a development environment, we reset to OS defaults
configure_permissions_ownership "$TOMCAT_BASE_DIR" -d "755" -f "644"
chmod a+x "$TOMCAT_BIN_DIR"/*.sh
# Make TOMCAT_HOME writable (non-recursively, for security reasons) both for root and non-root approaches
chown "$TOMCAT_DAEMON_USER" "$TOMCAT_HOME"
chmod g+rwX "$TOMCAT_HOME"
# Make TOMCAT_LIB_DIR writable (non-recursively, for security reasons) for non-root approach, some apps may copy files there
chmod g+rwX "$TOMCAT_LIB_DIR"
# Make required folders writable by the Tomcat web server user
for dir in "$TOMCAT_TMP_DIR" "$TOMCAT_LOGS_DIR" "$TOMCAT_CONF_DIR" "$TOMCAT_WORK_DIR" "$TOMCAT_WEBAPPS_DIR" "${TOMCAT_BASE_DIR}/webapps"; do
    ensure_dir_exists "$dir"
    # Use tomcat:root ownership for compatibility when running as a non-root user
    configure_permissions_ownership "$dir" -d "775" -f "664" -u "$TOMCAT_DAEMON_USER" -g "root"
done

# Allow enabling custom Tomcat webapps
mv "${TOMCAT_BASE_DIR}/webapps" "${TOMCAT_BASE_DIR}/webapps_default"
ln -sf "$TOMCAT_WEBAPPS_DIR" "${TOMCAT_BASE_DIR}/webapps"

# Create a setenv.sh script
# For more info, refer to section '(3.4) Using the "setenv" script' from https://tomcat.apache.org/tomcat-9.0-doc/RUNNING.txt
declare template_dir="${BITNAMI_ROOT_DIR}/scripts/tomcat/bitnami-templates"
render-template "${template_dir}/setenv.sh.tpl" > "${TOMCAT_BIN_DIR}/setenv.sh"
chmod g+rwX "${TOMCAT_BIN_DIR}/setenv.sh"

# Create 'apache-tomcat' symlink pointing to the 'tomcat' directory, for compatibility with Bitnami Docs guides
ln -sf tomcat "${BITNAMI_ROOT_DIR}/apache-tomcat"

# shellcheck disable=SC1091
# Load additional required libraries
. /opt/bitnami/scripts/libos.sh
. /opt/bitnami/scripts/libservice.sh

# Enable extra service management configuration
if [[ "$BITNAMI_SERVICE_MANAGER" = "monit" ]]; then
    generate_monit_conf "tomcat" "$TOMCAT_PID_FILE" /opt/bitnami/scripts/tomcat/start.sh /opt/bitnami/scripts/tomcat/stop.sh
elif [[ "$BITNAMI_SERVICE_MANAGER" = "systemd" ]]; then
    generate_systemd_conf "tomcat" \
        --name "Apache Tomcat" \
        --user "$TOMCAT_DAEMON_USER" \
        --group "$TOMCAT_DAEMON_GROUP" \
        --environment "JAVA_HOME=${BITNAMI_ROOT_DIR}/java" \
        --environment "JAVA_OPTS=-Djava.awt.headless=true -XX:+UseG1GC -Dfile.encoding=UTF-8 -Djava.net.preferIPv4Stack=true -Djava.net.preferIPv4Addresses=true -Duser.home=${TOMCAT_HOME}" \
        --exec-start "${TOMCAT_BIN_DIR}/startup.sh" \
        --exec-stop "${TOMCAT_BIN_DIR}/shutdown.sh" \
        --pid-file "$TOMCAT_PID_FILE"
else
    error "Unsupported service manager ${BITNAMI_SERVICE_MANAGER}"
    exit 1
fi
generate_logrotate_conf "tomcat" "${TOMCAT_LOGS_DIR}/*log"

# Allow the "bitnami" user to write to commonly used Tomcat directories, for improved developer experience
# while also allowing Tomcat to write to both folders
chown -R "bitnami:${TOMCAT_DAEMON_GROUP}" "$TOMCAT_WEBAPPS_DIR" "$TOMCAT_CONF_DIR"

# Create configuration files for setting Tomcat optimization parameters depending on the instance size
# Default to micro configuration until a resize is performed
ensure_dir_exists "${TOMCAT_CONF_DIR}/bitnami"
ln -sf "memory-micro.sh" "${TOMCAT_CONF_DIR}/bitnami/memory.sh"
read -r -a supported_machine_sizes <<< "$(get_supported_machine_sizes)"
for machine_size in "${supported_machine_sizes[@]}"; do
    case "$machine_size" in
        micro)
            xms=256M
            xmx=512M
            ;;
        small)
            xms=256M
            xmx=768M
            ;;
        medium)
            xms=1G
            xmx=2G
            ;;
        large)
            xms=2G
            xmx=4G
            ;;
        xlarge)
            xms=2G
            xmx=4G
            ;;
        2xlarge)
            xms=2G
            xmx=4G
            ;;
        *)
            error "Unknown machine size '${machine_size}'"
            exit 1
            ;;
        esac
    cat >"${TOMCAT_CONF_DIR}/bitnami/memory-${machine_size}.sh" <<EOF
# Bitnami memory configuration for Tomcat
#
# Note: This will be modified on server size changes

export JAVA_OPTS="-Xms${xms} -Xmx${xmx} \${JAVA_OPTS}"
EOF
done

# Enable memory configuration
cat >>"${TOMCAT_BIN_DIR}/setenv.sh" <<EOF

# Memory settings
. "${TOMCAT_CONF_DIR}/bitnami/memory.sh"
EOF
