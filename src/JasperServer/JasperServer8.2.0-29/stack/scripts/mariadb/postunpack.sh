#!/bin/bash
# Copyright VMware, Inc.
# SPDX-License-Identifier: APACHE-2.0

# shellcheck disable=SC1091

set -o errexit
set -o nounset
set -o pipefail
# set -o xtrace # Uncomment this line for debugging purposes

# Load libraries
. /opt/bitnami/scripts/libfs.sh
. /opt/bitnami/scripts/libmariadb.sh

# Load MariaDB environment variables
. /opt/bitnami/scripts/mariadb-env.sh

# Configure MariaDB options based on build-time defaults
info "Configuring default MariaDB options"
ensure_dir_exists "$DB_CONF_DIR"
mysql_create_default_config

for dir in "$DB_TMP_DIR" "$DB_LOGS_DIR" "$DB_CONF_DIR" "${DB_CONF_DIR}/bitnami" "$DB_VOLUME_DIR" "$DB_DATA_DIR"; do
    ensure_dir_exists "$dir"
    chmod -R g+rwX "$dir"
done

# Fix to avoid issues detecting plugins in mysql_install_db
ln -sf "$DB_BASE_DIR/plugin" "$DB_BASE_DIR/lib/plugin"

# Load additional required libraries
# shellcheck disable=SC1091
. /opt/bitnami/scripts/libos.sh
. /opt/bitnami/scripts/libservice.sh
. /opt/bitnami/scripts/libversion.sh

# Create 'data' directory symlink for compatibility purposes with previous VMs
ln -sf "$DB_DATA_DIR" "${DB_BASE_DIR}/data"

info "Creating MariaDB daemon user"
ensure_user_exists "$DB_DAEMON_USER" --group "$DB_DAEMON_GROUP"

# Configure ownership of directories that need write permissions
configure_permissions_ownership "$DB_DATA_DIR" -u "$DB_DAEMON_USER" -g "$DB_DAEMON_GROUP" -d "775" -f "664"

# Enable extra service management configuration
if [[ "$BITNAMI_SERVICE_MANAGER" = "monit" ]]; then
    generate_monit_conf "mariadb" "$DB_PID_FILE" /opt/bitnami/scripts/mariadb/start.sh /opt/bitnami/scripts/mariadb/stop.sh
elif [[ "$BITNAMI_SERVICE_MANAGER" = "systemd" ]]; then
    # Use 'simple' type to start service in foreground and consider started while it is running
    generate_systemd_conf "mariadb" \
        --name "MariaDB" \
        --type "simple" \
        --user "$DB_DAEMON_USER" \
        --group "$DB_DAEMON_GROUP" \
        --exec-start "${DB_SBIN_DIR}/mysqld --defaults-file=${DB_CONF_FILE} --basedir=${DB_BASE_DIR} --datadir=${DB_DATA_DIR} --socket=${DB_SOCKET_FILE} --pid-file=${DB_PID_FILE}" \
        --limit-nofile "65536" \
        --pid-file "$DB_PID_FILE"
else
    error "Unsupported service manager ${BITNAMI_SERVICE_MANAGER}"
    exit 1
fi
# 'su' option used to avoid: "error: skipping (...) because parent directory has insecure permissions (It's world writable or writable by group which is not "root")"
generate_logrotate_conf "mariadb" "${DB_LOGS_DIR}/*log" --extra "su ${DB_DAEMON_USER} ${DB_DAEMON_GROUP}"

# Create configuration files for setting MariaDB optimization parameters that will depend on the instance size
# Default to micro configuration until a resize is performed
ensure_dir_exists "${DB_CONF_DIR}/bitnami/memory"
ln -sf "memory/my-micro.conf" "${DB_CONF_DIR}/bitnami/memory.conf"
cat >>"$DB_CONF_FILE" <<<"!include ${DB_CONF_DIR}/bitnami/memory.conf"
read -r -a supported_machine_sizes <<< "$(get_supported_machine_sizes)"
for machine_size in "${supported_machine_sizes[@]}"; do
    case "$machine_size" in
        micro)
            innodb_buffer_pool_size=16M
            ;;
        small)
            innodb_buffer_pool_size=256M
            ;;
        medium)
            innodb_buffer_pool_size=256M
            ;;
        large)
            innodb_buffer_pool_size=2024M
            ;;
        xlarge)
            innodb_buffer_pool_size=2048M
            ;;
        2xlarge)
            innodb_buffer_pool_size=4096M
            ;;
        *)
            error "Unknown machine size '${machine_size}'"
            exit 1
            ;;
        esac
    cat >"${DB_CONF_DIR}/bitnami/memory/my-${machine_size}.conf" <<EOF
[mysqld]
#wait_timeout = 120
long_query_time = 1
query_cache_size=0
innodb_buffer_pool_size=${innodb_buffer_pool_size}
#innodb_log_file_size=128M
#innodb_flush_method=O_DIRECT
#tmp_table_size=64M
#max_connections = 2500
#max_user_connections = 2500
#key_buffer_size=64M
EOF
done

# Enable memory configuration by adding an '!include' directive in MariaDB's main configuration file
conf_to_add='!'"include ${DB_CONF_DIR}/bitnami/memory.conf"
grep -q "$conf_to_add" "$DB_CONF_FILE" || cat >>"$DB_CONF_FILE" <<<"$conf_to_add"

# Allow the "bitnami" user to write to commonly used MariaDB directories, for improved developer experience
if user_exists "bitnami"; then
    configure_permissions_ownership "$DB_CONF_FILE" -u "bitnami"
fi

info "Setting kernel configurations"
cat >/etc/sysctl.d/99-mariadb.conf <<EOF
fs.file-max=65536
EOF
sysctl -p /etc/sysctl.d/99-mariadb.conf
cat >/etc/security/limits.d/99-mariadb.conf <<EOF
mysql soft nofile 65536
mysql hard nofile 65536
EOF
